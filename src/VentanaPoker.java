import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Ventana principal del juego de poker.
 * Gestiona la navegación entre el menú y los diferentes tipos de poker.
 */
public class VentanaPoker extends JFrame {
    // Tipos de juego de poker
    private static final String TEXAS_HOLDEM = "Texas Hold'em";
    private static final String FIVE_CARD = "Five Card Draw";

    // Componentes de la ventana
    private JLayeredPane layeredPane;
    private JLabel fondoLabel;

    private Clip backgroundMusic;
    // Paneles de juego
    private JPanel panelActual;
    private PanelTexasHoldem panelTexasHoldem;
    private PanelPokerCincoCartas panelPokerCincoCartas; // Esta clase deberá ser implementada por tu compañero

    // Configuración del juego
    private int numeroJugadores;
    private int fichasIniciales;
    private int smallBlind;
    private int bigBlind;
    private String[] nombresJugadores;

    /**
     * Constructor principal
     */
    public VentanaPoker() {
        setTitle("Poker Game");
        setSize(1500, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializar configuración por defecto
        numeroJugadores = 4;
        fichasIniciales = 1000;
        smallBlind = 5;
        bigBlind = 10;

        // Iniciar con la pantalla de bienvenida
        iniciarPantallaBienvenida();

        // Reproducir música de fondo
        reproducirMusica("G:\\4toSemestre\\POO\\Poker-Proyecto-Final\\src\\recursos\\pokerFace.wav");
    }

    private void iniciarPantallaBienvenida() {
        getContentPane().removeAll();

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1500, 850));

        // 1. Añadir imagen de fondo
        try {
            ImageIcon fondoIcon = new ImageIcon("G:\\4toSemestre\\POO\\Poker-Proyecto-Final\\src\\recursos\\fondoPoker.png");
            // Redimensionar la imagen para que se ajuste al tamaño de la ventana
            Image imagenRedimensionada = fondoIcon.getImage().getScaledInstance(1500, 850, Image.SCALE_SMOOTH);
            fondoIcon = new ImageIcon(imagenRedimensionada);

            fondoLabel = new JLabel(fondoIcon);
            fondoLabel.setBounds(0, 0, 1500, 850);
            layeredPane.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);
        } catch (Exception e) {
            System.err.println("Error al cargar el fondo: " + e.getMessage());
            // Fondo alternativo si no se puede cargar la imagen
            fondoLabel = new JLabel();
            fondoLabel.setBackground(Color.BLACK);
            fondoLabel.setOpaque(true);
            fondoLabel.setBounds(0, 0, 1500, 850);
            layeredPane.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);
        }

        // 2. Crear panel para el botón (capa superior)
        JPanel panelBoton = new JPanel();
        panelBoton.setLayout(new BoxLayout(panelBoton, BoxLayout.Y_AXIS));
        panelBoton.setOpaque(false);
        panelBoton.setBounds(0, 0, 1500, 850);

        // 3. Crear título con efecto elegante
        JLabel tituloLabel = new JLabel("");
        tituloLabel.setFont(new Font("Serif", Font.BOLD, 72));
        tituloLabel.setForeground(new Color(255, 215, 0)); // Color dorado
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Añadir efecto de sombra al título
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(100, 0, 50, 0));

        // 4. Crear botón de inicio
        JButton botonIniciar = new JButton("INICIAR");
        estilizarBotonInicio(botonIniciar);
        botonIniciar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Añadir acción al botón
        botonIniciar.addActionListener(e -> iniciarMenuPrincipal());

        // 5. Añadir componentes al panel
        panelBoton.add(Box.createVerticalGlue()); // Espacio flexible en la parte superior
        panelBoton.add(tituloLabel);
        panelBoton.add(Box.createRigidArea(new Dimension(0, 100))); // Espacio fijo entre título y botón
        panelBoton.add(botonIniciar);
        panelBoton.add(Box.createVerticalGlue()); // Espacio flexible en la parte inferior

        // 6. Añadir panel al layeredPane
        layeredPane.add(panelBoton, JLayeredPane.PALETTE_LAYER);

        // 7. Añadir layeredPane al contentPane
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(layeredPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void estilizarBotonInicio(JButton boton) {
        boton.setPreferredSize(new Dimension(300, 80));
        boton.setMaximumSize(new Dimension(300, 80));
        boton.setFont(new Font("Arial", Font.BOLD, 24));
        boton.setBackground(new Color(180, 25, 33));
        boton.setForeground(new Color(242, 211, 174));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(200, 60, 30));
                boton.setForeground(new Color(255, 255, 255));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(180, 25, 33));
                boton.setForeground(new Color(242, 211, 174));
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                boton.setBackground(new Color(150, 25, 33));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                boton.setBackground(new Color(180, 25, 33));
            }
        });
    }

    private void reproducirMusica(String rutaArchivo) {
        try {
            File archivoMusica = new File(rutaArchivo);
            if (!archivoMusica.exists()) {
                System.err.println("Archivo de música no encontrado: " + rutaArchivo);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(archivoMusica);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);

            // Reproducir en bucle continuo
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);

            // Control de volumen (opcional)
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f); // Reducir volumen (en decibelios)

            // Iniciar reproducción
            backgroundMusic.start();
        } catch (Exception e) {
            System.err.println("Error al reproducir música: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void detenerMusica() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }


    /**
     * Muestra el menú principal
     */
    public void iniciarMenuPrincipal() {

        getContentPane().removeAll();

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1500, 850));

        // 1. Primero añadir el fondo (capa inferior)
        try {
            ImageIcon fondoIcon = new ImageIcon("G:\\4toSemestre\\POO\\Poker-Proyecto-Final\\src\\recursos\\fondoPoker.png");
            // Redimensionar la imagen para que se ajuste al tamaño de la ventana
            Image imagenRedimensionada = fondoIcon.getImage().getScaledInstance(1500, 850, Image.SCALE_SMOOTH);
            fondoIcon = new ImageIcon(imagenRedimensionada);

            JLabel fondoLabel = new JLabel(fondoIcon);
            fondoLabel.setBounds(0, 0, 1500, 850);
            layeredPane.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);
        } catch (Exception e) {
            System.err.println("Error al cargar el fondo: " + e.getMessage());
            // Fondo alternativo si no se puede cargar la imagen
            JLabel fondoLabel = new JLabel();
            fondoLabel.setBackground(Color.BLACK);
            fondoLabel.setOpaque(true);
            fondoLabel.setBounds(0, 0, 1500, 850);
            layeredPane.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);
        }

        //Crear panel para componentes
        JPanel panelComponentes = new JPanel();
        panelComponentes.setLayout(new BoxLayout(panelComponentes, BoxLayout.Y_AXIS));
        panelComponentes.setOpaque(false);
        panelComponentes.setBounds(0, 0, 1500, 850);

        // Añadir título
        JLabel tituloLabel = new JLabel("SELECCIONA TU JUEGO");
        tituloLabel.setFont(new Font("Serif", Font.BOLD, 48));
        tituloLabel.setForeground(new Color(255, 215, 0));
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(80, 0, 50, 0));

        panelComponentes.add(tituloLabel);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Margen superior

        // Crear y estilizar botones
        JButton botonTexasHoldem = new JButton("Jugar Texas Hold'em");
        JButton botonFiveCard = new JButton("Jugar Five Card Draw");
        JButton botonConfiguracion = new JButton("Configuraciones");


        estilizarBoton(botonTexasHoldem);
        estilizarBoton(botonFiveCard);
        estilizarBoton(botonConfiguracion);


        // Centrar botones
        botonTexasHoldem.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonFiveCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonConfiguracion.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Añadir botones con separación
        panelBotones.add(botonTexasHoldem);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));
        panelBotones.add(botonFiveCard);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));
        panelBotones.add(botonConfiguracion);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));

        // Añadir paneles al panel de componentes
        panelComponentes.add(panelBotones);

        // Añadir panel de componentes a la capa superior
        layeredPane.add(panelComponentes, JLayeredPane.PALETTE_LAYER);

        // Configurar acciones de los botones
        botonTexasHoldem.addActionListener(e -> configurarJugadores(TEXAS_HOLDEM));
        botonFiveCard.addActionListener(e -> configurarJugadores(FIVE_CARD));
        botonConfiguracion.addActionListener(e -> mostrarConfiguracion());
        // Añadir layeredPane al content pane
        getContentPane().add(layeredPane);

        revalidate();
        repaint();
    }

    // Aplica estilo a los botones
    private void estilizarBoton(JButton boton) {
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setPreferredSize(new Dimension(250, 150));
        boton.setMaximumSize(new Dimension(250, 150));
        boton.setFont(new Font("Arial", Font.BOLD, 15));
        boton.setBackground(new Color(180, 25, 33));
        boton.setForeground(new Color(242, 211, 174));
        boton.setFocusPainted(true);


        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(200, 60, 30));
                boton.setForeground(new Color(250,250,250)); // Texto dorado al pasar el mouse
            }

            @Override
            public void mousePressed(MouseEvent e) {
                boton.setBackground(new Color(150, 25, 33));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(180, 25, 33));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                boton.setBackground(new Color(180, 25, 33));
            }
        });
    }

    //Muestra la configuración del juego
    private void mostrarConfiguracion() {
        JPanel panelConfig = new JPanel(new GridLayout(0, 2, 10, 10));
        panelConfig.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Fichas iniciales
        JLabel lblFichas = new JLabel("Fichas iniciales:");
        SpinnerNumberModel modelFichas = new SpinnerNumberModel(fichasIniciales, 100, 10000, 100);
        JSpinner spinnerFichas = new JSpinner(modelFichas);

        // Small blind
        JLabel lblSmallBlind = new JLabel("Small Blind:");
        SpinnerNumberModel modelSmallBlind = new SpinnerNumberModel(smallBlind, 1, 100, 1);
        JSpinner spinnerSmallBlind = new JSpinner(modelSmallBlind);

        // Big blind
        JLabel lblBigBlind = new JLabel("Big Blind:");
        SpinnerNumberModel modelBigBlind = new SpinnerNumberModel(bigBlind, 2, 200, 2);
        JSpinner spinnerBigBlind = new JSpinner(modelBigBlind);

        // Añadir componentes al panel
        panelConfig.add(lblFichas);
        panelConfig.add(spinnerFichas);
        panelConfig.add(lblSmallBlind);
        panelConfig.add(spinnerSmallBlind);
        panelConfig.add(lblBigBlind);
        panelConfig.add(spinnerBigBlind);

        // Mostrar diálogo
        int resultado = JOptionPane.showConfirmDialog(
                this, panelConfig, "Configuración del juego",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Guardar configuración si se acepta
        if (resultado == JOptionPane.OK_OPTION) {
            fichasIniciales = (int) spinnerFichas.getValue();
            smallBlind = (int) spinnerSmallBlind.getValue();
            bigBlind = (int) spinnerBigBlind.getValue();

            // Asegurar que big blind sea al menos el doble del small blind
            if (bigBlind < smallBlind * 2) {
                bigBlind = smallBlind * 2;
                JOptionPane.showMessageDialog(this,
                        "Big Blind ajustado a " + bigBlind + " (debe ser al menos el doble del Small Blind)",
                        "Ajuste automático", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    //Configura el número y nombres de jugadores
    private void configurarJugadores(String tipoJuego) {

        String[] opcionesNumJugadores;

        if (tipoJuego.equals(TEXAS_HOLDEM)){
            opcionesNumJugadores = new String[]{"2 Jugadores", "3 Jugadores", "4 Jugadores", "5 Jugadores", "6 Jugadores",
                    "7 Jugadores", "8 Jugadores","9 Jugadores", "10 Jugadores"};
        } else {
            opcionesNumJugadores = new String[]{"2 Jugadores", "3 Jugadores", "4 Jugadores", "5 Jugadores", "6 Jugadores",
                    "7 Jugadores", "8 Jugadores"};
        }

        int seleccion = JOptionPane.showOptionDialog(
                this,
                "Seleccione el número de jugadores:",
                "Jugadores",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcionesNumJugadores,
                opcionesNumJugadores[0]);

        if (seleccion == JOptionPane.CLOSED_OPTION) {
            return; // Se canceló
        }


        // Actualizar número de jugadores
        numeroJugadores = seleccion + 2;
        nombresJugadores = new String[numeroJugadores];

        // Pedir nombres de jugadores
        JPanel panelNombres = new JPanel(new GridLayout(0, 2, 10, 10));
        panelNombres.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField[] camposTexto = new JTextField[numeroJugadores];

        for (int i = 0; i < numeroJugadores; i++) {
            panelNombres.add(new JLabel("Jugador " + (i+1) + ":"));
            camposTexto[i] = new JTextField("Jugador " + (i+1), 15);
            panelNombres.add(camposTexto[i]);
        }

        int resultado = JOptionPane.showConfirmDialog(
                this, panelNombres, "Nombres de jugadores",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            // Guardar nombres
            for (int i = 0; i < numeroJugadores; i++) {
                String nombre = camposTexto[i].getText();
                nombresJugadores[i] = nombre.isEmpty() ? "Jugador " + (i+1) : nombre;
            }

            // Iniciar el juego seleccionado
            iniciarJuego(tipoJuego);
        }
    }

    //Inicia el juego con el tipo seleccionado
    private void iniciarJuego(String tipoJuego) {
        detenerMusica();
        // Limpiar contenido anterior
        getContentPane().removeAll();

        // Crear el panel de juego según el tipo
        if (tipoJuego.equals(TEXAS_HOLDEM)) {
            // Crear juego de Texas Hold'em
            TexasHoldem juegoTexas = new TexasHoldem(numeroJugadores, fichasIniciales, smallBlind, bigBlind);
            juegoTexas.establecerNombresJugadores(nombresJugadores);

            // Crear panel y asignar al panel actual
            panelTexasHoldem = new PanelTexasHoldem(juegoTexas, this);
            panelActual = panelTexasHoldem;

        } else if (tipoJuego.equals(FIVE_CARD)) {
            // Crear juego de Five Card Draw
            PokerCincoCartas juegoPokerCincoCartas = new PokerCincoCartas(numeroJugadores, fichasIniciales);
            juegoPokerCincoCartas.establecerNombresJugadores(nombresJugadores);

            // Crear panel y asignar al panel actual
            panelPokerCincoCartas = new PanelPokerCincoCartas(juegoPokerCincoCartas, this);
            panelActual = panelPokerCincoCartas;
        }

        // Añadir el panel al contenedor principal
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelActual, BorderLayout.CENTER);

        // Actualizar ventana
        revalidate();
        repaint();
    }

    @Override
    public void dispose() {
        detenerMusica();
        super.dispose();
    }

    //Método main para iniciar la aplicación
    public static void main(String[] args) {
        new VentanaPoker().setVisible(true);
    }
}