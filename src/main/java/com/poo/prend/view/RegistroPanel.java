package com.poo.prend.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegistroPanel extends JPanel {
    // --- CAMPOS DE DATOS ---
    
    // Datos Usuario
    public JTextField txtDni, txtNombre, txtApellido, txtEdad, txtTelefono, txtDireccionUser, txtCorreo;
    public JPasswordField txtPass;
    
    // Datos Emprendimiento 
    // NOTA: Se eliminó txtDireccionEmp para coincidir con la base de datos
    public JTextField txtNombreEmp, txtDescEmp; 
    
    public JButton btnGuardarTodo;
    public JButton btnVolver;

    public RegistroPanel() {
        setLayout(new GridLayout(1, 2)); // Dividir pantalla: 50% Arte | 50% Formulario

        // ---------------------------------------------------------
        // 1. PANEL IZQUIERDO (Arte Visual - Idéntico al Login)
        // ---------------------------------------------------------
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2 - 20;
                
                // Cuadrado redondeado
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(6));
                g2.drawRoundRect(cx - 50, cy - 50, 100, 100, 30, 30);
                
                // Letra P
                g2.setFont(new Font("SansSerif", Font.BOLD, 60));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("P", cx - fm.stringWidth("P") / 2, cy + 25);

                // Texto REGISTRO (Diferente al Login para distinguir)
                g2.setFont(new Font("SansSerif", Font.BOLD, 40));
                String title = "REGISTRO";
                int titleW = g2.getFontMetrics().stringWidth(title);
                g2.drawString(title, cx - titleW / 2, cy + 90);
            }
        };
        leftPanel.setBackground(ModernUI.BG_DARK); // Fondo Oscuro
        add(leftPanel);

        // ---------------------------------------------------------
        // 2. PANEL DERECHO (Formulario en Scroll)
        // ---------------------------------------------------------
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(ModernUI.BG_LIGHT);

        // Header del Formulario
        JLabel lblHeader = new JLabel("Crear Cuenta");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblHeader.setForeground(Color.BLACK);
        lblHeader.setBorder(new EmptyBorder(30, 40, 10, 0));
        rightContainer.add(lblHeader, BorderLayout.NORTH);

        // Contenido del Formulario (GridBagLayout para control preciso)
        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setBackground(ModernUI.BG_LIGHT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 40, 5, 40); // Márgenes laterales
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // -> SECCIÓN: Datos Personales
        addSectionTitle(formContent, "Datos Personales", gbc);
        
        txtDni = addField(formContent, "DNI", gbc);
        txtNombre = addField(formContent, "Nombre", gbc);
        txtApellido = addField(formContent, "Apellido", gbc);
        txtEdad = addField(formContent, "Edad", gbc);
        txtTelefono = addField(formContent, "Teléfono", gbc);
        txtDireccionUser = addField(formContent, "Dirección Personal", gbc);
        txtCorreo = addField(formContent, "Correo Electrónico", gbc);
        
        // Campo Contraseña
        addLabel(formContent, "Contraseña", gbc);
        txtPass = new JPasswordField();
        styleField(txtPass);
        formContent.add(txtPass, gbc);
        gbc.gridy++;

        // Espaciador
        gbc.gridy++;
        formContent.add(Box.createVerticalStrut(15), gbc);

        // -> SECCIÓN: Datos del Negocio
        addSectionTitle(formContent, "Datos del Negocio", gbc);
        
        txtNombreEmp = addField(formContent, "Nombre del Emprendimiento", gbc);
        txtDescEmp = addField(formContent, "Descripción", gbc);
        // La dirección del emprendimiento ha sido removida aquí

        gbc.gridy++;
        formContent.add(Box.createVerticalStrut(20), gbc);

        // -> Botones de Acción
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(ModernUI.BG_LIGHT);
        
        btnVolver = new JButton("Cancelar");
        btnVolver.setForeground(ModernUI.TEXT_GRAY);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnGuardarTodo = ModernUI.createPrimaryButton("Registrarse");
        btnGuardarTodo.setPreferredSize(new Dimension(150, 40));

        btnPanel.add(btnVolver);
        btnPanel.add(btnGuardarTodo);
        
        formContent.add(btnPanel, gbc);

        // ScrollPane para manejar la altura del formulario
        JScrollPane scrollPane = new JScrollPane(formContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll más fluido
        rightContainer.add(scrollPane, BorderLayout.CENTER);

        add(rightContainer);
    }

    // --- MÉTODOS AUXILIARES PARA ESTILO ---

    private void addSectionTitle(JPanel panel, String title, GridBagConstraints gbc) {
        JLabel l = new JLabel(title);
        l.setFont(new Font("SansSerif", Font.BOLD, 16));
        l.setForeground(ModernUI.PURPLE_BTN);
        l.setBorder(new EmptyBorder(10, 0, 5, 0));
        gbc.gridy++;
        panel.add(l, gbc);
        gbc.gridy++;
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc) {
        JLabel l = new JLabel(text);
        l.setFont(ModernUI.FONT_BOLD);
        l.setForeground(ModernUI.TEXT_GRAY);
        gbc.gridy++;
        panel.add(l, gbc);
        gbc.gridy++;
    }

    private JTextField addField(JPanel panel, String label, GridBagConstraints gbc) {
        addLabel(panel, label, gbc);
        JTextField field = ModernUI.createModernTextField();
        field.setPreferredSize(new Dimension(0, 35));
        panel.add(field, gbc);
        gbc.gridy++;
        return field;
    }

    private void styleField(JComponent field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setFont(ModernUI.FONT_REGULAR);
        field.setPreferredSize(new Dimension(0, 35));
    }
}