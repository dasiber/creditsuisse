package com.exercise.creditsuisse;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.exercise.creditsuisse.persistence.model.LogEvent;

public class CreditsuisseLogReaderTest {
	private static Logger logger = LoggerFactory.getLogger(Class.class.getName());
	private BlockingQueue<LogEvent> queueStarted = new ArrayBlockingQueue<>(1024);
	private BlockingQueue<LogEvent> queueFinished = new ArrayBlockingQueue<>(1024);
	
	private LogsReader logReader = new LogsReader();	
	private LogEvent event = null;
	
    @Before
    public void init() {
    	
    }

    @Test
	public void whenRead1Event_thenOK() {
		String filePath = "src\\main\\resources\\1event.txt";		
		logReader.readLogFile(filePath, queueStarted, queueFinished);
		
		try {
			event = queueStarted.take();
		} catch (InterruptedException e) {
			logger.error("Error Taking from the queue - Exception: {}", e);
		}
		
		assertEquals("scsmbstgrb", event.getId());
	}
    
    @Test
	public void whenRead1EventServer_thenOK() {
		String filePath = "src\\main\\resources\\1eventServer.txt";
		logReader.readLogFile(filePath, queueStarted, queueFinished);
		
		try {
			event = queueStarted.take();
		} catch (InterruptedException e) {
			logger.error("Error Taking from the queue - Exception: {}", e);
		}
		
		assertEquals("scsmbstgra", event.getId());
	}
    
    @Test
    public void whenAdd3EventsQueue_thenOK() {
		String filePath = "src\\main\\resources\\log.txt";
		logReader.readLogFile(filePath, queueStarted, queueFinished);
		int counter = 0;
	
		try {
			while (!queueStarted.isEmpty()) {
				event = queueStarted.take();
				counter++;
			}
		} catch (InterruptedException e) {
			logger.error("Error Taking from the queue - Exception: {}", e);
		}
		
		assertEquals(counter, 3);
    }

}
