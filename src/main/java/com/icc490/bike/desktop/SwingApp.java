package com.icc490.bike.desktop;

import com.icc490.bike.desktop.panels.CreateRecordPanel;
import com.icc490.bike.desktop.panels.CreateRecordPanel.RecordCreationListener;
import com.icc490.bike.desktop.panels.RecordTablePanel;
import com.icc490.bike.desktop.panels.CheckOutPanel;
import com.icc490.bike.desktop.panels.CheckOutPanel.CheckOutListener;

import javax.swing.*;
import java.awt.*;

public class SwingApp extends JFrame implements RecordCreationListener, CheckOutListener {
    private ApiClient apiClient;
    private CheckOutPanel checkOutPanel;

    private static final Color PRIMARY_BLUE = new Color(50, 100, 160);
    private static final Color SECONDARY_BLUE = new Color(220, 230, 200);


    private CreateRecordPanel createRecordPanel;
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
        setSize(800, 600);
        setLocationRelativeTo(null);

        initUI();
        loadRecords();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(SECONDARY_BLUE);

        createRecordPanel = new CreateRecordPanel(apiClient, this);
        add(createRecordPanel, BorderLayout.NORTH);

        recordTablePanel = new RecordTablePanel(apiClient);
        add(recordTablePanel, BorderLayout.CENTER);

        checkOutPanel = new CheckOutPanel(apiClient, this);
        add(checkOutPanel, BorderLayout.SOUTH);

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
        SwingUtilities.invokeLater(() -> {
            new SwingApp().setVisible(true);
        });
    }
}