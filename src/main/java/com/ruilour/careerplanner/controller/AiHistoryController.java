package com.ruilour.careerplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AiHistoryController {

    @GetMapping("/ai-history")
    public String aiHistory() {
        return "ai-history";
    }
}