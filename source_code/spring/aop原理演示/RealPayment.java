package com.example.demo.aop;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-07 19:57
 * @description
 */
public class RealPayment implements Payment {

    @Override
    public void pay() {
        System.out.println("作为用户，我只关心支付功能。唱，跳，RAP，篮球");

    }
}
