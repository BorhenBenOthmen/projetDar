package com.ambulance.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Ambulance implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private EtatAmbulance etat;
    private PositionGPS dernierePosition;
    private LocalDateTime derniereMiseAJour;
    private Long incidentActuel;  // ← À AJOUTER

    public Ambulance(String id) {
        this.id = id;
        this.etat = EtatAmbulance.DISPONIBLE;
        this.derniereMiseAJour = LocalDateTime.now();
        this.incidentActuel = null;  // ← À AJOUTER
    }

    public String getId() { return id; }

    public EtatAmbulance getEtat() { return etat; }
    public void setEtat(EtatAmbulance etat) {
        this.etat = etat;
        this.derniereMiseAJour = LocalDateTime.now();
    }

    public PositionGPS getPosition() { return dernierePosition; }
    public void setPosition(PositionGPS pos) {
        this.dernierePosition = pos;
        this.derniereMiseAJour = LocalDateTime.now();
    }

    public LocalDateTime getDerniereMiseAJour() { return derniereMiseAJour; }

    // ← À AJOUTER
    public Long getIncidentActuel() {
        return incidentActuel;
    }

    public void setIncidentActuel(Long incidentId) {
        this.incidentActuel = incidentId;
    }

    @Override
    public String toString() {
        return "Ambulance{" +
                "id='" + id + '\'' +
                ", etat=" + etat +
                '}';
    }
}