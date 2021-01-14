package com.yuugu.sample.module.user;

import com.yuugu.sample.framework.base.UserTokenService;

public class UserTokenServiceImpl implements UserTokenService {

    @Override
    public String getCurrentUserToken() {
        return "Yuu-gu";
    }
}
