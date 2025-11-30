package com.poo.prend.main;

import com.poo.prend.controller.MainController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Prend {
    public static void main(String[] args) {
        // Ejecutar la aplicación en el hilo de despacho de eventos de Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Opcional: Intentar usar el Look and Feel del sistema para bordes de ventanas nativos
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, usar el predeterminado de Java (Metal)
                e.printStackTrace();
            }
            
            // Iniciar el controlador principal que carga la vista y lógica
            new MainController();
        });
    }
}
