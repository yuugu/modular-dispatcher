package com.yuugu.modular.dispatcher.core;

public interface ModuleClassProvider {
    Class<? extends Module> moduleClass();
    String moduleName();
}
