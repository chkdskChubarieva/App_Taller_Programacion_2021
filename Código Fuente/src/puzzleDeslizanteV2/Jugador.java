/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzleDeslizanteV2;

import java.io.Serializable;

/**
 *
 
 */
public class Jugador implements Serializable{
    String nombre;
    int movimientos;
    int horas,min,seg,puntaje;
    
    public Jugador(String nom,int movimientos,int horas,int min,int seg){
        nombre = nom;
        this.movimientos = movimientos;
        this.horas=horas;
        this.min=min;
        this.seg=seg;

    }
    
    public int getPuntaje(){
        //00:00:00
        puntaje=(horas*10000)+(min*100)+seg;
        puntaje=movimientos*puntaje;
        return puntaje;
    }

    public String toString(){
        
        if (nombre.length()>10) {
            return "        "+nombre.substring(0, 10)+"."+"   "+movimientos+"                 "+horas+":"+min+":"+seg;
        } else if (nombre.length()<10 && nombre.length()>5) {
                return "        "+nombre+"     "
                        + "         "+movimientos+"                 "+horas+":"+min+":"+seg; 
        } else {
            return "        "+nombre+"          "
                        + "         "+movimientos+"                 "+horas+":"+min+":"+seg; 
        }
    }
}
