package com.example.project.projectbackend.service;

import com.example.project.projectbackend.entity.Contribution;
import com.example.project.projectbackend.entity.Project;
import com.example.project.projectbackend.repository.ContributionRepository;
import com.example.project.projectbackend.repository.ProjectRepository;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final ProjectRepository projectRepository;
    private final ContributionRepository contributionRepository;

    @Autowired
    public RecommendationService(ProjectRepository projectRepository, ContributionRepository contributionRepository) {
        this.projectRepository = projectRepository;
        this.contributionRepository = contributionRepository;
    }

    public List<Project> recommendProjects(String supporterName) {
        // Récupérer les contributions passées de l’utilisateur
        List<Contribution> contributions = contributionRepository.findBySupporterName(supporterName);
        if (contributions.isEmpty()) {
            // Si pas de contributions, recommander les projets populaires (basés sur current_amount ou days_remaining)
            return projectRepository.findAll().stream()
                    .sorted((p1, p2) -> Float.compare(p2.getCurrent_amount(), p1.getCurrent_amount())) // Projets les plus financés
                    .limit(5)
                    .collect(Collectors.toList());
        }

        // Préparer les données pour Weka (exemple simplifié avec J48 pour la classification)
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("category", List.of("Equipement", "Tournament", "Other")));
        attributes.add(new Attribute("goal_amount"));
        attributes.add(new Attribute("current_amount"));
        attributes.add(new Attribute("days_remaining"));
        attributes.add(new Attribute("class", List.of("Recommend", "NotRecommend")));

        Instances dataset = new Instances("ProjectRecommendations", new ArrayList<>(attributes), 0);
        dataset.setClassIndex(attributes.size() - 1);

        // Ajouter des instances basées sur les contributions passées
        for (Contribution contribution : contributions) {
            Project project = contribution.getProject();
            Instance instance = new DenseInstance(attributes.size());

            // Mapper la catégorie (String) à son index dans la liste des valeurs
            String category = String.valueOf(project.getCategory());
            int categoryIndex = dataset.attribute("category").indexOfValue(category);
            if (categoryIndex == -1) {
                // Si la catégorie n’est pas dans la liste, utiliser "Other" (index 2)
                categoryIndex = dataset.attribute("category").indexOfValue("Other");
            }
            instance.setValue(attributes.get(0), categoryIndex);

            instance.setValue(attributes.get(1), project.getGoal_amount());
            instance.setValue(attributes.get(2), project.getCurrent_amount());
            instance.setValue(attributes.get(3), project.getDays_remaining());
            instance.setValue(attributes.get(4), "Recommend"); // Classer comme recommandable si contribué
            dataset.add(instance);
        }

        // Entraîner un modèle simple (J48 - arbre de décision)
        try {
            J48 tree = new J48();
            tree.buildClassifier(dataset);

            // Générer des recommandations pour tous les projets
            List<Project> allProjects = projectRepository.findAll();
            List<Project> recommendations = new ArrayList<>();

            for (Project project : allProjects) {
                Instance testInstance = new DenseInstance(attributes.size());

                // Mapper la catégorie du projet testé
                String testCategory = String.valueOf(project.getCategory());
                int testCategoryIndex = dataset.attribute("category").indexOfValue(testCategory);
                if (testCategoryIndex == -1) {
                    testCategoryIndex = dataset.attribute("category").indexOfValue("Other");
                }
                testInstance.setValue(attributes.get(0), testCategoryIndex);

                testInstance.setValue(attributes.get(1), project.getGoal_amount());
                testInstance.setValue(attributes.get(2), project.getCurrent_amount());
                testInstance.setValue(attributes.get(3), project.getDays_remaining());
                testInstance.setDataset(dataset);

                double classification = tree.classifyInstance(testInstance);
                if (classification == 0.0) { // 0.0 correspond à "Recommend"
                    recommendations.add(project);
                }
            }

            // Limiter à 5 recommandations (ou ajuster selon les besoins)
            return recommendations.stream()
                    .sorted((p1, p2) -> Float.compare(p2.getCurrent_amount(), p1.getCurrent_amount())) // Projets les plus financés
                    .limit(5)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            // En cas d’erreur, retourner les projets les plus populaires
            return projectRepository.findAll().stream()
                    .sorted((p1, p2) -> Float.compare(p2.getCurrent_amount(), p1.getCurrent_amount()))
                    .limit(5)
                    .collect(Collectors.toList());
        }
    }
}