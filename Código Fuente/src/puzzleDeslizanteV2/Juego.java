package puzzleDeslizanteV2;

import java.io.Serializable;

public class Juego implements Serializable {
    
    private int dificultadX;
    private int dificultadY;
    
    private int contadorCreacion = 0;
    private Baldosa[][] tablero;
    public int[][] datos;
    private Baldosa[][] solucion;

    private int xVacia;
    private int yVacia;
    private Registro maestro;
    
    public Juego(int dificultadX, int dificultadY) {
    maestro = new Registro();
        this.dificultadX = dificultadX;
        this.dificultadY = dificultadY;
        tablero = new Baldosa[dificultadX][dificultadY];
        datos=new int[dificultadX][dificultadY];
        for(int i=0; i<dificultadX; i++){
            for(int j=0; j<dificultadY; j++){
                
                tablero[i][j]= new Baldosa(false,contadorCreacion);
                contadorCreacion ++;
            }
            
        }

        xVacia = dificultadX-1;
        yVacia = dificultadY-1;
        tablero[xVacia][yVacia].vaciar();
        solucion = tablero;

    }


    public boolean mover(int x0, int y0, int x1, int y1){
        boolean movido = false;
        if (verificar(x0, y0, x1, y1)) {
            Baldosa auxiliar;
            auxiliar = tablero[x0][y0];

            tablero[x0][y0] = tablero[x1][y1];
            tablero[x1][y1] = auxiliar;

            maestro.registrar(x0,y0,x1,y1);

            
            Imprimir();
            movido = true;
        }
        return movido;
    }

    public boolean verificar(int x0, int y0, int x1, int y1){
        return (tablero[x0][y0].isVacia() || tablero[x1][y1].isVacia()) && ((Math.abs(x0-x1)==1.0 &&
                Math.abs(y0-y1)==0.0) || (Math.abs(x0-x1)==0.0 && Math.abs(y0-y1)==1.0));
    }


     public void ordenar(){
        while (!maestro.vacia()){
            mover(maestro.getX0(),maestro.getY0(),maestro.getX1(),maestro.getY1());
            Imprimir();
            
        }
    }
    public int getxVacia(){
        return xVacia;

    }
    public int getyVacia(){
        return yVacia;

    }

    public void desordenar(){
        Imprimir();
        
        //work in progress
        for (int movimientos = (int)(Math.random()*((dificultadX*dificultadY*10)-(dificultadX*dificultadY)*5+1)
                +dificultadX*dificultadY*5);movimientos>0; movimientos--){

            int hor0Ver1 = (int)(Math.random()*(2));
            int xPieza;
            int yPieza;

            if (hor0Ver1 ==0){
                if (yVacia == dificultadY-1) {
                    yPieza = yVacia - 1;
                }else if (yVacia==0){
                    yPieza = yVacia + 1;
                } else{
                    yPieza = yVacia + definirSumaOrResta();
                }
                xPieza = xVacia;
            } else {
                if (xVacia==dificultadX-1) {
                    xPieza = xVacia - 1;
                }else if(xVacia==0){
                    xPieza = xVacia + 1;
                } else{
                    xPieza = xVacia + definirSumaOrResta();
                }
                yPieza = yVacia;
            }
            mover(xVacia, yVacia, xPieza, yPieza);
            xVacia = xPieza;
            yVacia = yPieza;

            Imprimir();
            
        }

        
        while(xVacia<dificultadX-1){
            mover(xVacia,yVacia,xVacia+1,yVacia);
            xVacia ++;
            Imprimir();
           
        }
        
        while (yVacia<dificultadY-1){
            mover(xVacia,yVacia,xVacia,yVacia+1);
            yVacia++;
            Imprimir();
            
        }
    }

    private int definirSumaOrResta(){
        int polo = (int)(Math.random()*(2));
        int num;

        if (polo == 1){
            num = 1;
        } else {
            num = -1;
        }
        return num;
    }

    public void Imprimir(){
        for (int i = 0; i<dificultadX;i++){
            for (int j = 0; j<dificultadY;j++){
                int posiposi = tablero[i][j].getPosicion();
                datos[i][j]=posiposi;
                if (posiposi<10) {
                    
                } else {
                    
                }
            }
            
        }
    }

    public Baldosa[][] getTablero() {
        return tablero;
    }
    public Registro getRegistro() {
        return maestro;
    }
    
    public int[][] getTableroInt(){
        return datos;
    }
    public Registro getMaestro(){
        return maestro;
    }
    public void Imprimir2(){
        for (int i = 0; i<dificultadX;i++){
            for (int j = 0; j<dificultadY;j++){
                System.out.println(datos[i][j]);
            }
            
        }
    }
    

    /*public void reiniciar(Jugador jugador){
        jugador.tiempo.setSegundos(0);
    }

    public void iniciar(Jugador jugador){
        jugador.tiempo.avanzar();
    }

    public void pausar(Jugador jugador){
        jugador.tiempo.detenerse();
    }

    public void puntuar(int jugador){
        /*
        jugadores[jugador].setPuntaje(jugadores[jugador].getPuntaje()+1);
         
    }
    public void guardarPartida(){
    }*/
}
