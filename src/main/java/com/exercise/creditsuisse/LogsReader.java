package com.exercise.creditsuisse;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.exercise.creditsuisse.persistence.model.LogEvent;
import com.exercise.creditsuisse.persistence.model.States;

@Service
public class LogsReader {
	private static Logger logger = LoggerFactory.getLogger(Class.class.getName());

	private LogEvent event;
	
	public void readLogFile(String filePath, BlockingQueue<LogEvent> queueStarted,
			BlockingQueue<LogEvent> queueFinished) {
		if (Files.exists(Paths.get(filePath))) {
			FileInputStream inputStream = null;
			Scanner sc = null;
			try {
				// We use an inputStream to be able to handle big files. It 
				// doesn't load in memory the whole file just a line.
			    inputStream = new FileInputStream(filePath);
			    sc = new Scanner(inputStream, "UTF-8");
			    while (sc.hasNextLine()) {
			        String line = sc.nextLine();
			        JSONParser parser = new JSONParser();
			        try {
			        	JSONObject jsonObj = (JSONObject) parser.parse(line);
			        	String id = (String) jsonObj.get("id");
			        	String state = (String) jsonObj.get("state");
			        	String type = (String) jsonObj.get("type");
			        	String host = (String) jsonObj.get("host");
			        	Long ts = (Long) jsonObj.get("timestamp");
			        	
			        	event = new LogEvent(id, state, type, host, ts);
			        	
			        	if (state.equals("STARTED")) {
			        		queueStarted.put(event);
			        		logger.debug("Event  \"{}\" added to the queueStarted:", event);
			        	} else if (state.equals("FINISHED")) {
			        		queueFinished.put(event);
			        		logger.debug("Event  \"{}\" added to the queueFinished:", event);
			        	} else {
			        		logger.error("Error Parsing 'STATE' from Event: \"{}\"", event);
			        	}
			        	
			        } catch (Exception e) {
			        	logger.error("Error Parsing File: \"{}\" - : Exception: {}", filePath, e);
			        }			        
			    }
			    // Scanner suppresses exceptions
			    if (sc.ioException() != null) {
			        throw sc.ioException();
			    }
			} catch (IOException e) {
				logger.error("Error Reading File: \"{}\" - : Exception: {}", filePath, e);
			} finally {
			    if (inputStream != null) {
			        try {
						inputStream.close();
					} catch (IOException e) {
						logger.error("Error Closing File: \"{}\" - : Exception: {}", filePath, e);
					}
			    }
			    if (sc != null) {
			        sc.close();
			    }
			}			
			
		} else {
	    	logger.error("Error Opening File: \"{}\" doesn't exists", filePath);
	    }
	}
}
