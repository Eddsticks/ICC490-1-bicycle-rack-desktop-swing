package com.icc490.bike.desktop;

import com.icc490.bike.desktop.panels.CreateRecordPanel;
import com.icc490.bike.desktop.panels.CreateRecordPanel.RecordCreationListener;
import com.icc490.bike.desktop.panels.RecordTablePanel;
import com.icc490.bike.desktop.panels.RecordTablePanel.RecordActionListener;
import com.icc490.bike.desktop.gui.utils.AppColors;

import javax.swing.*;
import java.awt.*;

public class AdminDesktopApp extends JFrame implements RecordCreationListener, RecordActionListener {
    private ApiClient apiClient;

    private static final Color PRIMARY_BLUE = AppColors.PRIMARY_BLUE;
    private static final Color SECONDARY_BLUE = AppColors.SECONDARY_BLUE;

    private CreateRecordPanel createRecordPanel;
    private RecordTablePanel recordTablePanel;

    public AdminDesktopApp() {
        super("Gestión de Rack de Bicicletas UFRO");
        apiClient = new ApiClient();

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Look and Feel no pudo ser Establecido. Usando predeterminado.");
        }
        SwingUtilities.updateComponentTreeUI(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initUI();
        loadRecords();
        this.setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(AppColors.LIGHT_GRAY_BORDER);

        createRecordPanel = new CreateRecordPanel(apiClient, this);
        add(createRecordPanel, BorderLayout.NORTH);

        recordTablePanel = new RecordTablePanel(apiClient, this);
        add(recordTablePanel, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Recargar Registros");
        refreshButton.setBackground(PRIMARY_BLUE);
        refreshButton.setForeground(AppColors.WHITE_TEXT);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadRecords());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(SECONDARY_BLUE);
        bottomPanel.add(refreshButton);
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
        SwingUtilities.invokeLater(AdminDesktopApp::new);
    }
}