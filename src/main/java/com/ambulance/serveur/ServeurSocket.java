package com.ambulance.serveur;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServeurSocket implements Runnable {
    private int port;
    private ServerSocket serveurSocket;
    private ExecutorService threadPool;
    private boolean actif = true;

    public ServeurSocket(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    @Override
    public void run() {
        try {
            serveurSocket = new ServerSocket(port);
            System.out.println("[SOCKET SERVEUR] Démarré sur le port " + port);

            while (actif) {
                Socket socket = serveurSocket.accept();
                String adresseClient = socket.getInetAddress().getHostAddress();
                System.out.println("[SOCKET SERVEUR] Connexion reçue de : " + adresseClient);

                GestionnaireClientSocket gestionnaire = new GestionnaireClientSocket(socket, adresseClient);
                threadPool.execute(gestionnaire);
            }
        } catch (IOException e) {
            if (actif) {
                System.err.println("[SOCKET SERVEUR] Erreur : " + e.getMessage());
            }
        }
    }

    public void arreter() {
        actif = false;
        try {
            if (serveurSocket != null) serveurSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
        System.out.println("[SOCKET SERVEUR] Arrêté");
    }
}