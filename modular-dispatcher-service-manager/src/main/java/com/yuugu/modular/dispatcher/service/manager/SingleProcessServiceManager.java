package com.yuugu.modular.dispatcher.service.manager;

import com.yuugu.modular.dispatcher.core.ServiceFactory;
import com.yuugu.modular.dispatcher.core.ServiceManager;

import java.util.HashMap;
import java.util.Map;

public class SingleProcessServiceManager implements ServiceManager {

    private final Map<Class, ServiceFactory> serviceFactoryMap = new HashMap<>();

    @Override
    public void registerService(Class serviceClazz, ServiceFactory serviceFactory) {
        if (serviceClazz == null || serviceFactory == null) {
            throw new IllegalArgumentException("serviceClazz == null || serviceFactory == null");
        }
        if (serviceFactoryMap.containsKey(serviceClazz)) {
            return;
        }
        serviceFactoryMap.put(serviceClazz, serviceFactory);
    }

    @Override
    public void unregisterService(Class serviceClazz) {
        if (serviceClazz == null) {
            return;
        }

        if (serviceFactoryMap.containsKey(serviceClazz)) {
            serviceFactoryMap.remove(serviceClazz);
        }
    }

    @Override
    public <T> T getService(Class<T> serviceClazz) {

        if (serviceFactoryMap.containsKey(serviceClazz)) {
            return serviceFactoryMap.get(serviceClazz).create(serviceClazz);
        }

        return null;
    }
}
