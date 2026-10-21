package com.mycompany.it328_chocobloom;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    
    public static ArrayList<Category> menu = new ArrayList<>();

    public static void main(String[] args) {
        Category cat1 = new Category(1, "Chocolates");
        cat1.addProduct(new Product(101, "White Chocolate", "Creamy white chocolate bar", 15.0, 20, "white.png"));
        cat1.addProduct(new Product(102, "Dark Chocolate", "70% rich dark chocolate", 18.0, 15, "dark.png"));
        cat1.addProduct(new Product(103, "Milk Chocolate", "Smooth milk chocolate bar", 12.0, 25, "milk.png"));
        
        Category cat2 = new Category(2, "Gifts");
        cat2.addProduct(new Product(201, "Signature Chocolate Bouquet", "An elegant bouquet of premium assorted chocolates", 200.0, 5, "bouquet.png"));
        cat2.addProduct(new Product(202, "Luxury Chocolate Tray", "A luxurious tray filled with a selection of fine chocolates", 350.0, 3, "tray.png"));
        cat2.addProduct(new Product(203, "Elegant Chocolate Gift Box", "A beautifully wrapped gift box filled with premium chocolates", 100.0, 8, "box.png"));
        
        Category cat3 = new Category(3, "Hot Chocolate");
        cat3.addProduct(new Product(301, "Classic Hot Chocolate", "Traditional rich hot chocolate", 25.0, 50, "classic_hc.png"));
        cat3.addProduct(new Product(302, "Mocha Hot Chocolate", "Hot chocolate infused with coffee", 28.0, 35, "mocha_hc.png"));
        cat3.addProduct(new Product(303, "White Hot Chocolate", "white hot chocolate", 26.0, 45, "white_hc.png"));

        menu.add(cat1);
        menu.add(cat2);
        menu.add(cat3);

        System.out.println("[SERVER] Online Ordering System Server is Starting...");

        try (ServerSocket serverSocket = new ServerSocket(9090)) {
            System.out.println("[SERVER] Waiting for clients on port 9090...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] New connection received from: " + clientSocket.getInetAddress());

                ClientHandler clientThread = new ClientHandler(clientSocket);
                new Thread(clientThread).start();
            }

        } catch (IOException e) {
            System.out.println("[SERVER ERROR] " + e.getMessage());
        }
    }

    public static synchronized String processCartOrder(String[] cartItems) {
        boolean allSuccess = true;

        for (int i = 1; i < cartItems.length; i += 2) {
            int productId = Integer.parseInt(cartItems[i]);
            int quantityRequested = Integer.parseInt(cartItems[i+1]);
            
            for (Category c : menu) {
                for (Product p : c.getProducts()) {
                    if (p.getId() == productId) {
                        if (!p.reduceStock(quantityRequested)) {
                            allSuccess = false; 
                        }
                    }
                }
            }
        }
        
        if (allSuccess) {
            return "SUCCESS: Order processed and stocks reduced.";
        } else {
            return "FAILED: Some items were out of stock or invalid quantity.";
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private String username = "Unknown";

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
                System.out.println("[CLIENT " + username + " SAYS] " + request);

                if (request.startsWith("LOGIN")) {
                    String[] parts = request.split(",");
                    if (parts.length == 2) {
                        this.username = parts[1];
                        out.println("SUCCESS: Connected as " + username);
                    }

                } else if (request.equals("VIEW_MENU")) {
                    out.println("Available Menu:");
                    for (Category c : Server.menu) {
                        out.println("CAT," + c.getId() + "," + c.getName());
                        for (Product p : c.getProducts()) {
                            out.println("PROD," + p.getId() + "," + p.getName() + "," + p.getPrice() + "," + p.getStock());
                        }
                    }
                    out.println("END_MENU");
                    
                } else if (request.startsWith("CHECKOUT")) {
                    String[] parts = request.split(",");
                    String response = Server.processCartOrder(parts);
                    out.println(response);

                } else if (request.equals("EXIT")) {
                    out.println("Goodbye!");
                    break;
                } else {
                    out.println("Unknown Command.");
                }
            }
        } catch (IOException e) {
            System.out.println("[SERVER] Client " + username + " disconnected.");
        }
    }
}
