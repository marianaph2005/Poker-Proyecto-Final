import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

    // Paneles de juego
    private JPanel panelActual;
    private PanelTexasHoldem panelTexasHoldem;
    //private PanelFiveCard panelFiveCard; // Esta clase deberá ser implementada por tu compañero

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
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializar configuración por defecto
        numeroJugadores = 4;
        fichasIniciales = 1000;
        smallBlind = 5;
        bigBlind = 10;

        // Iniciar con el menú principal
        iniciarMenuPrincipal();
    }

    /**
     * Muestra el menú principal
     */
    public void iniciarMenuPrincipal() {
        getContentPane().removeAll();

        // Usar BorderLayout para organizar mejor los componentes
        setLayout(new BorderLayout());

        // Panel principal con fondo negro
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(Color.BLACK);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));

        // Panel para el logo
        JPanel panelLogo = new JPanel();
        panelLogo.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelLogo.setBackground(Color.BLACK);

        try {
            // Cargar la imagen del logo
            ImageIcon logoIcon = new ImageIcon("G:\\4toSemestre\\POO\\Poker-Proyecto-Final\\src\\recursos\\fondo_poker.png"); // Cambia esto por la ruta correcta
            // Redimensionar la imagen
            Image imagenRedimensionada = logoIcon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(imagenRedimensionada);

            JLabel logoLabel = new JLabel(logoIcon);
            panelLogo.add(logoLabel);
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
            // Si falla, mostramos un texto alternativo
            JLabel logoLabel = new JLabel("M&J ROYALE");
            logoLabel.setFont(new Font("Serif", Font.BOLD, 48));
            logoLabel.setForeground(Color.WHITE);
            panelLogo.add(logoLabel);
        }

        // Panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBackground(Color.BLACK);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Margen superior

        // Crear botones
        JButton botonTexasHoldem = new JButton("Jugar Texas Hold'em");
        JButton botonFiveCard = new JButton("Jugar Five Card Draw");
        JButton botonConfiguracion = new JButton("Configuraciones");
        JButton botonSalir = new JButton("Salir");

        // Estilizar botones
        estilizarBoton(botonTexasHoldem);
        estilizarBoton(botonFiveCard);
        estilizarBoton(botonConfiguracion);
        estilizarBoton(botonSalir);

        // Centrar los botones horizontalmente
        botonTexasHoldem.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonFiveCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonConfiguracion.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonSalir.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Añadir botones al panel con separación
        panelBotones.add(botonTexasHoldem);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));
        panelBotones.add(botonFiveCard);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));
        panelBotones.add(botonConfiguracion);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));
        panelBotones.add(botonSalir);

        // Añadir los paneles al panel principal
        panelPrincipal.add(panelLogo);
        panelPrincipal.add(panelBotones);

        // Añadir panel principal al frame
        add(panelPrincipal, BorderLayout.CENTER);

        // Configurar acciones de los botones
        botonTexasHoldem.addActionListener(e -> configurarJugadores(TEXAS_HOLDEM));
        botonFiveCard.addActionListener(e -> configurarJugadores(FIVE_CARD));
        botonConfiguracion.addActionListener(e -> mostrarConfiguracion());
        botonSalir.addActionListener(e -> System.exit(0));

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
        // Seleccionar número de jugadores
        String[] opcionesNumJugadores = {"2 Jugadores", "3 Jugadores", "4 Jugadores", "5 Jugadores", "6 Jugadores",
                "7 Jugadores", "8 Jugadores", "9 Jugadores", "10 Jugadores"};
        int seleccion = JOptionPane.showOptionDialog(
                this,
                "Seleccione el número de jugadores:",
                "Jugadores",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcionesNumJugadores,
                opcionesNumJugadores[2]); // 4 jugadores por defecto

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
            // Crear juego Five Card Draw
            // FiveCardDraw juegoFiveCard = new FiveCardDraw(numeroJugadores, fichasIniciales);
            // juegoFiveCard.establecerNombresJugadores(nombresJugadores);

            // Crear panel y asignar al panel actual
            // panelFiveCard = new PanelFiveCard(juegoFiveCard, this);
            // panelActual = panelFiveCard;

            iniciarMenuPrincipal();
            return;
        }

        // Añadir el panel al contenedor principal
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelActual, BorderLayout.CENTER);

        // Actualizar ventana
        revalidate();
        repaint();
    }

    //Método main para iniciar la aplicación
    public static void main(String[] args) {
        new VentanaPoker().setVisible(true);
    }
}