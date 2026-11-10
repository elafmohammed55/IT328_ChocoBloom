package com.mycompany.it328_chocobloom;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class CartFrame extends javax.swing.JFrame {

    private final CartData cart;
    private final Client client;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Item", "Price", "Quantity", "Subtotal"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public CartFrame() {
        this(new CartData(), null);
    }

    public CartFrame(CartData cart, Client client) {
        this.cart = cart;
        this.client = client;
        initComponents();
        cartTable.setModel(tableModel);
        setLocationRelativeTo(null);
        refreshCart();
    }

    public void refreshCart() {
        tableModel.setRowCount(0);
        for (CartItem item : cart.getItems()) {
            tableModel.addRow(new Object[]{
                item.getProduct().getName(),
                String.format("%.2f", item.getProduct().getPrice()),
                item.getQuantity(),
                String.format("%.2f", item.getSubtotal())
            });
        }
        totalLabel.setText(String.format("Total: %.2f", cart.getTotal()));
        orderButton.setEnabled(!cart.isEmpty());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        cartScrollPane = new javax.swing.JScrollPane();
        cartTable = new javax.swing.JTable();
        bottomPanel = new javax.swing.JPanel();
        totalLabel = new javax.swing.JLabel();
        orderButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Shopping Cart");

        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        topPanel.add(backButton);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        cartScrollPane.setPreferredSize(new java.awt.Dimension(480, 220));
        cartScrollPane.setViewportView(cartTable);

        getContentPane().add(cartScrollPane, java.awt.BorderLayout.CENTER);

        bottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 15, 10));

        totalLabel.setFont(totalLabel.getFont().deriveFont(totalLabel.getFont().getStyle() | java.awt.Font.BOLD));
        totalLabel.setText("Total: 0.00");
        bottomPanel.add(totalLabel);

        orderButton.setText("Order");
        orderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(orderButton);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        dispose();
    }//GEN-LAST:event_backButtonActionPerformed

    private void orderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderButtonActionPerformed
        if (client == null || cart.isEmpty()) {
            return;
        }
        orderButton.setEnabled(false);
        backButton.setEnabled(false);
        List<CartItem> items = cart.getItems();

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return client.placeOrder(items);
            }

            @Override
            protected void done() {
                backButton.setEnabled(true);
                String response;
                try {
                    response = get();
                } catch (Exception ex) {
                    response = "FAILED: Could not reach the server.";
                }

                if (response != null && response.startsWith("SUCCESS")) {
                    JOptionPane.showMessageDialog(CartFrame.this,
                            "Order is placed successfully.",
                            "Message", JOptionPane.INFORMATION_MESSAGE);
                    cart.clear();
                    dispose();
                } else {
                    String reason = (response == null) ? "No response from the server."
                            : response.replaceFirst("^FAILED:\\s*", "");
                    JOptionPane.showMessageDialog(CartFrame.this,
                            "Order is cancelled. " + reason,
                            "Message", JOptionPane.WARNING_MESSAGE);
                    refreshCart();
                }
            }
        }.execute();
    }//GEN-LAST:event_orderButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JScrollPane cartScrollPane;
    private javax.swing.JTable cartTable;
    private javax.swing.JButton orderButton;
    private javax.swing.JPanel topPanel;
    private javax.swing.JLabel totalLabel;
    // End of variables declaration//GEN-END:variables
}
