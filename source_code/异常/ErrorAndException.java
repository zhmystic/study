package com.suncar.card.service.controller.throwable;

import java.io.IOException;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-06 12:50
 * @description     异常演示
 */
public class ErrorAndException {

    //error
    private void throwError() {
        throw new StackOverflowError();
    }

    //运行时异常
    private void throwRuntimeException() {
        throw new RuntimeException();
    }

    //非运行时异常，不加try catch的话 会报错。
    // 需要强制检查并处理，上面两个则不需要。也可以直接抛出异常。throws IOException
    private void throwCheckedException() {
        try {
            //IOException直接继承Exception，并不继承RuntimeException
            throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
            //todo:可以增加相应的处理逻辑。
        }
    }

    public static void main(String[] args) {
        ErrorAndException eae = new ErrorAndException();
        //一旦遇到异常被抛出，后面的方法均不会被执行到。
        eae.throwError();
        eae.throwRuntimeException();
        eae.throwCheckedException();

    }
}
