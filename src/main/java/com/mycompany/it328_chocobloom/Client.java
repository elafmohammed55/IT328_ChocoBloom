package com.mycompany.it328_chocobloom;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    
    public void connect(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out = new PrintWriter(socket.getOutputStream(), true); 
            System.out.println("[CLIENT] Connected to server at " + serverAddress + ":" + port);
        } catch (IOException e) {
            System.err.println("[CLIENT ERROR] Connection failed: " + e.getMessage());
        }
    }

    
    public String login(String username) {
        this.username = username;
        if (out != null && in != null) {
            try {
                
                out.println("LOGIN," + username); 
                String response = in.readLine();
                System.out.println("[SERVER SAYS] " + response);
                return response;
            } catch (IOException e) {
                return "[CLIENT ERROR] Login failed: " + e.getMessage();
            }
        }
        return "FAILED: Not connected to server.";
    }

    
    public List<String> requestMenu() {
        List<String> menuItems = new ArrayList<>(); 
        
        if (out != null && in != null) {
            try {
                System.out.println("[CLIENT] Requesting menu for GUI...");
                out.println("VIEW_MENU"); 
                
                String response;
                while ((response = in.readLine()) != null) {
                    if (response.equals("END_MENU")) {
                        break; 
                    }
                    menuItems.add(response); 
                }
                System.out.println("[CLIENT] Menu received successfully!");
            } catch (IOException e) {
                System.err.println("[CLIENT ERROR] Failed to retrieve menu: " + e.getMessage());
            }
        }
        return menuItems; 
    }
    
 public List<Category> getParsedMenu() {
        List<String> rawMenu = requestMenu();
        List<Category> categories = new ArrayList<>();
        Category currentCategory = null;

        for (String line : rawMenu) {
            try {
                if (line.startsWith("CAT,")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        currentCategory = new Category(Integer.parseInt(parts[1].trim()), parts[2].trim());
                        categories.add(currentCategory);
                    }
                } 
                else if (line.startsWith("PROD,") && currentCategory != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        Product product = new Product(
                            Integer.parseInt(parts[1].trim()), 
                            parts[2].trim(),                   
                            "",                         
                            Double.parseDouble(parts[3].trim()), 
                            Integer.parseInt(parts[4].trim()),   
                            ""                          
                        );
                        currentCategory.addProduct(product);
                    }
                }
            } catch (Exception e) {
                System.err.println("[CLIENT PARSING ERROR] Failed to parse line: " + line + " -> " + e.getMessage());
            }
        }
        return categories;
    }
  
    public void requestLatestStock() {
        if (out != null && in != null) {
            try {
                System.out.println("[CLIENT] Requesting latest stock from server...");
                out.println("GET_STOCK"); 
                
                String response;
                while ((response = in.readLine()) != null) {
                    if (response.equals("END_STOCK")) { 
                        break;
                    }
                
                    if (response.startsWith("STOCK,")) {
                        String[] parts = response.split(",");
                        if (parts.length >= 3) {
                            int productId = Integer.parseInt(parts[1].trim());
                            int newQuantity = Integer.parseInt(parts[2].trim());
                        
                            CartData.updateAvailableStock(productId, newQuantity);
                        }
                    }
                }
                System.out.println("[CLIENT] Stock updated successfully!");
            } catch (Exception e) {
                System.err.println("[CLIENT ERROR] Failed to retrieve stock: " + e.getMessage());
            }
        }
    }
    public void disconnect() {
        if (out != null) {
           
            out.println("EXIT"); 
        }
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("[CLIENT] Disconnected from server.");
        } catch (IOException e) {
            System.err.println("[CLIENT ERROR] Disconnect failed: " + e.getMessage());
        }
    }

}
