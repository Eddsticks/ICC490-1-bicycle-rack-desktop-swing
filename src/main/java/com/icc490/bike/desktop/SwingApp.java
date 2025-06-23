package com.icc490.bike.desktop;

import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.model.RecordRequest;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SwingApp extends JFrame {
    private ApiClient apiClient;
    private JTextArea recordDisplayArea;
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField bicycleDescriptionField;

    //Colores
    private static final Color PRIMARY_BLUE = new Color(50,100,160);
    private static final Color SECONDARY_BLUE = new Color(220,230,200);
    private static final Color WHITE_TEXT = Color.WHITE;
    private static final Color DARK_TEXT = Color.BLACK;
    private static final Color LIGHT_GRAY_BORDER = new Color(200,200,200);

    public SwingApp() {
        super("Gestión de Rack de Bicicletas UFRO");
        apiClient = new ApiClient();

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Look and Fell no pudo ser Establecido. Usando predet.");
        }
        SwingUtilities.updateComponentTreeUI(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700,600);
        setLocationRelativeTo(null);

        //Paneles y Componentes

        //Principal:
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        mainPanel.setBackground(Color.WHITE);

        //Visualización de registros:
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder("Registro de Bicicletas"));

        recordDisplayArea = new JTextArea();
        recordDisplayArea.setEditable(false);
        recordDisplayArea.setLineWrap(true);
        recordDisplayArea.setWrapStyleWord(true);
        recordDisplayArea.setBackground(Color.WHITE);
        recordDisplayArea.setForeground(DARK_TEXT);

        JScrollPane scrollPane = new JScrollPane(recordDisplayArea);
        scrollPane.setPreferredSize(new Dimension(650, 250));
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        JButton loadRecordsButton = new JButton("Cargar Registros");
        loadRecordsButton.addActionListener(e ->  loadRecords());

        loadRecordsButton.setBackground(PRIMARY_BLUE);
        loadRecordsButton.setForeground(WHITE_TEXT);
        loadRecordsButton.setFocusPainted(false);
        displayPanel.add(loadRecordsButton, BorderLayout.SOUTH);

        //Creación de registros:
        JPanel createPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        createPanel.setBorder(BorderFactory.createTitledBorder("Crear Nuevo Registro"));
        createPanel.setBackground(Color.WHITE);

        JLabel studentIdLabel = new JLabel("ID Estudiante:");
        studentIdLabel.setForeground(DARK_TEXT);
        JLabel studentNameLabel = new JLabel("Nombre Estudiante:");
        studentNameLabel.setForeground(DARK_TEXT);
        JLabel bicycleDescriptionLabel = new JLabel("Descripción Bicicleta:");
        bicycleDescriptionLabel.setForeground(DARK_TEXT);

        studentIdField = new JTextField(20);
        studentIdField.setBackground(Color.WHITE);
        studentIdField.setForeground(DARK_TEXT);
        studentIdLabel.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY_BORDER));

        studentNameField = new JTextField(20);
        studentNameField.setBackground(Color.WHITE);
        studentNameField.setForeground(DARK_TEXT);
        studentNameField.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY_BORDER));

        bicycleDescriptionField = new JTextField(20);
        bicycleDescriptionField.setBackground(Color.white);
        bicycleDescriptionField.setForeground(DARK_TEXT);
        bicycleDescriptionField.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY_BORDER));

        createPanel.add(studentIdLabel);
        createPanel.add(studentIdField);
        createPanel.add(studentNameLabel);
        createPanel.add(studentNameField);
        createPanel.add(bicycleDescriptionLabel);
        createPanel.add(bicycleDescriptionField);

        JButton createRecordButton = new JButton("Crear Nuevo Registro");
        createRecordButton.addActionListener(e -> createRecord());
        createRecordButton.setBackground(PRIMARY_BLUE);
        createRecordButton.setForeground(WHITE_TEXT);
        createRecordButton.setFocusPainted(false);
        createPanel.add(createRecordButton);
        createPanel.add(new JPanel());

        //Adiciones panel principal:
        mainPanel.add(displayPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(new JSeparator());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createPanel);

        add(mainPanel);

        SwingUtilities.invokeLater(this::loadRecords);
    }

    /**
     * Muestra registros de biciletas desde la API
     */
    private void loadRecords() {
        recordDisplayArea.setText("Cargando registros desde API...");
        apiClient.getAllRecords().thenAccept(records -> {
            SwingUtilities.invokeLater(() -> {
                if (records != null && !records.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Object record : records) {
                        sb.append(record.toString()).append("\n");
                    }
                    recordDisplayArea.setText(sb.toString());
                } else {
                    recordDisplayArea.setText("No hay registros disponibles.");
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                recordDisplayArea.setText("Error al cargar registors: " + ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        "No fue posible cargar los registros. Asegurarse del funcionamiento de la API en " + ApiClient.BASE_URL,
                        "Error de Carga",
                        JOptionPane.ERROR_MESSAGE);
            });
            ex.printStackTrace();
            return null;
        });
    }

    /**
     * Envía una solicitud para crear un nuevo registro en la API.
     */
    private void createRecord() {
        String studentId = studentIdField.getText().trim();
        String studentName = studentNameField.getText().trim();
        String bicycleDescription = bicycleDescriptionField.getText().trim();

        if (studentId.isEmpty() || studentName.isEmpty() || bicycleDescription.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Error de entrada.",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        RecordRequest request = new RecordRequest(studentId, studentName, bicycleDescription);

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
                    loadRecords();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No fue posible crear el registro.\nRevisar consola para más detalles.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        }).exceptionally(ex -> {
           SwingUtilities.invokeLater(() -> {
               JOptionPane.showMessageDialog(this,
                       "Error al crear registro: " + ex.getMessage() + "\nRevisar consola para más detalles.",
                       "Error de API",
                       JOptionPane.ERROR_MESSAGE);
           });
           ex.printStackTrace();
           return null;
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SwingApp().setVisible(true);
        });
    }
}
