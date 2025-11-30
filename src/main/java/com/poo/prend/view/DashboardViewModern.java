package com.poo.prend.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class DashboardViewModern extends JPanel {
    
    // Navegación
    public JButton btnNavDashboard, btnNavInventario, btnNavPedidos, btnNavVentas, btnNavClientes, btnSalir;
    
    // Paneles (Cards)
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // --- DASHBOARD (HOME) ---
    public JLabel lblTotalVentas, lblTotalClientes, lblTotalProductos;
    public JLabel lblPromedioVenta, lblProductoMasVendido;
    
    // Gráficos Reales
    public ModernChartPanel chartIngresos;
    public ModernChartPanel chartPedidos;
    public ModernChartPanel chartMetodos;
    
    // --- INVENTARIO ---
    public JTable tablaInventario;
    public DefaultTableModel modeloInventario;
    public JButton btnAddProducto, btnEditProducto, btnDelProducto;

    // --- CLIENTES ---
    public JTable tablaClientes;
    public DefaultTableModel modeloClientes;
    public JButton btnAddCliente, btnEditCliente, btnDelCliente;

    // --- PEDIDOS (NUEVO: PENDIENTES) ---
    public JTable tablaPedidos;
    public DefaultTableModel modeloPedidos;
    public JButton btnNuevoPedido;
    public JButton btnCompletarPedido; // Acción clave
    public JButton btnCancelarPedido;

    // --- VENTAS (HISTORIAL: SOLO LECTURA) ---
    public JTable tablaVentas;
    public DefaultTableModel modeloVentas;
    
    public DashboardViewModern() {
        setLayout(new BorderLayout());
        setBackground(ModernUI.DASH_BG);

        // Sidebar
        add(createSidebar(), BorderLayout.WEST);

        // Contenido
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ModernUI.DASH_BG);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentPanel.add(createPanelResumen(), "DASHBOARD");
        contentPanel.add(createPanelInventario(), "INVENTARIO");
        contentPanel.add(createPanelClientes(), "CLIENTES");
        contentPanel.add(createPanelPedidos(), "PEDIDOS");
        contentPanel.add(createPanelVentas(), "VENTAS");

        add(contentPanel, BorderLayout.CENTER);
    }

    // --- PANELES ---
    private JPanel createPanelResumen() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        
        JLabel title = new JLabel("Resumen y Estadísticas en Tiempo Real");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        // KPI Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(2000, 120));
        
        lblTotalVentas = new JLabel("$ 0.00");
        lblTotalClientes = new JLabel("0");
        lblTotalProductos = new JLabel("0");
        
        cardsPanel.add(createMetricCard("Ingresos Totales", lblTotalVentas, ModernUI.ACCENT_GREEN));
        cardsPanel.add(createMetricCard("Clientes Registrados", lblTotalClientes, ModernUI.ACCENT_BLUE));
        cardsPanel.add(createMetricCard("Inventario (Unidades)", lblTotalProductos, Color.ORANGE));
        
        body.add(cardsPanel);
        body.add(Box.createVerticalStrut(20));

        // Gráficos Reales
        JPanel chartsContainer = new JPanel(new GridLayout(1, 3, 15, 0));
        chartsContainer.setOpaque(false);
        chartsContainer.setPreferredSize(new Dimension(0, 300));
        chartsContainer.setMaximumSize(new Dimension(2000, 300));

        chartIngresos = new ModernChartPanel(ModernChartPanel.Type.BAR, ModernUI.ACCENT_GREEN);
        chartPedidos = new ModernChartPanel(ModernChartPanel.Type.LINE, ModernUI.ACCENT_BLUE);
        chartMetodos = new ModernChartPanel(ModernChartPanel.Type.BAR, Color.ORANGE);

        chartsContainer.add(createChartWrapper("Ingresos por Venta", chartIngresos));
        chartsContainer.add(createChartWrapper("Volumen de Pedidos", chartPedidos));
        chartsContainer.add(createChartWrapper("Stock por Categoría", chartMetodos));

        body.add(chartsContainer);
        body.add(Box.createVerticalStrut(20));
        
        // Stats inferiores
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(2000, 80));
        
        lblPromedioVenta = new JLabel("$ 0.00");
        lblProductoMasVendido = new JLabel("---");
        statsPanel.add(createMetricCard("Ticket Promedio", lblPromedioVenta, new Color(100, 100, 255)));
        statsPanel.add(createMetricCard("Producto Estrella", lblProductoMasVendido, new Color(255, 100, 100)));
        body.add(statsPanel);

        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPanelInventario() {
        JPanel panel = createBasePanel("Gestión de Inventario");
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnDelProducto = createActionButton("Eliminar", new Color(200, 60, 60));
        btnEditProducto = createActionButton("Editar", new Color(255, 165, 0));
        btnAddProducto = createActionButton("+ Nuevo", ModernUI.ACCENT_BLUE);
        
        btnPanel.add(btnDelProducto);
        btnPanel.add(btnEditProducto);
        btnPanel.add(btnAddProducto);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        ((JPanel)panel.getComponent(0)).add(new JLabel("Gestión de Inventario"), BorderLayout.WEST); // Restaurar titulo
        
        modeloInventario = new DefaultTableModel(new Object[]{"ID", "Nombre", "Descripción", "Precio", "Stock", "Categoría"}, 0);
        tablaInventario = new JTable(modeloInventario);
        estilizarTabla(tablaInventario);
        panel.add(new JScrollPane(tablaInventario), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPanelClientes() {
        JPanel panel = createBasePanel("Directorio de Clientes");
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnDelCliente = createActionButton("Eliminar", new Color(200, 60, 60));
        btnEditCliente = createActionButton("Editar", new Color(255, 165, 0));
        btnAddCliente = createActionButton("+ Nuevo", ModernUI.DASH_CARD);
        
        btnPanel.add(btnDelCliente);
        btnPanel.add(btnEditCliente);
        btnPanel.add(btnAddCliente);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        
        modeloClientes = new DefaultTableModel(new Object[]{"ID", "DNI", "Nombre", "Teléfono", "Dirección", "Calif."}, 0);
        tablaClientes = new JTable(modeloClientes);
        estilizarTabla(tablaClientes);
        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPanelPedidos() {
        JPanel panel = createBasePanel("Pedidos Activos");
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        
        btnCancelarPedido = createActionButton("Cancelar", new Color(200, 60, 60));
        btnCompletarPedido = createActionButton("✓ Completar / Pagar", ModernUI.ACCENT_GREEN);
        btnNuevoPedido = createActionButton("+ Nuevo Pedido", ModernUI.ACCENT_BLUE);
        
        btnPanel.add(btnCancelarPedido);
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(btnCompletarPedido);
        btnPanel.add(btnNuevoPedido);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        
        modeloPedidos = new DefaultTableModel(new Object[]{"ID", "Fecha", "Cliente", "Total ($)", "Estado"}, 0);
        tablaPedidos = new JTable(modeloPedidos);
        estilizarTabla(tablaPedidos);
        panel.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPanelVentas() {
        JPanel panel = createBasePanel("Historial de Ventas (Solo Lectura)");
        
        modeloVentas = new DefaultTableModel(new Object[]{"ID Venta/Pedido", "Fecha", "Cliente", "Total ($)", "Método Pago"}, 0);
        tablaVentas = new JTable(modeloVentas);
        tablaVentas.setEnabled(false); // Solo lectura
        estilizarTabla(tablaVentas);
        panel.add(new JScrollPane(tablaVentas), BorderLayout.CENTER);
        return panel;
    }

    // --- UTILS ---
    private JPanel createBasePanel(String titleText) {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        // Header placeholder, el contenido se sobreescribe en métodos específicos
        // Para simplificar, devolvemos el panel base
        return panel; 
    }
    
    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        return btn;
    }

    private JPanel createChartWrapper(String title, JPanel chart) {
        JPanel wrapper = ModernUI.createCardPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(chart, BorderLayout.CENTER);
        return wrapper;
    }
    
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

        btnNavDashboard = createSidebarButton("Dashboard");
        btnNavPedidos = createSidebarButton("Pedidos"); // NEW
        btnNavInventario = createSidebarButton("Inventario");
        btnNavClientes = createSidebarButton("Clientes");
        btnNavVentas = createSidebarButton("Historial Ventas");

        sidebar.add(btnNavDashboard);
        sidebar.add(btnNavPedidos);
        sidebar.add(btnNavInventario);
        sidebar.add(btnNavClientes);
        sidebar.add(btnNavVentas);
        
        sidebar.add(Box.createVerticalGlue());
        btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setForeground(new Color(255, 100, 100));
        btnSalir.setContentAreaFilled(false);
        btnSalir.setBorderPainted(false);
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
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setForeground(Color.WHITE); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setForeground(Color.LIGHT_GRAY); }
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
        JTableHeader header = table.getTableHeader();
        header.setBackground(ModernUI.DASH_SIDEBAR);
        header.setForeground(Color.LIGHT_GRAY);
        header.setFont(ModernUI.FONT_BOLD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setBackground(ModernUI.DASH_CARD);
        centerRenderer.setForeground(Color.WHITE);
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(contentPanel, nombrePanel);
    }

    // --- CLASE DE GRÁFICOS PERSONALIZADOS (SIMULA LIBRERÍA REAL) ---
    public static class ModernChartPanel extends JPanel {
        public enum Type { BAR, LINE }
        private Type type;
        private List<Double> values = new ArrayList<>();
        private List<String> labels = new ArrayList<>();
        private Color color;

        public ModernChartPanel(Type type, Color color) {
            this.type = type;
            this.color = color;
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
            if(values.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int pad = 30;
            
            // Ejes
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(pad, h - pad, w - pad, h - pad); // X
            g2.drawLine(pad, pad, pad, h - pad); // Y

            double max = values.stream().mapToDouble(v->v).max().orElse(1);
            if(max == 0) max = 1;
            
            int stepX = (w - 2 * pad) / Math.max(1, values.size());

            if (type == Type.BAR) {
                int barW = Math.max(10, stepX - 20);
                for (int i = 0; i < values.size(); i++) {
                    int barH = (int) ((values.get(i) / max) * (h - 2 * pad));
                    int x = pad + 10 + i * stepX;
                    int y = h - pad - barH;
                    
                    // Sombra
                    g2.setColor(new Color(0,0,0,50));
                    g2.fillRoundRect(x+3, y+3, barW, barH, 5, 5);
                    // Barra
                    g2.setColor(color);
                    g2.fillRoundRect(x, y, barW, barH, 5, 5);
                    // Valor
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    String val = String.valueOf(values.get(i).intValue());
                    g2.drawString(val, x + (barW - g2.getFontMetrics().stringWidth(val))/2, y - 5);
                    // Etiqueta
                    g2.setColor(Color.GRAY);
                    String lbl = labels.get(i);
                    if(lbl.length()>6) lbl = lbl.substring(0,6);
                    g2.drawString(lbl, x + (barW - g2.getFontMetrics().stringWidth(lbl))/2, h - pad + 15);
                }
            } else if (type == Type.LINE) {
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f));
                Path2D path = new Path2D.Double();
                
                for(int i=0; i<values.size(); i++) {
                    int x = pad + (int)(stepX * (i + 0.5));
                    int y = h - pad - (int)((values.get(i)/max)*(h - 2*pad));
                    
                    if(i==0) path.moveTo(x, y);
                    else path.lineTo(x, y);
                    
                    // Punto
                    g2.fillOval(x-4, y-4, 8, 8);
                    
                    // Etiqueta
                    g2.setColor(Color.GRAY);
                    g2.drawString(labels.get(i), x - 10, h - pad + 15);
                    g2.setColor(color); // Restaurar color
                }
                g2.draw(path);
                
                // Área bajo la curva (gradiente)
                path.lineTo(pad + (int)(stepX * (values.size() - 0.5)), h - pad);
                path.lineTo(pad + (stepX * 0.5), h - pad);
                path.closePath();
                g2.setPaint(new GradientPaint(0, 0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 100), 
                                              0, h, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)));
                g2.fill(path);
            }
        }
    }
}