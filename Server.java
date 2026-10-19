
package com.mycompany.chocobloom;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    
    public static ArrayList<Product> inventory = new ArrayList<>();

    public static void main(String[] args) {
        inventory.add(new Product(1, "Box of Truffles", "12 pieces of assorted truffles", 50.0, 10, "truffles.png"));
        inventory.add(new Product(2, "Dark Chocolate Bar", "70% Cocoa premium bar", 15.0, 20, "dark.png"));
        inventory.add(new Product(3, "Hot Chocolate Mix", "Rich cocoa powder tin", 25.0, 15, "hot.png"));

        System.out.println("[SERVER] Chocolate Shop Server is Starting...");

        try (ServerSocket serverSocket = new ServerSocket(9090)) {
            System.out.println("[SERVER] Waiting for clients on port 9090...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] New Client Connected: " + clientSocket.getInetAddress());

                ClientHandler clientThread = new ClientHandler(clientSocket);
                new Thread(clientThread).start();
            }

        } catch (IOException e) {
            System.out.println("[SERVER ERROR] " + e.getMessage());
        }
    }

    public static synchronized String processOrder(String productName, int amountRequested) {
        for (Product p : inventory) {
            if (p.getName().equalsIgnoreCase(productName)) {
                if (p.reduceStock(amountRequested)) {
                    return "SUCCESS: Purchased " + amountRequested + "x " + productName + ". Remaining: " + p.getStock();
                } else {
                    return "FAILED: Out of stock or invalid quantity! Only " + p.getStock() + " left.";
                }
            }
        }
        return "FAILED: Product not found.";
    }
}

class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String request;
            
            while ((request = in.readLine()) != null) {
                System.out.println("[CLIENT SAYS] " + request);

                if (request.equals("VIEW_MENU")) {
                    out.println("Available Products:");
                    for (Product p : Server.inventory) {
                        out.println("- " + p.getName() + " | Price: " + p.getPrice() + " | Qty: " + p.getStock());
                    }
                    out.println("END_MENU");
                    
                } else if (request.startsWith("BUY")) {
                    String[] parts = request.split(",");
                    if(parts.length == 3) {
                        String name = parts[1];
                        int amount = Integer.parseInt(parts[2]);
                        
                        String response = Server.processOrder(name, amount);
                        out.println(response);
                        System.out.println("[SERVER RESPONSE] " + response);
                    }
                } else if (request.equals("EXIT")) {
                    out.println("Goodbye!");
                    break;
                } else {
                    out.println("Unknown Command.");
                }
            }
        } catch (IOException e) {
            System.out.println("[SERVER] A client disconnected unexpectedly.");
        }
    }
}