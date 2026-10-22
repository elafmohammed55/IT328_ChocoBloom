
package com.mycompany.it328_chocobloom;

import java.awt.Color;
import java.util.List;
import javax.swing.SwingWorker;

public class LoginFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFrame.class.getName());

    /*** Creates new form LoginFrame */
    private static final int PORT = 9090;
private final Client client = new Client();   // كلاس الفريق للاتصال بالسيرفر
    public LoginFrame() {
        initComponents();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        serverLabel = new javax.swing.JLabel();
        serverField = new javax.swing.JTextField();
        connectButton = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        titleLabel.setFont(new java.awt.Font("Helvetica Neue", 1, 30)); // NOI18N
        titleLabel.setForeground(new java.awt.Color(40, 26, 26));
        titleLabel.setText("ChocoBloom");

        usernameLabel.setText("Username:");

        usernameField.addActionListener(this::usernameFieldActionPerformed);

        serverLabel.setText("Server:");

        serverField.setText("localhost");
        serverField.addActionListener(this::serverFieldActionPerformed);

        connectButton.setText("Connect");
        connectButton.addActionListener(this::connectButtonActionPerformed);

        statusLabel.setText("Not connected");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(statusLabel)
                .addGap(153, 153, 153))
            .addGroup(layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(usernameLabel)
                            .addComponent(serverLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(usernameField)
                            .addComponent(serverField, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)))
                    .addComponent(connectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(70, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(titleLabel)
                .addGap(105, 105, 105))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(titleLabel)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverLabel)
                    .addComponent(serverField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(connectButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusLabel)
                .addGap(76, 76, 76))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void usernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernameFieldActionPerformed

    private void serverFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_serverFieldActionPerformed

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        // TODO add your handling code here:
        final String username = usernameField.getText().trim();
String host = serverField.getText().trim();
final String serverAddress = host.isEmpty() ? "localhost" : host;

// البروتوكول يفصل بالفاصلة، فما نسمح بها في الاسم
if (username.isEmpty() || username.contains(",")) {
    showStatus("Please enter a username (no commas).", Color.RED);
    return;
}

connectButton.setEnabled(false);
showStatus("Connecting...", Color.DARK_GRAY);

// الاتصال في Thread خلفي عشان النافذة ما تتجمد
new SwingWorker<List<String>, Void>() {
    @Override
    protected List<String> doInBackground() {
        client.connect(serverAddress, PORT);
        String reply = client.login(username);
        if (reply == null || !reply.startsWith("SUCCESS")) {
            return null;                      // فشل الاتصال
        }
        return client.requestMenu();          // نجح: نجيب المنيو
    }

    @Override
    protected void done() {
        connectButton.setEnabled(true);
        try {
            List<String> menuLines = get();
            if (menuLines == null) {
                client.disconnect();
                showStatus("Connection failed. Is the server running?", Color.RED);
                return;
            }
            showStatus("Connected as " + username, new Color(0, 130, 0));
            
            new MainFrame(client, username, menuLines).setVisible(true);
            dispose();      // نغلق شاشة الدخول
        } catch (Exception ex) {
            client.disconnect();
            showStatus("Error: " + ex.getMessage(), Color.RED);
        }
    }
}.execute();
    }//GEN-LAST:event_connectButtonActionPerformed
private void showStatus(String text, Color color) {
    statusLabel.setText(text);
    statusLabel.setForeground(color);
}
    
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
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JTextField serverField;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
