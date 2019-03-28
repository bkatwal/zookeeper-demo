package bkatwal.zookeeper.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** @author "Bikas Katwal" 26/03/19 */
@SpringBootApplication
@EnableAutoConfiguration()
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
