/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package medidordeenergia;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import static medidordeenergia.MedidorDeEnergia.gm;
/**
 *
 * @author Caio Barroso, Kreisler Brenner, Raphael Santos
 */
public class TratamentoDeErros {
    
    /**Este método verifica se uma data é válida
     * 
     *<pre>
     * Ex: 31/02/2015 retorna false pois fevereiro
     * nao possui 31 dias.
     *</pre>
     
     * @param data data no formato dd/mm/aaaa
     * @return 
     * <pre>
     * True se a data é válida 
     * False caso contrario.
     * </pre>
     */
    public boolean verificarDataValida(String data){
        
        String[] diaMesAno = data.split("/");
        int dia = Integer.parseInt(diaMesAno[0]);
        int mes = Integer.parseInt(diaMesAno[1]);
        int ano = Integer.parseInt(diaMesAno[2]);
  
                
        int[] mesP = new int[13];  //Dias que poussui o mes
        
        mesP[1]= 31; //Janeiro
        
        if(ano%4 == 0){
            mesP[2]= 29;  //Fevereiro se bissexto
        }
        else{
            mesP[2]=28; //Fevereiro 
        }
        mesP[3]= 31;//Marco
        mesP[4]= 30;//Abril 
        mesP[5]= 31;//Maio 
        mesP[6]= 30;//Junho 
        mesP[7]= 31;//Julho 
        mesP[8]= 31;//Agosto 
        mesP[9]= 30;//Setembro 
        mesP[10]= 31;//Outubro 
        mesP[11]= 30;//Novembro 
        mesP[12]= 31;//Dezembro 
        
        if(dia<=mesP[mes]){
            return true;
        }
        else{
      
            return false;
        }  
    }
    
    /**Este método verifica se as datas passadas estao dentro 
     * do intervalo da ArrayList refatorando-as nos seguintes
     * casos:
     * <pre>
     * (1)Somente a DataInicial no intervalo:
     *    Neste caso se a DataFinal é maior que a DataInicial
     *    definimos a DataFinal como a ultima data contida
     *    ArrayList
     * 
     * (2)Somente a DataFinal no Intervalo:
     *    Neste caso se a DataFinal é maior que a DataInicial
     *    definimos a DataInicial como a primeira data contida
     *    no ArrayList
     * 
     * (3)Nenhuma das datas estao no intervalo:
     *    Neste caso somente se a dataInicial for menor que 
     *    a primeira data contida no Arraylist, e a dataFinal
     *    for maior que ultima data contida no ArrayList, entao 
     *    definimos a dataInicial como a primeira data e a 
     *    dataFinal como a ultima data do ArrayList
     * 
     * (4)Demais ocasioes:
     *    Resulta mensagem para o usuário, informando o provável
     *    erro.
     * </pre>
     * @param dataI   dd/mm/aaaa ou mm/aaaa
     * @param dataF   dd/mm/aaaa ou mm/aaaa
     * @param horaI   hh:mm
     * @param horaF   hh:mm  
     * @return Vetor com datas refatoradas:
     * <pre>
     * [0] DataInicial - Data Inicial Refatorada 
     * [1] DataFinal - Data Final Refatorada 
     * [2] HoraInicial - para caso o filtro seja por hora
     * [3] HoraFinal - ´para caso o filtro seja por hora
     * </pre>
     */
    public String[] verificaIntervaloData(String dataI, String dataF, String horaI, String horaF){
       boolean condDataI = false;
       boolean condDataF = false;
       String[] DataRefatorada = new String[4];
       String primeiraData, ultimaData;
       ArrayList<Medidor> array;
       
       //Definir com qual arrayList usar dependendo do formato da data passada como parametro
       if((verificaFormatoData(dataI)==0) &&(verificaFormatoData(dataF)==0)){//Se a data esta no formato dd/mm/aaaa
        array = gm.listaDadosHora;
        primeiraData = array.get(0).getData();
        ultimaData = array.get(array.size()-1).getData();
       }
       else if((verificaFormatoData(dataI)==1) &&(verificaFormatoData(dataF)==1)){//Se a data esta no formato mm/aaaa
        array = gm.listaDadosMes;
        primeiraData = array.get(0).getData();
        ultimaData = array.get(array.size()-1).getData();
       }
       else{
           return null;
       }

        for(int i = 0; i<array.size(); i++){   //Verifica as datas passadas como parametro estao na ArrayList
            if(dataI.equals(array.get(i).getData())){
             condDataI = true;
    
            }
            if(dataF.equals(array.get(i).getData())){
             condDataF = true;
            }
        }
        //Se a maior data for a inicial entao resulta em erro
        if((compararMaiorData(dataI, dataF)).equals(dataI)){
           
            JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "O intervalo definido não é valido, verifique se a Data Inicial é a menor data!");
            return null;
        }
        //Se as duas datas forem iguais e a hora inicial maior que a hora final então resulta em erro
        else if((compararMaiorData(dataI,dataF).equals("iguais"))){
            
            if(comparaMaiorHora(horaI, horaF).equals(horaI)){
             
                JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "O intervalo definido não é valido, verifique se a Data Inicial é a menor data!");
                return null;
            }
        }
    
        
        if ((condDataI == true) && (condDataF == true)){ //Data Inicial e Data Final estão na ArrayList

            DataRefatorada[0] = dataI;
            DataRefatorada[1] = dataF;
            DataRefatorada[2] = horaI;
            DataRefatorada[3] = horaF;
            return DataRefatorada;
        }
        else if((condDataI == true)&&(condDataF == false)){//Somente a Data Inicial esta contida na ArrayList        

            if(compararMaiorData(dataI,dataF).equals(dataF)){
                DataRefatorada[0]= dataI;
                DataRefatorada[1]= ultimaData;
                DataRefatorada[3]= "23:00";
                return DataRefatorada;
            }
            else{
                
                JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "O intervalo definido não é valido, verifique se a Data Inicial é a menor data!");
                return null;
            }
        }
        else if((condDataI == false)&&(condDataF == true)){//Somente a Data Final esta contida na ArrayList        

            if(compararMaiorData(dataI,dataF).equals(dataF)){
                DataRefatorada[0]= primeiraData;
                DataRefatorada[1]= dataF;
                DataRefatorada[2]= "00:00";
                return DataRefatorada;
            }
            return null;

        }
        //Se nenhuma data esta contida na ArrayList
        else{
             //Se as datas estiverem em torno da ArrayList
             if(compararMaiorData(dataI,primeiraData).equals(primeiraData)){//Se a Data Inicial é menor que a primeira Data contida na Arraylist
                 if(compararMaiorData(dataF, ultimaData).equals(dataF)){//Se a Data Final é maior que a ultima Data contida na Arraylist
                     DataRefatorada[0]= primeiraData;
                     DataRefatorada[1]= ultimaData; 
                     DataRefatorada[2]= "00:00";
                     DataRefatorada[3]= "23:00";
                     return DataRefatorada;
                 }
             }
        }       
        //Outros casos
        JOptionPane.showMessageDialog(new FPrincipal().getRootPane(), "Não existe dados para este intervalo!");
        return null;
        
          
         
    }
    /**Este método compara duas datas e retorna a maior;
     * 
     * @param data1  dd/mm/aaaa ou mm/aaaa
     * @param data2  dd/mm/aaaa ou mm/aaaa
     * @return String da maior data
     */
    public String compararMaiorData(String data1,String data2){
        
        String[] x = data1.split("/");
        String[] y = data2.split("/");
     
        int i = 0;
        int[] dataX = new int[3];
        int[] dataY = new int[3];
       
        
            if((verificaFormatoData(data1)==0) &&(verificaFormatoData(data2)==0)){//dd/mm/aaaa
                i = 2;
                for(int a = 0; a<i+1;a++){
                   dataX[a]  = Integer.parseInt(x[a]);
                   dataY[a] = Integer.parseInt(y[a]);
                } 

            }
            else if((verificaFormatoData(data1)==1) && (verificaFormatoData(data2)==1)){//Formato mm/aaaa
                i = 1;
                for(int a = 0; a<i+1;a++){
                    dataX[a]  = Integer.parseInt(x[a]);
                    dataY[a] = Integer.parseInt(y[a]);
                }
            }
            else{
                return null;
            }
        
        try{
            // AnoX > AnoY
            if(dataX[i]>dataY[i]){
                return data1;
                
            }
            else if(dataX[i]<dataY[i]){
                return data2;
            }
            
            else{
                // MesX > MesY
                if(dataX[i-1]>dataY[i-1]){
                    return data1;
                }
                else if(dataX[i-1]<dataY[i-1]){
                    return data2;
                }
                else{
                     // DiaX > DiaY
                    if(dataX[i-2]> dataY[i-2]){
                        return data1;
                    }
                    else if(dataX[i-2]< dataY[i-2]){
                        return data2;
                    }
                    else {
                        
                        return "iguais"; //datas iguais
                    }
                }
            }
        }  
        /* Este catch foi utilizado para quando as datas estiverem no formato mm/aaaa:
           Isso fara com que retorne iguais, pois neste caso não fara a comparaçao diaX>diaY
           e os meses serao iguais.
        */
        catch(Exception erro){ 
            return "iguais";
        }
       
    }
    
    /**
     * Este método compara duas Strings de hora e retorna a maior
     * @param hora1 hh:mm
     * @param hora2 hh:mm
     * @return String com a maior hora e "iguais" caso sejam iguais
     */
    public String comparaMaiorHora(String hora1, String hora2){
        String[] aux = hora1.split(":");
        String[] aux2 = hora2.split(":");
        
        if(Integer.parseInt(aux[0])>Integer.parseInt(aux2[0])){
            return hora1;
        }
        else if(Integer.parseInt(aux[0])<Integer.parseInt(aux2[0])){
            return hora2;
        }
        return "iguais";
    }
    
    
    
    
  /**Este método verifica qual o formato em que a data 
   * se encontra
   * 
   * @param data String data
   * @return 
   * <pre>
   * 0  - formato = dd/mm/aaaa
   * 1  - formato = mm/aaaa
   * -1 - outro formato
   * </pre>
   */  
  public int verificaFormatoData(String data){
        String[] x = data.split("/");
         if(x.length==3){//Formato dd/mm/aaaa
             return 0;
         }
         else if(x.length==2){//Formato mm/aaaa
             return 1;    
         }
         else{
             return -1;
         }
    }
}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

