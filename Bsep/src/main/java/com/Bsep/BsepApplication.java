package com.Bsep;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class BsepApplication {

    public static void main(String[] args) {
        SpringApplication.run(BsepApplication.class, args);
    }

}
