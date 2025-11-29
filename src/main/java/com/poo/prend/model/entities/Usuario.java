package com.poo.prend.model.entities;

public class Usuario extends Persona {
    private int id; 
    private String correo; 
    private String contraseña; 
    private Emprendimiento emprendimiento; 

    public Usuario(int id, String correo, String contraseña, Emprendimiento emprendimiento) {
        this.id = id;
        this.correo = correo;
        this.contraseña = contraseña;
        this.emprendimiento = emprendimiento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Emprendimiento getEmprendimiento() {
        return emprendimiento;
    }

    public void setEmprendimiento(Emprendimiento emprendimiento) {
        this.emprendimiento = emprendimiento;
    }
}
