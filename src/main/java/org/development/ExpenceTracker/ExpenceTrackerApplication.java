package org.development.ExpenceTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ExpenceTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenceTrackerApplication.class, args);
	}

}
