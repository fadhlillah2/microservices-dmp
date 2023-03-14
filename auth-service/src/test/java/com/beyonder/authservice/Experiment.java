package com.beyonder.authservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class Experiment {

    @Test
    void test(){
        sayHello();
    }

    void sayHello() {
        System.out.println("hello");
    }
}
