package com.icc490.bike.desktop;

import com.icc490.bike.desktop.exception.ApiErrorResponse;
import com.icc490.bike.desktop.exception.ApiException;
import com.icc490.bike.desktop.gui.utils.AppColors; // Importar la nueva clase de colores
import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.model.RecordPageResponse;
import com.icc490.bike.desktop.model.RecordRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

public class SwingApp extends JFrame {
    private ApiClient apiClient;
    private DefaultTableModel tableModel;
    private JTable recordTable;
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField bicycleDescriptionField;
    private JTextField rackIdField;
    private JTextField hookField;
    private JTextField filterField;
    private JTextField checkOutRecordIdField;
    private String currentPageToken = null;
    private static final int PAGE_SIZE = 10;

    public SwingApp() {
        super("Gestión de Rack de Bicicletas UFRO");
        apiClient = new ApiClient();

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Look and Feel no pudo ser Establecido. Usando predet.");
        }
        SwingUtilities.updateComponentTreeUI(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        // Panel superior para la creación de registros
        JPanel createRecordPanel = new JPanel();
        createRecordPanel.setLayout(new GridBagLayout());
        createRecordPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppColors.LIGHT_GRAY_BORDER), "Registrar Nueva Bicicleta"));
        createRecordPanel.setBackground(AppColors.SECONDARY_BLUE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Estilos para JLabels
        Font labelFont = new Font("SansSerif", Font.BOLD, 12);

        // Student ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel studentIdLabel = new JLabel("ID Estudiante:");
        studentIdLabel.setForeground(AppColors.DARK_TEXT);
        studentIdLabel.setFont(labelFont);
        createRecordPanel.add(studentIdLabel, gbc);
        gbc.gridx = 1;
        studentIdField = new JTextField(15);
        createRecordPanel.add(studentIdField, gbc);

        // Student Name
        gbc.gridx = 2;
        gbc.gridy = 0;
        JLabel studentNameLabel = new JLabel("Nombre Estudiante:");
        studentNameLabel.setForeground(AppColors.DARK_TEXT);
        studentNameLabel.setFont(labelFont);
        createRecordPanel.add(studentNameLabel, gbc);
        gbc.gridx = 3;
        studentNameField = new JTextField(15);
        createRecordPanel.add(studentNameField, gbc);

        // Bicycle Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel bicycleDescriptionLabel = new JLabel("Descripción Bicicleta:");
        bicycleDescriptionLabel.setForeground(AppColors.DARK_TEXT);
        bicycleDescriptionLabel.setFont(labelFont);
        createRecordPanel.add(bicycleDescriptionLabel, gbc);
        gbc.gridx = 1;
        bicycleDescriptionField = new JTextField(15);
        createRecordPanel.add(bicycleDescriptionField, gbc);

        // Rack ID
        gbc.gridx = 2;
        gbc.gridy = 1;
        JLabel rackIdLabel = new JLabel("ID Rack:");
        rackIdLabel.setForeground(AppColors.DARK_TEXT);
        rackIdLabel.setFont(labelFont);
        createRecordPanel.add(rackIdLabel, gbc);
        gbc.gridx = 3;
        rackIdField = new JTextField(15);
        createRecordPanel.add(rackIdField, gbc);

        // Hook
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel hookLabel = new JLabel("Gancho:");
        hookLabel.setForeground(AppColors.DARK_TEXT);
        hookLabel.setFont(labelFont);
        createRecordPanel.add(hookLabel, gbc);
        gbc.gridx = 1;
        hookField = new JTextField(15);
        createRecordPanel.add(hookField, gbc);

        // Botón de Crear Registro
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton createRecordButton = new JButton("Registrar Bicicleta");
        createRecordButton.setBackground(AppColors.PRIMARY_BLUE);
        createRecordButton.setForeground(AppColors.WHITE_TEXT);
        createRecordButton.addActionListener(e -> createRecord());
        createRecordPanel.add(createRecordButton, gbc);

        add(createRecordPanel, BorderLayout.NORTH);

        // Panel central para la tabla de registros
        String[] columnNames = {"ID", "ID Estudiante", "Nombre Estudiante", "Descripción Bicicleta", "Rack", "Gancho", "Check-In", "Check-Out"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recordTable = new JTable(tableModel);
        recordTable.setFillsViewportHeight(true);
        recordTable.getTableHeader().setBackground(AppColors.PRIMARY_BLUE);
        recordTable.getTableHeader().setForeground(AppColors.WHITE_TEXT);
        recordTable.setBackground(AppColors.SECONDARY_BLUE);
        recordTable.setForeground(AppColors.DARK_TEXT);
        recordTable.setSelectionBackground(new Color(150, 180, 220));
        recordTable.setSelectionForeground(AppColors.DARK_TEXT);

        JScrollPane scrollPane = new JScrollPane(recordTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para acciones
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(AppColors.SECONDARY_BLUE);

        // Panel de filtrado
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.setBackground(AppColors.SECONDARY_BLUE);
        JLabel filterLabel = new JLabel("Filtro:");
        filterLabel.setForeground(AppColors.DARK_TEXT);
        filterLabel.setFont(labelFont);
        filterField = new JTextField(25);
        JButton applyFilterButton = new JButton("Aplicar Filtro");
        applyFilterButton.setBackground(AppColors.PRIMARY_BLUE);
        applyFilterButton.setForeground(AppColors.WHITE_TEXT);
        applyFilterButton.addActionListener(e -> {
            currentPageToken = null;
            loadRecords();
        });
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);
        filterPanel.add(applyFilterButton);
        bottomPanel.add(filterPanel, BorderLayout.NORTH);

        // Panel de paginación
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        paginationPanel.setBackground(AppColors.SECONDARY_BLUE);
        JButton prevButton = new JButton("Anterior");
        prevButton.setBackground(AppColors.PRIMARY_BLUE);
        prevButton.setForeground(AppColors.WHITE_TEXT);
        prevButton.addActionListener(e -> navigateRecords(false));
        JButton nextButton = new JButton("Siguiente");
        nextButton.setBackground(AppColors.PRIMARY_BLUE);
        nextButton.setForeground(AppColors.WHITE_TEXT);
        nextButton.addActionListener(e -> navigateRecords(true));
        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);
        bottomPanel.add(paginationPanel, BorderLayout.CENTER);

        // Panel de Check-out
        JPanel checkOutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        checkOutPanel.setBackground(AppColors.SECONDARY_BLUE);
        JLabel checkOutLabel = new JLabel("ID Registro para Check-Out:");
        checkOutLabel.setForeground(AppColors.DARK_TEXT);
        checkOutLabel.setFont(labelFont);
        checkOutRecordIdField = new JTextField(10);
        JButton checkOutButton = new JButton("Realizar Check-Out");
        checkOutButton.setBackground(AppColors.ACCENT_RED); // Usar el nuevo color para acciones
        checkOutButton.setForeground(AppColors.WHITE_TEXT);
        checkOutButton.addActionListener(e -> checkOutRecord());
        checkOutPanel.add(checkOutLabel);
        checkOutPanel.add(checkOutRecordIdField);
        checkOutPanel.add(checkOutButton);
        bottomPanel.add(checkOutPanel, BorderLayout.SOUTH);


        add(bottomPanel, BorderLayout.SOUTH);

        // Cargar registros al iniciar la aplicación
        loadRecords();
    }

    private void createRecord() {
        String studentId = studentIdField.getText().trim();
        String studentName = studentNameField.getText().trim();
        String bicycleDescription = bicycleDescriptionField.getText().trim();
        String rackIdText = rackIdField.getText().trim();
        String hookText = hookField.getText().trim();

        if (studentId.isEmpty() || studentName.isEmpty() || bicycleDescription.isEmpty() || rackIdText.isEmpty() || hookText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long rackId;
        Long hook;
        try {
            rackId = Long.parseLong(rackIdText);
            hook = Long.parseLong(hookText);
            if (rackId <= 0 || hook <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID de Rack y Gancho deben ser números enteros positivos.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        RecordRequest request = new RecordRequest(studentId, studentName, bicycleDescription, rackId, hook);

        apiClient.createRecord(request)
                .thenAccept(newRecord -> SwingUtilities.invokeLater(() -> {
                    if (newRecord != null) {
                        JOptionPane.showMessageDialog(this,
                                "Registro creado con ID: " + newRecord.getId(),
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        studentIdField.setText("");
                        studentNameField.setText("");
                        bicycleDescriptionField.setText("");
                        rackIdField.setText("");
                        hookField.setText("");
                        currentPageToken = null;
                        loadRecords();
                    }
                }))
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> {
                        handleApiException(ex, "Error al crear registro");
                    });
                    return null;
                });
    }

    private void checkOutRecord() {
        String recordIdText = checkOutRecordIdField.getText().trim();
        if (recordIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del registro para realizar el check-out.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long recordId;
        try {
            recordId = Long.parseLong(recordIdText);
            if (recordId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del registro debe ser un número entero positivo.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        apiClient.checkOutRecord(recordId)
                .thenAccept(updatedRecord -> SwingUtilities.invokeLater(() -> {
                    if (updatedRecord != null) {
                        JOptionPane.showMessageDialog(this,
                                "Registro con ID: " + updatedRecord.getId() + " ha sido marcado como 'Check-Out'.",
                                "Check-Out Exitoso",
                                JOptionPane.INFORMATION_MESSAGE);
                        checkOutRecordIdField.setText("");
                        loadRecords();
                    }
                }))
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> {
                        handleApiException(ex, "Error al realizar check-out");
                    });
                    return null;
                });
    }


    private void loadRecords() {
        String filter = filterField.getText().trim();
        apiClient.getRecords(currentPageToken, PAGE_SIZE, filter)
                .thenAccept(response -> SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            .withZone(ZoneId.systemDefault());

                    for (Record record : response.getRecords()) {
                        Vector<Object> row = new Vector<>();
                        row.add(record.getId());
                        row.add(record.getStudentId());
                        row.add(record.getStudentName());
                        row.add(record.getBicycleDescription());
                        row.add(record.getRack() != null ? record.getRack().getId() : "N/A");
                        row.add(record.getHook());
                        row.add(record.getCheckIn() != null ? formatter.format(record.getCheckIn()) : "N/A");
                        row.add(record.getCheckOut() != null ? formatter.format(record.getCheckOut()) : "Activo");
                        tableModel.addRow(row);
                    }
                    currentPageToken = response.getNextPageToken();
                }))
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> {
                        handleApiException(ex, "Error al cargar registros");
                    });
                    return null;
                });
    }

    private void navigateRecords(boolean isNext) {
        if (isNext) {
            if (currentPageToken == null || currentPageToken.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay más páginas.", "Fin de Registros", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else {
            if (currentPageToken == null) {
                JOptionPane.showMessageDialog(this, "Ya estás en la primera página.", "Inicio de Registros", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "La navegación 'Anterior' solo es posible si la API proporciona un token de página anterior explícito o números de página.\nRecargando la página actual si aplica.", "Navegación", JOptionPane.INFORMATION_MESSAGE);
        }
        loadRecords();
    }


    private void handleApiException(Throwable ex, String title) {
        Throwable cause = ex.getCause();
        if (cause instanceof ApiException) {
            ApiException apiException = (ApiException) cause;
            ApiErrorResponse errorResponse = apiException.getErrorResponse();
            String errorMessage;
            if (errorResponse != null && errorResponse.getError() != null) {
                if (errorResponse.getError() instanceof List) {
                    errorMessage = String.join("\n", (List<String>) errorResponse.getError());
                } else {
                    errorMessage = errorResponse.getError().toString();
                }
                JOptionPane.showMessageDialog(this,
                        "Error de API (" + errorResponse.getStatus() + "): " + errorMessage,
                        title,
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        apiException.getMessage(),
                        title,
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + ex.getMessage() + "\nRevisar consola para más detalles.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SwingApp().setVisible(true);
        });
    }
}