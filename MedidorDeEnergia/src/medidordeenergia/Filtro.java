/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package medidordeenergia;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 *@author Caio Barroso, Kreisler Brenner, Raphael Santos
 */
public class Filtro extends FileFilter{

    private String[] formato;
  
    private char Dotindex = '.';  
   
    public Filtro(String[] formato){
        this.formato = formato;
    }

    @Override
    public boolean accept(File f) {
      if(f.isDirectory()){
          return true;
      }
      for(int i = 0; i<3; i++){
         if((extension(f).equalsIgnoreCase(formato[i]))){
            return true;
         }   
      }
      return false;
    }
     

    @Override
    public String getDescription() {
       
        return "Arquivos "+formato[0]+"; "+ formato[1]+"; "+ formato[2];
        
    }
    public String extension(File f){
        String fileName = f.getName();
        int IndexFile = fileName.lastIndexOf(Dotindex);
        if (IndexFile>0&& IndexFile<fileName.length()-1){
            return fileName.substring(IndexFile+1);
        }
        else{
            return "";
        }
    
    }
}