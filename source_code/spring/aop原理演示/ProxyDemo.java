package com.example.demo.aop;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-07 20:00
 * @description
 */
public class ProxyDemo {

    public static void main(String[] args) {
        Payment payment = new AliPay(new RealPayment());
        payment.pay();
    }
}
