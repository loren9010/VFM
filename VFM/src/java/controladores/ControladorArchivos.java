/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import connection.Connection;
import dao.ArchivosJpaController;
import dao.UsuariosJpaController;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import modelo.Archivos;
import modelo.ArchivosPK;
import modelo.Usuarios;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Fabian
 */
@ManagedBean(name = "ControladorArchivos")
@SessionScoped
public class ControladorArchivos implements Serializable {

    private final String destination = "C:\\Users\\Public\\Documents\\Sispro\\";
    private UploadedFile file;
    private StreamedContent descargas;
    private EntityManagerFactory factory;
    private Archivos archivo;
    private List<Archivos> listaArchivos;
    private List<Archivos> listaArchivosCompartidos;
    private List<Archivos> FiltroArchivos;
    private String Usuario;

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String Usuario) {
        this.Usuario = Usuario;
    }
    
    public List<Archivos> getListaArchivosCompartidos() {
        return listaArchivosCompartidos;
    }

    public void setListaArchivosCompartidos(List<Archivos> listaArchivosCompartidos) {
        this.listaArchivosCompartidos = listaArchivosCompartidos;
    }

    public StreamedContent getDescargas() {
        return descargas;
    }

    public void setDescargas(StreamedContent descargas) {
        this.descargas = descargas;
    }

    public List<Archivos> getFiltroArchivos() {
        return FiltroArchivos;
    }

    public void setFiltroArchivos(List<Archivos> FiltroArchivos) {
        this.FiltroArchivos = FiltroArchivos;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Archivos getArchivo() {
        return archivo;
    }

    public void setArchivo(Archivos archivo) {
        this.archivo = archivo;
    }

    public List<Archivos> getListaArchivos() {
        return listaArchivos;
    }

    public void setListaArchivos(List<Archivos> listaArchivos) {
        this.listaArchivos = listaArchivos;
    }

    public void handleFileUpload(FileUploadEvent event) {
        setFile(event.getFile());
        try {
            
            transferFile(getFile().getFileName(), getFile().getInputstream());
            subirArchivos();
            
        } catch (IOException ex) {
            Logger.getLogger(ControladorArchivos.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext contex = FacesContext.getCurrentInstance();
            contex.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "falla al subir archivo",getFile().getFileName()));
        }
        FacesContext contex = FacesContext.getCurrentInstance();
        contex.addMessage(null, new FacesMessage("Terminado", "archivo subido con exito"));
    }

    public ControladorArchivos() {
    }

    public void transferFile(String FileName, InputStream in) {
        
        ControladorInicio controla = (ControladorInicio) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("ControladorInicio");
        String nombre= controla.getLogin();
        try {
            OutputStream out = new FileOutputStream(new File(destination+nombre+"\\" + FileName));
            int reader = 0;
            byte[] bytes = new byte[(int) getFile().getSize()];
            try {
                while ((reader = in.read(bytes)) != -1) {
                    out.write(bytes, 0, reader);
                }
                in.close();
                out.flush();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(ControladorArchivos.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ControladorArchivos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void subirArchivos() {

        factory = Connection.getEmf();

        ArchivosJpaController daoArchivos = new ArchivosJpaController(factory);
        UsuariosJpaController daoUsuario = new UsuariosJpaController(factory);
        Archivos a = new Archivos();
        ArchivosPK pk = new ArchivosPK();
        pk.setNombrearchivo(getFile().getFileName());
        ControladorInicio controla = (ControladorInicio) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("ControladorInicio");
        String nombre= controla.getLogin();
        pk.setNombreusuario(nombre);
        a.setPropietario(Boolean.TRUE);
        a.setUsuarios(daoUsuario.findUsuarios(controla.getLogin()));
        a.setArchivosPK(pk);
        a.setRutaarchivo(destination+nombre+"\\" + getFile().getFileName());
        a.setTam((int) getFile().getSize() + "");
        try {
            daoArchivos.create(a);
        } catch (PreexistingEntityException ex) {
            Logger.getLogger(ControladorArchivos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ControladorArchivos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarArchivos() {

        factory = Connection.getEmf();
        ArchivosJpaController daoArchivos = new ArchivosJpaController(factory);
        ControladorInicio controla = (ControladorInicio) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("ControladorInicio");
        String login = controla.getLogin();
        List<Archivos> listaAux = daoArchivos.findArchivosEntities();
        List<Archivos> listaFinal = new ArrayList<Archivos>();

        for (int i = 0; i < listaAux.size(); i++) {

            if (listaAux.get(i).getArchivosPK().getNombreusuario().equals(login) && listaAux.get(i).getPropietario()==true) {

                listaFinal.add(listaAux.get(i));
                System.out.println(listaAux.get(i).getArchivosPK().getNombreusuario());
            }

        }
        setListaArchivos(listaFinal);

    }
    
    public void cargarArchivosCompartidos(){
        
        factory = Connection.getEmf();
        ArchivosJpaController daoArchivos = new ArchivosJpaController(factory);
      
        ControladorInicio controla = (ControladorInicio) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("ControladorInicio");
        String login = controla.getLogin();
        List<Archivos> listaAux = daoArchivos.findArchivosEntities();
        List<Archivos> listaFinal = new ArrayList<Archivos>();

        for (int i = 0; i < listaAux.size(); i++) {

            if (listaAux.get(i).getArchivosPK().getNombreusuario().equals(login) && listaAux.get(i).getPropietario()==false) {

                listaFinal.add(listaAux.get(i));
            }

        }
        setListaArchivosCompartidos(listaFinal);
    }
    
    public void compartir() {
    
     factory= Connection.getEmf();
     ArchivosJpaController daoArchivos= new ArchivosJpaController(factory);
     UsuariosJpaController daoUsuario = new UsuariosJpaController(factory);
     System.out.println(archivo.getArchivosPK().getNombrearchivo());
     System.out.println(Usuario);
        Archivos a = new Archivos();
        ArchivosPK pk = new ArchivosPK();
        pk.setNombrearchivo(getArchivo().getArchivosPK().getNombrearchivo());
        pk.setNombreusuario(Usuario);
        a.setPropietario(Boolean.FALSE);
        a.setUsuarios(daoUsuario.findUsuarios(Usuario));
        a.setArchivosPK(pk);
        a.setRutaarchivo(destination+Usuario+"\\" + archivo.getArchivosPK().getNombrearchivo());
        a.setTam(archivo.getTam());
        try {
            daoArchivos.create(a);
            FacesContext contex = FacesContext.getCurrentInstance();
        contex.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo compartido con exito",archivo.getArchivosPK().getNombrearchivo()));
            
        } catch (Exception ex) {
            Logger.getLogger(ControladorArchivos.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public List<String> buscarUsuario(String entrada){
        factory = Connection.getEmf();
        UsuariosJpaController daoUsuarios = new UsuariosJpaController(factory);
        List<Usuarios> ListaUsuarios= daoUsuarios.findUsuariosEntities();
        List<String> listNombres= new ArrayList<String>();
        for(int i=0; i<ListaUsuarios.size(); i++){
        
        if(ListaUsuarios.get(i).getLogin().startsWith(entrada)){
            listNombres.add(ListaUsuarios.get(i).getLogin());
        }
        
        }
        return listNombres;
    
    }

    public void descargar() throws FileNotFoundException {
        try{
        File archivos = new File(getArchivo().getRutaarchivo());
        InputStream stream = new FileInputStream(archivos);
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        StreamedContent aux = new DefaultStreamedContent(stream, externalContext.getMimeType(archivos.getName()), archivos.getName());
        setDescargas(aux);
        }
        catch(Exception e){
            FacesContext contex = FacesContext.getCurrentInstance();
        contex.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "debe seleccionar un archivo","archivo no encontrado"));
              }
        
    }
    public void borrarArchivo(){
    factory= Connection.getEmf();
    ArchivosJpaController daoArchivos= new ArchivosJpaController(factory);
    List<Archivos> listaArchivos=daoArchivos.findArchivosEntities();
        try {
            for(int i=0; i<listaArchivos.size();i++){
            if(listaArchivos.get(i).getArchivosPK().getNombrearchivo().equals(archivo.getArchivosPK().getNombrearchivo())){
            daoArchivos.destroy(listaArchivos.get(i).getArchivosPK());
           
            }
            }
            
            File file = new File(archivo.getRutaarchivo());
            file.delete();
            setListaArchivos(daoArchivos.findArchivosEntities());
            FacesContext contex = FacesContext.getCurrentInstance();
            contex.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "archivo borrado con exito",archivo.getArchivosPK().getNombrearchivo()));
        
        } catch (NonexistentEntityException ex) {
        FacesContext contex = FacesContext.getCurrentInstance();
        contex.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "debe seleccionar un archivo","archivo no encontrado"));
        }
    
    
    
    }
}
