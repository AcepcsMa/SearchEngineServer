package search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class MyServer extends SpringBootServletInitializer{

    public static void main(String[] args) {

        SpringApplication.run(MyServer.class, args);
    }
}
