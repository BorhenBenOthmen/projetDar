package com.ambulance.serveur;

import com.ambulance.model.Ambulance;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainServeur {
    public static void main(String[] args) {
        try {
            // Démarrer registre RMI
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("[MAIN] Registre RMI créé sur port 1099");

            // Créer et enregistrer le service
            CentreUrgenceImpl service = new CentreUrgenceImpl();
            registry.rebind("CentreUrgenceService", service);
            System.out.println("[MAIN] Service enregistré : CentreUrgenceService");

            // ← CHANGER: utiliser des objets Ambulance au lieu de String
            service.enregistrerAmbulance(new Ambulance("AMB-001"));
            service.enregistrerAmbulance(new Ambulance("AMB-002"));
            service.enregistrerAmbulance(new Ambulance("AMB-003"));

            // Démarrer serveur socket
            ServeurSocket serveurSocket = new ServeurSocket(5000);
            Thread threadSocket = new Thread(serveurSocket);
            threadSocket.start();

            System.out.println("[MAIN] Serveur prêt !");
            System.out.println("[MAIN] Appuyez sur Ctrl+C pour arrêter");

            Thread.currentThread().join();

        } catch (RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}