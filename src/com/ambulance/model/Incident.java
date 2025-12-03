package com.ambulance.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Incident implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String adresse;
    private int gravite;
    private LocalDateTime dateCreation;
    private EtatIncident etat;
    private String ambulanceAffectee;

    public Incident(String id, String adresse, int gravite) {
        this.id = id;
        this.adresse = adresse;
        this.gravite = gravite;
        this.dateCreation = LocalDateTime.now();
        this.etat = EtatIncident.EN_ATTENTE;
    }

    public String getId() { return id; }
    public String getAdresse() { return adresse; }
    public int getGravite() { return gravite; }

    public EtatIncident getEtat() { return etat; }
    public void setEtat(EtatIncident etat) { this.etat = etat; }

    public String getAmbulanceAffectee() { return ambulanceAffectee; }
    public void setAmbulanceAffectee(String id) { this.ambulanceAffectee = id; }

    public LocalDateTime getDateCreation() { return dateCreation; }

    @Override
    public String toString() {
        return "Incident{" +
                "id='" + id + '\'' +
                ", adresse='" + adresse + '\'' +
                ", gravite=" + gravite +
                ", etat=" + etat +
                ", ambulance='" + ambulanceAffectee + '\'' +
                '}';
    }
}