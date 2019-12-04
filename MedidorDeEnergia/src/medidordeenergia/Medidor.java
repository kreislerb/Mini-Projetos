/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package medidordeenergia;

import java.io.Serializable;
import java.text.DecimalFormat;


/**
 *
 * @author Caio Barroso, Kreisler Brenner, Raphael Santos
 */
public class Medidor implements Serializable{
    
private String data, hora;
private double kwh;
    
    public Medidor(String data, String hora, double kwh) {
        
        this.data = data;
        this.hora = hora;
        this.kwh = kwh;
    }
    /**
     * Este construtor é utilizado para dados que nao sejam filtrados por hora
     * @param data
     * @param kwh 
     */
     public Medidor(String data, double kwh) {
        this.data = data;
        this.kwh = kwh;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public double getKwh() {
        return kwh;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }
    
    /**
     * 
     * @return (Data  - kw ) se nao o medidor nao conter hora;
     *        e(Data - hora - kw)se o medidor conter a hora;
     */
    @Override
    public String toString() {
        DecimalFormat fmt = new DecimalFormat("0.00");
        if (hora==null){
            return data+"\t"+fmt.format(kwh)+ "Kw\n";  
        }
        return data+"\t"+ hora+" horas"+"\t"+ fmt.format(kwh)+"Kw\n";
                
    }



    
}
