package com.poo.prend.controller;

import com.poo.prend.model.database.dao.*;
import com.poo.prend.model.entities.*;
import com.poo.prend.model.enums.Categoria;
import com.poo.prend.view.*; 

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class MainController {
    private MainFrame mainFrame;
    
    // Vistas 
    private LoginViewModern loginView;
    private DashboardViewModern dashboardView;
    private RegistroPanel registroPanel;

    // DAOs
    private UsuarioDAO usuarioDAO;
    private EmprendimientoDAO empDAO;
    private ProductoDAO productoDAO;
    private ClienteDAO clienteDAO;

    // Sesión
    private Usuario usuarioLogueado;

    public MainController() {
        // 1. Inicializar DAOs
        usuarioDAO = new UsuarioDAO();
        empDAO = new EmprendimientoDAO();
        productoDAO = new ProductoDAO();
        clienteDAO = new ClienteDAO();

        // 2. Inicializar Vistas
        mainFrame = new MainFrame();
        loginView = new LoginViewModern();
        dashboardView = new DashboardViewModern();
        registroPanel = new RegistroPanel();

        // 3. Configurar navegación
        mainFrame.cardPanel.add(loginView, "LOGIN");
        mainFrame.cardPanel.add(registroPanel, "REGISTRO");
        mainFrame.cardPanel.add(dashboardView, "DASHBOARD");

        // 4. Configurar Listeners
        initLoginEvents();
        initRegistroEvents();
        initDashboardEvents();

        // 5. Mostrar inicio
        mainFrame.mostrarPanel("LOGIN");
        mainFrame.setVisible(true);
    }

    private void initLoginEvents() {
        loginView.btnLogin.addActionListener(e -> {
            String correo = loginView.txtCorreo.getText();
            String pass = new String(loginView.txtPass.getPassword());
            if(correo.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Por favor complete todos los campos.");
                return;
            }
            autenticar(correo, pass);
        });

        loginView.btnIrARegistro.addActionListener(e -> mainFrame.mostrarPanel("REGISTRO"));
    }

    private void initRegistroEvents() {
        registroPanel.btnGuardarTodo.addActionListener(e -> registrarUsuarioYNegocio());
        registroPanel.btnVolver.addActionListener(e -> mainFrame.mostrarPanel("LOGIN"));
    }

    private void initDashboardEvents() {
        dashboardView.btnSalir.addActionListener(e -> cerrarSesion());
        dashboardView.btnAddProducto.addActionListener(e -> abrirDialogoProducto());
        dashboardView.btnAddCliente.addActionListener(e -> abrirDialogoCliente());
    }

    private void autenticar(String correo, String pass) {
        try {
            usuarioLogueado = usuarioDAO.login(correo, pass);
            
            if (usuarioLogueado != null) {
                // Verificar si tiene emprendimiento
                Emprendimiento emp = empDAO.obtenerPorUsuario(usuarioLogueado.getId());
                
                if (emp == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Usuario encontrado pero sin negocio configurado.");
                    // Aquí podrías redirigir a una pantalla para crear negocio solamente
                    return;
                }
                
                usuarioLogueado.setEmprendimiento(emp);
                JOptionPane.showMessageDialog(mainFrame, "Bienvenido " + usuarioLogueado.getNombre());
                
                cargarDatosDashboard();
                mainFrame.mostrarPanel("DASHBOARD");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error de base de datos: " + ex.getMessage());
        }
    }

    private void registrarUsuarioYNegocio() {
        // Validaciones básicas antes de intentar guardar
        if(registroPanel.txtDni.getText().isEmpty() || registroPanel.txtCorreo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "DNI y Correo son obligatorios.");
            return;
        }

        try {
            // 1. Crear Objeto Usuario
            Usuario u = new Usuario(0, registroPanel.txtCorreo.getText(), new String(registroPanel.txtPass.getPassword()), null);
            u.setDNI(registroPanel.txtDni.getText());
            u.setNombre(registroPanel.txtNombre.getText());
            u.setApellido(registroPanel.txtApellido.getText());
            u.setNumero(registroPanel.txtTelefono.getText());
            u.setDireccion(registroPanel.txtDireccionUser.getText());
            
            try {
                u.setEdad(Integer.parseInt(registroPanel.txtEdad.getText()));
            } catch (NumberFormatException e) { 
                u.setEdad(0); 
            }

            // 2. Guardar Usuario en BD (UsuarioDAO llenará el ID en el objeto 'u')
            usuarioDAO.crearUsuario(u); 

            // 3. Crear Objeto Emprendimiento
            Emprendimiento emp = new Emprendimiento(
                    registroPanel.txtNombreEmp.getText(),
                    registroPanel.txtDescEmp.getText()
            );
            
            // 4. Guardar Emprendimiento usando el ID del usuario recién creado
            empDAO.registrarEmprendimiento(emp, u.getId());

            JOptionPane.showMessageDialog(mainFrame, "¡Cuenta creada con éxito! Por favor inicie sesión.");
            limpiarCamposRegistro();
            mainFrame.mostrarPanel("LOGIN");

        } catch (SQLException ex) {
            if(ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(mainFrame, "El correo o DNI ya están registrados.");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Error SQL: " + ex.getMessage());
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage());
        }
    }

    private void cargarDatosDashboard() {
        if (usuarioLogueado == null || usuarioLogueado.getEmprendimiento() == null) return;
        
        // Usamos el ID del emprendimiento para cargar sus datos
        int empId = usuarioLogueado.getEmprendimiento().getId(); 

        try {
            List<Producto> productos = productoDAO.listarPorEmprendimiento(empId);
            dashboardView.modeloTabla.setRowCount(0);
            for (Producto p : productos) {
                dashboardView.modeloTabla.addRow(new Object[]{
                    p.getId(), 
                    p.getNombre(), 
                    String.format("$ %.2f", p.getPrecio()), 
                    p.getStock()
                });
            }

            dashboardView.lblTotalProductos.setText(String.valueOf(productos.size()));
            
            List<Cliente> clientes = clienteDAO.listarClientes(empId);
            dashboardView.lblTotalClientes.setText(String.valueOf(clientes.size()));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error al cargar datos del dashboard.");
        }
    }

    // ... Resto de métodos de diálogo (abrirDialogoProducto, etc.) se mantienen igual ...
    private void abrirDialogoProducto() {
        JTextField txtNombre = new JTextField();
        JTextField txtPrecio = new JTextField();
        JTextField txtStock = new JTextField();
        
        Object[] message = {
            "Nombre:", txtNombre,
            "Precio ($):", txtPrecio,
            "Stock:", txtStock
        };

        int option = JOptionPane.showConfirmDialog(mainFrame, message, "Nuevo Producto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Producto p = new Producto();
                p.setNombre(txtNombre.getText());
                p.setDescripcion("..."); 
                p.setPrecio(Double.parseDouble(txtPrecio.getText()));
                p.setStock(Integer.parseInt(txtStock.getText()));
                p.setCategoria(Categoria.ROPA); 

                // Usar ID del Emprendimiento, no del Usuario
                productoDAO.guardarProducto(p, usuarioLogueado.getEmprendimiento().getId());
                cargarDatosDashboard(); 
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Datos inválidos: " + ex.getMessage());
            }
        }
    }
    
    private void abrirDialogoCliente() {
        JTextField txtDni = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtTelefono = new JTextField();

        Object[] message = { 
            "DNI:", txtDni, 
            "Nombre:", txtNombre,
            "Apellido:", txtApellido,
            "Teléfono:", txtTelefono
        };

        int option = JOptionPane.showConfirmDialog(mainFrame, message, "Nuevo Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Cliente c = new Cliente(5, null, null);
                c.setDNI(txtDni.getText());
                c.setNombre(txtNombre.getText());
                c.setApellido(txtApellido.getText());
                c.setNumero(txtTelefono.getText());
                c.setDireccion("");
                
                clienteDAO.guardarCliente(c, usuarioLogueado.getEmprendimiento().getId());
                cargarDatosDashboard();
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error al guardar cliente: " + ex.getMessage());
            }
        }
    }

    private void cerrarSesion() {
        usuarioLogueado = null;
        loginView.txtPass.setText("");
        mainFrame.mostrarPanel("LOGIN");
    }
    
    private void limpiarCamposRegistro() {
        registroPanel.txtCorreo.setText("");
        registroPanel.txtPass.setText("");
        registroPanel.txtDni.setText("");
        registroPanel.txtNombre.setText("");
        registroPanel.txtApellido.setText("");
        registroPanel.txtTelefono.setText("");
        registroPanel.txtEdad.setText("");
        registroPanel.txtDireccionUser.setText("");
        registroPanel.txtNombreEmp.setText("");
        registroPanel.txtDescEmp.setText("");
    }
}