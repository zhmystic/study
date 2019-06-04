package com.suncar.card.service.controller.reflect;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-04 12:08
 * @description     反射
 */
public class Robot {

    private String name;
    public void sayHi(String helloSentence) {
        System.out.println(helloSentence + " " + name);
    }

    private String throwHello(String tag) {
        return "hello" + tag;
    }
    static {
        System.out.println("我被执行了！");
    }

}
