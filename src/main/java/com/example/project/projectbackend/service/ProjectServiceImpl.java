package com.example.project.projectbackend.service;

import com.example.project.projectbackend.entity.ProjectAnalytics;
import com.example.project.projectbackend.entity.ProjectPrediction;
import com.example.project.projectbackend.entity.Contribution;
import com.example.project.projectbackend.entity.Event;
import com.example.project.projectbackend.entity.Project;
import com.example.project.projectbackend.repository.ContributionRepository;
import com.example.project.projectbackend.repository.EventRepository;
import com.example.project.projectbackend.repository.ProjectRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import com.example.project.projectbackend.service.ResourceNotFoundException;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements IProjectService {

    private final ProjectRepository projectRepository;
    private final ContributionRepository contributionRepository;
    private final EventRepository eventRepository;
    private final EmailServiceProject emailService;
    @Override
    public List<Project> findProjectsWithImages() {
        return projectRepository.findProjectsWithImages();
    }

    @Override
    public List<Project> retrieveAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project retrieveProject(Integer projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }

    @Override
    public Project addProject(Project p) {
        if (p.getDuration_days() > 0 && p.getStart_date() == null) {
            p.setStart_date(LocalDateTime.now());
            p.setDays_remaining(p.getDuration_days());
        }
        Project savedProject = projectRepository.save(p);

        // Envoyer un e-mail à chaque supporter pour informer du nouveau projet
        List<Contribution> contributions = contributionRepository.findAll();
        for (Contribution contribution : contributions) {
            String supporterEmail = contribution.getEmail(); // Utilisez l'e-mail
            if (supporterEmail != null && !supporterEmail.isEmpty()) {
                emailService.sendProjectLaunchEmail(supporterEmail, savedProject.getName());
            }
        }

        return savedProject;
    }

    @Override
    public void removeProject(Integer projectId) {
        projectRepository.deleteById(projectId);
    }

    @Override
    public Project modifyProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public List<Project> addListProject(List<Project> projects) {
        return projectRepository.saveAll(projects);
    }

    @Override
    public float calculateTotalFundsCollected(Integer projectId) {
        Project project = retrieveProject(projectId);
        return project != null ? project.getCurrent_amount() : 0;
    }

    @Override
    public void addExpense(Integer projectId, float expenseAmount) {
        Project project = retrieveProject(projectId);
        if (project != null) {
            project.setTotal_expenses(project.getTotal_expenses() + expenseAmount);
            projectRepository.save(project);
        }
    }

    @Override
    public float getProgressPercentage(Integer projectId) {
        Project project = retrieveProject(projectId);
        return project != null ? project.getProgressPercentage() : 0;
    }

    @Override
    public Contribution addContribution(Integer projectId, Contribution contribution) {
        Project project = retrieveProject(projectId);
        if (project != null) {
            contribution.setProject(project);
            project.setCurrent_amount(project.getCurrent_amount() + contribution.getAmount());
            projectRepository.save(project);
            return contributionRepository.save(contribution);
        }
        return null;
    }

    @Override
    public Event addEvent(Integer projectId, Event event) {
        Project project = retrieveProject(projectId);
        if (project != null) {
            event.setProject(project);
            event.updateTotalRevenue();
            project.setCurrent_amount(project.getCurrent_amount() + event.getTotal_revenue());
            projectRepository.save(project);
            return eventRepository.save(event);
        }
        return null;
    }

    @Override
    public List<Project> searchProjectsByName(String name) {
        return projectRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public Project updateProjectImage(int idProject, String image) {
        return null;
    }

    @Override
    public ProjectAnalytics getProjectAnalytics(Integer projectId) {
        Project project = retrieveProject(projectId);
        if (project == null) return null;

        List<Contribution> contributions = contributionRepository.findByProjectId(projectId);
        float totalContributions = contributions.stream().map(Contribution::getAmount).reduce(0f, Float::sum);

        ProjectAnalytics analytics = new ProjectAnalytics();
        analytics.setTotalFundsCollected(project.getCurrent_amount());
        analytics.setTotalExpenses(project.getTotal_expenses());
        analytics.setTotalContributions(totalContributions);
        analytics.setNumberOfContributions(contributions.size());
        analytics.setProgressPercentage(project.getProgressPercentage());
        return analytics;
    }

    @Override
    public byte[] generateFinancialReport(Integer projectId) {
        Project project = retrieveProject(projectId);
        if (project == null) return null;

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Conteneur pour le titre et le logo
            Table headerTable = new Table(2); // 2 colonnes pour le titre et le logo
            headerTable.setWidth(500);
            headerTable.setMarginBottom(20);

            // Titre "Arena Boost" (colonne gauche)
            Paragraph title = new Paragraph("Arena Boost")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.LEFT);
            headerTable.addCell(new Cell().add(title).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));

            // Ajout du logo "ArenaBoostLogo" (colonne droite)
            String imagePath = "C:\\Users\\21620\\OneDrive\\Bureau\\Esprit\\Pi\\Ressources\\ArenaBoostLogo.png"; // Ajustez l'extension si nécessaire
            ImageData imageData = ImageDataFactory.create(imagePath);
            Image logo = new Image(imageData);
            logo.scaleToFit(100, 50); // Ajustez la taille du logo (largeur 100, hauteur 50)
            headerTable.addCell(new Cell().add(logo).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                    .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT));

            // Ajouter le conteneur au document
            document.add(headerTable);

            // Tableau pour les données financières
            Table table = new Table(2); // 2 colonnes
            table.setWidth(500);
            table.setMarginBottom(20);

            // En-têtes du tableau
            table.addCell(new Cell().add(new Paragraph("Description").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Cell().add(new Paragraph("Valeur").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Données financières
            table.addCell(new Cell().add(new Paragraph("Nom du Projet")));
            table.addCell(new Cell().add(new Paragraph(project.getName())));
            table.addCell(new Cell().add(new Paragraph("Objectif")));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", project.getGoal_amount()))));
            table.addCell(new Cell().add(new Paragraph("Fonds Collectés")));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", project.getCurrent_amount()))));
            table.addCell(new Cell().add(new Paragraph("Dépenses Totales")));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", project.getTotal_expenses()))));
            table.addCell(new Cell().add(new Paragraph("Progression")));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f%%", project.getProgressPercentage()))));

            document.add(table);

            // Générer un QR Code (URL corrigée)
            String qrContent = "http://localhost:8089/project/retrieve-project/" + projectId;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 100, 100);
            ByteArrayOutputStream qrOut = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrOut);
            ImageData qrImageData = ImageDataFactory.create(qrOut.toByteArray());
            Image qrImage = new Image(qrImageData);
            qrImage.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
            document.add(new Paragraph("Scanner pour plus d'infos :").setTextAlignment(TextAlignment.CENTER));
            document.add(qrImage);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ProjectPrediction predictProjectOutcome(Integer projectId) {
        Project project = retrieveProject(projectId);
        if (project == null) return null;

        try {
            // Définir les attributs pour la régression (prédire les fonds)
            ArrayList<Attribute> regressionAttributes = new ArrayList<>();
            regressionAttributes.add(new Attribute("goal_amount"));
            regressionAttributes.add(new Attribute("current_amount"));
            regressionAttributes.add(new Attribute("total_expenses"));
            regressionAttributes.add(new Attribute("duration_days")); // Total des jours
            regressionAttributes.add(new Attribute("days_remaining")); // Jours restants
            regressionAttributes.add(new Attribute("predicted_funds")); // Attribut de classe pour la régression

            // Préparer les données d'entraînement pour la régression
            Instances regressionData = new Instances("regression_data", regressionAttributes, 0);
            regressionData.setClassIndex(regressionAttributes.size() - 1); // Définir predicted_funds comme classe

            // Définir les attributs pour la classification (succès/échec)
            ArrayList<Attribute> classificationAttributes = new ArrayList<>();
            classificationAttributes.add(new Attribute("goal_amount"));
            classificationAttributes.add(new Attribute("current_amount"));
            classificationAttributes.add(new Attribute("total_expenses"));
            classificationAttributes.add(new Attribute("duration_days")); // Total des jours
            classificationAttributes.add(new Attribute("days_remaining")); // Jours restants
            ArrayList<String> classValues = new ArrayList<>();
            classValues.add("0"); // Échec
            classValues.add("1"); // Succès
            classificationAttributes.add(new Attribute("success", classValues));
            Instances classificationData = new Instances("classification_data", classificationAttributes, 0);
            classificationData.setClassIndex(classificationAttributes.size() - 1);

            // Ajouter des données historiques
            List<Project> projects = retrieveAllProjects();
            for (Project p : projects) {
                // Données pour la régression
                Instance regressionInstance = new DenseInstance(regressionAttributes.size());
                regressionInstance.setValue(regressionAttributes.get(0), p.getGoal_amount());
                regressionInstance.setValue(regressionAttributes.get(1), p.getCurrent_amount());
                regressionInstance.setValue(regressionAttributes.get(2), p.getTotal_expenses());
                regressionInstance.setValue(regressionAttributes.get(3), p.getDuration_days());
                regressionInstance.setValue(regressionAttributes.get(4), p.getDays_remaining()); // Utiliser days_remaining
                // Utiliser une estimation simple pour predicted_funds
                regressionInstance.setValue(regressionAttributes.get(5), Math.min(p.getCurrent_amount() * 1.5, p.getGoal_amount()));
                regressionData.add(regressionInstance);

                // Données pour la classification
                Instance classificationInstance = new DenseInstance(classificationAttributes.size());
                classificationInstance.setValue(classificationAttributes.get(0), p.getGoal_amount());
                classificationInstance.setValue(classificationAttributes.get(1), p.getCurrent_amount());
                classificationInstance.setValue(classificationAttributes.get(2), p.getTotal_expenses());
                classificationInstance.setValue(classificationAttributes.get(3), p.getDuration_days());
                classificationInstance.setValue(classificationAttributes.get(4), p.getDays_remaining());
                // Ajuster le seuil à 80% pour un succès
                classificationInstance.setValue(classificationAttributes.get(5), p.getCurrent_amount() >= (p.getGoal_amount() * 0.8) ? 1 : 0);
                classificationData.add(classificationInstance);
            }

            if (classificationData.size() < 2 || regressionData.size() < 2) {
                ProjectPrediction prediction = new ProjectPrediction();
                prediction.setWillReachGoal(false);
                prediction.setPredictedFunds(project.getCurrent_amount() * 1.2f); // Estimation par défaut : +20%
                return prediction;
            }

            // Classification : Succès ou échec
            J48 classifier = new J48();
            classifier.buildClassifier(classificationData);
            Instance newClassificationInstance = new DenseInstance(classificationAttributes.size());
            newClassificationInstance.setValue(classificationAttributes.get(0), project.getGoal_amount());
            newClassificationInstance.setValue(classificationAttributes.get(1), project.getCurrent_amount());
            newClassificationInstance.setValue(classificationAttributes.get(2), project.getTotal_expenses());
            newClassificationInstance.setValue(classificationAttributes.get(3), project.getDuration_days());
            newClassificationInstance.setValue(classificationAttributes.get(4), project.getDays_remaining());
            newClassificationInstance.setDataset(classificationData);
            double classValue = classifier.classifyInstance(newClassificationInstance);

            // Régression : Montant estimé
            LinearRegression regression = new LinearRegression();
            regression.buildClassifier(regressionData);
            Instance newRegressionInstance = new DenseInstance(regressionAttributes.size());
            newRegressionInstance.setValue(regressionAttributes.get(0), project.getGoal_amount());
            newRegressionInstance.setValue(regressionAttributes.get(1), project.getCurrent_amount());
            newRegressionInstance.setValue(regressionAttributes.get(2), project.getTotal_expenses());
            newRegressionInstance.setValue(regressionAttributes.get(3), project.getDuration_days());
            newRegressionInstance.setValue(regressionAttributes.get(4), project.getDays_remaining());
            newRegressionInstance.setDataset(regressionData);
            double predictedFunds = regression.classifyInstance(newRegressionInstance);

            ProjectPrediction prediction = new ProjectPrediction();
            prediction.setWillReachGoal(classValue == 1);
            // Assurez que predictedFunds ne soit pas inférieur à current_amount et ne dépasse pas goal_amount
            prediction.setPredictedFunds((float) Math.min(Math.max(predictedFunds, project.getCurrent_amount()), project.getGoal_amount()));
            return prediction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // In ProjectService.java
    public Project updateProjectImage(Integer projectId, String imagePath) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        project.setImage(imagePath);
        return projectRepository.save(project);
    }
}