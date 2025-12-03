package com.ambulance.serveur;

import com.ambulance.model.*;
import com.ambulance.service.CentreUrgenceService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class CentreUrgenceImpl extends UnicastRemoteObject implements CentreUrgenceService {

    private static final long serialVersionUID = 1L;

    private final Map<Long, Incident> incidents = new HashMap<>();
    private final Map<String, Ambulance> ambulances = new HashMap<>();
    private final Map<String, PositionGPS> positions = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    private long incidentCounter = 1;

    public CentreUrgenceImpl() throws RemoteException {
        super();
    }

    // ========== INCIDENTS ==========

    @Override
    public Incident declarerIncident(String adresse, int gravite) throws RemoteException {
        lock.lock();
        try {
            Long id = incidentCounter++;
            Incident incident = new Incident(id, adresse, gravite);
            incidents.put(id, incident);
            System.out.println("[RMI] Incident créé : #" + id + " - " + adresse);
            return incident;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Incident> consulterIncidents() throws RemoteException {
        lock.lock();
        try {
            return new ArrayList<>(incidents.values());
        } finally {
            lock.unlock();
        }
    }

    public Incident getIncidentParId(Long id) throws RemoteException {
        lock.lock();
        try {
            return incidents.get(id);
        } finally {
            lock.unlock();
        }
    }

    // ========== AMBULANCES ==========

    public void enregistrerAmbulance(Ambulance ambulance) throws RemoteException {
        lock.lock();
        try {
            ambulances.put(ambulance.getId(), ambulance);
            positions.put(ambulance.getId(), new PositionGPS(36.8, 10.1));
            System.out.println("[RMI] Ambulance enregistrée : " + ambulance.getId());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Ambulance> getAmbulancesDisponibles() throws RemoteException {
        lock.lock();
        try {
            List<Ambulance> disponibles = new ArrayList<>();
            for (Ambulance amb : ambulances.values()) {
                if (amb.getEtat() == EtatAmbulance.DISPONIBLE) {
                    disponibles.add(amb);
                }
            }
            return disponibles;
        } finally {
            lock.unlock();
        }
    }

    public Ambulance getAmbulanceParId(String id) throws RemoteException {
        lock.lock();
        try {
            return ambulances.get(id);
        } finally {
            lock.unlock();
        }
    }

    // ========== AFFECTATIONS ==========

    public void affecterAmbulance(Long idIncident, String idAmbulance) throws RemoteException {
        lock.lock();
        try {
            Incident incident = incidents.get(idIncident);
            Ambulance ambulance = ambulances.get(idAmbulance);

            if (incident != null && ambulance != null) {
                incident.setAmbulanceAffectee(idAmbulance);
                incident.setEtat(EtatIncident.AFFECTE);
                ambulance.setEtat(EtatAmbulance.EN_ROUTE);
                ambulance.setIncidentActuel(idIncident);
                System.out.println("[RMI] Ambulance " + idAmbulance + " affectée à incident #" + idIncident);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void affecterAmbulance(String idIncident, String idAmbulance) throws RemoteException {
        try {
            Long incidentId = Long.parseLong(idIncident);
            affecterAmbulance(incidentId, idAmbulance);
        } catch (NumberFormatException e) {
            System.err.println("Erreur: ID incident invalide - " + idIncident);
        }
    }

    // ========== ÉTATS ==========

    @Override
    public void mettreAJourEtat(String idAmbulance, EtatAmbulance etat) throws RemoteException {
        lock.lock();
        try {
            Ambulance ambulance = ambulances.get(idAmbulance);
            if (ambulance != null) {
                ambulance.setEtat(etat);
                System.out.println("[RMI] État de " + idAmbulance + " : " + etat);
            }
        } finally {
            lock.unlock();
        }
    }

    // ========== POSITIONS ==========

    @Override
    public Map<String, PositionGPS> consulterPositions() throws RemoteException {
        lock.lock();
        try {
            return new HashMap<>(positions);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void miseAJourPosition(String idAmbulance, PositionGPS position) throws RemoteException {
        lock.lock();
        try {
            positions.put(idAmbulance, position);
            System.out.println("[RMI] Position de " + idAmbulance + " : " + position);
        } finally {
            lock.unlock();
        }
    }
}
