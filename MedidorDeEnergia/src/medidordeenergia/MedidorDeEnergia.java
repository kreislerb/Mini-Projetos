/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package medidordeenergia;

/**
 *
 * @author Caio Barroso, Kreisler Brenner, Raphael Santos
 */
public class MedidorDeEnergia {

    public static GerenciadorMedidor gm = new GerenciadorMedidor();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
      
        
        new FPrincipal().setVisible(true);

    }
    
}
