package com.yuugu.modular.dispatcher.core;

import android.content.Context;

import java.util.List;

// abstract description a module

public interface Module {

    /**
     * called when module been attached to application,
     *
     * @param context
     */
    void attach(Context context);

    /**
     * called when module been detached from application
     */
    void detach();

    /**
     * supported services declaration
     *
     * @return
     */
    List<Class> supportedServices();

    /**
     * service factory to create a service instance for another module
     *
     * @return
     */
    ServiceFactory serviceFactory();

    /**
     * current module's dependency on other modules
     *
     * @return list of other modules' name
     */
    List<String> dependsOn();

}
