package com.suncar.card.service.controller.reflect;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-04 16:03
 * @description     检查下自定义的类加载器
 */
public class ClassLoaderChecker {

    public static void main(String[] args) throws Exception {
        MyClassLoader m = new MyClassLoader("/Users/zhanghaoming/Desktop/","myClassLoader");
        Class c = m.loadClass("Wali");
        System.out.println(c.getClassLoader());
        System.out.println(c.getClassLoader().getParent());
        System.out.println(c.getClassLoader().getParent().getParent());
        System.out.println((c.getClassLoader().getParent().getParent()).getParent());

        c.newInstance();
    }
}
