package com.example.demo.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-07 17:18
 * @description
 */
@Data
@Component
public class Person {

    @Value("1")
    private Long id;

    @Value("Jack")
    private String name;

    @Qualifier(value = "bird")
    @Autowired
    private Pet pet;

    public void call() {
        pet.move();
    }

}
