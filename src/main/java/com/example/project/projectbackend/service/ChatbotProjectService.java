package com.example.project.projectbackend.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.project.projectbackend.entity.Project;
import com.example.project.projectbackend.repository.ProjectRepository;

@Service
public class ChatbotProjectService {

    private final ChatClient chatClient;
    private final ProjectRepository projectRepository;

    @Autowired
    public ChatbotProjectService(ChatClient chatClient, ProjectRepository projectRepository) {
        this.chatClient = chatClient;
        this.projectRepository = projectRepository;
    }

    public String getProjectResponse(Integer projectId, String question) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return "Projet non trouvé.";
        }

        String prompt = String.format(
                "Répondez à la question suivante sur le projet '%s' (Objectif: %.2f, Fonds collectés: %.2f, Dépenses: %.2f, Durée totale: %d jours, Jours restants: %d): %s",
                project.getName(), project.getGoal_amount(), project.getCurrent_amount(),
                project.getTotal_expenses(), project.getDuration_days(), project.getDays_remaining(), question
        );

        try {
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            return "Erreur lors de la génération de la réponse : " + e.getMessage();
        }
    }
}