// En CheckOutPanel.java
package com.icc490.bike.desktop.panels;

import com.icc490.bike.desktop.ApiClient;
import com.icc490.bike.desktop.exception.ApiException;
import com.icc490.bike.desktop.model.Record;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletionException;

public class CheckOutPanel extends JPanel {
    private JTextField recordIdField;
    private JButton checkOutButton;
    private ApiClient apiClient;
    private CheckOutListener checkOutListener;

    public CheckOutPanel(ApiClient apiClient, CheckOutListener listener) {
        this.apiClient = apiClient;
        this.checkOutListener = listener;
        initComponents();
        setupLayout();
        addListeners();
    }

    private void initComponents() {
        recordIdField = new JTextField(10);
        checkOutButton = new JButton("Realizar Check-out");
        checkOutButton.setBackground(new Color(180, 80, 80));
        checkOutButton.setForeground(Color.WHITE);
        checkOutButton.setFocusPainted(false);
        recordIdField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Realizar Check-out"));

        add(new JLabel("ID del Registro:"));
        add(recordIdField);
        add(checkOutButton);
    }

    private void addListeners() {
        checkOutButton.addActionListener(e -> performCheckOut());
    }

    private void performCheckOut() {
        String recordIdText = recordIdField.getText();
        if (recordIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el ID del registro.", "Error de Entrada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long recordId = Long.parseLong(recordIdText);

            apiClient.checkOutRecord(recordId).thenAccept(checkedOutRecord -> {
                SwingUtilities.invokeLater(() -> {
                    if (checkedOutRecord != null) {
                        JOptionPane.showMessageDialog(this, "Registro ID " + checkedOutRecord.getId() + " marcado como devuelto.", "Check-out Exitoso", JOptionPane.INFORMATION_MESSAGE);
                        recordIdField.setText("");
                        if (checkOutListener != null) {
                            checkOutListener.onRecordCheckedOut();
                        }
                    } else {

                    }
                });
            }).exceptionally(ex -> {
                SwingUtilities.invokeLater(() -> {
                    Throwable cause = ex;
                    if (ex instanceof CompletionException && ex.getCause() != null) {
                        cause = ex.getCause(); // Desenvuelve CompletionException
                    }

                    String errorMessage = "Error al realizar check-out: " + cause.getMessage();

                    if (cause instanceof ApiException) {
                        ApiException apiEx = (ApiException) cause;
                        errorMessage = "Error de la API [Estado: " + apiEx.getErrorResponse().getStatus() + "]: " + apiEx.getErrorResponse().getError();
                    } else if (cause.getMessage() != null && cause.getMessage().contains("java.net.http.HttpTimeoutException")) {
                        errorMessage = "Error de conexión con el servidor. Verifique que la API esté en ejecución y sea accesible.";
                    }

                    JOptionPane.showMessageDialog(this,
                            errorMessage + "\nRevisar consola para más detalles.",
                            "Error de Check-out",
                            JOptionPane.ERROR_MESSAGE);
                });
                ex.printStackTrace();
                return null;
            });

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de registro válido (número).", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    public interface CheckOutListener {
        void onRecordCheckedOut();
    }
}