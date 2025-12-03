package com.ambulance.serveur.jms;

import com.ambulance.model.Incident;
import com.ambulance.model.PositionGPS;
import com.ambulance.model.Ambulance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JmsProducer {

    private final JmsTemplate jmsTemplate;

    @Value("${jms.topic.alertes}")
    private String topicAlertes;

    @Value("${jms.topic.positions}")
    private String topicPositions;

    @Value("${jms.topic.status}")
    private String topicStatus;

    public JmsProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Publie une nouvelle alerte d'incident (pour toutes les ambulances)
     */
    public void publierAlerte(Incident incident) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "NOUVELLE_MISSION");
        message.put("incident", incident);
        message.put("timestamp", System.currentTimeMillis());

        jmsTemplate.convertAndSend(topicAlertes, message);
        System.out.println("📢 JMS: Alerte publiée pour incident #" + incident.getId());
    }

    /**
     * Publie l'affectation d'une ambulance à un incident
     */
    public void publierAffectation(Incident incident, Ambulance ambulance) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "AFFECTATION");
        message.put("incident", incident);
        message.put("ambulance", ambulance);
        message.put("timestamp", System.currentTimeMillis());

        jmsTemplate.convertAndSend(topicAlertes, message);
        System.out.println("📢 JMS: Affectation publiée - Ambulance " +
                ambulance.getPosition() + " → Incident #" + incident.getId());
    }

    /**
     * Publie une mise à jour de position (pour statistiques/suivi)
     */
    public void publierPosition(String ambulanceId, PositionGPS position) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "POSITION_UPDATE");
        message.put("ambulanceId", ambulanceId);
        message.put("position", position);
        message.put("timestamp", System.currentTimeMillis());

        jmsTemplate.convertAndSend(topicPositions, message);
        System.out.println("📍 JMS: Position publiée pour " + ambulanceId);
    }

    /**
     * Publie un changement de statut d'ambulance
     */
    public void publierChangementStatut(Ambulance ambulance) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "STATUT_CHANGE");
        message.put("ambulance", ambulance);
        message.put("timestamp", System.currentTimeMillis());

        jmsTemplate.convertAndSend(topicStatus, message);
        System.out.println("🔔 JMS: Statut changé pour " + ambulance.getId() +
                " → " + ambulance.getEtat());
    }

    /**
     * Publie la fin d'une mission
     */
    public void publierFinMission(Incident incident, Ambulance ambulance) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "FIN_MISSION");
        message.put("incident", incident);
        message.put("ambulance", ambulance);
        message.put("timestamp", System.currentTimeMillis());

        jmsTemplate.convertAndSend(topicAlertes, message);
        System.out.println("✅ JMS: Fin de mission publiée pour incident #" + incident.getId());
    }
}