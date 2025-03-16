package puzzleDeslizanteV2;

import java.io.Serializable;

public class Registro implements Serializable{
    private int x0, y0, x1, y1, pasos;
    private Registro anterior;
    
    public Registro() {
        pasos = 0;
        x0 = -1;
        y0 = -1;
        x1 = -1;
        y1 = -1;
        anterior = null;
    }

    private Registro(int x0, int y0, int x1, int y1, Registro ant, int pasos) {
        this.pasos = pasos;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        anterior = ant;
    }
    
  

    public boolean vacia() {
        return anterior == null;
    }

    public void registrar(int x0, int y0, int x1, int y1) {
        if (vacia()) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            pasos ++;
            anterior = new Registro();
        } else {
            if ((this.x0 == x0 && this.y0 == y0 && this.x1==x1 && this.y1==y1)||
                    (this.x0 == x1 && this.y0 == y1 && this.x1==x0 && this.y1==y0)){
                borrarRegistro();
            } else{
                anterior = new Registro(this.x0, this.y0, this.x1, this.y1, anterior, this.pasos);
                this.x0 = x0;
                this.y0 = y0;
                this.x1 = x1;
                this.y1 = y1;
                pasos ++;
            }
        }
    }

    private void borrarRegistro() {
        if (!vacia()) {
            
            x0 = anterior.x0;
            y0 = anterior.y0;
            x1 = anterior.x1;
            y1 = anterior.y1;
            pasos = anterior.pasos;
            anterior = anterior.anterior;
        }
    }

    public int getX0() {
        return x0;
    }

    public int getY0() {
        return y0;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }
    public int getPasos(){
        return pasos;
    }
    /*
    public void Imprimir() {
        if (!vacia()) {
            System.out.println("[" + x0 + "]" + "[" + y0 + "]" + " con " + "[" + x1 + "]" + "[" + y1 + "]");
            anterior.Imprimir();
        }
    }
     */
}
