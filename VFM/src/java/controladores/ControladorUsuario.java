/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controladores;

import connection.Connection;
import dao.UsuariosJpaController;
import java.io.File;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import modelo.Usuarios;

/**
 *
 * @author Fabian
 */

@ManagedBean(name="ControladorUsuario")
@SessionScoped
public class ControladorUsuario implements Serializable{
    
    public EntityManagerFactory factory;
    public String login,password,nombre,apellido,correo,celular;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    
    
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCelular() {
        return celular;
    }
    
    
    
    public ControladorUsuario(){
    
    }
    
    public void registrarse(){
    
    factory = Connection.getEmf();
    UsuariosJpaController daoUsuario= new UsuariosJpaController(factory);
    Usuarios u= daoUsuario.findUsuarios(getLogin());
        System.out.println("va a registrar");
        if(u==null){
            System.out.println("entro a registrar");
            Usuarios nuevo= new Usuarios();
            nuevo.setNombre(getNombre());
            nuevo.setApellido(getApellido());
            nuevo.setCelular(getCelular());
            nuevo.setCorreo(getCorreo());
            nuevo.setLogin(getLogin());
            nuevo.setPass(getPassword());
        try {
            daoUsuario.create(nuevo);
            File carpeta= new File("C:\\Users\\Public\\Documents\\Sispro\\"+getLogin());
            carpeta.mkdir();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario creado con extito", "Bienvenido"+ getLogin())); 
            
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No se pudo crear el usuario", getLogin())); 
            
        }
        }
        else{
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "El usuario ya existe", getLogin())); 
            
        
        }
    
    }
    
    
}
