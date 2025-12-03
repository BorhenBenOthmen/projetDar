package com.ambulance.serveur.websocket;

import com.ambulance.model.*;
import com.ambulance.serveur.CentreUrgenceImpl;
import com.ambulance.serveur.jms.JmsProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final CentreUrgenceImpl centreUrgence;
    private final JmsProducer jmsProducer;
    private final ObjectMapper objectMapper;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToAmbulance = new ConcurrentHashMap<>();

    public WebSocketHandler(CentreUrgenceImpl centreUrgence, JmsProducer jmsProducer) {
        this.centreUrgence = centreUrgence;
        this.jmsProducer = jmsProducer;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("🔌 WebSocket connecté: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String payload = textMessage.getPayload();
        System.out.println("📩 WebSocket reçu: " + payload);

        try {
            Message message = objectMapper.readValue(payload, Message.class);

            switch (message.getType()) {
                case "REGISTER":
                    handleRegister(session, message);
                    break;
                case "ACCEPT_MISSION":
                    handleAcceptMission(session, message);
                    break;
                case "POSITION_UPDATE":
                    handlePositionUpdate(session, message);
                    break;
                case "ARRIVE_SUR_LIEU":
                    handleArriveSurLieu(session, message);
                    break;
                case "DEBUT_TRANSPORT":
                    handleDebutTransport(session, message);
                    break;
                case "FIN_MISSION":
                    handleFinMission(session, message);
                    break;
                default:
                    sendError(session, "Type inconnu: " + message.getType());
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            sendError(session, "Erreur: " + e.getMessage());
        }
    }

    private void handleRegister(WebSocketSession session, Message message) throws RemoteException, IOException {
        String ambulanceId = (String) message.getData().get("ambulanceId");

        if (ambulanceId == null) {
            sendError(session, "ambulanceId manquant");
            return;
        }

        sessions.put(ambulanceId, session);
        sessionToAmbulance.put(session.getId(), ambulanceId);

        Ambulance ambulance = new Ambulance(ambulanceId);
        ambulance.setEtat(EtatAmbulance.DISPONIBLE);
        centreUrgence.enregistrerAmbulance(ambulance);

        Message response = new Message("REGISTERED", Map.of(
                "ambulanceId", ambulanceId,
                "status", "success"
        ));
        sendMessage(session, response);

        System.out.println("✅ Ambulance enregistrée: " + ambulanceId);
    }

    private void handleAcceptMission(WebSocketSession session, Message message) throws RemoteException, IOException {
        String ambulanceId = sessionToAmbulance.get(session.getId());
        Long incidentId = ((Number) message.getData().get("incidentId")).longValue();

        Incident incident = centreUrgence.getIncidentParId(incidentId);
        Ambulance ambulance = centreUrgence.getAmbulanceParId(ambulanceId);

        if (incident != null && ambulance != null) {
            centreUrgence.affecterAmbulance(incidentId, ambulanceId);
            jmsProducer.publierAffectation(incident, ambulance);

            Message response = new Message("MISSION_ACCEPTED", Map.of(
                    "incidentId", incidentId,
                    "status", "success"
            ));
            sendMessage(session, response);

            System.out.println("✅ Mission acceptée: " + ambulanceId);
        }
    }

    private void handlePositionUpdate(WebSocketSession session, Message message) throws RemoteException, IOException {
        String ambulanceId = sessionToAmbulance.get(session.getId());

        Double latitude = ((Number) message.getData().get("latitude")).doubleValue();
        Double longitude = ((Number) message.getData().get("longitude")).doubleValue();

        PositionGPS position = new PositionGPS(latitude, longitude);
        centreUrgence.miseAJourPosition(ambulanceId, position);
        jmsProducer.publierPosition(ambulanceId, position);

        System.out.println("📍 Position: " + ambulanceId);
    }

    private void handleArriveSurLieu(WebSocketSession session, Message message) throws RemoteException, IOException {
        String ambulanceId = sessionToAmbulance.get(session.getId());

        Ambulance ambulance = centreUrgence.getAmbulanceParId(ambulanceId);
        if (ambulance != null) {
            centreUrgence.mettreAJourEtat(ambulanceId, EtatAmbulance.OCCUPEE);
            jmsProducer.publierChangementStatut(ambulance);

            Message response = new Message("STATUS_UPDATED", Map.of(
                    "status", "ARRIVE_SUR_LIEU"
            ));
            sendMessage(session, response);
        }
    }

    private void handleDebutTransport(WebSocketSession session, Message message) throws IOException {
        String ambulanceId = sessionToAmbulance.get(session.getId());

        Message response = new Message("STATUS_UPDATED", Map.of(
                "status", "TRANSPORT_EN_COURS"
        ));
        sendMessage(session, response);

        System.out.println("🚑 Transport: " + ambulanceId);
    }

    private void handleFinMission(WebSocketSession session, Message message) throws RemoteException, IOException {
        String ambulanceId = sessionToAmbulance.get(session.getId());
        Long incidentId = ((Number) message.getData().get("incidentId")).longValue();

        Incident incident = centreUrgence.getIncidentParId(incidentId);
        Ambulance ambulance = centreUrgence.getAmbulanceParId(ambulanceId);

        if (incident != null && ambulance != null) {
            incident.setEtat(EtatIncident.TERMINE);
            centreUrgence.mettreAJourEtat(ambulanceId, EtatAmbulance.DISPONIBLE);
            ambulance.setIncidentActuel(null);

            jmsProducer.publierFinMission(incident, ambulance);

            Message response = new Message("MISSION_COMPLETED", Map.of(
                    "incidentId", incidentId
            ));
            sendMessage(session, response);

            System.out.println("✅ Fin mission: " + ambulanceId);
        }
    }

    private void sendMessage(WebSocketSession session, Message message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(json));
    }

    private void sendError(WebSocketSession session, String error) throws IOException {
        Message message = new Message("ERROR", Map.of("message", error));
        sendMessage(session, message);
    }

    public void broadcastToAll(Message message) {
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return;
        }

        sessions.values().forEach(session -> {
            try {
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                System.err.println("❌ Erreur envoi: " + e.getMessage());
            }
        });
    }

    public void sendToAmbulance(String ambulanceId, Message message) throws IOException {
        WebSocketSession session = sessions.get(ambulanceId);
        if (session != null && session.isOpen()) {
            sendMessage(session, message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String ambulanceId = sessionToAmbulance.remove(session.getId());
        if (ambulanceId != null) {
            sessions.remove(ambulanceId);
            System.out.println("🔌 WebSocket déconnecté: " + ambulanceId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("❌ Erreur WebSocket: " + exception.getMessage());
    }
}