/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package medidordeenergia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author Caio Barroso, Kreisler Brenner, Raphael Santos
 */
public class GerenciadorMedidor {
    /**
     * ArrayList com espaçamentos de Hora
     */
     public ArrayList<Medidor> listaDadosHora = new ArrayList();
     /**
     * ArrayList com espaçamentos de Dia
     */
     public ArrayList<Medidor> listaDadosDia = new ArrayList();
     /**
     * ArrayList com espaçamentos de Mes
     */
     public ArrayList<Medidor> listaDadosMes = new ArrayList();
   
    
      /**Este método importa os dados contidos em um arquivo CSV
      * especifico do tipo Medidor
      * @param nomeArq - Nome do arquivo CSV
      * @throws FileNotFoundException 
      */  
      public void importarCSV(String nomeArq) throws FileNotFoundException{
       
       ArrayList aux = new ArrayList();
       try{
            Scanner ler = new Scanner(new File(nomeArq)); 
            String[] valores;
            
            while (ler.hasNext()){
                String x = ler.nextLine();
                valores = x.split(";");
                aux.add(new Medidor((valores[0]), 
                                    (valores[1]), 
                  Double.parseDouble(valores[2])/1000)); // Converter de watts para kwh
            }
            listaDadosHora = aux;
            
       }
       catch(FileNotFoundException erro){
           JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "O Arquivo  \""+nomeArq+"\"  não foi encontrado", "Erro", JOptionPane.WARNING_MESSAGE, null);
       }
       
       
    }
      
     /** Este Método calcula o consumo total de Kw
     *  em uma data especifica
     * @param data - Data - Formato: dd/mm/aaaa
     * @return consumo total de Kw em um dia especificado
     */
    public double consumoDiarioKW(String data){
        double consumoTotalDia = 0;
        for(int i = 0; i< listaDadosHora.size(); i++){            
            if(listaDadosHora.get(i).getData().equals(data)){
                consumoTotalDia += listaDadosHora.get(i).getKwh();
            }
        }
        return consumoTotalDia;
    }
    
    /** Este Método calcula o consumo total de Kw
     *  em um mês especifico
     * 
     * @param mes mm
     * @param ano aaaa
     * @return consumo total de Kw em um mes especificado
     */
     public double consumoMensalKW(String mes, String ano){
        String[] valor;
        double consumoTotalMes = 0;
        for(int i = 0; i< listaDadosHora.size(); i++){
            valor = listaDadosHora.get(i).getData().split("/");
            if((valor[1].equals(mes))&&(valor[2].equals(ano))){ 
                consumoTotalMes += listaDadosHora.get(i).getKwh();
            }
        }
        return consumoTotalMes;
    }
     
  
    /**
     * Este método retorna  uma  ArrayList  com 
     * dados dos medidores  em hora relativos ao 
     * intervalo de tempo expecificado.
     * @param dataI  Data Inicial - Formato: dd/mm/aaaa
     * @param horaI  Hora Inicial - Formato: hh:mm
     * @param dataF  Data Final   - Formato: dd/mm/aaaa
     * @param horaF  Hora Final   - Formato: hh:mm
     * @return ArrayList das medições encontradas no intervalo.
     */
    public ArrayList<Medidor> consultarDadosEntreDatasPorHora(String dataI, String horaI, String dataF, String horaF){
        ArrayList <Medidor> dados = new ArrayList();
        int a;
        //Percorre ate a data e hora inicial
        for(int i = 0; i<listaDadosHora.size(); i++){
            if((listaDadosHora.get(i).getData().equals(dataI))&&(listaDadosHora.get(i).getHora().equals(horaI))){
                //Adiciona os valores à ArrayList ate a data e hora final
                for(a = i; a<listaDadosHora.size(); a++){
                   dados.add(listaDadosHora.get(a)) ;
                   if((listaDadosHora.get(a).getData().equals(dataF)) && (listaDadosHora.get(a).getHora().equals(horaF))){
                      return dados;
                   }
                }
            }
        }
        return null;
        
    }

    /**Este método retorna  uma  ArrayList  com 
     * dados dos medidores  em dia relativos ao 
     * intervalo de tempo expecificado.
     *  
     * @param dataI  Data Inicial - Formato: dd/mm/aaaa
     * @param dataF  Data Final - Formato: dd/mm/aaaa
     * @return ArrayList das medições encontradas no intervalo.
     */
    public ArrayList<Medidor> consultarDadosEntreDatasPorDia(String dataI, String dataF){
        ArrayList <Medidor> dados = listaDadosDia;
        ArrayList<Medidor> aux = new ArrayList();
        int a;
        //Percorre ate a data inicial
        for(int i = 0; i<dados.size(); i++){
            if(dados.get(i).getData().equals(dataI)){
                //Adiciona os valores à ArrayList ate a data final
                for(a = i; a<dados.size(); a++){
                    aux.add(new Medidor(dados.get(a).getData(), dados.get(a).getKwh()));
                    if(dados.get(a).getData().equals(dataF)){
                    return aux;
                   }
                }
            }
        }
        return null;
    }
     
    /**Este método retorna  uma  ArrayList  com 
     * dados dos medidores mensais relativos ao 
     * intervalo de tempo expecificado.
     *  
     * @param dataI Data Inicial - Formato: mm/aaaa
     * @param dataF Data Final - Formato: mm/aaaa
     * @return ArrayList das medições encontradas no intervalo.
     */
     public ArrayList<Medidor> consultarDadosEntreDatasPorMes(String dataI, String dataF){
        ArrayList <Medidor> dados = listaDadosMes;
        ArrayList<Medidor> aux = new ArrayList();
        int a;
        
        //Percorre ate a data inicial
        for(int i = 0; i<dados.size(); i++){
            if(dados.get(i).getData().equals(dataI)){
                //Adiciona os valores à ArrayList ate a data e hora final
                for(a = i; a<dados.size(); a++){
                   aux.add(new Medidor(dados.get(a).getData(), dados.get(a).getKwh()));
                   if(dados.get(a).getData().equals(dataF)){
                        return aux;
                   }
                }
            }
        }
        return null;
     }
     
    
    
    /**Este método converte uma ArrayList do tipo Medidor
     * onde os intervalos de medidas são dados por hora
     * para uma ArrayList com intervalos de dia
     * 
     */
    public void converterListaHoraParaDia(){
        ArrayList <Medidor> dados = this.listaDadosHora;
        ArrayList<Medidor> aux = new ArrayList();
        double consumoDia;
       
            for(int i = 0; i<dados.size(); i+=24){
                
                consumoDia = consumoDiarioKW(dados.get(i).getData());
                aux.add(new Medidor(dados.get(i).getData(), consumoDia)); 
                
            }
        this.listaDadosDia = aux;
    }
     
    /**Este método converte uma ArrayList do tipo Medidor
     * onde os intervalos de medidas são dados por dia
     * para uma ArrayList com intervalos mensais
     * 
     */
     public void converterListaDiaParaMes(){
        ArrayList <Medidor> dados = this.listaDadosDia;
        ArrayList<Medidor> aux = new ArrayList();
        double consumoMes;
        String[] data;
        String mesAtual = "0";
       
            for(int i = 0; i<dados.size(); i++){
                data = dados.get(i).getData().split("/"); // Separar dia/mes/ano em variaveis diferentes
                      if (!mesAtual.equals(data[1])){ // Se a condiçao de mesAtual for diferente do mes
                      consumoMes = consumoMensalKW(data[1], data[2]); //data 1 = mes  data 2 = ano
                      aux.add(new Medidor(data[1]+"/"+data[2], consumoMes)); 
                      mesAtual = data[1];
                }
            }
        this.listaDadosMes = aux;
    }
    
     /**Este  método  calcula  a Média de Kwh  dos 
     * dados dos medidores relativos ao intervalo 
     * de tempo em horas.
     * @param dataI Data Inicial - Formato: dd/mm/aaaa
     * @param horaI Hora Inicial - Formato: hh:mm
     * @param dataF Data Final   - Formato: dd/mm/aaaa
     * @param horaF Hora Final   - Formato: hh:mm
     * @return - Media de Kwh entre o intervalo de tempo em horas
     */
    public double mediaDeKwhPorIntervaloDeHora(String dataI, String horaI, String dataF, String horaF){
         double totalKwh = 0;
         ArrayList<Medidor> aux = consultarDadosEntreDatasPorHora(dataI, horaI, dataF, horaF);
         for(Medidor m : aux) {
            totalKwh += m.getKwh();
         }
         return totalKwh/aux.size();
    }
    
    /**
     * Este  método  calcula  a Média de Kwh  dos 
     * dados dos medidores relativos ao intervalo 
     * de tempo em dias.
     * @param dataI Data Inicial - Formato: dd/mm/aaaa
     * @param dataF Data Inicial - Formato: dd/mm/aaaa
     * @return - Media de Kwh entre o intervalo de tempo em dias.
     */
     public double mediaDeKwhPorIntervaloDeDia(String dataI, String dataF){
         double totalKwh = 0;
         ArrayList<Medidor> aux = consultarDadosEntreDatasPorDia(dataI, dataF);
         for (Medidor m : aux) {
            totalKwh += m.getKwh();
         }
        
         return totalKwh/aux.size();
    }
     
     
     /**Este  método  calcula  a Média de Kwh  dos 
      * dados dos medidores relativos ao intervalo 
      * de tempo em meses.
      * @param dataI Data Inicial - Formato: mm/aaaa
      * @param dataF Data Final - Formato: mm/aaaa
      * @return  Media de Kwh entre o intervalo de tempo em meses.
      */
     public double mediaDeKwhPorIntervaloDeMes(String dataI, String dataF){
         double totalKwh = 0;
         ArrayList<Medidor> aux = consultarDadosEntreDatasPorMes(dataI, dataF);
         for (Medidor m : aux) {
            totalKwh += m.getKwh();
         }
         return totalKwh/aux.size();
    }
   
    
    /** Este método gera uma lista com o(s) maximo(s)
     * valores encontrados em intervalos de hora em 
     * um tempo especificado.
     * @param dataI Data Inicial - Formato: dd/mm/aaaa
     * @param horaI Hora Inicial - Formato: hh:mm
     * @param dataF Data Final - Formato: dd/mm/aaaa
     * @param horaF Hora Final - Formato: hh:mm
     * @return lista com medidor de maior valor em kw encontrados em intervalos de hora
     */
    public ArrayList<Medidor> maximosDeKwPorIntervaloDeHora(String dataI, String horaI, String dataF, String horaF){
        double maximoKw = 0;
        ArrayList<Medidor> listaM = new ArrayList();
        ArrayList<Medidor> aux = consultarDadosEntreDatasPorHora(dataI, horaI, dataF, horaF);
        for (Medidor m : aux){
           if (m.getKwh()>maximoKw){
               maximoKw = m.getKwh();  
           }     
        }
        for (Medidor m : aux) {
           if (m.getKwh()==maximoKw){
               listaM.add(m);  
           }     
        }
        return listaM;
    }
    
    /**Este método gera uma lista com o(s) minimo(s)
     * valores encontrados em intervalos de hora em 
     * um tempo especificado.
     * 
     * @param dataI Data Inicial - Formato: dd/mm/aaaa
     * @param horaI Hora Inicial - Formato: hh:mm
     * @param dataF Data Final - Formato: dd/mm/aaaa
     * @param horaF Hora Final - Formato: hh:mm
     * @return lista com medidor de menor valor em kw encontrados em intervalos de hora
     */
    public ArrayList<Medidor> minimosDeKwPorIntervaloDeHora(String dataI, String horaI, String dataF, String horaF){
        double minimoKw = 1000;
        ArrayList<Medidor> listaM = new ArrayList();
         ArrayList<Medidor> aux = consultarDadosEntreDatasPorHora(dataI, horaI, dataF, horaF);
        for (Medidor m : aux) {
           if (m.getKwh()<minimoKw){
               minimoKw = m.getKwh();  
           }     
        }
         for (Medidor m : aux) {
           if (m.getKwh()==minimoKw){
                listaM.add(m);  
           }     
        }
        return listaM;
    }
    
    
    /**Este método gera uma lista com o(s) máximo(s)
     * valores encontrados em intervalos de dia em 
     * um tempo especificado.
     * 
     * @param dataI Data Final - Formato: dd/mm/aaaa
     * @param dataF Data Final - Formato: dd/mm/aaaa
     * @return lista com medidor de maior valor em kw encontrados em intervalos de dia
     */
     public ArrayList<Medidor>  maximosDeKwPorIntervaloDeDia(String dataI, String dataF){
        double maximoKw = 0;
        ArrayList<Medidor> listaM = new ArrayList();
        ArrayList<Medidor> aux = consultarDadosEntreDatasPorDia(dataI, dataF);
        for (Medidor m : aux){ 
           
           if (m.getKwh()>maximoKw){
               maximoKw = m.getKwh();   
           }     
        }
        for (Medidor m : aux){
           if (maximoKw == m.getKwh()){
               listaM.add(m);
           }     
        }
        return listaM;
    }
     
     /**Este método gera uma lista com o(s) minimo(s)
      * valores encontrados em intervalos de dia em 
      *  um tempo especificado.
      * 
      * @param dataI Data Inicial - Formato: dd/mm/aaaa
      * @param dataF Data Final - Formato: dd/mm/aaaa
      * @return lista com medidor de menor valor em kw encontrados em intervalos de dia
      */
      public ArrayList<Medidor>  minimosDeKwPorIntervaloDeDia(String dataI, String dataF){
        double minimoKw = 1000;
        ArrayList<Medidor> listaM = new ArrayList();
        ArrayList<Medidor> aux = consultarDadosEntreDatasPorDia(dataI, dataF);
        for (Medidor m : aux){ 
          
           if (m.getKwh()<minimoKw){
               minimoKw = m.getKwh();   
           }     
        }
        for (Medidor m : aux){
           if (minimoKw == m.getKwh()){
               listaM.add(m);
           }     
        }
        return listaM;
    }
      /**Este método gera uma lista com o(s) máximo(s)
       * valores encontrados em intervalos de meses em 
       * um tempo especificado.
       * @param dataI Data Inicial - Formato: mm/aaaa
       * @param dataF Data Final - Formato: mm/aaaa
       * @return lista com medidor de maior valor em kwh encontrados em intervalos de mes
       */
       public ArrayList<Medidor>  maximosDeKwPorIntervaloDeMes(String dataI, String dataF){
        double maximoKw = 0;
        ArrayList<Medidor> listaM = new ArrayList();
        ArrayList<Medidor> aux = consultarDadosEntreDatasPorMes(dataI, dataF);
        for (Medidor m : aux){ 
           
           if (m.getKwh()>maximoKw){
               maximoKw = m.getKwh();   
           }     
        }
        for (Medidor m : aux){
           if (maximoKw == m.getKwh()){
               listaM.add(m);
           }     
        }
        return listaM;
    }
     
       /**Este método gera uma lista com o(s) mínimo(s)
       * valores encontrados em intervalos de meses em 
       * um tempo especificado.
       * @param dataI Data Inicial - Formato: mm/aaaa
       * @param dataF Data Final - Formato: mm/aaaa
       * @return lista com medidor de menor valor em kw encontrados no intervalo de mes
       */
      public ArrayList<Medidor>  minimosDeKwPorIntervaloDeMes(String dataI, String dataF){
        double minimoKw = 10000000;
        ArrayList<Medidor> listaM = new ArrayList();
        ArrayList<Medidor> aux = consultarDadosEntreDatasPorMes(dataI, dataF);
        for (Medidor m : aux){ 
          
           if (m.getKwh()<minimoKw){
               minimoKw = m.getKwh();   
           }     
        }
        for (Medidor m : aux){
           if (minimoKw == m.getKwh()){
               listaM.add(m);
           }     
        }
        return listaM;
    }
     

}
