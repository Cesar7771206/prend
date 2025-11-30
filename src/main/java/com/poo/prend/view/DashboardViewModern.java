package com.poo.prend.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class DashboardViewModern extends JPanel {
    
    public JLabel lblTotalVentas, lblTotalClientes, lblTotalProductos;
    public JTable tablaInventario;
    public DefaultTableModel modeloTabla;
    public JButton btnAddProducto, btnAddCliente, btnSalir;

    public DashboardViewModern() {
        setLayout(new BorderLayout());
        setBackground(ModernUI.DASH_BG);

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(ModernUI.DASH_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblLogo = new JLabel("PREND");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(40));

        sidebar.add(createSidebarItem("Dashboard", true));
        sidebar.add(createSidebarItem("Inventario", false));
        sidebar.add(createSidebarItem("Ventas", false));
        sidebar.add(createSidebarItem("Clientes", false));
        sidebar.add(Box.createVerticalGlue());
        
        btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setForeground(Color.GRAY);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(btnSalir);

        add(sidebar, BorderLayout.WEST);

        // --- CONTENIDO PRINCIPAL ---
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(ModernUI.DASH_BG);
        mainContent.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("Resumen General");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);
        
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerActions.setOpaque(false);
        btnAddProducto = ModernUI.createPrimaryButton("+ Producto");
        btnAddProducto.setBackground(ModernUI.ACCENT_BLUE);
        btnAddCliente = ModernUI.createPrimaryButton("+ Cliente");
        btnAddCliente.setBackground(ModernUI.DASH_CARD);
        headerActions.add(btnAddCliente);
        headerActions.add(btnAddProducto);
        header.add(headerActions, BorderLayout.EAST);

        mainContent.add(header, BorderLayout.NORTH);

        // Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(0, 120));

        lblTotalVentas = new JLabel("$ 0.00");
        cardsPanel.add(createMetricCard("Ingresos Totales", lblTotalVentas, ModernUI.ACCENT_GREEN));
        
        lblTotalClientes = new JLabel("0");
        cardsPanel.add(createMetricCard("Clientes Activos", lblTotalClientes, ModernUI.ACCENT_BLUE));
        
        lblTotalProductos = new JLabel("0");
        cardsPanel.add(createMetricCard("Productos en Stock", lblTotalProductos, Color.ORANGE));

        // Centro (Tabla)
        JPanel centerSection = new JPanel(new BorderLayout(0, 20));
        centerSection.setOpaque(false);
        centerSection.add(cardsPanel, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Producto");
        modeloTabla.addColumn("Precio");
        modeloTabla.addColumn("Stock");
        
        tablaInventario = new JTable(modeloTabla);
        estilizarTabla(tablaInventario);

        JScrollPane scrollPane = new JScrollPane(tablaInventario);
        scrollPane.getViewport().setBackground(ModernUI.DASH_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernUI.DASH_CARD));
        
        JPanel tableContainer = ModernUI.createCardPanel();
        tableContainer.setLayout(new BorderLayout());
        JLabel lblTable = new JLabel("Inventario Reciente");
        lblTable.setForeground(Color.WHITE);
        lblTable.setFont(ModernUI.FONT_BOLD);
        lblTable.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        tableContainer.add(lblTable, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        centerSection.add(tableContainer, BorderLayout.CENTER);

        mainContent.add(centerSection, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private JLabel createSidebarItem(String text, boolean active) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 16));
        label.setForeground(active ? ModernUI.ACCENT_BLUE : Color.GRAY);
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return label;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color graphColor) {
        JPanel card = ModernUI.createCardPanel();
        card.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setFont(ModernUI.FONT_SMALL);
        
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        JPanel graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(graphColor);
                g2.setStroke(new BasicStroke(2));
                int w = getWidth(), h = getHeight();
                g2.drawPolyline(new int[]{0, w/4, w/2, 3*w/4, w}, new int[]{h, h-10, h-5, h-15, h-20}, 5);
            }
        };
        graphPanel.setOpaque(false);
        graphPanel.setPreferredSize(new Dimension(0, 30));
        card.add(graphPanel, BorderLayout.SOUTH);

        return card;
    }

    private void estilizarTabla(JTable table) {
        table.setBackground(ModernUI.DASH_CARD);
        table.setForeground(Color.WHITE);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(ModernUI.DASH_BG);
        header.setForeground(Color.GRAY);
        header.setFont(ModernUI.FONT_BOLD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setBackground(ModernUI.DASH_CARD);
        centerRenderer.setForeground(Color.WHITE);
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}