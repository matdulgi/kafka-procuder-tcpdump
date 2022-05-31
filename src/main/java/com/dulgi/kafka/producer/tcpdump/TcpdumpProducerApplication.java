package com.dulgi.kafka.producer.tcpdump;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
public class TcpdumpProducerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TcpdumpProducerApplication.class);
        application.run();
    }
}

