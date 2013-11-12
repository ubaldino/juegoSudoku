package escucha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Sudoku extends JFrame {

  /**
   *
   */
  private static final long serialVersionUID = 5152031829661339751L;
  public JPanel _panelPrincipal;
  public JPanel[] _zonas;
  public JMenuBar _barraMenu;
  public JMenu _mArchivo;
  public JMenu _mOpciones;
  private JMenu _mAyuda;
  private JMenuItem _verSolucion;
  private JMenuItem _nuevoSudoku;
  public JMenuItem _salir;
  private JMenuItem _acercaDe;
  private boolean _fin;
  public int _numerosIniciales;
  public int _solucion[];
  private int _solucionInicial[];
  private boolean _usadosFilas[][];
  private boolean _usadosColumnas[][];
  private boolean _usadosZonas[][];
  public int numZonaAux;
  public JTextField etiqueta;
  public JTextField casilla;

  /**
   * Constructor por omisión:
   */
  public Sudoku() {
    this.setTitle("Sudoku - Realizado por Sudoku-umss");
    this.setSize(500, 500);
    Dimension min = new Dimension(300, 300);
    this.setMinimumSize(min);
    //this.setLocation(320, 100);
    this.setLocationRelativeTo(null);
    this.setVisible(true);
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new OyenteSalir());

    _barraMenu = new JMenuBar();
    _mArchivo = new JMenu("Archivo");
    _nuevoSudoku = new JMenuItem("Nuevo Sudoku");
    _nuevoSudoku.addActionListener(new OyenteNuevoSudoku());

    _salir = new JMenuItem("Salir");
    _salir.addActionListener(new OyenteSalir());

    _mArchivo.add(_nuevoSudoku);
    _mArchivo.addSeparator();
    _mArchivo.add(_salir);
    _barraMenu.add(_mArchivo);

    _mOpciones = new JMenu("Opciones");
    _verSolucion = new JMenuItem("Ver la solución");
    _verSolucion.addActionListener(new OyenteVerSolucion());
    _mOpciones.add(_verSolucion);
    _barraMenu.add(_mOpciones);

    _mAyuda = new JMenu("Ayuda");
    _acercaDe = new JMenuItem("Acerda de Sudoku");
    _acercaDe.addActionListener(new OyenteAcercaDe());
    _mAyuda.add(_acercaDe);
    _barraMenu.add(_mAyuda);



    this.setJMenuBar(_barraMenu);

    _solucion = new int[81];
    _solucionInicial = new int[81];
    _usadosFilas = new boolean[9][9];
    _usadosColumnas = new boolean[9][9];
    _usadosZonas = new boolean[9][9];

  }

  /**
   * El siguiente método sirve para inicializar el tablero y hacer la llamada al
   * algoritmo de generación de los numeros.
   */
  public void nuevoSudoku() {


    int k = 0;

    // Inicialización de los tres vectores auxiliares:
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
        _usadosFilas[i][j] = false;
        _usadosColumnas[i][j] = false;
        _usadosZonas[i][j] = false;
        _solucion[k] = -1;
        k++;
      }
    }

    /* Primero sitúo un nº al azar entre 0 y 9 en la primera casilla del
     * tablero y resuelvo el Sudoku. De esta forma hemos generado una
     * solucion: */
    int valor, pos;
    valor = (int) (Math.random() * 9);
    _solucion[0] = valor;
    _usadosFilas[0][valor] = true;
    _usadosColumnas[0][valor] = true;
    _usadosZonas[0][valor] = true;

    resolver(_solucion, 0, _usadosFilas, _usadosColumnas, _usadosZonas);


    _numerosIniciales = 40;
    int casillasOcultas = 81 - _numerosIniciales;
    for (int i = 0; i < 80; i++) {
      _solucionInicial[i] = _solucion[i];
    }
    while (casillasOcultas > 0) {
      pos = (int) (Math.random() * 80);
      if (_solucionInicial[pos] >= 0) {
        _solucionInicial[pos] = -1;
        casillasOcultas--;
      }
    }
    /* Mostramos las casillas que sirven de guía al jugador */
    mostrarTablero(_solucionInicial);
    _fin = false;

    /* El motivo de no haber calculado el Sudoku tras haber generado 40 números
     * aleatorios es que el coste temporal era enorme para generar la solución.
     * Si sólo se pone una casilla y se resuelve el coste es mucho menor.
     * El efecto que percibe el jugador es que el ordenador lo resuelve de
     * forma casi inmediata.
     */

  }

  /**
   * Función que contiene el algoritmo de Vuelta Atrás para generar una solución
   * (la primera encontrada)
   *
   * @param solucion Es el vector en el que se guardan los numeroes de todas las
   * casillas [1..N]
   * @param k	Es el nivel del árbol. Cada nivel representa a cada una de las
   * casillas de la solución [1..N]
   * @param usadosFilas	La coordenada i representa las filas y la j los numeroes
   * @param usadosColumnas La coordenada i representa las columnas y la j los
   * numeroes
   */
  void resolver(int solucion[],
          int k,
          boolean usadosFilas[][],
          boolean usadosColumnas[][],
          boolean usadosZonas[][]) {
    /* 	Primero comprobamos si la casilla a revisar tiene un valor fijo dado
     comienzo del programa o no: */
    if (solucion[k] < 0) // No es un valor fijo
    {
      int numero;
      int numFila = 0;
      int numCol = 0;
      int zonaFila = 0;
      int zonaCol = 0;
      int numZona = 0;
      // Generamos todos los hijos para este nodo. Tantos como numeros:
      for (numero = 0; (numero < 9) && (!_fin); numero++) {
        numFila = k / 9;
        numCol = k - (numFila * 9);
        zonaFila = numFila / 3;
        zonaCol = numCol / 3;
        numZona = 3 * zonaFila + zonaCol;

        // Comprobamos si es factible:
        if ((usadosFilas[k / 9][numero] == false)
                && (usadosColumnas[k % 9][numero] == false)
                && (usadosZonas[numZona][numero]) == false) {
          // Marcaje:
          solucion[k] = numero;
          usadosFilas[k / 9][numero] = true;
          usadosColumnas[k % 9][numero] = true;
          usadosZonas[numZona][numero] = true;
          // Comprobamos si hemos terminado de generar todos los niveles:
          if (k == 80) {
            _fin = true;
          } else // Aún no hemos terminado de generar todos los niveles:
          {
            resolver(solucion, k + 1, usadosFilas, usadosColumnas, usadosZonas);
          }
          if (_fin == false) {
            // Desmarcaje:
            usadosFilas[k / 9][numero] = false;
            usadosColumnas[k % 9][numero] = false;
            usadosZonas[numZona][numero] = false;
            solucion[k] = -1;
          }
        }
      }
    } else // La casilla es un valor fijo y no lo podemos tocar
    {
      if (k == 80) {
        _fin = true;
      } else // Aún no hemos terminado de generar todos los niveles:
      {
        resolver(solucion, k + 1, usadosFilas, usadosColumnas, usadosZonas);
      }
    }

  }

  /**
   * El siguiente método genera campos de texto y los pone en el panel con
   * formato GridLayout (NxN) con un numero de fondo que cambia en función del
   * nº correspondiente a cada casilla de la solución:
   *
   * @param solucion es el vector de enteros que cumplen con la condición de
   * rellenado de la matriz.
   */
  public void mostrarTablero(int solucion[]) {

    if (_panelPrincipal == null) {
      _panelPrincipal = new JPanel();
      _zonas = new JPanel[9];
      for (int i = 0; i < 9; i++) {
        _zonas[i] = new JPanel();
      }
    } else {
      for (int i = 0; i < 9; i++) {
        _zonas[i].removeAll();
      }
      _panelPrincipal.removeAll();
    }
    // El siguiente diseño es para la matriz principal
    // Está compuesta de 9 subpaneles de 9x9

    GridLayout g = new GridLayout(3, 3);
    _panelPrincipal.setLayout(g);

    // Generamos cada uno de esos paneles:
    for (int i = 0; i < 9; i++) {
      _zonas[i].setLayout(new GridLayout(9 / 3, 9 / 3));
      _zonas[i].setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
      _panelPrincipal.add(_zonas[i]);
    }
    this.add(_panelPrincipal);
    _panelPrincipal.updateUI();

    int numZona = 0;
    int colAux = 0;
    numZonaAux = 0;
    for (int i = 0; i < 81; i++) {
      if (colAux >= 3) {
        numZona++;
        colAux = 0;
        if (numZona >= 3) {
          numZona = 0;
        }
      }
      numZonaAux = (i / 27) * 3;
      etiqueta = new JTextField();



      etiqueta.addMouseListener(new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
          //source = ((JTextField) e.getSource());
          casilla = ((JTextField) e.getSource());
          
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
      });

      if (solucion[i] != -1) {
        etiqueta.setText("" + (solucion[i] + 1));
      }
      etiqueta.setHorizontalAlignment(JTextField.CENTER);
      if (_solucion[i] == _solucionInicial[i]) {
        Font f = new Font("Monospaced", Font.BOLD, 16);
        etiqueta.setFont(f);
      }
      _zonas[numZona + numZonaAux].add(etiqueta);
      _zonas[numZona + numZonaAux].updateUI();
      _panelPrincipal.updateUI();
      colAux++;
    }
  }

  public JTextField getCasilla() {
    return casilla;
  }

  public void escribirEnPantalla(String numero) {
    this.etiqueta.setText(numero);
  }

  /**
   * Clase oyente para la gestión de un nuevo Sudoku:
   */
  public class OyenteNuevoSudoku implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      nuevoSudoku();
    }
  }

  /**
   * Clase oyente para la ver la solución:
   */
  public class OyenteVerSolucion implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      mostrarTablero(_solucion);
      Object aux = e.getSource();
      System.out.println(e.toString());
    }
  }

  /**
   * Oyente para la opción del menú de salir:
   */
  public class OyenteSalir implements ActionListener, WindowListener {

    public void actionPerformed(ActionEvent arg0) {
      cerrar();
    }

    public void cerrar() {
      int seleccion = JOptionPane.showConfirmDialog(null, "¿Desea salir del programa?", "Advertencia", JOptionPane.OK_CANCEL_OPTION,
              JOptionPane.WARNING_MESSAGE);
      if (seleccion == JOptionPane.OK_OPTION) {
        System.exit(0);
      }
    }

    public void windowActivated(WindowEvent arg0) {
    }

    public void windowClosed(WindowEvent arg0) {
    }

    public void windowClosing(WindowEvent arg0) {
      cerrar();
    }

    public void windowDeactivated(WindowEvent arg0) {
    }

    public void windowDeiconified(WindowEvent arg0) {
    }

    public void windowIconified(WindowEvent arg0) {
    }

    public void windowOpened(WindowEvent arg0) {
    }
  }

  /**
   * Clase oyente para ver los datos del programa:
   */
  public class OyenteAcercaDe implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      JOptionPane.showMessageDialog(null, "Sudoku    \n" + "      --------------------\n" + " Realizado por Grupo Sudok-umss\n" + " para la asignatura MTP de ITIS");
    }
  }
}
