package com.icc490.bike.desktop.panels;

import com.icc490.bike.desktop.ApiClient;
import com.icc490.bike.desktop.exception.ApiException;
import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.gui.utils.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletionException;

public class RecordTablePanel extends JPanel {
    private ApiClient apiClient;
    private JTable recordTable;
    private DefaultTableModel tableModel;
    private JButton checkOutButton; // Nuevo botón

    private RecordActionListener recordActionListener;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public interface RecordActionListener {
        void onRecordCheckedOut();
    }

    public RecordTablePanel(ApiClient apiClient, RecordActionListener listener) {
        this.apiClient = apiClient;
        this.recordActionListener = listener;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppColors.LIGHT_GRAY_BORDER),
                "Registros de Bicicletas",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                AppColors.DARK_TEXT
        ));
        setBackground(AppColors.SECONDARY_BLUE);

        setupTable();
        add(new JScrollPane(recordTable), BorderLayout.CENTER);

        setupButtons();
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(AppColors.SECONDARY_BLUE);
        buttonPanel.add(checkOutButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupTable() {
        String[] columnNames = {"ID","Gancho", "Matrícula", "Nombre Estudiante", "Descripción Bicicleta", "Check-in", "Check-out"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recordTable = new JTable(tableModel);
        recordTable.setFillsViewportHeight(true);
        recordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Estilos de la tabla
        recordTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recordTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        recordTable.getTableHeader().setBackground(AppColors.PRIMARY_BLUE);
        recordTable.getTableHeader().setForeground(Color.BLACK);
        recordTable.setRowHeight(25);
        recordTable.setGridColor(AppColors.LIGHT_GRAY_BORDER);
        recordTable.setBackground(Color.WHITE);
    }

    private void setupButtons() {
        checkOutButton = new JButton("Realizar Check-out");
        checkOutButton.setBackground(AppColors.ACCENT_RED);
        checkOutButton.setForeground(AppColors.WHITE_TEXT);
        checkOutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        checkOutButton.setFocusPainted(false);
        checkOutButton.setBorder(BorderFactory.createLineBorder(AppColors.ACCENT_RED.darker(), 1));

        checkOutButton.addActionListener(e -> {
            int selectedRow = recordTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, selecciona un registro para realizar el check-out.",
                        "Sin Selección",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Long recordId = (Long) tableModel.getValueAt(selectedRow, 0);
            String currentCheckOutStatus = (String) tableModel.getValueAt(selectedRow, 5);

            if (!"Activo".equals(currentCheckOutStatus)) {
                JOptionPane.showMessageDialog(this,
                        "Este registro ya ha sido devuelto.",
                        "Registro ya devuelto",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que quieres devolver la bicicleta con ID de registro: " + recordId + "?",
                    "Confirmar Check-out",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                performCheckOut(recordId);
            }
        });
    }

    private void performCheckOut(Long recordId) {
        apiClient.checkOutRecord(recordId)
                .thenAccept(updatedRecord -> {
                    SwingUtilities.invokeLater(() -> {
                        if (updatedRecord != null) {
                            JOptionPane.showMessageDialog(this,
                                    "Bicicleta devuelta exitosamente.",
                                    "Check-out Exitoso",
                                    JOptionPane.INFORMATION_MESSAGE);
                            if (recordActionListener != null) {
                                recordActionListener.onRecordCheckedOut();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "No fue posible devolver el registro.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }).exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> {
                        String errorMessage = "Error al devolver registro: " + ex.getMessage();
                        Throwable cause = ex.getCause();

                        if (cause instanceof ApiException) {
                            ApiException apiEx = (ApiException) cause;
                            errorMessage = apiEx.getMessage();
                        } else if (cause != null && cause.getMessage() != null) {
                            errorMessage = "Error de conexión: " + cause.getMessage().replace("java.net.http.HttpTimeoutException: HTTP exchange timed out", "Error de conexión con el servidor. Verifique que la API esté en ejecución y sea accesible.");
                        }

                        JOptionPane.showMessageDialog(this,
                                errorMessage,
                                "Error de API al devolver",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    ex.printStackTrace();
                    return null;
                });
    }

    public void loadRecords() {
        tableModel.setRowCount(0);
        apiClient.getAllRecords().thenAccept(records -> {
            SwingUtilities.invokeLater(() -> {
                if (records != null && !records.isEmpty()) {
                    for (Record record : records) {
                        String checkInFormatted = record.getCheckIn() != null ? DATE_FORMATTER.format(record.getCheckIn()) : "N/A";
                        String checkOutFormatted = record.getCheckOut() != null ? DATE_FORMATTER.format(record.getCheckOut()) : "Activo";

                        tableModel.addRow(new Object[]{
                                record.getId(),
                                record.getHook(),
                                record.getStudentId(),
                                record.getStudentName(),
                                record.getBicycleDescription(),
                                checkInFormatted,
                                checkOutFormatted
                        });
                    }
                } else {
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                String errorMessage = "Error al cargar registros: " + ex.getMessage();
                Throwable cause = ex.getCause();

                if (cause instanceof ApiException) {
                    ApiException apiEx = (ApiException) cause;
                    errorMessage = apiEx.getMessage();
                } else if (cause != null && cause.getMessage() != null) {
                    errorMessage = "Error de conexión: " + cause.getMessage().replace("java.net.http.HttpTimeoutException: HTTP exchange timed out", "Error de conexión con el servidor. Verifique que la API esté en ejecución y sea accesible.");
                }

                JOptionPane.showMessageDialog(this,
                        errorMessage,
                        "Error de API",
                        JOptionPane.ERROR_MESSAGE);
            });
            ex.printStackTrace();
            return null;
        });
    }
}