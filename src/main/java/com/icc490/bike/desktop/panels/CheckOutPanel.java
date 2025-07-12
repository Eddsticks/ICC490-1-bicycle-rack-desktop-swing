package com.icc490.bike.desktop.panels;

import com.icc490.bike.desktop.ApiClient;
import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.panels.RecordTablePanel.RecordActionListener; // Importar el listener correcto
import com.icc490.bike.desktop.gui.utils.AppColors;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.List;

public class CheckOutPanel extends JPanel {
    private JTextField recordIdField;
    private JButton checkOutButton;

    private ApiClient apiClient;
    private RecordActionListener listener;

    public CheckOutPanel(ApiClient apiClient, RecordActionListener listener) {
        this.apiClient = apiClient;
        this.listener = listener;
        initComponents();
        setupLayout();
        addListeners();
    }

    private void initComponents() {
        recordIdField = new JTextField(20);
        checkOutButton = new JButton("Realizar Check-out");
        checkOutButton.setBackground(AppColors.ACCENT_RED);
        checkOutButton.setForeground(AppColors.WHITE_TEXT);
        checkOutButton.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(createLabel("ID Registro a devolver:"), gbc);
        gbc.gridx = 1;
        add(recordIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(checkOutButton, gbc);

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(AppColors.LIGHT_GRAY_BORDER), "Realizar Check-out"));
        setBackground(new Color(230, 220, 200));
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppColors.DARK_TEXT);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private void addListeners() {
        checkOutButton.addActionListener(e -> {
            String recordIdStr = recordIdField.getText();
            if (recordIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo ID de Registro es obligatorio.", "Error de Entrada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Long recordId;
            try {
                recordId = Long.parseLong(recordIdStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID de Registro debe ser un número válido.", "Error de Entrada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Long finalRecordId = recordId;

            apiClient.getAllRecords().thenAccept(records -> {
                Optional<Record> recordToCheckoutOpt;
                if (records != null) {
                    recordToCheckoutOpt = records.stream()
                            .filter(r -> r.getId() != null && r.getId().equals(finalRecordId))
                            .findFirst();
                } else {
                    recordToCheckoutOpt = Optional.empty();
                }

                SwingUtilities.invokeLater(() -> {
                    if (recordToCheckoutOpt.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "Registro con ID " + finalRecordId + " no encontrado.",
                                "Error de Búsqueda",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    Record recordToCheckout = recordToCheckoutOpt.get();

                    if (recordToCheckout.getCheckOut() != null) {
                        JOptionPane.showMessageDialog(this,
                                "Este registro (ID: " + finalRecordId + ") ya ha sido devuelto.",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE);
                        clearFields();
                        if (listener != null) {
                            listener.onRecordCheckedOut();
                        }
                    } else {
                        apiClient.checkOutRecord(finalRecordId).thenAccept(checkedOutRecord -> {
                            SwingUtilities.invokeLater(() -> {
                                if (checkedOutRecord != null && checkedOutRecord.getCheckOut() != null) {
                                    JOptionPane.showMessageDialog(this, "Check-out exitoso para el registro ID: " + finalRecordId, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                    clearFields();
                                    if (listener != null) {
                                        listener.onRecordCheckedOut(); // Notificar a SwingApp para recargar la tabla
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(this, "No fue posible procesar el check-out. El servidor no retornó un estado actualizado o válido.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
                        }).exceptionally(ex -> {
                            SwingUtilities.invokeLater(() -> {
                                String errorMessage = "Error al realizar check-out: " + ex.getMessage();
                                Throwable cause = ex.getCause();
                                if (cause != null && cause.getMessage() != null) {
                                    String actualMessage = cause.getMessage();
                                    if (actualMessage.contains("java.net.ConnectException")) {
                                        errorMessage = "Error de conexión con el servidor. Verifique que la API esté en ejecución y sea accesible.";
                                    } else if (actualMessage.contains("java.net.http.HttpTimeoutException")) {
                                        errorMessage = "La solicitud al servidor ha excedido el tiempo de espera.";
                                    } else {
                                        errorMessage = "Error de API al realizar check-out: " + actualMessage;
                                    }
                                }
                                JOptionPane.showMessageDialog(this,
                                        errorMessage + "\nRevisar consola para más detalles.",
                                        "Error de API",
                                        JOptionPane.ERROR_MESSAGE);
                            });
                            ex.printStackTrace();
                            return null;
                        });
                    }
                });
            }).exceptionally(ex -> {
                SwingUtilities.invokeLater(() -> {
                    String errorMessage = "Error al verificar el estado del registro: " + ex.getMessage();
                    Throwable cause = ex.getCause();
                    if (cause != null && cause.getMessage() != null) {
                        String actualMessage = cause.getMessage();
                        if (actualMessage.contains("java.net.ConnectException")) {
                            errorMessage = "Error de conexión con el servidor al verificar el registro. Verifique que la API esté en ejecución y sea accesible.";
                        } else if (actualMessage.contains("java.net.http.HttpTimeoutException")) {
                            errorMessage = "La verificación del registro ha excedido el tiempo de espera.";
                        } else {
                            errorMessage = "Error de API al verificar registro: " + actualMessage;
                        }
                    }
                    JOptionPane.showMessageDialog(this,
                            errorMessage + "\nRevisar consola para más detalles.",
                            "Error de Conexión/Validación",
                            JOptionPane.ERROR_MESSAGE);
                });
                ex.printStackTrace();
                return null;
            });
        });
    }

    private void clearFields() {
        recordIdField.setText("");
    }
}