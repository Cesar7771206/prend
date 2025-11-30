package com.poo.prend.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardViewModern extends JPanel {
    
    // Navegación
    public JButton btnNavDashboard, btnNavInventario, btnNavVentas, btnNavClientes, btnSalir;
    
    // Paneles (Cards)
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // --- ELEMENTOS DEL DASHBOARD (HOME) ---
    public JLabel lblTotalVentas, lblTotalClientes, lblTotalProductos;
    public JLabel lblPromedioVenta, lblProductoMasVendido; // Nuevos KPIs
    public SimpleChartPanel chartVentas; // Gráfico personalizado
    
    // --- ELEMENTOS DE INVENTARIO ---
    public JTable tablaInventario;
    public DefaultTableModel modeloInventario;
    public JButton btnAddProducto;

    // --- ELEMENTOS DE CLIENTES ---
    public JTable tablaClientes;
    public DefaultTableModel modeloClientes;
    public JButton btnAddCliente;

    // --- ELEMENTOS DE VENTAS ---
    public JTable tablaVentas;
    public DefaultTableModel modeloVentas;
    public JButton btnNuevaVenta;

    public DashboardViewModern() {
        setLayout(new BorderLayout());
        setBackground(ModernUI.DASH_BG);

        // 1. SIDEBAR
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. CONTENIDO PRINCIPAL (CARD LAYOUT)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ModernUI.DASH_BG);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Agregar las "Páginas"
        contentPanel.add(createPanelResumen(), "DASHBOARD");
        contentPanel.add(createPanelInventario(), "INVENTARIO");
        contentPanel.add(createPanelClientes(), "CLIENTES");
        contentPanel.add(createPanelVentas(), "VENTAS");

        add(contentPanel, BorderLayout.CENTER);
    }

    // --- PANELES DE SECCIONES ---

    private JPanel createPanelResumen() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        // Header
        JLabel title = new JLabel("Resumen del Negocio");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        // Contenido Scrollable
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        // 1. Tarjetas Superiores (KPIs)
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(2000, 140)); // Altura fija
        
        lblTotalVentas = new JLabel("$ 0.00");
        lblTotalClientes = new JLabel("0");
        lblTotalProductos = new JLabel("0");
        
        cardsPanel.add(createMetricCard("Ingresos Totales", lblTotalVentas, ModernUI.ACCENT_GREEN));
        cardsPanel.add(createMetricCard("Clientes Activos", lblTotalClientes, ModernUI.ACCENT_BLUE));
        cardsPanel.add(createMetricCard("Stock Total", lblTotalProductos, Color.ORANGE));
        
        body.add(cardsPanel);
        body.add(Box.createVerticalStrut(20));

        // 2. Gráfico Central
        JPanel graphContainer = ModernUI.createCardPanel();
        graphContainer.setLayout(new BorderLayout());
        graphContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblChartTitle = new JLabel("Tendencia de Ventas (Últimos 5 registros)");
        lblChartTitle.setForeground(Color.LIGHT_GRAY);
        graphContainer.add(lblChartTitle, BorderLayout.NORTH);
        
        chartVentas = new SimpleChartPanel();
        chartVentas.setPreferredSize(new Dimension(0, 250));
        graphContainer.add(chartVentas, BorderLayout.CENTER);
        
        body.add(graphContainer);
        body.add(Box.createVerticalStrut(20));

        // 3. Datos Inferiores (Promedios, etc)
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(2000, 100));

        lblPromedioVenta = new JLabel("$ 0.00");
        lblProductoMasVendido = new JLabel("---");

        statsPanel.add(createMetricCard("Ticket Promedio", lblPromedioVenta, new Color(100, 100, 255)));
        statsPanel.add(createMetricCard("Producto Estrella", lblProductoMasVendido, new Color(255, 100, 100)));

        body.add(statsPanel);

        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        // Header acciones
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Gestión de Inventario");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        btnAddProducto = ModernUI.createPrimaryButton("+ Nuevo Producto");
        btnAddProducto.setBackground(ModernUI.ACCENT_BLUE);
        
        header.add(title, BorderLayout.WEST);
        header.add(btnAddProducto, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Tabla
        modeloInventario = new DefaultTableModel(new Object[]{"ID", "Nombre", "Precio", "Stock", "Categoría"}, 0);
        tablaInventario = new JTable(modeloInventario);
        estilizarTabla(tablaInventario);
        
        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.getViewport().setBackground(ModernUI.DASH_BG);
        scroll.setBorder(BorderFactory.createLineBorder(ModernUI.DASH_CARD));
        
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Directorio de Clientes");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        btnAddCliente = ModernUI.createPrimaryButton("+ Nuevo Cliente");
        btnAddCliente.setBackground(ModernUI.DASH_CARD);
        
        header.add(title, BorderLayout.WEST);
        header.add(btnAddCliente, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        modeloClientes = new DefaultTableModel(new Object[]{"ID", "DNI", "Nombre", "Teléfono", "Compras"}, 0);
        tablaClientes = new JTable(modeloClientes);
        estilizarTabla(tablaClientes);
        
        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.getViewport().setBackground(ModernUI.DASH_BG);
        scroll.setBorder(BorderFactory.createLineBorder(ModernUI.DASH_CARD));
        
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Historial de Ventas");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        btnNuevaVenta = ModernUI.createPrimaryButton("+ Registrar Venta");
        btnNuevaVenta.setBackground(ModernUI.ACCENT_GREEN);
        
        header.add(title, BorderLayout.WEST);
        header.add(btnNuevaVenta, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        modeloVentas = new DefaultTableModel(new Object[]{"ID Pedido", "Fecha", "Cliente", "Total", "Estado"}, 0);
        tablaVentas = new JTable(modeloVentas);
        estilizarTabla(tablaVentas);
        
        JScrollPane scroll = new JScrollPane(tablaVentas);
        scroll.getViewport().setBackground(ModernUI.DASH_BG);
        scroll.setBorder(BorderFactory.createLineBorder(ModernUI.DASH_CARD));
        
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // --- COMPONENTES AUXILIARES ---

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(ModernUI.DASH_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblLogo = new JLabel("PREND");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 28));
        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(40));

        // Botones de Navegación
        btnNavDashboard = createSidebarButton("Dashboard");
        btnNavInventario = createSidebarButton("Inventario");
        btnNavClientes = createSidebarButton("Clientes");
        btnNavVentas = createSidebarButton("Ventas");

        sidebar.add(btnNavDashboard);
        sidebar.add(btnNavInventario);
        sidebar.add(btnNavClientes);
        sidebar.add(btnNavVentas);
        
        sidebar.add(Box.createVerticalGlue());
        
        btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setForeground(new Color(255, 100, 100));
        btnSalir.setContentAreaFilled(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sidebar.add(btnSalir);

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.LIGHT_GRAY);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover simple
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.LIGHT_GRAY);
            }
        });
        return btn;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = ModernUI.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setFont(ModernUI.FONT_SMALL);
        
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void estilizarTabla(JTable table) {
        table.setBackground(ModernUI.DASH_CARD);
        table.setForeground(Color.WHITE);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(ModernUI.DASH_SIDEBAR);
        header.setForeground(Color.LIGHT_GRAY);
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

    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(contentPanel, nombrePanel);
    }

    // --- CLASE INTERNA PARA GRÁFICO PERSONALIZADO ---
    public static class SimpleChartPanel extends JPanel {
        private List<Double> values;
        private List<String> labels;

        public SimpleChartPanel() {
            this.values = new ArrayList<>();
            this.labels = new ArrayList<>();
            setOpaque(false);
        }

        public void setData(List<Double> values, List<String> labels) {
            this.values = values;
            this.labels = labels;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (values.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 30;
            int barWidth = (width - 2 * padding) / values.size() - 20;
            
            // Encontrar valor máximo para escalar
            double maxVal = values.stream().mapToDouble(v -> v).max().orElse(1.0);
            if(maxVal == 0) maxVal = 1;

            // Ejes
            g2.setColor(Color.GRAY);
            g2.drawLine(padding, height - padding, width - padding, height - padding); // Eje X
            g2.drawLine(padding, height - padding, padding, padding); // Eje Y

            // Dibujar barras
            for (int i = 0; i < values.size(); i++) {
                int barHeight = (int) ((values.get(i) / maxVal) * (height - 2 * padding));
                int x = padding + 10 + i * (barWidth + 20);
                int y = height - padding - barHeight;

                // Barra
                g2.setColor(ModernUI.ACCENT_BLUE);
                g2.fillRoundRect(x, y, barWidth, barHeight, 10, 10);

                // Etiqueta Valor
                g2.setColor(Color.WHITE);
                String valStr = String.valueOf(values.get(i).intValue());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2, y - 5);

                // Etiqueta Eje X
                g2.setColor(Color.LIGHT_GRAY);
                String lbl = labels.get(i);
                g2.drawString(lbl, x + (barWidth - fm.stringWidth(lbl)) / 2, height - padding + 15);
            }
        }
    }
}