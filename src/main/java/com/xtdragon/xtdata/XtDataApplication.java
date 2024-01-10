package com.xtdragon.xtdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class XtDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(XtDataApplication.class, args);
    }

}
