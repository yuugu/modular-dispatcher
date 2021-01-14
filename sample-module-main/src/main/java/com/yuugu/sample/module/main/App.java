package com.yuugu.sample.module.main;

import android.app.Application;

import com.yuugu.modular.dispatcher.core.ModularDispatcher;
import com.yuugu.modular.dispatcher.core.ModularServer;
import com.yuugu.modular.dispatcher.service.manager.SingleProcessServiceManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ModularServer server = ModularServer.create(this)
                .setDebug(true)
                .registerModule("user")
                .registerModule("share")
                .setServiceManager(new SingleProcessServiceManager())
                .build();

        ModularDispatcher.get().init(server);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ModularDispatcher.get().terminate();
    }
}
