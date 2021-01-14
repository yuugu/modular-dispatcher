package com.yuugu.modular.dispatcher.packages;

import android.util.Log;

import com.yuugu.modular.dispatcher.core.Module;
import com.yuugu.modular.dispatcher.core.ModulePackage;

import java.util.Map;

public class GeneratedClassModulePackage implements ModulePackage {

    /**
     * modular toolkit would auto generate this class,
     * notice that put this class to your Proguard rules.
     */
    private static final String M = "com.yuugu.modular.dispatcher.auto.generated.M";

    private ModulePackage modulePackage;

    public GeneratedClassModulePackage() {
        try {
            modulePackage = (ModulePackage) Class.forName(M).newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("ModularServer","error while create ModulePackage from class " + M + " : " + e.getMessage());
        }
    }

    @Override
    public Map<String, Class<? extends Module>> modules() {
        if (modulePackage == null) {
            return null;
        }
        return modulePackage.modules();
    }
}
