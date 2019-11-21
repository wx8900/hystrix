package com.example.test.service;

import org.springframework.stereotype.Service;

/**
 * @author Jack
 */
@Service
public class HelloService {

    public String hello() {
        return "Now, calling hello method!";
    }
}
