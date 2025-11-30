package com.poo.prend.controller;

import com.poo.prend.model.database.dao.*;
import com.poo.prend.model.entities.*;
import com.poo.prend.model.enums.Categoria;
import com.poo.prend.model.enums.Estado;
import com.poo.prend.model.enums.MetodoPago;
import com.poo.prend.view.*; 

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableModel;

public class MainController {
    private MainFrame mainFrame;
    private LoginViewModern loginView;
    private DashboardViewModern dashboardView;
    private RegistroPanel registroPanel;

    private UsuarioDAO usuarioDAO;
    private EmprendimientoDAO empDAO;
    private ProductoDAO productoDAO;
    private ClienteDAO clienteDAO;
    private PedidoDAO pedidoDAO;
    private VentaDAO ventaDAO;

    private Usuario usuarioLogueado;

    public MainController() {
        usuarioDAO = new UsuarioDAO();
        empDAO = new EmprendimientoDAO();
        productoDAO = new ProductoDAO();
        clienteDAO = new ClienteDAO();
        pedidoDAO = new PedidoDAO();
        ventaDAO = new VentaDAO();

        mainFrame = new MainFrame();
        loginView = new LoginViewModern();
        dashboardView = new DashboardViewModern();
        registroPanel = new RegistroPanel();

        mainFrame.cardPanel.add(loginView, "LOGIN");
        mainFrame.cardPanel.add(registroPanel, "REGISTRO");
        mainFrame.cardPanel.add(dashboardView, "DASHBOARD");

        initLoginEvents();
        initRegistroEvents();
        initDashboardEvents();

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
        // --- NAVEGACIÓN ---
        dashboardView.btnNavDashboard.addActionListener(e -> { dashboardView.mostrarPanel("DASHBOARD"); cargarDatosDashboard(); });
        dashboardView.btnNavPedidos.addActionListener(e -> { dashboardView.mostrarPanel("PEDIDOS"); cargarPedidosActivos(); });
        dashboardView.btnNavInventario.addActionListener(e -> { dashboardView.mostrarPanel("INVENTARIO"); cargarInventario(); });
        dashboardView.btnNavClientes.addActionListener(e -> { dashboardView.mostrarPanel("CLIENTES"); cargarClientes(); });
        dashboardView.btnNavVentas.addActionListener(e -> { dashboardView.mostrarPanel("VENTAS"); cargarHistorialVentas(); });
        dashboardView.btnSalir.addActionListener(e -> cerrarSesion());

        // --- ACCIONES INVENTARIO ---
        dashboardView.btnAddProducto.addActionListener(e -> abrirDialogoProducto(null)); // null = Crear
        dashboardView.btnEditProducto.addActionListener(e -> editarProductoSeleccionado());
        dashboardView.btnDelProducto.addActionListener(e -> eliminarProductoSeleccionado());

        // --- ACCIONES CLIENTES ---
        dashboardView.btnAddCliente.addActionListener(e -> abrirDialogoCliente(null)); // null = Crear
        dashboardView.btnEditCliente.addActionListener(e -> editarClienteSeleccionado());
        dashboardView.btnDelCliente.addActionListener(e -> eliminarClienteSeleccionado());

        // --- ACCIONES PEDIDOS ---
        dashboardView.btnNuevoPedido.addActionListener(e -> abrirDialogoNuevoPedido());
        dashboardView.btnCompletarPedido.addActionListener(e -> completarPedidoSeleccionado());
        dashboardView.btnCancelarPedido.addActionListener(e -> eliminarPedidoSeleccionado(dashboardView.tablaPedidos, dashboardView.modeloPedidos));
        
        // Ajuste visual inicial
        actualizarTitulosVisuales();
    }
    
    // Método auxiliar para cambiar los títulos de las tarjetas en la vista sin editar el archivo View
    private void actualizarTitulosVisuales() {
        try {
            // Cambiar "Ticket Promedio" -> "Cliente Top"
            Container parent1 = dashboardView.lblPromedioVenta.getParent();
            for(Component c : parent1.getComponents()) {
                if(c instanceof JLabel) {
                    JLabel l = (JLabel)c;
                    if(l.getText().contains("Ticket") || l.getText().contains("Promedio")) l.setText("Cliente Top");
                }
            }
            // Cambiar "Producto Estrella" -> "Más Vendido"
            Container parent2 = dashboardView.lblProductoMasVendido.getParent();
            for(Component c : parent2.getComponents()) {
                if(c instanceof JLabel) {
                    JLabel l = (JLabel)c;
                    if(l.getText().contains("Estrella")) l.setText("Más Vendido");
                }
            }
        } catch(Exception e) { /* Ignorar errores de UI */ }
    }

    // ==========================================
    // LÓGICA DE PEDIDOS Y VENTAS (CORE)
    // ==========================================

    private void abrirDialogoNuevoPedido() {
        // Diálogo simplificado para crear PEDIDO PENDIENTE
        JDialog dialog = new JDialog(mainFrame, "Nuevo Pedido", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<Cliente> cbClientes = new JComboBox<>();
        JComboBox<Producto> cbProductos = new JComboBox<>();
        JTextField txtCant = new JTextField("1");
        
        // Cargar datos
        try {
            int empId = usuarioLogueado.getEmprendimiento().getId();
            clienteDAO.listarClientes(empId).forEach(cbClientes::addItem);
            productoDAO.listarPorEmprendimiento(empId).forEach(cbProductos::addItem);
        } catch (SQLException e) { e.printStackTrace(); }

        // Renderizadores
        cbClientes.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof Cliente) setText(((Cliente)value).getNombre());
                return this;
            }
        });
        cbProductos.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof Producto) setText(((Producto)value).getNombre() + " (Stock: " + ((Producto)value).getStock() + ")");
                return this;
            }
        });

        form.add(new JLabel("Cliente:")); form.add(cbClientes);
        form.add(new JLabel("Producto:")); form.add(cbProductos);
        form.add(new JLabel("Cantidad:")); form.add(txtCant);

        JButton btnSave = new JButton("Crear Pedido");
        btnSave.addActionListener(e -> {
            try {
                Cliente cli = (Cliente) cbClientes.getSelectedItem();
                Producto prod = (Producto) cbProductos.getSelectedItem();
                int cant = Integer.parseInt(txtCant.getText());

                if(cli == null || prod == null || cant <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Datos inválidos"); return;
                }
                if(cant > prod.getStock()) {
                    JOptionPane.showMessageDialog(dialog, "Stock insuficiente"); return;
                }

                // Crear Pedido PENDIENTE
                Pedido p = new Pedido();
                p.setCliente(cli);
                p.setFechaPedido(new Date());
                p.setEstado(Estado.PENDIENTE);
                
                ItemPedido item = new ItemPedido(0, prod, cant, prod.getPrecio());
                List<ItemPedido> items = new ArrayList<>();
                items.add(item);
                p.setItems(items);

                pedidoDAO.crearPedido(p, usuarioLogueado.getEmprendimiento().getId());
                
                // Descontar stock inmediatamente (reserva)
                prod.setStock(prod.getStock() - cant);
                productoDAO.actualizarProducto(prod);

                JOptionPane.showMessageDialog(dialog, "Pedido creado (Pendiente de pago).");
                dialog.dispose();
                cargarPedidosActivos();
                cargarDatosDashboard();

            } catch (Exception ex) { ex.printStackTrace(); }
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnSave, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void completarPedidoSeleccionado() {
        int row = dashboardView.tablaPedidos.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(mainFrame, "Seleccione un pedido pendiente."); return; }
        
        int idPedido = (int) dashboardView.modeloPedidos.getValueAt(row, 0);
        
        // Diálogo para elegir método de pago
        JComboBox<MetodoPago> cbPago = new JComboBox<>(MetodoPago.values());
        int res = JOptionPane.showConfirmDialog(mainFrame, cbPago, "Confirmar Pago / Completar", JOptionPane.OK_CANCEL_OPTION);
        
        if(res == JOptionPane.OK_OPTION) {
            try {
                // 1. Actualizar estado a ENTREGADO/COMPLETADO
                pedidoDAO.actualizarEstado(idPedido, Estado.ENTREGADO);
                
                // 2. Registrar Venta Financiera
                Venta v = new Venta();
                v.setId(idPedido);
                v.setFechaVenta(new Date());
                v.setMetodoPago((MetodoPago) cbPago.getSelectedItem());
                ventaDAO.registrarVenta(v);

                JOptionPane.showMessageDialog(mainFrame, "Pedido completado y movido a Ventas.");
                cargarPedidosActivos();
                cargarDatosDashboard();
                
            } catch (SQLException e) { JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage()); }
        }
    }

    // ==========================================
    // EDICIÓN Y ELIMINACIÓN
    // ==========================================

    private void editarProductoSeleccionado() {
        int row = dashboardView.tablaInventario.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(mainFrame, "Seleccione un producto."); return; }
        int id = (int) dashboardView.modeloInventario.getValueAt(row, 0);

        try {
            // Buscar producto original (simplificado: iteramos lista o traemos de BD)
            // Para ser robustos, buscamos en BD (reusamos lógica de lista filtrada)
            Producto p = productoDAO.listarPorEmprendimiento(usuarioLogueado.getEmprendimiento().getId())
                        .stream().filter(prod -> prod.getId() == id).findFirst().orElse(null);
            
            if(p != null) abrirDialogoProducto(p); // Abrimos diálogo en modo EDICIÓN
            
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    private void editarClienteSeleccionado() {
        int row = dashboardView.tablaClientes.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(mainFrame, "Seleccione un cliente."); return; }
        int id = (int) dashboardView.modeloClientes.getValueAt(row, 0);
        
        try {
            Cliente c = clienteDAO.listarClientes(usuarioLogueado.getEmprendimiento().getId())
                        .stream().filter(cli -> cli.getId() == id).findFirst().orElse(null);
            
            if(c != null) abrirDialogoCliente(c);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void eliminarProductoSeleccionado() {
        int row = dashboardView.tablaInventario.getSelectedRow();
        if(row == -1) return;
        int id = (int) dashboardView.modeloInventario.getValueAt(row, 0);
        if(JOptionPane.showConfirmDialog(mainFrame, "¿Eliminar producto?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { productoDAO.eliminarProducto(id); cargarInventario(); cargarDatosDashboard(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void eliminarClienteSeleccionado() {
        int row = dashboardView.tablaClientes.getSelectedRow();
        if(row == -1) return;
        int id = (int) dashboardView.modeloClientes.getValueAt(row, 0);
        if(JOptionPane.showConfirmDialog(mainFrame, "¿Eliminar cliente y sus pedidos?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { clienteDAO.eliminarCliente(id); cargarClientes(); cargarDatosDashboard(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void eliminarPedidoSeleccionado(JTable tabla, DefaultTableModel modelo) {
        int row = tabla.getSelectedRow();
        if(row == -1) return;
        int id = (int) modelo.getValueAt(row, 0);
        if(JOptionPane.showConfirmDialog(mainFrame, "¿Eliminar registro?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { pedidoDAO.eliminarPedido(id); cargarPedidosActivos(); cargarHistorialVentas(); cargarDatosDashboard(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ==========================================
    // DIÁLOGOS DE CREACIÓN / EDICIÓN (REUTILIZABLES)
    // ==========================================

    private void abrirDialogoProducto(Producto prodEditar) {
        boolean esEdicion = (prodEditar != null);
        String titulo = esEdicion ? "Editar Producto" : "Nuevo Producto";
        
        JTextField txtNombre = new JTextField(esEdicion ? prodEditar.getNombre() : "");
        JTextField txtDesc = new JTextField(esEdicion ? prodEditar.getDescripcion() : "");
        JTextField txtPrecio = new JTextField(esEdicion ? String.valueOf(prodEditar.getPrecio()) : "");
        JTextField txtStock = new JTextField(esEdicion ? String.valueOf(prodEditar.getStock()) : "");
        JComboBox<Categoria> cmbCat = new JComboBox<>(Categoria.values());
        if(esEdicion) cmbCat.setSelectedItem(prodEditar.getCategoria());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre:")); panel.add(txtNombre);
        panel.add(new JLabel("Descripción:")); panel.add(txtDesc);
        panel.add(new JLabel("Precio:")); panel.add(txtPrecio);
        panel.add(new JLabel("Stock:")); panel.add(txtStock);
        panel.add(new JLabel("Categoría:")); panel.add(cmbCat);

        if(JOptionPane.showConfirmDialog(mainFrame, panel, titulo, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Producto p = esEdicion ? prodEditar : new Producto();
                p.setNombre(txtNombre.getText());
                p.setDescripcion(txtDesc.getText());
                p.setPrecio(Double.parseDouble(txtPrecio.getText()));
                p.setStock(Integer.parseInt(txtStock.getText()));
                p.setCategoria((Categoria) cmbCat.getSelectedItem());

                if(esEdicion) productoDAO.actualizarProducto(p);
                else productoDAO.guardarProducto(p, usuarioLogueado.getEmprendimiento().getId());
                
                cargarInventario(); cargarDatosDashboard();
            } catch (Exception e) { JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage()); }
        }
    }

    private void abrirDialogoCliente(Cliente cliEditar) {
        boolean esEdicion = (cliEditar != null);
        
        JTextField txtDni = new JTextField(esEdicion ? cliEditar.getDNI() : "");
        JTextField txtNom = new JTextField(esEdicion ? cliEditar.getNombre() : "");
        JTextField txtApe = new JTextField(esEdicion ? cliEditar.getApellido() : "");
        JTextField txtTel = new JTextField(esEdicion ? cliEditar.getNumero() : "");
        JTextField txtDir = new JTextField(esEdicion ? cliEditar.getDireccion() : "");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("DNI:")); panel.add(txtDni);
        panel.add(new JLabel("Nombre:")); panel.add(txtNom);
        panel.add(new JLabel("Apellido:")); panel.add(txtApe);
        panel.add(new JLabel("Teléfono:")); panel.add(txtTel);
        panel.add(new JLabel("Dirección:")); panel.add(txtDir);

        if(JOptionPane.showConfirmDialog(mainFrame, panel, esEdicion ? "Editar Cliente" : "Nuevo Cliente", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Cliente c = esEdicion ? cliEditar : new Cliente(0, null, null);
                c.setDNI(txtDni.getText());
                c.setNombre(txtNom.getText());
                c.setApellido(txtApe.getText());
                c.setNumero(txtTel.getText());
                c.setDireccion(txtDir.getText());
                if(!esEdicion) c.setCalificacion(5);

                if(esEdicion) clienteDAO.actualizarCliente(c);
                else clienteDAO.guardarCliente(c, usuarioLogueado.getEmprendimiento().getId());
                
                cargarClientes(); cargarDatosDashboard();
            } catch (Exception e) { JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage()); }
        }
    }

    // ==========================================
    // CARGA DE DATOS (TABLAS Y DASHBOARD)
    // ==========================================

    private void cargarPedidosActivos() {
        int empId = usuarioLogueado.getEmprendimiento().getId();
        try {
            List<Pedido> todos = pedidoDAO.listarPedidos(empId);
            dashboardView.modeloPedidos.setRowCount(0);
            for(Pedido p : todos) {
                // FILTRO LÓGICO: Solo mostrar PENDIENTE o EN_CAMINO en esta pestaña
                if(p.getEstado() == Estado.PENDIENTE || p.getEstado() == Estado.PENDIENTE) {
                     double total = p.getItems().stream().mapToDouble(i->i.getCantidad()*i.getPrecioUnitario()).sum();
                     dashboardView.modeloPedidos.addRow(new Object[]{
                         p.getId(), p.getFechaPedido(), p.getCliente().getNombre(), String.format("%.2f", total), p.getEstado()
                     });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void cargarHistorialVentas() {
        int empId = usuarioLogueado.getEmprendimiento().getId();
        try {
            List<Pedido> todos = pedidoDAO.listarPedidos(empId);
            dashboardView.modeloVentas.setRowCount(0);
            for(Pedido p : todos) {
                // FILTRO LÓGICO: Solo mostrar ENTREGADO/COMPLETADO
                if(p.getEstado() == Estado.ENTREGADO || p.getEstado() == Estado.CANCELADO) {
                     double total = p.getItems().stream().mapToDouble(i->i.getCantidad()*i.getPrecioUnitario()).sum();
                     dashboardView.modeloVentas.addRow(new Object[]{
                         p.getId(), p.getFechaPedido(), p.getCliente().getNombre(), String.format("%.2f", total), "Ver Detalle"
                     });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void cargarDatosDashboard() {
        if (usuarioLogueado == null) return;
        int empId = usuarioLogueado.getEmprendimiento().getId();

        try {
            List<Pedido> pedidos = pedidoDAO.listarPedidos(empId);
            List<Producto> productos = productoDAO.listarPorEmprendimiento(empId);
            List<Cliente> clientes = clienteDAO.listarClientes(empId);

            // Filtrar solo ventas completadas para ingresos
            List<Pedido> ventasCompletadas = pedidos.stream()
                .filter(p -> p.getEstado() == Estado.ENTREGADO)
                .collect(Collectors.toList());

            double ingresos = ventasCompletadas.stream()
                .flatMap(p -> p.getItems().stream())
                .mapToDouble(i -> i.getCantidad() * i.getPrecioUnitario())
                .sum();

            // Actualizar Etiquetas Principales
            dashboardView.lblTotalVentas.setText(String.format("$ %.2f", ingresos));
            dashboardView.lblTotalClientes.setText(String.valueOf(clientes.size()));
            dashboardView.lblTotalProductos.setText(String.valueOf(productos.stream().mapToInt(Producto::getStock).sum()));

            // --- 1. CLIENTE TOP (El que más ha gastado) ---
            Map<Integer, Double> comprasPorCliente = ventasCompletadas.stream()
                .collect(Collectors.groupingBy(
                    p -> p.getCliente().getId(),
                    Collectors.summingDouble(p -> p.getItems().stream().mapToDouble(i -> i.getCantidad() * i.getPrecioUnitario()).sum())
                ));

            String clienteTop = "---";
            if (!comprasPorCliente.isEmpty()) {
                Map.Entry<Integer, Double> maxCliente = comprasPorCliente.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
                
                if (maxCliente != null) {
                    clienteTop = clientes.stream()
                        .filter(c -> c.getId() == maxCliente.getKey())
                        .findFirst()
                        .map(c -> c.getNombre() + " " + c.getApellido())
                        .orElse("Desconocido");
                }
            }
            // Reutilizamos el label de promedio para el nombre del cliente
            dashboardView.lblPromedioVenta.setText(clienteTop); 

            // --- 2. PRODUCTO MÁS VENDIDO (Por Cantidad) ---
            Map<Integer, Integer> cantidadPorProducto = ventasCompletadas.stream()
                .flatMap(p -> p.getItems().stream())
                .collect(Collectors.groupingBy(
                    i -> i.getProducto().getId(),
                    Collectors.summingInt(ItemPedido::getCantidad)
                ));
            
            String prodTop = "---";
            if (!cantidadPorProducto.isEmpty()) {
                Map.Entry<Integer, Integer> maxProd = cantidadPorProducto.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
                
                if (maxProd != null) {
                    prodTop = productos.stream()
                        .filter(p -> p.getId() == maxProd.getKey())
                        .findFirst()
                        .map(Producto::getNombre)
                        .orElse("---");
                    prodTop += " (" + maxProd.getValue() + ")"; // Ej: "Camisa (15)"
                }
            }
            dashboardView.lblProductoMasVendido.setText(prodTop);

            // ACTUALIZAR GRÁFICOS REALES
            
            // 1. Ingresos (Últimas 5 ventas completadas)
            List<Double> dataIngresos = new ArrayList<>();
            List<String> labelIngresos = new ArrayList<>();
            for(int i = 0; i < Math.min(5, ventasCompletadas.size()); i++) {
                Pedido p = ventasCompletadas.get(i);
                double tot = p.getItems().stream().mapToDouble(it->it.getCantidad()*it.getPrecioUnitario()).sum();
                dataIngresos.add(tot);
                labelIngresos.add("P-" + p.getId());
            }
            dashboardView.chartIngresos.setData(dataIngresos, labelIngresos);

            // 2. Volumen Pedidos (Línea)
            List<Double> dataVol = new ArrayList<>();
            List<String> labelVol = new ArrayList<>();
            int count = 0;
            for(Pedido p : pedidos) {
                if(count++ >= 7) break; 
                double itemsCount = p.getItems().stream().mapToInt(ItemPedido::getCantidad).sum();
                dataVol.add(itemsCount);
                labelVol.add(String.valueOf(p.getId()));
            }
            dashboardView.chartPedidos.setData(dataVol, labelVol);

            // 3. Stock por Categoría
            Map<Categoria, Integer> stockPorCat = productos.stream()
                .collect(Collectors.groupingBy(Producto::getCategoria, Collectors.summingInt(Producto::getStock)));
            
            List<Double> dataCat = new ArrayList<>();
            List<String> labelCat = new ArrayList<>();
            stockPorCat.forEach((k, v) -> {
                dataCat.add(v.doubleValue());
                labelCat.add(k.toString());
            });
            dashboardView.chartMetodos.setData(dataCat, labelCat);

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarInventario() {
        int empId = usuarioLogueado.getEmprendimiento().getId();
        try {
            List<Producto> lista = productoDAO.listarPorEmprendimiento(empId);
            dashboardView.modeloInventario.setRowCount(0);
            for (Producto p : lista) {
                dashboardView.modeloInventario.addRow(new Object[]{p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock(), p.getCategoria()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarClientes() {
        int empId = usuarioLogueado.getEmprendimiento().getId();
        try {
            List<Cliente> lista = clienteDAO.listarClientes(empId);
            dashboardView.modeloClientes.setRowCount(0);
            for (Cliente c : lista) {
                dashboardView.modeloClientes.addRow(new Object[]{c.getId(), c.getDNI(), c.getNombre() + " " + c.getApellido(), c.getNumero(), c.getDireccion(), c.getCalificacion()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- MÉTODOS DE APOYO ---
    private void autenticar(String correo, String pass) {
        try {
            usuarioLogueado = usuarioDAO.login(correo, pass);
            if (usuarioLogueado != null) {
                Emprendimiento emp = empDAO.obtenerPorUsuario(usuarioLogueado.getId());
                if(emp != null) {
                    usuarioLogueado.setEmprendimiento(emp);
                    cargarDatosDashboard();
                    actualizarTitulosVisuales(); // Asegurar que se actualicen los títulos al entrar
                    mainFrame.mostrarPanel("DASHBOARD");
                } else JOptionPane.showMessageDialog(mainFrame, "Sin negocio configurado.");
            } else JOptionPane.showMessageDialog(mainFrame, "Credenciales incorrectas.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void registrarUsuarioYNegocio() {
        // ... (Misma lógica previa de registro)
        if(registroPanel.txtDni.getText().isEmpty() || registroPanel.txtCorreo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "DNI y Correo obligatorios."); return;
        }
        try {
            Usuario u = new Usuario(0, registroPanel.txtCorreo.getText(), new String(registroPanel.txtPass.getPassword()), null);
            u.setDNI(registroPanel.txtDni.getText()); u.setNombre(registroPanel.txtNombre.getText()); u.setApellido(registroPanel.txtApellido.getText());
            u.setNumero(registroPanel.txtTelefono.getText()); u.setDireccion(registroPanel.txtDireccionUser.getText());
            try { u.setEdad(Integer.parseInt(registroPanel.txtEdad.getText())); } catch (Exception e) { u.setEdad(0); }
            usuarioDAO.crearUsuario(u); 
            Emprendimiento emp = new Emprendimiento(registroPanel.txtNombreEmp.getText(), registroPanel.txtDescEmp.getText());
            empDAO.registrarEmprendimiento(emp, u.getId());
            JOptionPane.showMessageDialog(mainFrame, "Cuenta creada.");
            limpiarCamposRegistro();
            mainFrame.mostrarPanel("LOGIN");
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
    
    private void cerrarSesion() { usuarioLogueado = null; loginView.txtPass.setText(""); mainFrame.mostrarPanel("LOGIN"); }
    private void limpiarCamposRegistro() { registroPanel.txtCorreo.setText(""); registroPanel.txtPass.setText(""); }
}