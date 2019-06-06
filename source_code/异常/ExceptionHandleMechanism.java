package com.suncar.card.service.controller.throwable;

import lombok.extern.slf4j.Slf4j;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-06 13:06
 * @description
 */
@Slf4j
public class ExceptionHandleMechanism {

    public static int dowork() {
        try {
            int i = 10/0;   //会抛出异常
            System.out.println("i的值为：" + i);
        }catch (ArithmeticException e) {
            System.out.println("Exception：" + e);
            return 0;
        } catch (Exception e) {
            System.out.println("Exception：" + e);
            return 1;
        }finally {
            System.out.println("Finally");
            return 2;
        }
    }

    public static void main(String[] args) {
        System.out.println("执行后的值为：" + dowork());
        System.out.println("Mission Complete");
    }
}
