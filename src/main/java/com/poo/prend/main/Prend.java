package com.poo.prend.main;

import com.poo.prend.controller.MainController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Prend {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainController();
        });
    }
}
