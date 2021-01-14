package com.yuugu.modular.dispatcher.core;

public final class ModularDispatcher {

    private static final String TAG = "ModularDispatcher";
    private static final String ERROR_INIT_FIRST = "method ModularDispatcher.get().init(ModularServer server) must been called first";

    private static volatile ModularDispatcher instance;

    private ModuleManager server;

    /**
     * Get single instance for {@link ModularDispatcher}
     *
     * @return instance of ModularDispatcher
     */
    public static ModularDispatcher get() {
        if (instance == null) {
            synchronized (ModularDispatcher.class) {
                if (instance == null) {
                    instance = new  ModularDispatcher();
                }
            }
        }

        return instance;
    }

    private ModularDispatcher() {}

    /**
     * init() must be called first before any other method calls
     *
     * @param moduleManager
     */
    public void init(ModuleManager moduleManager) {

        if (this.server != null) {
            throw new RuntimeException("server init before, should call init() only once");
        }

        if (moduleManager == null) {
            throw new IllegalArgumentException("moduleManager == null");
        }

        this.server = moduleManager;

        L.i(TAG, "init: server = " + server);

        L.i(TAG, "init: call server init");

        server.init();
    }

    public void terminate() {
        if (server != null) {
            server.terminate();
        }
    }

    public <T> T service(Class<T> serviceClazz) {
        assertInitCalledBefore();
        return server.service(serviceClazz);
    }

    public void attach(Class<? extends Module> moduleClass) {
        assertInitCalledBefore();
        server.attach(moduleClass);
    }

    public void detach(Class<? extends Module> moduleClass) {
        assertInitCalledBefore();
        server.detach(moduleClass);
    }

    private void assertInitCalledBefore() {
        if (server == null) {
            throw new IllegalArgumentException(ERROR_INIT_FIRST);
        }
    }
}
