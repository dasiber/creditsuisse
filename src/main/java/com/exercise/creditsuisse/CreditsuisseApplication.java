package com.exercise.creditsuisse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("com.exercise.creditsuisse")
@EnableJpaRepositories("com.exercise.creditsuisse.persistence.repo")
@EntityScan("com.exercise.creditsuisse.persistence.model")
@SpringBootApplication
public class CreditsuisseApplication {

	@Autowired
	private LogsHandler logsHandler;
	
	public static void main(String[] args) {
		SpringApplication.run(CreditsuisseApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner initial() {
		String filePath = "src\\main\\resources\\log.txt";
		logsHandler.handleLogFile(filePath);
		return null;
	}
	
}
