package com.ambulance.service;

import com.ambulance.model.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface CentreUrgenceService extends Remote {
    Incident declarerIncident(String adresse, int gravite) throws RemoteException;
    List<Incident> consulterIncidents() throws RemoteException;
    List<Ambulance> getAmbulancesDisponibles() throws RemoteException;
    void affecterAmbulance(String idIncident, String idAmbulance) throws RemoteException;
    void mettreAJourEtat(String idAmbulance, EtatAmbulance etat) throws RemoteException;
    Map<String, PositionGPS> consulterPositions() throws RemoteException;
    void miseAJourPosition(String idAmbulance, PositionGPS position) throws RemoteException;
}