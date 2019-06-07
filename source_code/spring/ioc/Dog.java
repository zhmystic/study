package com.example.demo.entity;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-07 17:50
 * @description
 */
@Component
public class Dog implements Pet {

    @Override
    public void move() {
        System.out.println("Running");
    }
}
