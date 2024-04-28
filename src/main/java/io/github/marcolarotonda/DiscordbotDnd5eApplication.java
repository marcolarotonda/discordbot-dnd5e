package io.github.marcolarotonda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscordbotDnd5eApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(DiscordbotDnd5eApplication.class);
		springApplication.setWebApplicationType(WebApplicationType.NONE);
		springApplication.run(args);
	}

}
