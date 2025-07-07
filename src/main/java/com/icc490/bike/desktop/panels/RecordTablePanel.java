package com.icc490.bike.desktop.panels;

import com.icc490.bike.desktop.ApiClient;
import com.icc490.bike.desktop.model.Record;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecordTablePanel extends JPanel {
    private ApiClient apiClient;
    private JTable recordTable;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public RecordTablePanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Registros de Bicicletas"));

        setupTable();
        add(new JScrollPane(recordTable), BorderLayout.CENTER); // Envuelve la tabla en un JScrollPane
    }

    private void setupTable() {
        String[] columnNames = {"ID", "ID Estudiante", "Nombre Estudiante", "Descripción Bicicleta", "Check-in", "Check-out"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recordTable = new JTable(tableModel);
        recordTable.setFillsViewportHeight(true);
        recordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        recordTable.getTableHeader().setReorderingAllowed(false);
        recordTable.setAutoCreateRowSorter(true);
    }

    public void loadRecords() {
        apiClient.getAllRecords().thenAccept(records -> {
            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                if (records != null && !records.isEmpty()) {
                    for (Object obj : records) {
                        if (obj instanceof Record) {
                            Record record = (Record) obj;
                            String checkInFormatted = record.getCheckIn() != null ? DATE_FORMATTER.format(record.getCheckIn()) : "N/A";
                            String checkOutFormatted = record.getCheckOut() != null ? DATE_FORMATTER.format(record.getCheckOut()) : "Activo";

                            tableModel.addRow(new Object[]{
                                    record.getId(),
                                    record.getStudentId(),
                                    record.getStudentName(),
                                    record.getBicycleDescription(),
                                    checkInFormatted,
                                    checkOutFormatted
                            });
                        }
                    }
                } else {
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "Error al cargar registros: " + ex.getMessage() + "\nRevisar consola para más detalles.",
                        "Error de API",
                        JOptionPane.ERROR_MESSAGE);
            });
            ex.printStackTrace();
            return null;
        });
    }
}