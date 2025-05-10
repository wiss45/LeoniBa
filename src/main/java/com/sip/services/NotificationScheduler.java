/*package com.sip.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sip.entities.Plan;
import com.sip.entities.Projet;
import com.sip.entities.User;
import com.sip.repositories.PlanRepository;

@Component
public class NotificationScheduler {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *") // chaque jour à 08:00
    public void envoyerNotifications() {
        LocalDate demain = LocalDate.now().plusDays(1);

        List<Plan> plans = planRepository.findByDateLivraisonAndNotificationEnvoyeeFalse(demain);

        for (Plan plan : plans) {
            Projet projet = plan.getProjet();

            if (projet != null && projet.getResponsable() != null) {
                User responsable = projet.getResponsable();

                String email = responsable.getEmail();
                String nomResponsable = responsable.getNom();
                String nomProjet = projet.getName();
                String nomEquipement = plan.getEquipement().getName(); // Assure-toi que ce champ existe

                String sujet = "Rappel : Livraison de l'équipement demain";
                String contenu = "Bonjour " + nomResponsable + ",\n\n"
                        + "L'équipement \"" + nomEquipement + "\" (projet : " + nomProjet + ") sera livré demain (" + demain + ").";

                emailService.sendEmail(email, sujet, contenu);

                plan.setNotificationEnvoyee(true);
                planRepository.save(plan);
            }
        }
    }
}
*/