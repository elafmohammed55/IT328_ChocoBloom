
package com.mycompany.it328_chocobloom;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());

    
    private Client client;
private final CartData cart = new CartData();   // سلة المستخدم (شغل زميلتك)

// ===== Member 1 - Phase 2 =====
/** productId -> its row on screen, so a stock update finds the right row. */
private final Map<Integer, ProductItemPanel> productPanels = new HashMap<>();
/** Asks the server for the stock every 2 seconds (Functions 5 and 8). */
private static final int STOCK_REFRESH_MS = 2000;
private Timer stockTimer;
private boolean stockRequestRunning = false;
private OrderHistoryFrame historyFrame;

/** For the Design tab preview only. */
public MainFrame() {
    initComponents();
}

public MainFrame(Client client, String username, List<String> menuLines) {
    initComponents();
    this.client = client;
    connectedLabel.setText("\u2714 Connected as " + username);
    setSize(900, 600);
    setLocationRelativeTo(null);
    loadCategories(parseMenu(menuLines));

    // live stock: when any user orders or cancels, every user's rows update
    stockTimer = new Timer(STOCK_REFRESH_MS, e -> refreshStock());
    stockTimer.start();

    // when the window is closed, tell the server we are leaving
    addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            stockTimer.stop();
            MainFrame.this.client.disconnect();
        }
    });
}

/** One tab per category; each tab has a ProductsListPanel filled with ProductItemPanel rows. */
private void loadCategories(List<Category> menu) {
    categoryTabs.removeAll();                 // remove the design-time sample tabs
    productPanels.clear();
    for (Category category : menu) {
        ProductsListPanel list = new ProductsListPanel();
        for (Product product : category.getProducts()) {
            ProductItemPanel item = new ProductItemPanel();
            item.setProduct(product);
            item.setCart(cart);
            list.addProduct(item);
            productPanels.put(product.getId(), item);
        }
        JPanel wrapper = new JPanel(new BorderLayout());   // keeps rows at the top
        wrapper.add(list, BorderLayout.NORTH);
        categoryTabs.addTab(category.getName(), new JScrollPane(wrapper));
    }
}

/**
 * Member 1 - Phase 2 (Functions 5 and 8).
 * Gets the latest stock from the server in a background thread, then
 * calls setStock on every ProductItemPanel. A row whose stock reaches 0
 * gets its quantity box and Add button disabled (inside setStock).
 */
public void refreshStock() {
    if (client == null || stockRequestRunning) {
        return;                       // previous request still running
    }
    stockRequestRunning = true;
    new SwingWorker<List<String>, Void>() {
        @Override
        protected List<String> doInBackground() {
            return client.requestMenu();
        }

        @Override
        protected void done() {
            stockRequestRunning = false;
            try {
                applyStock(parseMenu(get()));
            } catch (Exception ex) {
                // server not reachable this time: try again on the next tick
            }
        }
    }.execute();
}

/** Puts the stock values from the server on the screen (runs on the Swing thread). */
private void applyStock(List<Category> menu) {
    for (Category c : menu) {
        for (Product p : c.getProducts()) {
            ProductItemPanel row = productPanels.get(p.getId());
            if (row != null) {
                row.setStock(p.getStock());
            }
        }
    }
}

/**
 * Converts the server's text lines into Category/Product objects.
 *   CAT,<id>,<name>
 *   PROD,<id>,<name>,<description>,<price>,<stock>,<imagePath>
 */
private List<Category> parseMenu(List<String> lines) {
    List<Category> menu = new ArrayList<>();
    Category current = null;
    for (String line : lines) {
        String[] f = line.split(",");
        if (f[0].equals("CAT") && f.length == 3) {
            current = new Category(Integer.parseInt(f[1]), f[2]);
            menu.add(current);
        } else if (f[0].equals("PROD") && f.length == 7 && current != null) {
            current.addProduct(new Product(
                    Integer.parseInt(f[1]), f[2], f[3],
                    Double.parseDouble(f[4]), Integer.parseInt(f[5]), f[6]));
        }
    }
    return menu;
}

    
    @SuppressWarnings("unchecked") 
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        connectedLabel = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        historyButton = new javax.swing.JButton();
        cartButton = new javax.swing.JButton();
        categoryTabs = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        topPanel.setPreferredSize(new java.awt.Dimension(900, 40));
        topPanel.setLayout(new java.awt.BorderLayout());

        connectedLabel.setForeground(new java.awt.Color(0, 204, 0));
        connectedLabel.setText("Connected as ...");
        topPanel.add(connectedLabel, java.awt.BorderLayout.WEST);

        buttonsPanel.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        historyButton.setText("Order History");
        historyButton.setPreferredSize(new java.awt.Dimension(130, 50));
        historyButton.addActionListener(this::historyButtonActionPerformed);
        buttonsPanel.add(historyButton);

        cartButton.setText("Cart");
        cartButton.setPreferredSize(new java.awt.Dimension(100, 50));
        cartButton.addActionListener(this::cartButtonActionPerformed);
        buttonsPanel.add(cartButton);

        topPanel.add(buttonsPanel, java.awt.BorderLayout.LINE_END);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);
        getContentPane().add(categoryTabs, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cartButtonActionPerformed
        
        cartButton.setEnabled(false);

        // ask the server for the latest stock (background thread so the window never freezes)
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() {
                return client.requestMenu();
            }

            @Override
            protected void done() {
                cartButton.setEnabled(true);
                try {
                    // if the cart has more than the stock it is reduced; stock 0 removes the item
                    List<Category> menu = parseMenu(get());
                    for (Category c : menu) {
                        for (Product p : c.getProducts()) {
                            cart.updateAvailableStock(p.getId(), p.getStock());
                        }
                    }
                    applyStock(menu);
                } catch (Exception ex) {
                    // server not reachable: show the cart as it is
                }
                new CartFrame(cart, client).setVisible(true);
            }
        }.execute();
    
    }//GEN-LAST:event_cartButtonActionPerformed

    private void historyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historyButtonActionPerformed
        // Member 1 - Phase 2 (Function 6): one Order History window, reloaded every time it opens
        if (client == null) {
            return;
        }
        if (historyFrame == null || !historyFrame.isDisplayable()) {
            historyFrame = new OrderHistoryFrame(client, this::refreshStock);
        }
        historyFrame.loadOrders();
        historyFrame.setVisible(true);
        historyFrame.toFront();
    }//GEN-LAST:event_historyButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton cartButton;
    private javax.swing.JTabbedPane categoryTabs;
    private javax.swing.JLabel connectedLabel;
    private javax.swing.JButton historyButton;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
