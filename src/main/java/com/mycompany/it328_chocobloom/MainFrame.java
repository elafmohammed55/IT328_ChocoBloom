
package com.mycompany.it328_chocobloom;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());

    
    private Client client;
private final CartData cart = new CartData();   // سلة المستخدم (شغل زميلتك)

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

    // when the window is closed, tell the server we are leaving
    addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            MainFrame.this.client.disconnect();
        }
    });
}

/** One tab per category; each tab has a ProductsListPanel filled with ProductItemPanel rows. */
private void loadCategories(List<Category> menu) {
    categoryTabs.removeAll();                 // remove the design-time sample tabs
    for (Category category : menu) {
        ProductsListPanel list = new ProductsListPanel();
        for (Product product : category.getProducts()) {
            ProductItemPanel item = new ProductItemPanel();
            item.setProduct(product);
            item.setCart(cart);
            list.addProduct(item);
        }
        JPanel wrapper = new JPanel(new BorderLayout());   // keeps rows at the top
        wrapper.add(list, BorderLayout.NORTH);
        categoryTabs.addTab(category.getName(), new JScrollPane(wrapper));
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
        cartButton = new javax.swing.JButton();
        categoryTabs = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        topPanel.setPreferredSize(new java.awt.Dimension(900, 40));
        topPanel.setLayout(new java.awt.BorderLayout());

        connectedLabel.setForeground(new java.awt.Color(0, 204, 0));
        connectedLabel.setText("Connected as ...");
        topPanel.add(connectedLabel, java.awt.BorderLayout.WEST);

        cartButton.setText("Cart");
        cartButton.setPreferredSize(new java.awt.Dimension(100, 50));
        cartButton.addActionListener(this::cartButtonActionPerformed);
        topPanel.add(cartButton, java.awt.BorderLayout.LINE_END);

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
                    for (Category c : parseMenu(get())) {
                        for (Product p : c.getProducts()) {
                            cart.updateAvailableStock(p.getId(), p.getStock());
                        }
                    }
                } catch (Exception ex) {
                    // server not reachable: show the cart as it is
                }
                new CartFrame(cart).setVisible(true);
            }
        }.execute();
    
    }//GEN-LAST:event_cartButtonActionPerformed

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
    private javax.swing.JButton cartButton;
    private javax.swing.JTabbedPane categoryTabs;
    private javax.swing.JLabel connectedLabel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
