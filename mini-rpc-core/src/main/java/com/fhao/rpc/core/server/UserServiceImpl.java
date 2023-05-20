package com.fhao.rpc.core.server;


import com.fhao.rpc.interfaces.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public void test() {
        System.out.println("test");
    }
}
