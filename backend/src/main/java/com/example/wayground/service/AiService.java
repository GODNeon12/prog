package com.example.wayground.service;

import com.example.wayground.config.OpenAiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

@Service
public class AiService {

    private final OpenAiConfig config;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public AiService(OpenAiConfig config) {
        this.config = config;
    }

    /**
     * Sends the user prompt to OpenAI Chat Completion endpoint and returns the generated script text.
     *
     * @param prompt user supplied prompt
     * @return generated script (plain text)
     * @throws IOException          on network / parsing errors
     * @throws InterruptedException if the request is interrupted
     */
    public String generateScript(String prompt) throws IOException, InterruptedException {
        // Build request payload
        var requestBody = mapper.createObjectNode()
                .put("model", "gpt-4o-mini")
                .set("messages", mapper.createArrayNode()
                        .add(mapper.createObjectNode()
                                .put("role", "system")
                                .put("content", "You are a code generator. Return only the script, no explanations."))
                        .add(mapper.createObjectNode()
                                .put("role", "user")
                                .put("content", prompt)));

        var request = HttpRequest.newBuilder()
                .uri(URI.create(config.getBaseUrl() + "/chat/completions"))
                .header("Authorization", "Bearer " + config.getApiKey())
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("OpenAI API error: HTTP " + response.statusCode() + " – " + response.body());
        }

        JsonNode root = mapper.readTree(response.body());
        JsonNode contentNode = root.path("choices").get(0).path("message").path("content");
        return contentNode.asText().trim();
    }
}
