package com.xtdragon.xtdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//@EnableConfigurationProperties(MinioConfigure.class)
@ConfigurationPropertiesScan("com.xtdragon.xtdata.config")
public class XtDataApplication {


    public static void main(String[] args) {
        SpringApplication.run(XtDataApplication.class, args);
    }

}
