package com.daviipkp.smartsteve.repository;

import com.daviipkp.smartsteve.implementations.triggers.TimeTrigger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TriggersRepository extends JpaRepository<TimeTrigger,Long> {
    public List<TimeTrigger> findAllByTargetTimeBetween(LocalDateTime start, LocalDateTime end);
    public List<TimeTrigger> findAll();
}
