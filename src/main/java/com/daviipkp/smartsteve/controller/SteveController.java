package com.daviipkp.smartsteve.controller;

import com.daviipkp.smartsteve.services.DualBrainService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/steve")
public class SteveController {

    private final DualBrainService dbs;

    public SteveController(DualBrainService dbs) {
        this.dbs = dbs;
    }

    @GetMapping("/talked")
    public String userTalked(@RequestParam String command) {
        return dbs.processCommand(command);
    }

    @PostMapping("/chat")
    public String  userChat(@RequestParam String command) {
        return dbs.processCommand(command);
    }

}
