/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controladores;

import connection.Connection;
import dao.UsuariosJpaController;
import java.io.Serializable;
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

@ManagedBean(name="ControladorInicio")
@SessionScoped
public class ControladorInicio implements Serializable{
    
    
    public EntityManagerFactory factory;
    public String login;
    public String password;
    
    public ControladorInicio(){}
    
    public void inicializar() {       
        factory = Connection.getEmf();
    }
    
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password){
        this.password=password;
    }
    
    public String registrar(){
    
    String registro= "registrarse?faces-redirect=true";    
    
    return registro;
    }
    
    public String regresar(){
    
    String registro= "index?faces-redirect=true";    
    
    return registro;
    }
    
    public String validar(){
        
        String valor= "error?faces-redirect=true";
        
        UsuariosJpaController daoUsuarios= new UsuariosJpaController(this.factory);
        Usuarios u=daoUsuarios.findUsuarios(this.login);
        if(u!=null){
        
            if(this.password.equalsIgnoreCase(u.getPass())){
                
            valor= "usuario?faces-redirect=true"; 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", login)); 
            return valor;
            }
            else{
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"login error","contraseña o usuario invalido")); 
             
            }     
        }
        else{
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"login error","contraseña o usuario invalido")); 
                     
        }
      return valor;
    }
}
