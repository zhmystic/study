package com.suncar.card.service.controller.reflect;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-04 17:04
 * @description
 */
public class LoadDifference {

    public static void main(String[] args) throws ClassNotFoundException {

        //第一种方法
//        Class<Robot> robotClass = Robot.class;
//        ClassLoader classLoader = robotClass.getClassLoader();
        //第二种方法
        Class<?> aClass = Class.forName("com.suncar.card.service.controller.reflect.Robot");

    }
}
