package com.yuugu.modular.dispatcher.core;

import android.content.Context;
import android.text.TextUtils;

import com.yuugu.modular.dispatcher.packages.GeneratedClassModulePackage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ModularServer implements ModuleManager {

    private static final String TAG = "ModularServer";
    private static final String PROVIDER_CLASS_PATH_PREFIX = "ModuleClassProvider_";
    private static final String PACKAGE_NAME = "com.yuugu.modular.dispatcher.auto.generated";

    private final Context context;
    private final ModulePackage modulePackage;
    private final ServiceManager serviceManager;
    private final Map<Class<? extends Module>, Module> class2ModuleMap;
    private final Set<String> modules;
    private final Map<String, Class<? extends Module>> name2ClassMap;

    private ModularServer(Builder builder) {
        this.context = builder.context;
        this.modulePackage = builder.modulePackage;
        this.serviceManager = builder.serviceManager;
        this.class2ModuleMap = new LinkedHashMap<>();
        this.name2ClassMap = new HashMap<>();
        this.modules = builder.modules;
    }

    @Override
    public void init() {
        L.i(TAG, "init start....");
        createBuiltinModules();
        attachBuiltinModules();
    }

    @Override
    public void attach(Class<? extends Module> moduleClass) {
        // 1. Ensure current module is not attached before
        if (class2ModuleMap.get(moduleClass) != null) {
            L.i(TAG, "attach: attached before, skip " + moduleClass.getName());
            return;
        }

        Module module = createModuleInstance(moduleClass);

        // 2. Ensure all dependency are available for current module
        List<String> dependency = module.dependsOn();
        if (!Utils.isEmpty(dependency)) {
            for (String moduleName : dependency) {
                if (!name2ClassMap.containsKey(moduleName)) {
                    throw new IllegalArgumentException(moduleClass + "'s depends on module \'" + moduleName + "\', but it's not exist");
                }
            }
        }

        class2ModuleMap.put(moduleClass, module);
        attachModuleInternal(module);

        L.i(TAG, "attach: attached done for " + moduleClass.getName());
    }

    @Override
    public void detach(Class<? extends Module> moduleClass) {
        Module module = class2ModuleMap.get(moduleClass);
        if (module == null) {
            L.i(TAG, "detach: not attached or detached before, skip " + moduleClass.getName());
            return;
        }

        detachModuleInternal(module);
        class2ModuleMap.remove(moduleClass);
        L.i(TAG, "detach: detached done for " + moduleClass.getName());
    }

    @Override
    public <T> T service(Class<T> serviceClazz) {
        L.i(TAG, "get service: serviceClazz = " + serviceClazz);
        T service = serviceManager.getService(serviceClazz);
        L.i(TAG, "get service: service = " + service);
        return service;
    }

    @Override
    public void terminate() {
        Iterator<Map.Entry<Class<? extends Module>, Module>> entries = class2ModuleMap.entrySet().iterator();
        if (entries == null) return;
        while (entries.hasNext()) {
            Map.Entry<Class<? extends Module>, Module> entry = entries.next();
            Module module = entry.getValue();
            detachModuleInternal(module);
        }

        class2ModuleMap.clear();
    }

    private Module createModuleInstance(Class<? extends Module> moduleClass) {
        try {
            Module module = moduleClass.newInstance();
            return module;
        } catch (Throwable e) {
            throw new RuntimeException("error while create new instance of module: " + moduleClass + ", error : " + e.getMessage());
        }
    }

    private void detachModuleInternal(Module module) {
        // 1. unregister services when both are not empty
        ServiceFactory serviceFactory = module.serviceFactory();
        List<Class> services = module.supportedServices();
        if (!Utils.isEmpty(services) && serviceFactory != null) {
            for (Class serviceClass : services) {
                serviceManager.unregisterService(serviceClass);
            }
        }
        // 2. callback for module's detach
        module.detach();
    }

    private void attachBuiltinModules() {
        Iterator<Map.Entry<Class<? extends Module>, Module>> entries = class2ModuleMap.entrySet().iterator();
        if (entries == null) return;

        while (entries.hasNext()) {
            Map.Entry<Class<? extends Module>, Module> entry = entries.next();
            Module module = entry.getValue();
            L.i(TAG, "attachBuiltinModules: ------> " + entry.getKey());
            attachModuleInternal(module);
        }
    }

    private void attachModuleInternal(Module module) {
        L.i(TAG, "attachModuleInternal: module " + module);
        if (module == null) {
            return;
        }

        ServiceFactory serviceFactory = module.serviceFactory();
        List<Class> services = module.supportedServices();
        L.i(TAG, "attachModuleInternal: serviceFactory = " + serviceFactory);
        L.i(TAG, "attachModuleInternal: services = " + services);

        // 1. Only register services when both serviceFactory and services are not empty
        if (!Utils.isEmpty(services) && serviceFactory != null) {
            for (Class serviceClass : services) {
                L.i(TAG, "attachModuleInternal: registerService = " + serviceClass);
                serviceManager.registerService(serviceClass, serviceFactory);
            }
        }

        // 2. Callback for module's attach
        module.attach(context);
    }

    private void createBuiltinModules() {

        L.i(TAG, "createBuiltinModules start....");

        final Map<String, Class<? extends Module>> modulesClassMap = new HashMap<>();
        final Map<String, Class<? extends Module>> packageModulesMap = modulePackage.modules();
        if (packageModulesMap != null) {
            modulesClassMap.putAll(packageModulesMap);
        }

        if (modules != null && modules.size() > 0) {
            L.i(TAG, "createBuiltinModules start register module for ModuleClassProvider...");
            for (String singleModuleName : modules) {
                final String fullProviderClassName = PACKAGE_NAME + "." + PROVIDER_CLASS_PATH_PREFIX + upperCamelCase(singleModuleName);
                try {
                    ModuleClassProvider moduleClassProvider = (ModuleClassProvider) Class.forName(fullProviderClassName).newInstance();
                    if (!singleModuleName.equals(moduleClassProvider.moduleName())) {
                        throw new RuntimeException("name not same while creating instance for " + fullProviderClassName);
                    }
                    modulesClassMap.put(singleModuleName, moduleClassProvider.moduleClass());
                    L.i(TAG, "createBuiltinModules , register module : " + singleModuleName);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    throw new RuntimeException("error while create ModuleClassProvider for " + fullProviderClassName + " : " + ex.getMessage());
                }
            }
        }

        name2ClassMap.clear();
        name2ClassMap.putAll(modulesClassMap);

        final Map<String, Module> name2ModuleMap = new HashMap<>();

        if (modulesClassMap == null) {
            return;
        }

        // 1. fill name-module map
        Iterator<Map.Entry<String, Class<? extends Module>>> entries = modulesClassMap.entrySet().iterator();
        while (entries.hasNext()) {

            Map.Entry<String, Class<? extends Module>> entry = entries.next();
            if (entry == null) continue;

            String name = entry.getKey();
            Class<? extends Module> moduleClass = entry.getValue();
            Module module = createModuleInstance(moduleClass);

            name2ModuleMap.put(name, module);

            L.i(TAG, "createBuiltinModules: name = " + name + ", moduleClass = " + moduleClass + ", module = " + module);
        }

        L.i(TAG, "createBuiltinModules: name2ModuleMap = " + name2ModuleMap);

        // 2. calculate init order according to name-module map
        final List<String> orderedDependencyModuleNames = DependencyCalculate.from(name2ModuleMap).calculate();

        L.i(TAG, "createBuiltinModules: orderedDependencyModuleNames = " + orderedDependencyModuleNames);

        class2ModuleMap.clear();

        // 3. fill clazz-module map according to orderedDependencyModuleNames
        for (String name : orderedDependencyModuleNames) {

            Class<? extends Module> clazz = modulesClassMap.get(name);
            Module module = name2ModuleMap.get(name);
            class2ModuleMap.put(clazz, module);

            L.i(TAG, "createBuiltinModules: fill class2ModuleMap: clazz = " + clazz + ", module = " + module);
        }
    }

    public static Builder create(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private Context context;
        private ModulePackage modulePackage = new GeneratedClassModulePackage();
        private ServiceManager serviceManager;
        private Set<String> modules = new HashSet<>();
        private boolean isDebug = false;

        private Builder(Context context) {
            this.context = context;
        }

        public Builder setDebug(boolean debug) {
            isDebug = debug;
            return this;
        }

        public Builder registerModule(String moduleName) {
            if (TextUtils.isEmpty(moduleName)) {
                throw new IllegalArgumentException("moduleName == null");
            }
            modules.add(moduleName);
            return this;
        }

        public Builder setModulePackage(ModulePackage modulePackage) {
            this.modulePackage = modulePackage;
            return this;
        }

        public Builder setServiceManager(ServiceManager serviceManager) {
            this.serviceManager = serviceManager;
            return this;
        }

        public ModularServer build() {

            if (context == null) {
                throw new IllegalArgumentException("context == null");
            }

            if (modulePackage == null) {
                throw new IllegalArgumentException("modulePackage == null");
            }

            if (serviceManager == null) {
                throw new IllegalArgumentException("serviceManager == null");
            }

            L.setEnabled(isDebug);

            return new ModularServer(this);
        }
    }

    private String upperCamelCase(String name) {
        if (name.contains("-")) {
            String[] arr = name.split("-");
            StringBuilder ret = new StringBuilder();
            for (String s : arr) {
                if (isNotEmpty(s)) ret.append(capitalize(s));
            }
            return ret.toString();
        }
        return capitalize(name);
    }

    private String capitalize(String str) {
        return isNotEmpty(str) ? str.substring(0, 1).toUpperCase() + str.substring(1) : str;
    }

    private boolean isNotEmpty(String str) {
        return str != null && str.trim().length() > 0;
    }
}
