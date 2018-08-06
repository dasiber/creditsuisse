package com.exercise.creditsuisse.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Entity
@Table(name="logevents")
public class LogEvent {
	@Id
	@Column(nullable = false)
	private String id;	
	
	@Column(nullable = true)
	private long duration;
	
	@Column(nullable = true)
	private String type;
	
	@Column(nullable = true)
	private String host;
	
	@Column(nullable = true)
	private long ts;
	
	@Column(nullable = true)
	private boolean alert;

	private String state;
	
	public LogEvent(String id, String state, String type, String host, long ts) {
		super();
		this.id = id;
		this.ts = ts;
		this.type = type;
		this.host = host;
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public long getDuration() {
		return duration;
	}

	public long getTs() {
		return ts;
	}

	public boolean isAlert() {
		return alert;
	}

	public String getType() {
		return type;
	}

	public String getHost() {
		return host;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "LogEvent [id=" + id + ", alert=" + alert + ", duration=" 
				+ duration + ", type=" + type + ", host="
				+ host + ", ts=" + ts + "]";
	}
	
	
	public LogEvent() {
		super();
	}

}
