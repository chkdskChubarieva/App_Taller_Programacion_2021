/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puzzleDeslizanteV2;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;

import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Grupo 2 
 *
 */
public class Interfaz extends javax.swing.JFrame{

    private Puntaje puntaje;
    private Cronometro t;
    private Jugador jugador;
    private Seleccionar selec;
    private JButton[][] baldosa;
    private Juego juego;
    private Registro maestroI;
    public String nombreJ, rutaI;
    private int[][] datos;
    public int dificultadX, dificultadY, contador, iVacio, jVacio;
    private int contadorBaldosa = 0;
    private boolean desordenado = false;
    private boolean pausado = false;
    private boolean guardado = false;
    private boolean recuperado = false;
    private boolean primerBoton=true;
    private ArrayList<Jugador> jugadorArrayList = new ArrayList<Jugador>();
    public ArrayList<Interfaz> lista = new ArrayList();
    public Juego getJuego() {
    return this.juego;
    }

    public Interfaz(int X, int Y, String nombreJugador, String rutaImagen) {
        nombreJ = nombreJugador;
        initComponents2();
        this.lista.add(this);
        dificultadX = X;
        dificultadY = Y;
        
        rutaI = rutaImagen;
        selec = new Seleccionar();
        puntaje = new Puntaje();
        juego = new Juego(dificultadX, dificultadY);
        datos = new int[dificultadX][dificultadY];
        
        
        
        
        nomJ.setText(nombreJ);

        setBaldosa();
        setResizable(false);
        setLocationRelativeTo(null);

        t = new Cronometro();

        t.tiempo.addActionListener(accionesCronometro);
        recuperarRanking();
        colocarPausado();
        colocarCuadroPausa();
        cerrar();
        cuadroPausa.setVisible(false);
    }

    public void recuperarRanking() {
        try {
            FileInputStream fis3 = new FileInputStream("ranking.txt");
            BufferedInputStream bis3 = new BufferedInputStream(fis3);
            ObjectInputStream ois3 = new ObjectInputStream(bis3);
            jugadorArrayList = (ArrayList<Jugador>) ois3.readObject();

            ois3.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

    }

    public void setBaldosa() {
        baldosa = new JButton[dificultadX][dificultadY];
        int pixelesX = 10;
        int pixelesY = 10;
        int bordeX1 = 10 + dificultadX;
        int bordeY1 = 10 + dificultadY;
        int tamanioA = (panelJ.getWidth() - bordeX1) / dificultadY;
        int tamanioB = (panelJ.getHeight() - bordeY1) / dificultadX;
        for (int i = 0; i < dificultadX; i++) {
            for (int j = 0; j < dificultadY; j++) {
                baldosa[i][j] = new JButton();
                baldosa[i][j].setBackground(Color.red);
                baldosa[i][j].setBounds(pixelesX, pixelesY, tamanioA, tamanioB);
                ImageIcon iconobtn = new ImageIcon("src/images/Galeria/img" + contadorBaldosa + ".jpg");
                ImageIcon iconobtnEscalado = new ImageIcon(iconobtn.getImage().getScaledInstance(tamanioA, tamanioB, Image.SCALE_DEFAULT));
                baldosa[i][j].setIcon(iconobtnEscalado);
                ButtonController boton = new ButtonController();
                baldosa[i][j].addActionListener(boton);
                panelJ.add(baldosa[i][j]);
                pixelesX = pixelesX + tamanioA;
                contadorBaldosa++;
                
                //Editar esta parte para cambiar la esquina
                if (i == (dificultadX - 1) && j == (dificultadY - 1)) {
                    baldosa[i][j].setVisible(false);
                    iVacio = i;
                    jVacio = j;
                }
            }
            pixelesX = 10;
            pixelesY = pixelesY + tamanioB;
        }
    }
    
    public void ordenarI() {  
                
        maestroI = juego.getMaestro();
        while (!maestroI.vacia()) {
            moverBaldosaOrdenar(maestroI.getX0(), maestroI.getY0(), maestroI.getX1(), maestroI.getY1());
        }
    }
    private void moverBaldosaOrdenar(int x0, int y0, int x1, int y1) {
        juego.mover(x0, y0, x1, y1);
        baldosa[x1][y1].setIcon(baldosa[x0][y0].getIcon());
        baldosa[x0][y0].setVisible(false);
        baldosa[x1][y1].setVisible(true);
        maestroI = juego.getMaestro();

    }

    private void moverBaldosa(int i, int j, int desplazamientoX, int desplazamientoY) {
        if (!pausado) {
            actualizarEtiquetaContador();

            if (desordenado) {
                t.tiempo.start();
            }
            
            juego.mover(i + desplazamientoX, j + desplazamientoY, i, j);
            baldosa[i + desplazamientoX][j + desplazamientoY].setIcon(baldosa[i][j].getIcon());
            baldosa[i][j].setVisible(false);
            baldosa[i + desplazamientoX][j + desplazamientoY].setVisible(true);
            iVacio = i;
            jVacio = j;
            actualizarEtiquetaPasos();
           
          
            if (revisar()) {
                
                
                t.tiempo.stop();
               
                JOptionPane.showMessageDialog(null, "Felicidades!!!");

                Jugador jugadorActual = new Jugador(nombreJ, contador, t.horas, t.minutos, t.segundos);
                jugadorArrayList.add(jugadorActual);

                try {
                    FileOutputStream ranking = new FileOutputStream("ranking.txt");
                    BufferedOutputStream bos3 = new BufferedOutputStream(ranking);
                    ObjectOutputStream oss3 = new ObjectOutputStream(bos3);
                    oss3.writeObject(jugadorArrayList);
                    oss3.close();
                } catch (Exception ex1) {
                    ex1.printStackTrace();
                }

            }
        }
    }
    

    private class ButtonController implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < dificultadX; i++) {
                for (int j = 0; j < dificultadY; j++) {

                    if (e.getSource().equals(baldosa[i][j]) && !revisar()) {
                        if(primerBoton==false){
                        }else{
                        if ((j + 1) >= dificultadY) {
                        } else {
                            if (baldosa[i][j + 1].isVisible() == false) {
                                moverBaldosa(i, j, 0, 1);
                            }
                        }
                        if ((j - 1) < 0) {
                        } else {
                            if (baldosa[i][j - 1].isVisible() == false) {
                                moverBaldosa(i, j, 0, -1);
                            }
                        }
                        if ((i - 1) < 0) {
                        } else {
                            if (baldosa[i - 1][j].isVisible() == false) {
                                moverBaldosa(i, j, -1, 0);
                            }
                        }
                        if ((i + 1) >= dificultadX) {
                        } else {
                            if (baldosa[i + 1][j].isVisible() == false) {
                                moverBaldosa(i, j, 1, 0);
                            }
                        }
                    }}
                }
            }
        }
    }
    @SuppressWarnings("unchecked")

    

    private ActionListener accionesCronometro = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            actualizarEtiquetaTiempo();
        }
    };

    private boolean revisar() {
        return juego.getRegistro().vacia();
    }

    private void actualizarEtiquetaTiempo() {
        String texto = (t.horas <= 9 ? "0" : "") + t.horas + ":" + (t.minutos <= 9 ? "0" : "") + t.minutos + ":" + (t.segundos <= 9 ? "0" : "") + t.segundos;
        etiqueta_tiempo.setText(texto);
    }

    private void actualizarEtiquetaContador() {
        contador++;
        etiqueta_contador.setText(Integer.toString(contador));
    }

    public void actualizarEtiquetaPasos() {
        etiqueta_pasos.setText(Integer.toString(maestroI.getPasos()));
    }

    private void colocarImagenes() {

        desordenado = true;
        if (recuperado == false) {
            ordenarI();
            juego.desordenar();
        } else {
            maestroI = juego.getMaestro();
        }
        datos = juego.getTableroInt();

        int pixelesX = 10 + dificultadX;
        int pixelesY = 10 + dificultadY;
        int tamanioA = (panelJ.getWidth() - pixelesX) / dificultadY;
        int tamanioB = (panelJ.getHeight() - pixelesY) / dificultadX;
        
        for (int i = 0; i < dificultadX; i++) {
            for (int j = 0; j < dificultadY; j++) {
                int valor = datos[i][j];
                ImageIcon iconobtn = new ImageIcon("src/images/Galeria/img" + valor + ".jpg");
                ImageIcon iconobtnEscalado = new ImageIcon(iconobtn.getImage().getScaledInstance(tamanioA, tamanioB, Image.SCALE_DEFAULT));
                baldosa[i][j].setIcon(iconobtnEscalado);
                if (i == (dificultadX - 1) && j == (dificultadY - 1)) {

                    baldosa[i][j].setVisible(true);
                }
               
                baldosa[iVacio][jVacio].setVisible(false);
            }
        }
    }

    public void setJuego(Juego recuperado) {
        setRecuperado(true);
        juego = recuperado;
        colocarImagenes();
    }

    public void setDatosGuardados(DatosGuardados d) {
        rutaI = d.imagenDelJuego;
        colocarCuadroPausa();
        iVacio = d.iVacio;
        jVacio = d.jVacio;
        nomJ.setText(d.nombreJugador);
        contador = (d.movimientosRealizados) - 1;
        actualizarEtiquetaContador();
        t.horas = d.horas;
        t.minutos = d.min;
        t.segundos = d.seg;
        t.tiempo.stop();
        actualizarEtiquetaTiempo();
        pausado = true;
        btnContinuar.setEnabled(true);
        btnPausar.setEnabled(false);
    }

    private void setRecuperado(boolean v) {
        recuperado = v;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel2 = new javax.swing.JPanel();
        btnContinuar = new javax.swing.JButton();
        nomJ = new javax.swing.JLabel();
        timerPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        etiqueta_tiempo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        etiqueta_contador = new javax.swing.JLabel();
        btnDesordenar = new javax.swing.JButton();
        cuadroImg = new javax.swing.JButton();
        panelJ = new javax.swing.JPanel();
        cuadroPausa = new javax.swing.JLabel();
        btnPausar = new javax.swing.JButton();
        jButtonPista = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        etiqueta_pasos = new javax.swing.JLabel();
        btnAtras = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        btnInicio = new javax.swing.JMenu();
        btnGuardar = new javax.swing.JMenu();
        btnCargarP = new javax.swing.JMenu();
        btnJuego = new javax.swing.JMenu();
        btnNuevoJuego = new javax.swing.JMenuItem();
        btnReinicio = new javax.swing.JMenuItem();
        btnOrdenar = new javax.swing.JMenuItem();
        btnConfig = new javax.swing.JMenu();
        ranking = new javax.swing.JRadioButtonMenuItem();
        btnAyuda = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        btnSalir = new javax.swing.JMenu();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Puzzle Rompecabezas Deslizante");
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(255, 0, 0));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnContinuar.setBackground(new java.awt.Color(51, 51, 51));
        btnContinuar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnContinuar.setForeground(new java.awt.Color(204, 204, 204));
        btnContinuar.setText("Continuar");
        btnContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarActionPerformed(evt);
            }
        });
        jPanel2.add(btnContinuar, new org.netbeans.lib.awtextra.AbsoluteConstraints(684, 425, -1, -1));

        nomJ.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        nomJ.setForeground(new java.awt.Color(204, 204, 204));
        nomJ.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nomJ.setText("NombreJ");
        jPanel2.add(nomJ, new org.netbeans.lib.awtextra.AbsoluteConstraints(89, 23, 297, -1));

        timerPanel.setBackground(new java.awt.Color(51, 51, 51));
        timerPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        timerPanel.setForeground(new java.awt.Color(102, 102, 102));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Tiempo");

        etiqueta_tiempo.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        etiqueta_tiempo.setForeground(new java.awt.Color(204, 204, 204));
        etiqueta_tiempo.setText("00:00:00");

        javax.swing.GroupLayout timerPanelLayout = new javax.swing.GroupLayout(timerPanel);
        timerPanel.setLayout(timerPanelLayout);
        timerPanelLayout.setHorizontalGroup(
            timerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(11, 11, 11))
            .addGroup(timerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(etiqueta_tiempo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        timerPanelLayout.setVerticalGroup(
            timerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timerPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(etiqueta_tiempo)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel2.add(timerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(552, 361, -1, -1));

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setForeground(new java.awt.Color(204, 204, 204));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("Movimientos");

        etiqueta_contador.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        etiqueta_contador.setForeground(new java.awt.Color(204, 204, 204));
        etiqueta_contador.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(etiqueta_contador)
                .addGap(68, 68, 68))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiqueta_contador)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 14, -1, -1));

        btnDesordenar.setBackground(new java.awt.Color(51, 51, 51));
        btnDesordenar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnDesordenar.setForeground(new java.awt.Color(204, 204, 204));
        btnDesordenar.setText("Comenzar Juego");
        btnDesordenar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesordenarActionPerformed(evt);
            }
        });
        jPanel2.add(btnDesordenar, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 470, -1, -1));

        cuadroImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cuadroImgActionPerformed(evt);
            }
        });
        jPanel2.add(cuadroImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 134, 268, 213));

        panelJ.setBackground(new java.awt.Color(51, 51, 51));
        panelJ.setForeground(new java.awt.Color(0, 51, 51));
        panelJ.setOpaque(false);
        panelJ.setPreferredSize(new java.awt.Dimension(408, 310));

        javax.swing.GroupLayout panelJLayout = new javax.swing.GroupLayout(panelJ);
        panelJ.setLayout(panelJLayout);
        panelJLayout.setHorizontalGroup(
            panelJLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cuadroPausa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        panelJLayout.setVerticalGroup(
            panelJLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cuadroPausa, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
        );

        jPanel2.add(panelJ, new org.netbeans.lib.awtextra.AbsoluteConstraints(31, 70, 469, 376));

        btnPausar.setBackground(new java.awt.Color(51, 51, 51));
        btnPausar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnPausar.setForeground(new java.awt.Color(204, 204, 204));
        btnPausar.setText("Pausar");
        btnPausar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPausarActionPerformed(evt);
            }
        });
        jPanel2.add(btnPausar, new org.netbeans.lib.awtextra.AbsoluteConstraints(684, 365, -1, -1));

        jButtonPista.setBackground(new java.awt.Color(51, 51, 51));
        jButtonPista.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButtonPista.setForeground(new java.awt.Color(204, 204, 204));
        jButtonPista.setText("Pista");
        jButtonPista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPistaActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonPista, new org.netbeans.lib.awtextra.AbsoluteConstraints(716, 57, -1, -1));

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("Pasos Restantes");

        etiqueta_pasos.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        etiqueta_pasos.setForeground(new java.awt.Color(204, 204, 204));
        etiqueta_pasos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiqueta_pasos.setText("0");
        etiqueta_pasos.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiqueta_pasos, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiqueta_pasos, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap())
        );

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 480, 250, 50));

        btnAtras.setBackground(new java.awt.Color(51, 51, 51));
        btnAtras.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAtras.setForeground(new java.awt.Color(204, 204, 204));
        btnAtras.setText("Añadir nuevo juego");
        btnAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasActionPerformed(evt);
            }
        });
        jPanel2.add(btnAtras, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 470, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Fondos/fondo1.jpg"))); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 830, 540));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 830, 540));

        jMenuBar1.setBackground(new java.awt.Color(51, 51, 51));
        jMenuBar1.setForeground(new java.awt.Color(204, 204, 204));

        btnInicio.setText("Inicio");
        btnInicio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnInicioMousePressed(evt);
            }
        });
        btnInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInicioActionPerformed(evt);
            }
        });
        jMenuBar1.add(btnInicio);

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Iconos/guardar.png"))); // NOI18N
        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGuardarMouseClicked(evt);
            }
        });
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jMenuBar1.add(btnGuardar);

        btnCargarP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Iconos/cargar.png"))); // NOI18N
        btnCargarP.setText("Cargar Partida");
        btnCargarP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCargarPMouseClicked(evt);
            }
        });
        btnCargarP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarPActionPerformed(evt);
            }
        });
        jMenuBar1.add(btnCargarP);

        btnJuego.setText("Juego");
        btnJuego.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJuegoActionPerformed(evt);
            }
        });

        btnNuevoJuego.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        btnNuevoJuego.setText("Nuevo Juego");
        btnNuevoJuego.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoJuegoActionPerformed(evt);
            }
        });
        btnJuego.add(btnNuevoJuego);

        btnReinicio.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        btnReinicio.setText("Reiniciar Partida");
        btnReinicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReinicioActionPerformed(evt);
            }
        });
        btnJuego.add(btnReinicio);

        btnOrdenar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        btnOrdenar.setText("Ordenar Puzzle");
        btnOrdenar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdenarActionPerformed(evt);
            }
        });
        btnJuego.add(btnOrdenar);

        jMenuBar1.add(btnJuego);

        btnConfig.setText("Mejores Partidas");

        ranking.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        ranking.setSelected(true);
        ranking.setText("Puntaje");
        ranking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rankingActionPerformed(evt);
            }
        });
        btnConfig.add(ranking);

        jMenuBar1.add(btnConfig);

        btnAyuda.setText("Ayuda");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem4.setText("Acerca del Juego");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        btnAyuda.add(jMenuItem4);

        jMenuBar1.add(btnAyuda);

        btnSalir.setText("Salir");
        btnSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSalirMouseClicked(evt);
            }
        });
        jMenuBar1.add(btnSalir);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public JTabbedPane tabPane;
    int counter = 0;
    
    private void initComponents2() {
        
        tabPane = new JTabbedPane();
        
        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel2 = new javax.swing.JPanel();
        btnContinuar = new javax.swing.JButton();
        nomJ = new javax.swing.JLabel();
        timerPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        etiqueta_tiempo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        etiqueta_contador = new javax.swing.JLabel();
        btnDesordenar = new javax.swing.JButton();
        cuadroImg = new javax.swing.JButton();
        panelJ = new javax.swing.JPanel();
        cuadroPausa = new javax.swing.JLabel();
        btnPausar = new javax.swing.JButton();
        jButtonPista = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        etiqueta_pasos = new javax.swing.JLabel();
        btnAtras = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        btnInicio = new javax.swing.JMenu();
        btnGuardar = new javax.swing.JMenu();
        btnCargarP = new javax.swing.JMenu();
        btnJuego = new javax.swing.JMenu();
        btnNuevoJuego = new javax.swing.JMenuItem();
        btnReinicio = new javax.swing.JMenuItem();
        btnOrdenar = new javax.swing.JMenuItem();
        btnConfig = new javax.swing.JMenu();
        ranking = new javax.swing.JRadioButtonMenuItem();
        btnAyuda = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        btnSalir = new javax.swing.JMenu();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Puzzle Rompecabezas Deslizante");
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(255, 0, 0));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnContinuar.setBackground(new java.awt.Color(51, 51, 51));
        btnContinuar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnContinuar.setForeground(new java.awt.Color(204, 204, 204));
        btnContinuar.setText("Continuar");
        btnContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarActionPerformed(evt);
            }
        });
        jPanel2.add(btnContinuar, new org.netbeans.lib.awtextra.AbsoluteConstraints(684, 425, -1, -1));

        nomJ.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        nomJ.setForeground(new java.awt.Color(204, 204, 204));
        nomJ.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nomJ.setText("NombreJ");
        jPanel2.add(nomJ, new org.netbeans.lib.awtextra.AbsoluteConstraints(89, 23, 297, -1));

        timerPanel.setBackground(new java.awt.Color(51, 51, 51));
        timerPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        timerPanel.setForeground(new java.awt.Color(102, 102, 102));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Tiempo");

        etiqueta_tiempo.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        etiqueta_tiempo.setForeground(new java.awt.Color(204, 204, 204));
        etiqueta_tiempo.setText("00:00:00");

        javax.swing.GroupLayout timerPanelLayout = new javax.swing.GroupLayout(timerPanel);
        timerPanel.setLayout(timerPanelLayout);
        timerPanelLayout.setHorizontalGroup(
            timerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(11, 11, 11))
            .addGroup(timerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(etiqueta_tiempo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        timerPanelLayout.setVerticalGroup(
            timerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timerPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(etiqueta_tiempo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(timerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(552, 361, -1, -1));

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setForeground(new java.awt.Color(204, 204, 204));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("Movimientos");

        etiqueta_contador.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        etiqueta_contador.setForeground(new java.awt.Color(204, 204, 204));
        etiqueta_contador.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(etiqueta_contador)
                .addGap(68, 68, 68))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiqueta_contador)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 14, -1, -1));

        btnDesordenar.setBackground(new java.awt.Color(51, 51, 51));
        btnDesordenar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnDesordenar.setForeground(new java.awt.Color(204, 204, 204));
        btnDesordenar.setText("Comenzar Juego");
        btnDesordenar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesordenarActionPerformed(evt);
            }
        });
        jPanel2.add(btnDesordenar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 470, -1, -1));

        cuadroImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cuadroImgActionPerformed(evt);
            }
        });
        jPanel2.add(cuadroImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 134, 268, 213));

        panelJ.setBackground(new java.awt.Color(51, 51, 51));
        panelJ.setForeground(new java.awt.Color(0, 51, 51));
        panelJ.setOpaque(false);
        panelJ.setPreferredSize(new java.awt.Dimension(408, 310));

        javax.swing.GroupLayout panelJLayout = new javax.swing.GroupLayout(panelJ);
        panelJ.setLayout(panelJLayout);
        panelJLayout.setHorizontalGroup(
            panelJLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cuadroPausa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelJLayout.setVerticalGroup(
            panelJLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cuadroPausa, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
        );

        jPanel2.add(panelJ, new org.netbeans.lib.awtextra.AbsoluteConstraints(31, 70, 469, 376));

        btnPausar.setBackground(new java.awt.Color(51, 51, 51));
        btnPausar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnPausar.setForeground(new java.awt.Color(204, 204, 204));
        btnPausar.setText("Pausar");
        btnPausar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPausarActionPerformed(evt);
            }
        });
        jPanel2.add(btnPausar, new org.netbeans.lib.awtextra.AbsoluteConstraints(684, 365, -1, -1));

        jButtonPista.setBackground(new java.awt.Color(51, 51, 51));
        jButtonPista.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButtonPista.setForeground(new java.awt.Color(204, 204, 204));
        jButtonPista.setText("Pista");
        jButtonPista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPistaActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonPista, new org.netbeans.lib.awtextra.AbsoluteConstraints(716, 57, -1, -1));

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("Pasos Restantes");

        etiqueta_pasos.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        etiqueta_pasos.setForeground(new java.awt.Color(204, 204, 204));
        etiqueta_pasos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiqueta_pasos.setText("0");
        etiqueta_pasos.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiqueta_pasos, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiqueta_pasos, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap())
        );

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 480, 250, 50));

        btnAtras.setBackground(new java.awt.Color(51, 51, 51));
        btnAtras.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAtras.setForeground(new java.awt.Color(204, 204, 204));
        btnAtras.setText("Editar Configuración");
        btnAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasActionPerformed(evt);
            }
        });
        jPanel2.add(btnAtras, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 470, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Fondos/fondo1.jpg"))); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 830, 540));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 830, 540));

        
        
        
        counter ++;
        this.addTabPanel(jPanel2, nombreJ + counter);
        getContentPane().add(tabPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 830, 600));
        
        
        
        
        jMenuBar1.setBackground(new java.awt.Color(51, 51, 51));
        jMenuBar1.setForeground(new java.awt.Color(204, 204, 204));

        btnInicio.setText("Inicio");
        btnInicio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnInicioMousePressed(evt);
            }
        });
        btnInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInicioActionPerformed(evt);
            }
        });
        
        jMenuBar1.add(btnInicio);

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Iconos/guardar.png"))); // NOI18N
        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGuardarMouseClicked(evt);
            }
        });
        jMenuBar1.add(btnGuardar);

        btnCargarP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Iconos/cargar.png"))); // NOI18N
        btnCargarP.setText("Cargar Partida");
        btnCargarP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCargarPMouseClicked(evt);
            }
        });
        jMenuBar1.add(btnCargarP);

        btnJuego.setText("Juego");
        btnJuego.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJuegoActionPerformed(evt);
            }
        });

        btnNuevoJuego.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        btnNuevoJuego.setText("Nuevo Juego");
        btnNuevoJuego.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoJuegoActionPerformed(evt);
            }
        });
        btnJuego.add(btnNuevoJuego);

        btnReinicio.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        btnReinicio.setText("Reiniciar Partida");
        btnReinicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReinicioActionPerformed(evt);
            }
        });
        btnJuego.add(btnReinicio);

        btnOrdenar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        btnOrdenar.setText("Ordenar Puzzle");
        btnOrdenar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdenarActionPerformed(evt);
            }
        });
        btnJuego.add(btnOrdenar);

        jMenuBar1.add(btnJuego);

        btnConfig.setText("Mejores Partidas");

        ranking.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        ranking.setSelected(true);
        ranking.setText("Puntaje");
        ranking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rankingActionPerformed(evt);
            }
        });
        btnConfig.add(ranking);

        jMenuBar1.add(btnConfig);

        btnAyuda.setText("Ayuda");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem4.setText("Acerca del Juego");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        btnAyuda.add(jMenuItem4);

        jMenuBar1.add(btnAyuda);

        btnSalir.setText("Salir");
        btnSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSalirMouseClicked(evt);
            }
        });
        jMenuBar1.add(btnSalir);

        setJMenuBar(jMenuBar1);

        pack();

    }// </editor-fold>                        
    
    public void addTabPanel(JPanel jpanel, String nombrePartida) {
        
        tabPane.addTab("" , jpanel);
       
        FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 0);

        // Make a small JPanel with the layout and make it non-opaque
        JPanel pnlTab = new JPanel(f);
        pnlTab.setOpaque(false);

    
        JTextField lblTitle = new javax.swing.JTextField(nombrePartida);
        JButton btnClose = new JButton("X");
        btnClose.setOpaque(false);
        btnClose.setRolloverEnabled(true);
        btnClose.setBorder(null);
        btnClose.setFocusable(false);
        pnlTab.add(lblTitle);
        pnlTab.add(btnClose);
        
        
        
        tabPane.setTabComponentAt(counter - 1, pnlTab);
        
        
        
        ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int valor = 0;
            
                valor = JOptionPane.showConfirmDialog(null, "Desea guardar la partida?", "Advertencia", JOptionPane.YES_NO_OPTION);
                if (valor == JOptionPane.YES_OPTION) {
                    guardarPartida();
                    
                    tabPane.remove(tabPane.getSelectedComponent());
                    int index1=tabPane.getSelectedIndex();
                    lista.remove(index1);
                    counter--;
                }else{
                    tabPane.remove(tabPane.getSelectedComponent());
                    int index1=tabPane.getSelectedIndex();
                    lista.remove(index1);
                    counter--;
                }
            }
        };
        btnClose.addActionListener(listener);

        tabPane.setSelectedComponent(tabPane.getSelectedComponent());
        
        pack();
    }
    
    
    private void btnJuegoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJuegoActionPerformed
        setBaldosa();
    }//GEN-LAST:event_btnJuegoActionPerformed

    private void btnReinicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReinicioActionPerformed

        desordenado = false;
        ordenarI();
        t.reiniciarTiempo();
        contador = 0;
        actualizarEtiquetaTiempo();
        etiqueta_contador.setText(Integer.toString(contador));
    }//GEN-LAST:event_btnReinicioActionPerformed

    private void btnNuevoJuegoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoJuegoActionPerformed

        volverInicio();
        selec.ventanaInicio.setVisible(false);
        selec.ventanaSelec.setVisible(true);
    }//GEN-LAST:event_btnNuevoJuegoActionPerformed

    private void btnOrdenarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdenarActionPerformed
        
        ordenarI();

    }//GEN-LAST:event_btnOrdenarActionPerformed

    private void btnDesordenarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesordenarActionPerformed
        iVacio = dificultadX - 1;
        jVacio = dificultadY - 1;
        recuperado = false;
        try {
                SplitImage.createFiles(rutaI, dificultadX, dificultadY);
            } catch (IOException e) {
                e.printStackTrace();
            }
        colocarImagenes();
        actualizarEtiquetaPasos();
    }//GEN-LAST:event_btnDesordenarActionPerformed

    private void jButtonPistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPistaActionPerformed

        //Se puede añadir un contador de pistas para penalizar al jugador
        maestroI = juego.getMaestro();
        
        if (!maestroI.vacia()) {
            moverBaldosaOrdenar(maestroI.getX0(), maestroI.getY0(), maestroI.getX1(), maestroI.getY1());
            t.tiempo.start();
            actualizarEtiquetaPasos();
            actualizarEtiquetaContador();
        }
        if (revisar()) {
                t.tiempo.stop();
                JOptionPane.showMessageDialog(null, "Felicidades!!!");
        }
        
        
    }//GEN-LAST:event_jButtonPistaActionPerformed

    private void btnPausarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPausarActionPerformed
        // TODO add your handling code here:
        pausarJuego();
    }//GEN-LAST:event_btnPausarActionPerformed

    public void pausarJuego() {
        cuadroPausa.setVisible(true);
        pausado = true;
        t.tiempo.stop();
        btnContinuar.setEnabled(true);
        btnPausar.setEnabled(false);
    }

    
    private void btnContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuarActionPerformed
        // TODO add your handling code here:
        cuadroPausa.setVisible(false);
        pausado = false;
        t.tiempo.start();
        btnPausar.setEnabled(true);
        btnContinuar.setEnabled(false);
    }//GEN-LAST:event_btnContinuarActionPerformed

    private void rankingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rankingActionPerformed

        puntaje.setVisible(true);
        puntaje.recuperarRanking();
        puntaje.setPuntaje();
    }//GEN-LAST:event_rankingActionPerformed

    private void btnInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInicioActionPerformed
        
    }//GEN-LAST:event_btnInicioActionPerformed

    private void btnInicioMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInicioMousePressed
        // TODO add your handling code here:
        volverInicio();
    }//GEN-LAST:event_btnInicioMousePressed

    private void btnSalirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalirMouseClicked
        // TODO add your handling code here:
       confirmarSalida();
    }//GEN-LAST:event_btnSalirMouseClicked
    
    private void btnGuardarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarMouseClicked
        // TODO add your handling code here: 
        pausarJuego();
        guardarPartida();

    }//GEN-LAST:event_btnGuardarMouseClicked

    public void cerrar() {
        try {
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    confirmarSalida();
                }
            });
            this.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmarSalida() {
        int valor = 0;
        String[] opciones = {"Guardar", "No Guardar", "Cancelar"};

        if (guardado) {
            valor = JOptionPane.showConfirmDialog(this, "Está seguro de cerrar la aplicación?", "Advertencia", JOptionPane.YES_NO_OPTION);
            if (valor == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Gracias por jugar", "Gracias", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        } else {
            int s = JOptionPane.showOptionDialog(this, "Está seguro de cerrar la aplicación sin guardar la partida?", "Salir del Juego", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (s == 0) {
                guardarPartida();
                System.exit(0);
            } else if (s == 1) {
                System.exit(0);
            }
        }

    }

    private void guardarPartida() {
        int index = this.tabPane.getSelectedIndex();
        Interfaz interfazActual = this.lista.get(index);
        
        DatosGuardados datosGuardados = new DatosGuardados();
        datosGuardados.nombreJugador = interfazActual.nombreJ;
        datosGuardados.dificultadX = interfazActual.dificultadX;
        datosGuardados.dificultadY = interfazActual.dificultadY;
        datosGuardados.imagenDelJuego = interfazActual.rutaI;
        datosGuardados.movimientosRealizados = interfazActual.contador;
        datosGuardados.horas = interfazActual.t.horas;
        datosGuardados.min = interfazActual.t.minutos;
        datosGuardados.iVacio = interfazActual.iVacio;
        datosGuardados.jVacio = interfazActual.jVacio;
        datosGuardados.seg = interfazActual.t.segundos;
        CargarPartida guardarPartida = new CargarPartida(datosGuardados, interfazActual.juego);
        Buscador finder = new Buscador();
        
        int resultado = finder.buscadorI.showSaveDialog(null);;
        
        if(JFileChooser.APPROVE_OPTION == resultado){
        try {

            FileOutputStream archivo1 = new FileOutputStream(finder.buscadorI.getSelectedFile() + ".txt");
            BufferedOutputStream bos1 = new BufferedOutputStream(archivo1);
            ObjectOutputStream oss1 = new ObjectOutputStream(bos1);
            oss1.writeObject(guardarPartida);
            oss1.close();
            guardado = true;
            JOptionPane.showMessageDialog(this, "Partida guardada con éxito");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        }
        
        }

    private void colocarPausado() {

        ImageIcon iconobtn1 = new ImageIcon("src/images/Iconos/pausado.png");
        ImageIcon iconobtnEscalado1 = new ImageIcon(iconobtn1.getImage().getScaledInstance(cuadroPausa.getWidth(), cuadroPausa.getHeight(), Image.SCALE_DEFAULT));
        cuadroPausa.setIcon(iconobtnEscalado1);

    }

    public void colocarCuadroPausa() {

        ImageIcon iconobtn1 = new ImageIcon(rutaI);
        ImageIcon iconobtnEscalado1 = new ImageIcon(iconobtn1.getImage().getScaledInstance(cuadroImg.getWidth(), cuadroImg.getHeight(), Image.SCALE_DEFAULT));
        cuadroImg.setIcon(iconobtnEscalado1);

    }


    private void cuadroImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cuadroImgActionPerformed

    }//GEN-LAST:event_cuadroImgActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        Ayuda help = new Ayuda();
        help.setVisible(true);

    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void btnCargarPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCargarPMouseClicked
        // TODO add your handling code here:
        pausarJuego();
        int resultado;
        
        Buscador buscador = new Buscador();
        FileNameExtensionFilter formato = new FileNameExtensionFilter("TXT", "txt");
        buscador.buscadorI.setFileFilter(formato);
        resultado = buscador.buscadorI.showOpenDialog(null);
        if (JFileChooser.APPROVE_OPTION == resultado) {
            selec.archivo = buscador.buscadorI.getSelectedFile();

            try {
                FileInputStream fis1 = new FileInputStream(selec.archivo.getAbsolutePath());
                BufferedInputStream bis1 = new BufferedInputStream(fis1);
                ObjectInputStream ois1 = new ObjectInputStream(bis1);
                CargarPartida partidaRecuperada = (CargarPartida) ois1.readObject();
                ois1.close();
                DatosGuardados datosRecuperados = partidaRecuperada.getDatosGuardados();
                Juego juegoRecuperado = partidaRecuperada.getJuego();

                String a = datosRecuperados.imagenDelJuego;
                int b = datosRecuperados.dificultadX;
                int c = datosRecuperados.dificultadY;
                SplitImage.createFiles(a, b, c);

                this.setVisible(true);
                //Principal principal=new Principal();
                
                Interfaz interfaz2 = new Interfaz(b, c, datosRecuperados.nombreJugador, datosRecuperados.imagenDelJuego);
                
                interfaz2.setDatosGuardados(datosRecuperados);
                interfaz2.setJuego(juegoRecuperado);
                interfaz2.colocarImagenes();
                interfaz2.actualizarEtiquetaPasos();
                interfaz2.colocarCuadroPausa();
                interfaz2.pausarJuego();
                interfaz2.btnAtras.setVisible(false);
                interfaz2.setVisible(false);
                //interfaz2.cerrar();
                interfaz2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                counter ++;
                this.addTabPanel(interfaz2.jPanel2, interfaz2.nombreJ);
                this.lista.add(interfaz2);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

        }

    }//GEN-LAST:event_btnCargarPMouseClicked

    private void btnCargarPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCargarPActionPerformed

    
    private void btnAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtrasActionPerformed
            Seleccionar sel=new Seleccionar(dificultadX, dificultadY,nombreJ,rutaI,this);
            
            sel.setVisible(true);
            sel.bb=true;
            sel.btnAtrasD.setVisible(false);
            sel.ventanaInicio.setVisible(false);
            sel.ventanaSelec.setVisible(true);
            sel.btnAtrasD.setVisible(false);
            sel.actualizarDatos();
    }//GEN-LAST:event_btnAtrasActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardarActionPerformed
    private void volverInicio() {
        int valor = 0;
        String[] opciones = {"Guardar", "No Guardar", "Cancelar"};

        if (guardado) {
            valor = JOptionPane.showConfirmDialog(this, "Está seguro de volver al inicio?", "Advertencia", JOptionPane.YES_NO_OPTION);
            if (valor == JOptionPane.YES_OPTION) {
               this.setVisible(false);
               selec.setVisible(true);
            }
        } else {
            int s = JOptionPane.showOptionDialog(this, "Está seguro de volver al inicio sin guardar la partida?", "Salir del Juego", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (s == 0) {
                guardarPartida();
                this.setVisible(false);
                selec.setVisible(true);
            } else if (s == 1) {
               this.setVisible(false);
                selec.setVisible(true);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnAtras;
    private javax.swing.JMenu btnAyuda;
    private javax.swing.JMenu btnCargarP;
    private javax.swing.JMenu btnConfig;
    private javax.swing.JButton btnContinuar;
    private javax.swing.JButton btnDesordenar;
    private javax.swing.JMenu btnGuardar;
    private javax.swing.JMenu btnInicio;
    private javax.swing.JMenu btnJuego;
    private javax.swing.JMenuItem btnNuevoJuego;
    private javax.swing.JMenuItem btnOrdenar;
    private javax.swing.JButton btnPausar;
    private javax.swing.JMenuItem btnReinicio;
    private javax.swing.JMenu btnSalir;
    private javax.swing.JButton cuadroImg;
    private javax.swing.JLabel cuadroPausa;
    private javax.swing.JLabel etiqueta_contador;
    private javax.swing.JLabel etiqueta_pasos;
    private javax.swing.JLabel etiqueta_tiempo;
    private javax.swing.JButton jButtonPista;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel nomJ;
    private javax.swing.JPanel panelJ;
    private javax.swing.JRadioButtonMenuItem ranking;
    private javax.swing.JPanel timerPanel;
    // End of variables declaration//GEN-END:variables
}
