package com.swst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Auther: fregun
 * @Date: 19-11-6 17:26
 * @Description:
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        System.out.println("执行开始");
        SpringApplication.run(Application.class,args);
        System.out.println("执行结束");
    }
}