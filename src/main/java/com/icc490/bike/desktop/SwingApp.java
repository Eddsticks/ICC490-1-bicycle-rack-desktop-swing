// src/main/java/com/icc490/bike/desktop/SwingApp.java
package com.icc490.bike.desktop;

import com.icc490.bike.desktop.panels.CreateRecordPanel;
import com.icc490.bike.desktop.model.Record; // Necesario para loadRecords
import com.icc490.bike.desktop.panels.CreateRecordPanel.RecordCreationListener; // Importar la interfaz

import javax.swing.*;
import java.awt.*;
import java.util.List; // Necesario para manejar la lista de records

public class SwingApp extends JFrame implements RecordCreationListener { // Implementa la interfaz
    private ApiClient apiClient;

    // Colores (puedes considerar moverlos a una clase de constantes si se usan en más lugares)
    private static final Color PRIMARY_BLUE = new Color(50, 100, 160);
    private static final Color SECONDARY_BLUE = new Color(220, 230, 200);
    private static final Color WHITE_TEXT = Color.WHITE;
    private static final Color DARK_TEXT = Color.BLACK;
    private static final Color LIGHT_GRAY_BORDER = new Color(200, 200, 200);

    // Paneles
    private CreateRecordPanel createRecordPanel;
    // Otros paneles irán aquí: RecordTablePanel, FilterAndPaginationPanel, CheckOutPanel

    // Componentes que aún no están en paneles dedicados
    private JTextArea recordDisplayArea; // Esto se moverá a RecordTablePanel

    public SwingApp() {
        super("Gestión de Rack de Bicicletas UFRO");
        apiClient = new ApiClient();

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Look and Feel no pudo ser Establecido. Usando predeterminado.");
        }
        SwingUtilities.updateComponentTreeUI(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null); // Centrar la ventana

        initUI();
        loadRecords(); // Cargar registros al iniciar
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10)); // Espaciado entre componentes principales
        getContentPane().setBackground(SECONDARY_BLUE);

        // Panel de creación de registros (ahora una instancia de CreateRecordPanel)
        createRecordPanel = new CreateRecordPanel(apiClient, this); // 'this' como listener
        add(createRecordPanel, BorderLayout.NORTH);

        // Área de visualización de registros (temporalmente aquí, se moverá a RecordTablePanel)
        recordDisplayArea = new JTextArea();
        recordDisplayArea.setEditable(false);
        recordDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(recordDisplayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_BLUE), "Registros Actuales"));
        add(scrollPane, BorderLayout.CENTER);

        // Aquí irían los otros paneles (tabla, filtros, checkout)
        // Ejemplo (aún no implementados):
        // recordTablePanel = new RecordTablePanel(apiClient);
        // add(recordTablePanel, BorderLayout.CENTER); // Esto reemplazaría recordDisplayArea

        // filterAndPaginationPanel = new FilterAndPaginationPanel(apiClient, this);
        // add(filterAndPaginationPanel, BorderLayout.SOUTH);

        // checkOutPanel = new CheckOutPanel(apiClient, this);
        // add(checkOutPanel, BorderLayout.EAST);
    }

    // Método para cargar y mostrar los registros
    // Este método eventualmente se moverá al RecordTablePanel, pero SwingApp lo llamará.
    private void loadRecords() {
        apiClient.getAllRecords().thenAccept(records -> {
            SwingUtilities.invokeLater(() -> {
                recordDisplayArea.setText(""); // Limpiar el área antes de actualizar
                if (records != null && !records.isEmpty()) {
                    for (Object obj : records) {
                        if (obj instanceof Record) {
                            Record record = (Record) obj;
                            recordDisplayArea.append(record.toString() + "\\n");
                        }
                    }
                } else {
                    recordDisplayArea.setText("No hay registros de bicicletas.");
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "Error al cargar registros: " + ex.getMessage() + "\\nRevisar consola para más detalles.",
                        "Error de API",
                        JOptionPane.ERROR_MESSAGE);
            });
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public void onRecordCreated() {
        loadRecords(); // Cuando un registro es creado en CreateRecordPanel, recargar la tabla
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SwingApp().setVisible(true);
        });
    }
}