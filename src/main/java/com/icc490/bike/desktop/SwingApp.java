package com.icc490.bike.desktop;

import com.icc490.bike.desktop.panels.CreateRecordPanel;
import com.icc490.bike.desktop.panels.CreateRecordPanel.RecordCreationListener;
import com.icc490.bike.desktop.panels.RecordTablePanel;
import com.icc490.bike.desktop.panels.RecordTablePanel.RecordActionListener;
import com.icc490.bike.desktop.panels.CheckOutPanel;
import com.icc490.bike.desktop.gui.utils.AppColors;

import javax.swing.*;
import java.awt.*;

public class SwingApp extends JFrame implements RecordCreationListener, RecordActionListener {
    private ApiClient apiClient;

    private CreateRecordPanel createRecordPanel;
    private CheckOutPanel checkOutPanel;
    private RecordTablePanel recordTablePanel;

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
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initUI();
        loadRecords();
        this.setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(AppColors.LIGHT_GRAY_BORDER);

        // Panel lateral izquierdo para creación y check-out
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setBackground(AppColors.SECONDARY_BLUE);

        createRecordPanel = new CreateRecordPanel(apiClient, this);
        checkOutPanel = new CheckOutPanel(apiClient, this);

        leftPanel.add(createRecordPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(checkOutPanel);
        leftPanel.add(Box.createVerticalGlue());

        // Panel central para la tabla de registros
        recordTablePanel = new RecordTablePanel(apiClient, this);

        // Panel inferior para el botón de recargar
        JButton refreshButton = new JButton("Recargar Registros");
        refreshButton.setBackground(AppColors.PRIMARY_BLUE);
        refreshButton.setForeground(AppColors.WHITE_TEXT);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadRecords());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(AppColors.SECONDARY_BLUE);
        bottomPanel.add(refreshButton);

        add(leftPanel, BorderLayout.WEST);
        add(new JScrollPane(recordTablePanel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadRecords() {
        recordTablePanel.loadRecords();
    }

    @Override
    public void onRecordCreated() {
        loadRecords();
    }

    @Override
    public void onRecordCheckedOut() {
        loadRecords();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingApp::new);
    }
}