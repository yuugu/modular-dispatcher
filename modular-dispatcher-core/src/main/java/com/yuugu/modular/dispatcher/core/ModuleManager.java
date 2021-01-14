package com.yuugu.modular.dispatcher.core;

public interface ModuleManager {

    /**
     * init modules
     */
    void init();

    /**
     * dynamic attach a new module
     * @param moduleClass
     */
    void attach(Class<? extends Module> moduleClass);

    /**
     * dynamic detach a module
     * @param moduleClass
     */
    void detach(Class<? extends Module> moduleClass);

    /**
     * get a instance of module's service
     *
     * @param serviceClazz
     * @param <T>
     * @return
     */
    <T> T service(Class<T> serviceClazz);

    /**
     * terminate whole module manager
     */
    void terminate();
}

