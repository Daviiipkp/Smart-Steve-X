package com.daviipkp.smartsteve.Instance;

import com.daviipkp.smartsteve.services.LLMService;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "trigger_type")
public abstract class Trigger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    String context;

    public Trigger(String context){this.context=context;};

    public Trigger() {
    }

    public abstract boolean shouldFire();

    public abstract void execute(LLMService llmS);
}
