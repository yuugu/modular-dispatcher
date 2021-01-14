package com.yuugu.modular.dispatcher.core;

import java.util.Map;

public interface ModulePackage {
    Map<String, Class<? extends Module>> modules();
}
