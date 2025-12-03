package com.ambulance.serveur;

import java.io.*;
import java.net.Socket;

public class GestionnaireClientSocket implements Runnable {
    private Socket socket;
    private String clientId;

    public GestionnaireClientSocket(Socket socket, String clientId) {
        this.socket = socket;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try (BufferedReader lecteur = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String message;
            System.out.println("[GESTIONNAIRE " + clientId + "] Connecté");

            while ((message = lecteur.readLine()) != null) {
                System.out.println("[GESTIONNAIRE " + clientId + "] Message : " + message);
                writer.println("ACK: " + message);
            }

        } catch (IOException e) {
            System.out.println("[GESTIONNAIRE " + clientId + "] Déconnecté");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}