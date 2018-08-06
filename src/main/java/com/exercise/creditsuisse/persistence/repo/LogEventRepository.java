package com.exercise.creditsuisse.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.exercise.creditsuisse.persistence.model.LogEvent;

@Repository
public interface LogEventRepository extends CrudRepository<LogEvent, String> {
}
