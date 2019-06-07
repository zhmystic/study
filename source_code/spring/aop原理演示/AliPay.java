package com.example.demo.aop;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-07 19:58
 * @description
 */
public class AliPay implements Payment {

    private Payment payment;

    public AliPay(Payment payment) {
        this.payment = payment;
    }

    public void beforePay() {
        System.out.println("从招行取款");
    }

    @Override
    public void pay() {
        beforePay();
        payment.pay();
        afterPay();
    }

    public void afterPay() {
        System.out.println("交钱给CXK");
    }
}
