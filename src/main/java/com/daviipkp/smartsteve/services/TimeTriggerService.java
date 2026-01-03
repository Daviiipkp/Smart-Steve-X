package com.daviipkp.smartsteve.services;

import com.daviipkp.smartsteve.repository.TriggersRepository;
import com.daviipkp.smartsteve.implementations.triggers.TimeTrigger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeTriggerService {

    private final TriggersRepository repository;
    private final LLMService llmService;

    public TimeTriggerService(TriggersRepository repository,  LLMService llmService) {
        this.repository = repository;
        this.llmService = llmService;
    }

    @Scheduled(fixedRate = 3000)
    public void checkTriggers() {
        List<TimeTrigger> activeTriggers = repository.findAll();
        for (TimeTrigger t : activeTriggers) {
            if (t.shouldFire()) fire(t);
        }
    }

    private void fire(TimeTrigger t) {
        t.execute(llmService);
        repository.deleteById(t.getId());
    }

}
