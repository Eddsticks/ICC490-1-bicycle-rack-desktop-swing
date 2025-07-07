package com.icc490.bike.desktop.panels;

import com.icc490.bike.desktop.ApiClient;
import com.icc490.bike.desktop.model.RecordRequest;

import javax.swing.*;
import java.awt.*;

public class CreateRecordPanel extends JPanel {
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField bicycleDescriptionField;
    private JTextField rackIdField; // Nuevo campo
    private JTextField hookField;   // Nuevo campo
    private JButton createRecordButton;

    private ApiClient apiClient;
    private RecordCreationListener recordCreationListener;

    private static final Color PRIMARY_BLUE = new Color(50,100,160);
    private static final Color WHITE_TEXT = Color.WHITE;
    private static final Color DARK_TEXT = Color.BLACK;
    private static final Color LIGHT_GRAY_BORDER = new Color(200,200,200);

    public CreateRecordPanel(ApiClient apiClient, RecordCreationListener listener) {
        this.apiClient = apiClient;
        this.recordCreationListener = listener;
        initComponents();
        setupLayout();
        addListeners();
    }

    private void initComponents() {
        studentIdField = new JTextField(20);
        studentNameField = new JTextField(20);
        bicycleDescriptionField = new JTextField(20);
        rackIdField = new JTextField(5); // Tamaño más pequeño para IDs numéricos
        hookField = new JTextField(5);   // Tamaño más pequeño para IDs numéricos
        createRecordButton = new JButton("Registrar Bicicleta");

        // Estilos
        createRecordButton.setBackground(PRIMARY_BLUE);
        createRecordButton.setForeground(WHITE_TEXT);
        createRecordButton.setFont(new Font("Arial", Font.BOLD, 14));
        createRecordButton.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY_BORDER, 2));

        studentIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY_BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        studentNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY_BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        bicycleDescriptionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY_BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        rackIdField.setBorder(BorderFactory.createCompoundBorder( // Estilo para rackIdField
                BorderFactory.createLineBorder(LIGHT_GRAY_BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        hookField.setBorder(BorderFactory.createCompoundBorder(   // Estilo para hookField
                BorderFactory.createLineBorder(LIGHT_GRAY_BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Margen entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título del formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Registrar Nueva Bicicleta", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(DARK_TEXT);
        add(titleLabel, gbc);

        // Campos del formulario
        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("ID Estudiante:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        add(studentIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Nombre Estudiante:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        add(studentNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Descripción Bicicleta:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        add(bicycleDescriptionField, gbc);

        // Nuevos campos Rack ID y Hook
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("ID Rack:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        add(rackIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Hook (Gancho):", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        add(hookField, gbc);

        // Botón Registrar
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(createRecordButton, gbc);

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_BLUE), "Registro"));
    }

    private void addListeners() {
        createRecordButton.addActionListener(e -> createRecord());
    }

    private void createRecord() {
        String studentId = studentIdField.getText();
        String studentName = studentNameField.getText();
        String bicycleDescription = bicycleDescriptionField.getText();
        String rackIdText = rackIdField.getText();
        String hookText = hookField.getText();

        if (studentId.isEmpty() || studentName.isEmpty() || bicycleDescription.isEmpty() || rackIdText.isEmpty() || hookText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios para registrar una bicicleta.",
                    "Error de Entrada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long rackId;
        Long hook;
        try {
            rackId = Long.parseLong(rackIdText);
            hook = Long.parseLong(hookText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Los campos 'ID Rack' y 'Hook (Gancho)' deben ser números válidos.",
                    "Error de Formato",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        RecordRequest request = new RecordRequest(studentId, studentName, bicycleDescription, rackId, hook);

        apiClient.createRecord(request).thenAccept(newRecord -> {
            SwingUtilities.invokeLater(() -> {
                if (newRecord != null) {
                    JOptionPane.showMessageDialog(this,
                            "Registro creado con ID: " + newRecord.getId(),
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    studentIdField.setText("");
                    studentNameField.setText("");
                    bicycleDescriptionField.setText("");
                    rackIdField.setText(""); // Limpiar campo
                    hookField.setText("");   // Limpiar campo
                    if (recordCreationListener != null) {
                        recordCreationListener.onRecordCreated();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No fue posible crear el registro.\\nRevisar consola para más detalles.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                String errorMessage = "Error al crear registro: " + ex.getMessage();
                Throwable cause = ex.getCause();
                if (cause != null && cause.getMessage() != null) {
                    errorMessage = "Error al crear registro: " + cause.getMessage().replace("java.net.http.HttpTimeoutException: HTTP exchange timed out", "Error de conexión con el servidor. Verifique que la API esté en ejecución y sea accesible.");
                }

                JOptionPane.showMessageDialog(this,
                        errorMessage + "\\nRevisar consola para más detalles.",
                        "Error de API",
                        JOptionPane.ERROR_MESSAGE);
            });
            ex.printStackTrace();
            return null;
        });
    }

    public interface RecordCreationListener {
        void onRecordCreated();
    }
}