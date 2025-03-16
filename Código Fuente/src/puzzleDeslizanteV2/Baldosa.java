package puzzleDeslizanteV2;

import java.io.Serializable;

public class Baldosa implements Serializable {

    private int posicion;
    private boolean vacia;

    public Baldosa(boolean vacia, int posicion){
        this.vacia = vacia;
        this.posicion = posicion;
    }

    public void vaciar(){
        vacia = true;
    }

    public boolean isVacia(){
        return vacia;
    }

    public int getPosicion() {
        return posicion;
    }
}
