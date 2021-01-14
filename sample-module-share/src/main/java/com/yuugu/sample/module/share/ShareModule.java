package com.yuugu.sample.module.share;

import android.content.Context;
import android.util.Log;

import com.yuugu.modular.dispatcher.annotations.SingleModule;
import com.yuugu.modular.dispatcher.core.ModularDispatcher;
import com.yuugu.modular.dispatcher.core.Module;
import com.yuugu.modular.dispatcher.core.ServiceFactory;
import com.yuugu.sample.framework.base.ShareService;
import com.yuugu.sample.framework.base.UserTokenService;

import java.util.ArrayList;
import java.util.List;

@SingleModule("share")
public class ShareModule implements Module {

    private final ShareService shareService = new ShareServiceImpl();

    @Override
    public void attach(Context context) {
        Log.i("init-test", "attach: ShareModule");
    }

    @Override
    public void detach() {

    }

    @Override
    public List<Class> supportedServices() {
        List<Class> services = new ArrayList<>();
        services.add(ShareService.class);
        return services;
    }

    @Override
    public ServiceFactory serviceFactory() {
        return new ServiceFactory() {
            @Override
            public <T> T create(Class<T> serviceClazz) {
                if (serviceClazz == ShareService.class) {
                    return (T) shareService;
                }
                return null;
            }
        };
    }

    @Override
    public List<String> dependsOn() {
        return null;
    }
}
