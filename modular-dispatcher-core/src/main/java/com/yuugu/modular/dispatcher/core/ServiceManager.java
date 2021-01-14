package com.yuugu.modular.dispatcher.core;

public interface ServiceManager {

    void registerService(Class serviceClazz, ServiceFactory serviceFactory);

    void unregisterService(Class serviceClazz);

    <T> T getService(Class<T> serviceClazz);
}
