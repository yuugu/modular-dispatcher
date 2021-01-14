package com.yuugu.sample.module.user;

import android.content.Context;
import android.util.Log;

import com.yuugu.modular.dispatcher.annotations.SingleModule;
import com.yuugu.modular.dispatcher.core.Module;
import com.yuugu.modular.dispatcher.core.ServiceFactory;
import com.yuugu.sample.framework.base.ShareService;
import com.yuugu.sample.framework.base.UserTokenService;

import java.util.ArrayList;
import java.util.List;

@SingleModule("user")
public class UserModule implements Module {

    private final UserTokenServiceImpl userTokenService = new UserTokenServiceImpl();

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
        services.add(UserTokenService.class);
        return services;
    }

    @Override
    public ServiceFactory serviceFactory() {
        return new ServiceFactory() {
            @Override
            public <T> T create(Class<T> serviceClazz) {
                if (serviceClazz == UserTokenService.class) {
                    return (T) userTokenService;
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
