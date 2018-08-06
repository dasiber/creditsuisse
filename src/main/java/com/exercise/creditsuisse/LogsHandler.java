package com.exercise.creditsuisse;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exercise.creditsuisse.persistence.model.LogEvent;

@Service
public class LogsHandler {
	private static BlockingQueue<LogEvent> queueStarted = new ArrayBlockingQueue<>(1024);
	private static BlockingQueue<LogEvent> queueFinished = new ArrayBlockingQueue<>(1024);

	@Autowired
	private LogsAnalyzer logAnalyzer;
	
	@Autowired
	private LogsReader logReader;
	
	private final int numThreads = 3;
	
	public void handleLogFile(String filePath) {
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
		// This thread reads lines from the log and adds them to the queues
		executorService.execute(() -> {
			logReader.readLogFile(filePath, queueStarted, queueFinished);
		});
		
		// This thread handles the queue with all the STARTED events storing
		// the events in the DB
		executorService.execute(() -> {
			logAnalyzer.storeLogsDB(queueStarted);
		});
		
		// This thread handles the queue with all the FINISHED events calculating
		// the duration of the event
		executorService.execute(() -> {
			logAnalyzer.analyzeLogs(queueFinished);
		});
	}
}
