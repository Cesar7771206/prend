package com.poo.prend.view;

// Este archivo permite el uso de componentes modernos (Ya que no me dejo usar FX :c)

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernUI {
    // --- PALETA DE COLORES ---
    public static final Color BG_DARK = Color.decode("#181818");       // Fondo general oscuro
    public static final Color BG_LIGHT = Color.WHITE;                  // Fondo claro (para login derecho)
    
    // Dashboard Colors
    public static final Color DASH_BG = Color.decode("#101018");       // Fondo Dashboard profundo
    public static final Color DASH_SIDEBAR = Color.decode("#1A1A2E");  // Sidebar
    public static final Color DASH_CARD = Color.decode("#202035");     // Tarjetas
    
    // Accents
    public static final Color PURPLE_BTN = Color.decode("#7C71F5");    // Botón principal
    public static final Color ACCENT_BLUE = Color.decode("#00D4FF");   // Azul neón
    public static final Color ACCENT_GREEN = Color.decode("#00E676");  // Verde neón
    
    // Text
    public static final Color TEXT_WHITE = Color.WHITE;
    public static final Color TEXT_GRAY = Color.decode("#888888");

    // Fonts
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);

    // --- COMPONENTES PERSONALIZADOS ---

    // Botón Principal (Morado)
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setBackground(PURPLE_BTN);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Input Moderno (Solo línea inferior o borde sutil)
    public static JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setFont(FONT_REGULAR);
        return field;
    }
    
    // Panel Tarjeta (Redondeado y oscuro)
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DASH_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        return panel;
    }
}