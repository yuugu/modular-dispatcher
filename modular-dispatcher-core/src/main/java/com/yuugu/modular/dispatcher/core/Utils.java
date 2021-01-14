package com.yuugu.modular.dispatcher.core;

import java.util.Collection;

class Utils {

    private Utils() {}

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() < 1;
    }
}
