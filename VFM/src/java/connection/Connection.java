/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

/**
 *
 * @author Fabian
 */


import javax.persistence.EntityManagerFactory;  
import javax.persistence.Persistence;  
  
public abstract class Connection {  
    
//    protected static Connection conexion;
    protected static EntityManagerFactory emf;  
    public Connection(){  
        //emf = Persistence.createEntityManagerFactory("SISPRO2PU");       
    } 
    
    private static EntityManagerFactory createConnection(){
        emf = Persistence.createEntityManagerFactory("VFMPU");  
        return emf;
    }
    public static EntityManagerFactory getEmf() {
        if(emf== null){
           emf = createConnection();
        }
        return emf;
    }
      
}  
