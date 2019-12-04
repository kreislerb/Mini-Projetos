/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package medidordeenergia;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 *@author Caio Barroso, Kreisler Brenner, Raphael Santos
 */
public class LeitorArquivoBinario {
     private ObjectInputStream objetoLeitura;
    
    public void abriArquivoLeitura(String nomeArquivo) {
        
        try{
            FileInputStream arquivoLeitura = new FileInputStream(nomeArquivo);
            objetoLeitura = new ObjectInputStream(arquivoLeitura);
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "Erro ao abrir o arquivo","Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void fecharArquivoLeitura(){
        try{
            if (objetoLeitura != null){
                objetoLeitura.close();
            }
        }
        catch (IOException e){
            JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "Erro ao fechar o arquivo","Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    public ArrayList obterObjetoArquivo(){
      
        ArrayList objetosLidos = new ArrayList();
        try{
            while(true){
                objetosLidos.add(objetoLeitura.readObject());   
            }
        }
       
        catch(EOFException e){
            return objetosLidos;
        }
           
        catch (IOException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "Erro ao ler o arquivo","Aviso", JOptionPane.WARNING_MESSAGE);
        }
        return objetosLidos;
    }
    
}
