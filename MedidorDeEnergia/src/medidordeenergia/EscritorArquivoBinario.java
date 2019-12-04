/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package medidordeenergia;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Caio Barroso, Kreisler Brenner, Raphael Santos
 */
public class EscritorArquivoBinario {
    
    private ObjectOutputStream objetoEscrita;
    
    public void abriArquivoEscrita(String nomeArquivo) {
        
        try{
            FileOutputStream arquivoEscrita = new FileOutputStream(nomeArquivo);
            objetoEscrita = new ObjectOutputStream(arquivoEscrita);
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "Erro ao salvar o arquivo","Aviso", JOptionPane.WARNING_MESSAGE);
            
        }
    }
    
    public void fecharArquivoEscrita(){
        try{
            if (objetoEscrita != null){
                objetoEscrita.close();
            }
        }
        catch (IOException e){
            JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "Erro ao salvar o arquivo","Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    public void escreverObjetoArquivo(ArrayList objetos){
      
        try{
            for(int i= 0; i<objetos.size();i++){
              
                objetoEscrita.writeObject(objetos.get(i));
                objetoEscrita.flush();
            }    
             JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "Arquivo salvo com sucesso", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (IOException e){
             JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "Erro ao salvar o arquivo", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
        catch(NullPointerException e){
            JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "Não existe dados a serem salvos!\n\tO arquivo não foi criado.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    
}
