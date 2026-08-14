package com.example.wayground.controller;

import com.example.wayground.service.AiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ScriptController {

    private final AiService aiService;

    public ScriptController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody PromptRequest request) {
        try {
            String script = aiService.generateScript(request.prompt());
            return ResponseEntity.ok(new ScriptResponse(script));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to generate script: " + e.getMessage()));
        }
    }

    // DTOs ---------------------------------------------------------
    public record PromptRequest(String prompt) {}
    public record ScriptResponse(String script) {}
    public record ErrorResponse(String error) {}
}
