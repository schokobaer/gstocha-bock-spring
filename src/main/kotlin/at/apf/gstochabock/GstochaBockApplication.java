package at.apf.gstochabock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class GstochaBockApplication {

    public static void main(String[] args) {
        SpringApplication.run(GstochaBockApplication.class, args);
    }
}
