package com.swst;

import com.swst.utils.ThresholdThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @Auther: fregun
 * @Date: 19-11-6 17:26
 * @Description:
 */
@SpringBootApplication
@EnableConfigurationProperties
public class Application {
    public static void main(String[] args) {
        new Thread(new ThresholdThread(8)).start();
        System.out.println("执行开始");
        SpringApplication.run(Application.class,args);
        System.out.println("执行结束");
    }
}
