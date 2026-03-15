package club.sportsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SportsAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportsAppApplication.class, args);
	}

}
