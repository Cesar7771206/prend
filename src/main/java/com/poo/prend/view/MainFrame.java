package com.poo.prend.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public JPanel cardPanel;
    public CardLayout cardLayout;

    public MainFrame() {
        setTitle("PREND - Sistema de Gestión");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        
        setIconImage(crearIconoApp());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        add(cardPanel);
    }
    
    public void mostrarPanel(String nombre) {
        cardLayout.show(cardPanel, nombre);
    }
    
    // Generar el icono
    private Image crearIconoApp() {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(64, 64, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(ModernUI.PURPLE_BTN);
        g2.fillRoundRect(0, 0, 64, 64, 20, 20);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 40));
        g2.drawString("P", 20, 48);
        g2.dispose();
        return image;
    }
}