package com.example.project.projectbackend.config;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder()
                .chatModel(ollamaChatModel)
                .build();
    }

    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi) {
        return new OllamaChatModel(
                ollamaApi,
                OllamaOptions.create().withModel("mistral"),
                "mistral", // Nom explicite du modèle
                false,     // Streaming désactivé pour un chatbot simple
                3,         // 3 tentatives max en cas d’échec
                30000      // Timeout de 30 secondes
        );
    }

    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi("http://localhost:11434"); // Ajustez le port si nécessaire (ex. 11435)
    }
}