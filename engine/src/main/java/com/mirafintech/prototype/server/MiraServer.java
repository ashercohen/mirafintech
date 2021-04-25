package com.mirafintech.prototype.server;

import com.mirafintech.prototype.config.SpringDataConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@Import(SpringDataConfiguration.class) // spring configuration is located in a different package hence this annotation
@SpringBootApplication
public class MiraServer {

    public static void main(String[] args) {
        SpringApplication.run(MiraServer.class, args);
    }
}
