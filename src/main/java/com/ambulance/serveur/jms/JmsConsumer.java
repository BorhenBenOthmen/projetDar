package com.ambulance.serveur.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Écoute les messages JMS (optionnel - pour logging/monitoring)
 */
@Component
public class JmsConsumer {

    /**
     * Écoute les alertes publiées
     */
    @JmsListener(destination = "${jms.topic.alertes}", containerFactory = "jmsListenerContainerFactory")
    public void ecouterAlertes(Map<String, Object> message) {
        String type = (String) message.get("type");
        System.out.println("🎧 JMS Consumer reçu: " + type);

        // Ici vous pouvez logger, sauvegarder en BD, etc.
        // Par exemple: enregistrer dans un fichier log ou base de données
    }

    /**
     * Écoute les positions (pour analytics)
     */
    @JmsListener(destination = "${jms.topic.positions}", containerFactory = "jmsListenerContainerFactory")
    public void ecouterPositions(Map<String, Object> message) {
        String ambulanceId = (String) message.get("ambulanceId");
        System.out.println("🎧 Position reçue pour: " + ambulanceId);

        // Ici: sauvegarder l'historique des positions en BD
    }

    /**
     * Écoute les changements de statut
     */
    @JmsListener(destination = "${jms.topic.status}", containerFactory = "jmsListenerContainerFactory")
    public void ecouterStatuts(Map<String, Object> message) {
        System.out.println("🎧 Changement de statut reçu");

        // Ici: mettre à jour dashboard admin en temps réel
    }
}