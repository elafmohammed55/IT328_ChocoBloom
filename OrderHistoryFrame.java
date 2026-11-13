package com.mycompany.it328_chocobloom;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Member 1 - Phase 2.
 *   Function 6: shows the user's orders, each one identified by its timestamp.
 *   Function 6: Cancel Order button for the selected order.
 *   Function 7: shows the server's confirmation (or failure) message.
 * Same look as CartFrame: Back on top, table in the middle, action button at the bottom.
 */
public class OrderHistoryFrame extends javax.swing.JFrame {

    private final Client client;
    private final Runnable onStockChanged;              // MainFrame::refreshStock
    private final List<Integer> orderIds = new ArrayList<>();   // same order as the table rows
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Order Time", "Items", "Total"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /** For the Design tab preview only. */
    public OrderHistoryFrame() {
        this(null, null);
    }

    public OrderHistoryFrame(Client client, Runnable onStockChanged) {
        this.client = client;
        this.onStockChanged = onStockChanged;
        initComponents();
        ordersTable.setModel(tableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(320);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        ordersTable.getSelectionModel().addListSelectionListener(e -> updateCancelButton());
        setLocationRelativeTo(null);
        updateCancelButton();
    }

    /** Asks the server for this user's orders and fills the table. */
    public void loadOrders() {
        if (client == null) {
            return;
        }
        statusLabel.setText("Loading orders...");
        refreshButton.setEnabled(false);
        cancelButton.setEnabled(false);

        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() {
                return client.getMyOrders();
            }

            @Override
            protected void done() {
                refreshButton.setEnabled(true);
                try {
                    showOrders(get());
                } catch (Exception ex) {
                    statusLabel.setText("Could not reach the server.");
                }
            }
        }.execute();
    }

    /** Each line: ORDER,<orderId>,<timestamp>,<total>,<item x qty; item x qty> */
    private void showOrders(List<String> lines) {
        tableModel.setRowCount(0);
        orderIds.clear();
        for (String line : lines) {
            String[] f = line.split(",", 5);
            if (f.length < 5) {
                continue;
            }
            try {
                orderIds.add(Integer.parseInt(f[1]));
                tableModel.addRow(new Object[]{
                    f[2],
                    f[4],
                    String.format("%.2f", Double.parseDouble(f[3]))
                });
            } catch (NumberFormatException ex) {
                // skip a bad line
            }
        }
        statusLabel.setText(orderIds.isEmpty()
                ? "You have no orders yet."
                : "Select an order to cancel it.");
        updateCancelButton();
    }

    private void updateCancelButton() {
        cancelButton.setEnabled(client != null && ordersTable.getSelectedRow() >= 0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        refreshButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        ordersScrollPane = new javax.swing.JScrollPane();
        ordersTable = new javax.swing.JTable();
        bottomPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Order History");

        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        topPanel.add(refreshButton);

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        topPanel.add(backButton);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        ordersScrollPane.setPreferredSize(new java.awt.Dimension(560, 220));
        ordersScrollPane.setViewportView(ordersTable);

        getContentPane().add(ordersScrollPane, java.awt.BorderLayout.CENTER);

        bottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 15, 10));

        statusLabel.setText("Select an order to cancel it.");
        bottomPanel.add(statusLabel);

        cancelButton.setText("Cancel Order");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(cancelButton);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        dispose();
    }//GEN-LAST:event_backButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        loadOrders();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        int row = ordersTable.getSelectedRow();
        if (client == null || row < 0 || row >= orderIds.size()) {
            return;
        }
        final int orderId = orderIds.get(row);
        final String timestamp = (String) tableModel.getValueAt(row, 0);

        int answer = JOptionPane.showConfirmDialog(this,
                "Cancel the order placed at " + timestamp + "?",
                "Cancel Order", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        cancelButton.setEnabled(false);
        backButton.setEnabled(false);

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return client.cancelOrder(orderId);
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

                // Function 7: confirmation from the server
                if (response != null && response.startsWith("SUCCESS")) {
                    JOptionPane.showMessageDialog(OrderHistoryFrame.this,
                            "The order placed at " + timestamp + " is cancelled successfully.",
                            "Message", JOptionPane.INFORMATION_MESSAGE);
                    if (onStockChanged != null) {
                        onStockChanged.run();       // Function 8: show the returned stock now
                    }
                } else {
                    String reason = (response == null) ? "No response from the server."
                            : response.replaceFirst("^FAILED:\\s*", "");
                    JOptionPane.showMessageDialog(OrderHistoryFrame.this,
                            "The order could not be cancelled. " + reason,
                            "Message", JOptionPane.WARNING_MESSAGE);
                }
                loadOrders();
            }
        }.execute();
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane ordersScrollPane;
    private javax.swing.JTable ordersTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
