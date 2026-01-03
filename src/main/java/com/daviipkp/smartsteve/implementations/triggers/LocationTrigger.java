package com.daviipkp.smartsteve.implementations.triggers;

import com.daviipkp.smartsteve.Instance.Trigger;
import com.daviipkp.smartsteve.services.LLMService;
import jakarta.persistence.Entity;

@Entity
public class LocationTrigger extends Trigger {


    public LocationTrigger(String context) {
        super(context);
    }

    public LocationTrigger() {

    }

    @Override
    public boolean shouldFire() {
        return false;
    }

    @Override
    public void execute(LLMService llmS) {

    }
}
