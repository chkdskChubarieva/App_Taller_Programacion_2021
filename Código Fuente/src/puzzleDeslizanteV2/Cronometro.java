/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzleDeslizanteV2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Cronometro {
    Timer tiempo;
    int segundos = 0;
    int minutos = 0;
    int horas = 0;
    
     public Cronometro() {        
        tiempo = new Timer(1000, acciones);
    }
     
     private ActionListener acciones = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
           etiquetaTiempo();
        }
    };
    
   public void etiquetaTiempo(){
            segundos ++;
            if(segundos == 60){
                minutos ++;
                segundos = 0;
            } 
            if(minutos == 60){
                horas ++;
                minutos = 0;
            }
            if(horas == 24){
                horas = 0;   
       }   
   }
   public void reiniciarTiempo(){
    if(tiempo.isRunning()){
            tiempo.stop();
        }
      
        segundos = 0;
        minutos = 0;
        horas = 0;
   }
}
