package com.ambulance.serveur.config;

import com.ambulance.serveur.CentreUrgenceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Configuration
public class RmiConfig {

    @Value("${rmi.port}")
    private int rmiPort;

    @Value("${rmi.service.name}")
    private String serviceName;

    /**
     * Initialise le registre RMI
     */
    @Bean
    public Registry rmiRegistry() throws RemoteException {
        try {
            // Essaie de créer le registre
            return LocateRegistry.createRegistry(rmiPort);
        } catch (RemoteException e) {
            // Si déjà créé, le récupère
            return LocateRegistry.getRegistry(rmiPort);
        }
    }

    /**
     * Enregistre le service RMI
     */
    @Bean
    public String registerRmiService(Registry registry, CentreUrgenceImpl centreUrgence)
            throws RemoteException {
        registry.rebind(serviceName, centreUrgence);
        System.out.println("✅ Service RMI enregistré : rmi://localhost:" + rmiPort + "/" + serviceName);
        return serviceName;
    }
}