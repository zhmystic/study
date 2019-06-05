package com.suncar.card.service.controller.gc;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-05 16:01
 * @description
 */
public class ReferenceCounterProblem {

    public static void main(String[] args) {

        MyObject object1 =  new MyObject();
        MyObject object2 =  new MyObject();

        //循环引用情况
        object1.childNode = object2;
        object2.childNode = object1;

    }
}
