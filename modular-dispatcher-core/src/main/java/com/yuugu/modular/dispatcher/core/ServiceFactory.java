package com.yuugu.modular.dispatcher.core;

/**
 * It is used to get new instances from the module.
 * It is dominated
 * by the internal design,
 * whether to re create or get the same instance each time
 */
public interface ServiceFactory {
    <T> T create(Class<T> serviceClazz);
}
