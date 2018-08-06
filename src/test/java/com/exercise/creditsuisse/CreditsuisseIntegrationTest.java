package com.exercise.creditsuisse;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.exercise.creditsuisse.persistence.model.LogEvent;
import com.exercise.creditsuisse.persistence.repo.LogEventRepository;

public class CreditsuisseIntegrationTest {
	private static BlockingQueue<LogEvent> queueStarted = new ArrayBlockingQueue<>(1024);
	private static BlockingQueue<LogEvent> queueFinished = new ArrayBlockingQueue<>(1024);

	private LogsReader logReader = new LogsReader();
	
	@Autowired
	private LogsAnalyzer logAnalyzer;
	
	@Autowired
	private LogsHandler logsHandler;
	
	@Autowired
	private LogEventRepository eventRepo;	
	
	String filePath = "src\\main\\resources\\3eventStarted.txt";
	
	@Test
	public void whenStore3EventsDB_thenOK() {
		String filePath = "src\\main\\resources\\3eventStarted.txt";
		ExecutorService executorService = Executors.newFixedThreadPool(1);

		logReader.readLogFile(filePath, queueStarted, queueFinished);
		
		executorService.execute(() -> {
			logAnalyzer.storeLogsDB(queueStarted);
		});
	}
	
	@Test
	@Bean
	public void when1EventIntegrationTest_thenOK() {
		String filePath = "src\\main\\resources\\eventSF.txt";
		logsHandler.handleLogFile(filePath);	
	}

}
