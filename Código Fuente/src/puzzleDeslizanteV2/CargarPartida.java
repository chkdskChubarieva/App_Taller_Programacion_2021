package puzzleDeslizanteV2;

import java.io.Serializable;

public class CargarPartida implements Serializable {
    public DatosGuardados datosGuardados;
    public Juego juego;
    public CargarPartida(DatosGuardados datosGuardados,Juego juego){
        this.datosGuardados=datosGuardados;
        this.juego=juego;
    }
    public Juego getJuego(){
        return juego;
    }
    public DatosGuardados getDatosGuardados(){
        return datosGuardados;
    }
    public void setDatosGuardados(DatosGuardados datos){
        datosGuardados=datos;
    }
    public void setJuego(Juego j){
        juego=j;
    }
}
