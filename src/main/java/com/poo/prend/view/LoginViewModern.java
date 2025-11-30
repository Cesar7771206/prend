package com.poo.prend.view;

import javax.swing.*;
import java.awt.*;

public class LoginViewModern extends JPanel {
    public JTextField txtCorreo;
    public JPasswordField txtPass;
    public JButton btnLogin;
    public JButton btnIrARegistro;

    public LoginViewModern() {
        setLayout(new GridLayout(1, 2)); // División 50% - 50%

        // --- PANEL IZQUIERDO (Arte Visual) ---
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibujar Logo
                int cx = getWidth() / 2;
                int cy = getHeight() / 2 - 20;
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(6));
                g2.drawRoundRect(cx - 50, cy - 50, 100, 100, 30, 30);
                
                g2.setFont(new Font("SansSerif", Font.BOLD, 60));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("P", cx - fm.stringWidth("P") / 2, cy + 25);

                g2.setFont(new Font("SansSerif", Font.BOLD, 40));
                String title = "PREND";
                int titleW = g2.getFontMetrics().stringWidth(title);
                g2.drawString(title, cx - titleW / 2, cy + 90);
            }
        };
        leftPanel.setBackground(ModernUI.BG_DARK); // Fondo Oscuro
        
        // --- PANEL DERECHO (Formulario) ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(ModernUI.BG_LIGHT); // Fondo Claro
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; 
        
        JLabel lblTitle = new JLabel("LOG IN");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitle.setForeground(Color.BLACK);
        gbc.gridy = 0;
        rightPanel.add(lblTitle, gbc);
        
        gbc.gridy++;
        rightPanel.add(Box.createVerticalStrut(20), gbc);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(ModernUI.FONT_BOLD);
        lblEmail.setForeground(ModernUI.TEXT_GRAY);
        gbc.gridy++;
        rightPanel.add(lblEmail, gbc);
        
        txtCorreo = ModernUI.createModernTextField();
        txtCorreo.setPreferredSize(new Dimension(300, 40));
        gbc.gridy++;
        rightPanel.add(txtCorreo, gbc);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(ModernUI.FONT_BOLD);
        lblPass.setForeground(ModernUI.TEXT_GRAY);
        gbc.gridy++;
        rightPanel.add(lblPass, gbc);
        
        txtPass = new JPasswordField();
        txtPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        txtPass.setPreferredSize(new Dimension(300, 40));
        gbc.gridy++;
        rightPanel.add(txtPass, gbc);

        JLabel lblForgot = new JLabel("¿Olvidaste tu contraseña?");
        lblForgot.setForeground(ModernUI.PURPLE_BTN);
        lblForgot.setFont(ModernUI.FONT_SMALL);
        lblForgot.setHorizontalAlignment(SwingConstants.RIGHT);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy++;
        rightPanel.add(lblForgot, gbc);

        gbc.gridy++;
        rightPanel.add(Box.createVerticalStrut(20), gbc);

        btnLogin = ModernUI.createPrimaryButton("Sign in");
        btnLogin.setPreferredSize(new Dimension(300, 45));
        gbc.gridy++;
        rightPanel.add(btnLogin, gbc);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setBackground(ModernUI.BG_LIGHT);
        footerPanel.add(new JLabel("Nuevo en PREND?"));
        btnIrARegistro = new JButton("Crear una Cuenta");
        btnIrARegistro.setForeground(ModernUI.PURPLE_BTN);
        btnIrARegistro.setBorderPainted(false);
        btnIrARegistro.setContentAreaFilled(false);
        btnIrARegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footerPanel.add(btnIrARegistro);
        
        gbc.gridy++;
        rightPanel.add(footerPanel, gbc);

        add(leftPanel);
        add(rightPanel);
    }
}