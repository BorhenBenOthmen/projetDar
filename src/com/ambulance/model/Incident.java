package com.ambulance.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Incident implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;  // ← CHANGER de String à Long
    private String adresse;
    private int gravite;
    private LocalDateTime dateCreation;
    private EtatIncident etat;
    private String ambulanceAffectee;

    // ← AJOUTER ce constructeur avec Long
    public Incident(Long id, String adresse, int gravite) {
        this.id = id;
        this.adresse = adresse;
        this.gravite = gravite;
        this.dateCreation = LocalDateTime.now();
        this.etat = EtatIncident.EN_ATTENTE;
        this.ambulanceAffectee = null;
    }

    // ← GARDER l'ancien constructeur pour compatibilité
    public Incident(String id, String adresse, int gravite) {
        this(Long.parseLong(id), adresse, gravite);
    }

    public Long getId() { return id; }

    public String getAdresse() { return adresse; }

    public int getGravite() { return gravite; }

    public LocalDateTime getDateCreation() { return dateCreation; }

    public EtatIncident getEtat() { return etat; }
    public void setEtat(EtatIncident etat) {
        this.etat = etat;
    }

    public String getAmbulanceAffectee() { return ambulanceAffectee; }
    public void setAmbulanceAffectee(String ambulanceId) {
        this.ambulanceAffectee = ambulanceId;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "id=" + id +
                ", adresse='" + adresse + '\'' +
                ", gravite=" + gravite +
                ", etat=" + etat +
                '}';
    }
}