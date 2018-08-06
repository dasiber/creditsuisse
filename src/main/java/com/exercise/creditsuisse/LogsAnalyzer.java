package com.exercise.creditsuisse;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.exercise.creditsuisse.persistence.model.LogEvent;
import com.exercise.creditsuisse.persistence.repo.LogEventRepository;

@Service
public class LogsAnalyzer {
	private static Logger logger = LoggerFactory.getLogger(Class.class.getName());

	@Autowired
	private LogEventRepository eventRepo;
	// This number depends on the number of CPUs and the load of the system
	private int numThreads = 2; 
	
	/**
	 * While there are events in the queue and threads available we assign
	 * a new event to a thread to store the next event in the DB.
	 * @param queueStarted
	 */
	public void storeLogsDB(BlockingQueue<LogEvent> queueStarted) {
		// This is the operation that will take longer as we are inserting
		// the events in the database for the first time
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
		while (true) {
			if (!queueStarted.isEmpty()) {
				executorService.execute(()-> {					
					try {
						LogEvent event = queueStarted.take();
						eventRepo.save(event);
						logger.debug("Inserting STARTED Event \"{}\"", event);
					} catch (InterruptedException e) {
						logger.error("Error taking from the queue to store log DB"
								+ "- Exception: {}", e);
					}
				});
			}
		}	
	}
	
	/**
	 * While there are events in the queue and threads available we assign
	 * a new event to a thread to calculate the duration.
	 * @param queueFinished
	 */
	public void analyzeLogs(BlockingQueue<LogEvent> queueFinished) {
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
		while (true) {
			if (!queueFinished.isEmpty()) {
				executorService.execute(()-> {
					analyzeEvent(queueFinished);
				});
			}
		}	
	}

	/**
	 * It calculates the duration of the events. If an event which state is
	 * FINISHED does not find the STARTED event in the database to calculate
	 * the duration then it is returned to the queue. 
	 * @param queueFinished
	 */
	private void analyzeEvent(BlockingQueue<LogEvent> queueFinished) {
		// We need to know when the event started to know the event duration
		LogEvent event;
		
		try {
			event = queueFinished.take();
			Optional<LogEvent> optEventDB = eventRepo.findById(event.getId());
			
			LogEvent eventDB;

			// If we didn't find the value in the database there was an error
			// and we logged it
			if (optEventDB.isPresent()) {
				eventDB = optEventDB.get();
				eventDB.setDuration(Math.abs(event.getTs() - eventDB.getTs()));
				
				// If the duration is bigger than 4s we update the value alarm in
				// the database
				if (eventDB.getDuration() > 4) {
					eventDB.setAlert(true);
					logger.error("Alert!!! \"{}\" event was longer than 4ms - \"{}\"", eventDB.getId(), eventDB);
				}			

				eventRepo.save(eventDB);
				logger.info("Event updated in DB \"{}\"", eventDB);
			} else {
				queueFinished.put(event);
				String msg = "Event added back to the queue. STARTED event "
						+ "no processed yet - \"{}\"";
				logger.info(msg, event);
			}
		} catch (InterruptedException e) {
			logger.error("Error Analyzing FINISHED Event - Exception: {}", e);
		}	
	}
}
