package com.suncar.card.service.controller.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-04 12:10
 * @description
 */
public class ReflectSample {

    public static void main(String[] args) throws Exception {
        Class rc = Class.forName("com.suncar.card.service.controller.reflect.Robot");
        //创建rc的示例，需要强转。
        Robot r = (Robot)rc.newInstance();
        System.out.println("class name is " + rc.getName());
        // 获取私有方法throwHello
        // getDeclaredMethod,这个方法能够获取所有的方法，无论是private还是public。
        // 但是不可以获取到继承的方法活着实现的一些接口的方法。
        Method getHello = rc.getDeclaredMethod("throwHello", String.class);
        // 如果不是private方法，不需要写这句话。是的话，需要写这句。
        getHello.setAccessible(true);
        Object str = getHello.invoke(r,"Bob");
        System.out.println("getHello result is " + str);

        //获取共有方法sayHi
        Method sayHi = rc.getMethod("sayHi", String.class);
        sayHi.invoke(r, "welcome");

        //获取字段name
        Field name = rc.getDeclaredField("name");
        name.setAccessible(true);
        name.set(r,"zhangsan");
        sayHi.invoke(r,"welcome");


    }
}
