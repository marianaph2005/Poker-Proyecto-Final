import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


/**
 * Panel para visualizar y jugar a Texas Hold'em
 * Gestiona la interfaz gráfica del juego y la interacción con el usuario
 */
public class PanelTexasHoldem extends JPanel {
    // Juego y ventana
    private TexasHoldem juego;
    private VentanaPoker ventana;


    // Paneles de la interfaz
    private JPanel panelMesa;
    private JPanel panelCartasComunitarias;
    private JPanel panelJugadores;
    private JPanel panelControles;
    private JPanel panelInfo;

    // Componentes de información
    private JLabel lblEstadoJuego;
    private JLabel lblPozo;
    private JLabel lblApuestaActual;
    private JLabel lblTurnoActual;
    private JLabel lblEtapa;

    // Componentes para acciones del jugador
    private JButton btnPasar;
    private JButton btnIgualar;
    private JButton btnApostar;
    private JButton btnSubir;
    private JButton btnRetirarse;
    private JButton btnIniciarJuego;
    private JButton btnInstrucciones;
    private JSpinner spinnerApuesta;


    // Componentes visuales para jugadores y cartas
    private ArrayList<PanelJugador> panelesJugadores;
    private ArrayList<JLabel> cartasComunitarias;

    // Constantes para gestionar tamaños
    private static final int ANCHO_CARTA = 80;
    private static final int ALTO_CARTA = 120;
    private static final Color COLOR_MESA = new Color(15, 80, 20);
    private static final Color COLOR_DEALER = new Color(255, 215, 0);

    /**
     * Constructor del panel
     */
    public PanelTexasHoldem(TexasHoldem juego, VentanaPoker ventana) {
        this.juego = juego;
        this.ventana = ventana;
        this.panelesJugadores = new ArrayList<>();
        this.cartasComunitarias = new ArrayList<>();

        // Inicializar interfaz
        inicializarInterfaz();

        // Actualizar estado inicial
        actualizarEstadoJuego();
    }

    /**
     * Inicializa todos los componentes de la interfaz
     */
    private void inicializarInterfaz() {
        // Configuración básica
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_MESA);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1000, 800));

        try {
            ImageIcon fondoIcon = new ImageIcon("G:\\4toSemestre\\POO\\Poker-Proyecto-Final\\src\\recursos\\fondo_mesa.webp");
            Image imgRedimensionada = fondoIcon.getImage().getScaledInstance(1000, 800, Image.SCALE_SMOOTH);
            fondoIcon = new ImageIcon(imgRedimensionada);
            lblPozo = new JLabel(fondoIcon);
            lblPozo.setBounds(0, 0, 1000, 800);
            layeredPane.add(lblPozo, JLayeredPane.DEFAULT_LAYER);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de fondo: " + e.getMessage());
        }


        // Panel de información
        crearPanelInfo();

        // Panel central (mesa)
        crearPanelMesa();

        // Panel de controles
        crearPanelControles();

        // Añadir componentes al panel principal
        add(panelInfo, BorderLayout.NORTH);
        add(panelMesa, BorderLayout.CENTER);
        add(panelControles, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel de información superior
     */
    private void crearPanelInfo() {
        panelInfo = new JPanel(new GridLayout(1, 5, 10, 0));
        panelInfo.setBackground(new Color(50, 50, 50));
        panelInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Etiquetas de información
        lblEstadoJuego = crearEtiquetaInfo("Estado: Esperando inicio");
        lblPozo = crearEtiquetaInfo("Pozo: $0");
        lblApuestaActual = crearEtiquetaInfo("Apuesta: $0");
        lblTurnoActual = crearEtiquetaInfo("Turno: -");
        lblEtapa = crearEtiquetaInfo("Etapa: -");

        // Añadir etiquetas al panel
        panelInfo.add(lblEstadoJuego);
        panelInfo.add(lblPozo);
        panelInfo.add(lblApuestaActual);
        panelInfo.add(lblTurnoActual);
        panelInfo.add(lblEtapa);
    }

    /**
     * Crea una etiqueta con estilo informativo
     */
    private JLabel crearEtiquetaInfo(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Serif", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    /**
     * Crea el panel principal de la mesa de juego
     */
    private void crearPanelMesa() {
        panelMesa = new JPanel(new BorderLayout(10, 10));
        panelMesa.setOpaque(false);

        // Panel para cartas comunitarias
        crearPanelCartasComunitarias();

        // Panel para jugadores (en los bordes)
        crearPanelJugadores();

        // Añadir ambos paneles a la mesa
        panelMesa.add(panelCartasComunitarias, BorderLayout.CENTER);
        panelMesa.add(panelJugadores, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel que contiene las cartas comunitarias
     */
    private void crearPanelCartasComunitarias() {
        panelCartasComunitarias = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelCartasComunitarias.setBackground(COLOR_MESA);
        panelCartasComunitarias.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 90, 40), 3),
                BorderFactory.createEmptyBorder(30, 10, 30, 10)
        ));

        // Crear espacio para 5 cartas comunitarias
        for (int i = 0; i < 5; i++) {
            JLabel cartaLabel = new JLabel();
            cartaLabel.setPreferredSize(new Dimension(ANCHO_CARTA, ALTO_CARTA));
            cartaLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            cartaLabel.setBackground(new Color(200, 200, 200));
            cartaLabel.setOpaque(true);

            cartasComunitarias.add(cartaLabel);
            panelCartasComunitarias.add(cartaLabel);
        }
    }

    /**
     * Crea el panel para mostrar a los jugadores
     */
    private void crearPanelJugadores() {
        panelJugadores = new JPanel();

        // Usar un layout diferente según el número de jugadores
        int numJugadores = juego.getJugadores().size();

        if (numJugadores <= 4) {
            panelJugadores.setLayout(new GridLayout(1, numJugadores, 10, 0));
        } else {
            // Para 5-6 jugadores, usar dos filas
            panelJugadores.setLayout(new GridLayout(2, (numJugadores + 1) / 2, 10, 10));
        }

        panelJugadores.setBackground(COLOR_MESA);
        panelJugadores.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear un panel para cada jugador
        for (int i = 0; i < numJugadores; i++) {
            PanelJugador panelJugador = new PanelJugador(juego.getJugadores().get(i), i);
            panelesJugadores.add(panelJugador);
            panelJugadores.add(panelJugador);
        }
    }

    /**
     * Crea el panel de controles inferior
     */
    private void crearPanelControles() {
        panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelControles.setBackground(new Color(50, 50, 50));
        panelControles.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Iniciar juego
        btnIniciarJuego = crearBoton("Iniciar Juego", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarJuego();
            }
        });

        btnInstrucciones = crearBoton("Instrucciones", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarInstrucciones();
            }
        });
        btnInstrucciones.setEnabled(false);

        // Botón pasar
        btnPasar = crearBoton("Pasar", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.pasar();
                actualizarEstadoJuego();
            }
        });
        btnPasar.setEnabled(false);

        // Botón igualar
        btnIgualar = crearBoton("Igualar", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.igualar();
                actualizarEstadoJuego();
            }
        });
        btnIgualar.setEnabled(false);

        // Spinner para cantidad a apostar/subir
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 14));
        lblCantidad.setForeground(Color.WHITE);

        SpinnerNumberModel modeloApuesta = new SpinnerNumberModel(10, 1, 1000, 5);
        spinnerApuesta = new JSpinner(modeloApuesta);
        spinnerApuesta.setPreferredSize(new Dimension(80, 30));
        ((JSpinner.DefaultEditor) spinnerApuesta.getEditor()).getTextField().setEditable(true);

        // Botón apostar (exclusivo cuando no hay apuesta previa)
        btnApostar = crearBoton("Apostar", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cantidad = (int) spinnerApuesta.getValue();
                juego.apostar(cantidad);
                actualizarEstadoJuego();
            }
        });
        btnApostar.setEnabled(false);

        //Botón subir (exclusivo cuando ya hay una apuesta)
        btnSubir = crearBoton("Subir", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cantidad = (int) spinnerApuesta.getValue();
                juego.subir(cantidad);
                actualizarEstadoJuego();
            }
        });
        btnSubir.setEnabled(false);

        // Botón retirarse
        btnRetirarse = crearBoton("Retirarse", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.retirarse();
                actualizarEstadoJuego();
            }
        });
        btnRetirarse.setEnabled(false);

        // Botón volver al menú
        JButton btnMenu = crearBoton("Menú Principal", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        PanelTexasHoldem.this,
                        "¿Seguro que deseas volver al menú principal?\nSe perderá la partida actual.",
                        "Volver al Menú",
                        JOptionPane.YES_NO_OPTION
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    ventana.iniciarMenuPrincipal();
                }
            }
        });

        // Añadir componentes al panel
        panelControles.add(btnIniciarJuego);
        panelControles.add(btnInstrucciones);
        panelControles.add(btnPasar);
        panelControles.add(btnIgualar);
        panelControles.add(lblCantidad);
        panelControles.add(spinnerApuesta);
        panelControles.add(btnApostar);
        panelControles.add(btnSubir);
        panelControles.add(btnRetirarse);
        panelControles.add(btnMenu);
    }

    /**
     * Crea un botón con estilo para los controles
     */
    private JButton crearBoton(String texto, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.addActionListener(listener);
        boton.setBackground(new Color(150, 0, 0));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createRaisedBevelBorder());

        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (boton.isEnabled()) {
                    boton.setBackground(new Color(180, 0, 0));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (boton.isEnabled()) {
                    boton.setBackground(new Color(150, 0, 0));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (boton.isEnabled()) {
                    boton.setBackground(new Color(120, 0, 0));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (boton.isEnabled()) {
                    boton.setBackground(new Color(150, 0, 0));
                }
            }
        });

        return boton;
    }

    /**
     * Inicia una nueva ronda de juego
     */
    private void iniciarJuego() {
        juego.jugarRonda();
        actualizarEstadoJuego();

        // Desactivar botón de iniciar y activar controles de juego
        btnIniciarJuego.setEnabled(false);
        btnPasar.setEnabled(true);
        btnIgualar.setEnabled(false);
        btnApostar.setEnabled(true);
        btnSubir.setEnabled(false);
        btnRetirarse.setEnabled(true);
    }

    /**
     * Actualiza toda la interfaz según el estado actual del juego
     */
    private void actualizarEstadoJuego() {
        // Obtener toda la información del estado
        JuegoPoker.EstadoJuegoInfo estado = juego.obtenerEstadoJuego();
        boolean esMostrarCartas = juego.getEtapaActual() == TexasHoldem.Etapa.SHOWDOWN;

        // Actualizar etiquetas de información
        lblEstadoJuego.setText("Estado: " + estado.mensajeEstado);
        lblPozo.setText("Pozo: $" + estado.pozo);
        lblApuestaActual.setText("Apuesta: $" + estado.apuestaActual);
        lblTurnoActual.setText("Turno: " + estado.turnoActualNombre);
        lblEtapa.setText("Etapa: " + juego.getNombreEtapa());

        // Actualizar información de jugadores
        for (int i = 0; i < panelesJugadores.size(); i++) {

            if (esMostrarCartas) {

                estado.turnoActualIndice = i;
            }
            panelesJugadores.get(i).actualizar(estado.infoJugadores.get(i));
        }

        // Restaurar el índice correcto después de la actualización
        if (esMostrarCartas) {
            estado.turnoActualIndice = juego.getTurnoActual();
        }

        // Actualizar cartas comunitarias
        actualizarCartasComunitarias();

        // Actualizar botones según el estado
        actualizarControles(estado);

        // Marcar visualmente el jugador actual
        marcarJugadorActual(estado.turnoActualIndice);

        // Marcar posiciones de dealer, small blind y big blind
        marcarPosicionesEspeciales();
    }

    /**
     * Actualiza la visualización de las cartas comunitarias
     */
    private void actualizarCartasComunitarias() {
        ArrayList<Carta> cartas = juego.getCartasComunitarias();

        for (int i = 0; i < 5; i++) {
            JLabel cartaLabel = cartasComunitarias.get(i);

            if (i < cartas.size()) {
                Carta carta = cartas.get(i);

                ImageIcon imagenCarta = carta.getImagen(ANCHO_CARTA, ALTO_CARTA);
                cartaLabel.setIcon(imagenCarta);
                cartaLabel.setText(""); // Limpiar texto si había alguno
                cartaLabel.setBackground(Color.WHITE);
            } else {
                cartaLabel.setIcon(null); // Eliminar imagen si no hay carta
                cartaLabel.setText("");
                cartaLabel.setBackground(new Color(200, 200, 200));
            }
        }
    }

    /**
     * Actualiza los controles según el estado del juego
     */
    private void actualizarControles(JuegoPoker.EstadoJuegoInfo estado) {
        TexasHoldem.Etapa etapaActual = juego.getEtapaActual();

        // Si el juego está finalizado, deshabilitar controles de acción
        if (estado.estadoActual == JuegoPoker.EstadoJuego.RONDA_FINALIZADA ||
                estado.estadoActual == JuegoPoker.EstadoJuego.JUEGO_TERMINADO) {
            btnPasar.setEnabled(false);
            btnIgualar.setEnabled(false);
            btnApostar.setEnabled(false);
            btnSubir.setEnabled(false);
            btnRetirarse.setEnabled(false);
            btnIniciarJuego.setEnabled(true);
            return;
        }

        // Si no ha comenzado, solo habilitar iniciar juego
        if (estado.estadoActual == JuegoPoker.EstadoJuego.ESPERANDO_INICIO) {
            btnPasar.setEnabled(false);
            btnIgualar.setEnabled(false);
            btnApostar.setEnabled(false);
            btnSubir.setEnabled(false);
            btnRetirarse.setEnabled(false);
            btnIniciarJuego.setEnabled(true);
            return;
        }

        // Habilitar controles según el estado
        if (estado.estadoActual == JuegoPoker.EstadoJuego.ESPERANDO_ACCION ||
                estado.estadoActual == JuegoPoker.EstadoJuego.REPARTIENDO_CARTAS) {

            btnIniciarJuego.setEnabled(false);

            // El botón de pasar solo se habilita si no hay apuesta activa
            btnPasar.setEnabled(estado.apuestaActual == 0);

            // El botón de igualar solo se habilita si hay apuesta activa
            btnIgualar.setEnabled(estado.apuestaActual > 0);

            // El botón de apostar solo se habilita si NO hay apuesta activa
            btnApostar.setEnabled(estado.apuestaActual == 0);

            // El botón de subir solo se habilita si hay apuesta activa
            btnSubir.setEnabled(estado.apuestaActual > 0);

            btnRetirarse.setEnabled(true);

            // Ajustar el spinner según la apuesta actual
            int minApuesta;
            SpinnerNumberModel model = (SpinnerNumberModel) spinnerApuesta.getModel();

            if (estado.apuestaActual > 0) {
                // Si hay apuesta, la mínima es esa apuesta (para subir)
                minApuesta = estado.apuestaActual;
                model.setMinimum(minApuesta);
                model.setValue(minApuesta);
            } else {
                // Si no hay apuesta, la mínima es el big blind (para apostar)
                minApuesta = juego.getBigBlindValue();
                model.setMinimum(minApuesta);
                model.setValue(minApuesta);
            }
        }
    }

    //Marca visualmente el jugador que tiene el turno actual
    private void marcarJugadorActual(int indiceJugador) {
        for (PanelJugador panel : panelesJugadores) {
            panel.setEsTurnoActual(false);
        }
        // Marcar el jugador actual
        if (indiceJugador >= 0 && indiceJugador < panelesJugadores.size()) {
            panelesJugadores.get(indiceJugador).setEsTurnoActual(true);
        }
    }

    //Marca las posiciones especiales: dealer, small blind y big blind
    private void marcarPosicionesEspeciales() {
        for (PanelJugador panel : panelesJugadores) {
            panel.setPosicionEspecial("");
        }

        // Marcar posiciones especiales
        int posicionDealer = juego.getPosicionDealer();
        int posicionSmallBlind = juego.getPosicionSmallBlind();
        int posicionBigBlind = juego.getPosicionBigBlind();

        if (posicionDealer >= 0 && posicionDealer < panelesJugadores.size()) {
            panelesJugadores.get(posicionDealer).setPosicionEspecial("D");
        }

        if (posicionSmallBlind >= 0 && posicionSmallBlind < panelesJugadores.size()) {
            panelesJugadores.get(posicionSmallBlind).setPosicionEspecial("SB");
        }

        if (posicionBigBlind >= 0 && posicionBigBlind < panelesJugadores.size()) {
            panelesJugadores.get(posicionBigBlind).setPosicionEspecial("BB");
        }
    }

    //Clase interna para representar visualmente a un jugador
    private class PanelJugador extends JPanel {
        private Jugador jugador;
        private int índice;
        private JLabel lblNombre;
        private JLabel lblFichas;
        private JLabel lblEstado;
        private JPanel panelCartas;
        private JLabel carta1;
        private JLabel carta2;
        private JLabel labelPosicion;
        private boolean esTurnoActual;
        private String posicionEspecial;
        private ImageIcon imagenReverso;


        public PanelJugador(Jugador jugador, int indice) {
            this.jugador = jugador;
            this.índice = indice;
            this.esTurnoActual = false;
            this.posicionEspecial = "";

            try {
                ImageIcon iconoOriginal = new ImageIcon("G:\\4toSemestre\\POO\\Poker-Proyecto-Final\\src\\recursos\\reverso.png");
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ANCHO_CARTA, ALTO_CARTA, Image.SCALE_SMOOTH);
                imagenReverso = new ImageIcon(imagenEscalada);
            } catch (Exception e) {
                System.err.println("Error al cargar la imagen del reverso: " + e.getMessage());
                imagenReverso = null;
            }

            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 2),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            setBackground(new Color(70, 70, 70));
            setPreferredSize(new Dimension(180, 200));

            // Panel superior con nombre y estado
            JPanel panelSuperior = new JPanel(new BorderLayout(5, 0));
            panelSuperior.setOpaque(false);

            lblNombre = new JLabel(jugador.getNombre());
            lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
            lblNombre.setForeground(Color.WHITE);

            labelPosicion = new JLabel("");
            labelPosicion.setFont(new Font("Arial", Font.BOLD, 14));
            labelPosicion.setForeground(COLOR_DEALER);
            labelPosicion.setHorizontalAlignment(SwingConstants.RIGHT);

            panelSuperior.add(lblNombre, BorderLayout.WEST);
            panelSuperior.add(labelPosicion, BorderLayout.EAST);

            panelCartas = new JPanel(new GridLayout(1, 2, 5, 0));
            panelCartas.setOpaque(false);

            ImageIcon iconoOriginal = new ImageIcon("ruta/imagen.png");
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);

            carta1 = new JLabel();
            carta1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            carta1.setOpaque(true);
            carta1.setHorizontalAlignment(SwingConstants.CENTER);

            carta2 = new JLabel();
            carta2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            carta2.setOpaque(true);
            carta2.setHorizontalAlignment(SwingConstants.CENTER);

            panelCartas.add(carta1);
            panelCartas.add(carta2);

            // Panel inferior con fichas y estado
            JPanel panelInferior = new JPanel(new BorderLayout(5, 0));
            panelInferior.setOpaque(false);

            lblFichas = new JLabel("$" + jugador.getFichas());
            lblFichas.setFont(new Font("Arial", Font.BOLD, 14));
            lblFichas.setForeground(new Color(255, 255, 150));

            lblEstado = new JLabel("Activo");
            lblEstado.setFont(new Font("Arial", Font.ITALIC, 12));
            lblEstado.setForeground(Color.GREEN);
            lblEstado.setHorizontalAlignment(SwingConstants.RIGHT);

            panelInferior.add(lblFichas, BorderLayout.WEST);
            panelInferior.add(lblEstado, BorderLayout.EAST);
            add(panelSuperior, BorderLayout.NORTH);
            add(panelCartas, BorderLayout.CENTER);
            add(panelInferior, BorderLayout.SOUTH);
        }

        //Actualiza la información del jugador
        public void actualizar(Jugador.Info info) {
            // Actualizar etiquetas
            lblFichas.setText("$" + info.fichas);

            // Actualizar estado
            if (!info.activo) {
                lblEstado.setText("Retirado");
                lblEstado.setForeground(Color.RED);
            } else if (info.allIn) {
                lblEstado.setText("All-In");
                lblEstado.setForeground(new Color(255, 150, 0));
            } else {
                lblEstado.setText("Activo");
                lblEstado.setForeground(Color.GREEN);
            }

            // Verificar si estamos en SHOWDOWN
            boolean esMostrarCartas = juego.getEtapaActual() == TexasHoldem.Etapa.SHOWDOWN;
            boolean esTurnoJugador = this.jugador.equals(juego.getJugadores().get(juego.getTurnoActual()));

            if (info.tieneCartas && info.cartas != null && info.cartas.size() == 2 &&
                    (esTurnoJugador || esMostrarCartas)) {

                // Usar el tamaño adecuado para las cartas de jugador
                ImageIcon imgCarta1 = info.cartas.get(0).getImagen(90, 130);
                ImageIcon imgCarta2 = info.cartas.get(1).getImagen(90, 130);

                // Verificar que las imágenes se cargaron correctamente
                if (imgCarta1.getIconWidth() <= 0) {
                    System.err.println("Error: La imagen de la carta 1 no se cargó correctamente");
                    carta1.setText("ERROR");
                } else {
                    carta1.setIcon(imgCarta1);
                    carta1.setText("");
                }

                if (imgCarta2.getIconWidth() <= 0) {
                    System.err.println("Error: La imagen de la carta 2 no se cargó correctamente");
                    carta2.setText("ERROR");
                } else {
                    carta2.setIcon(imgCarta2);
                    carta2.setText("");
                }
                carta1.setOpaque(false);
                carta2.setOpaque(false);
            } else {
                // Si las cartas no son visibles o el jugador no tiene cartas
                carta1.setIcon(imagenReverso);
                carta1.setBackground(new Color(200, 30, 30));
                carta1.setOpaque(true);

                carta2.setIcon(imagenReverso);
                carta2.setBackground(new Color(200, 30, 30));
                carta2.setOpaque(true);
            }
            // Actualizar el borde según si es el turno actual
            if (esTurnoActual) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            } else {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 2),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
            // Actualizar la posición especial
            if (!posicionEspecial.isEmpty()) {
                labelPosicion.setText(posicionEspecial);
            } else {
                labelPosicion.setText("");
            }
        }

        //Establece si este jugador tiene el turno actual
        public void setEsTurnoActual(boolean esTurnoActual) {
            this.esTurnoActual = esTurnoActual;

            if (esTurnoActual) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                setBackground(new Color(90, 90, 90));
            } else {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 2),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                setBackground(new Color(70, 70, 70));
            }
        }

        //Establece la posición especial del jugador (D, SB, BB)
        public void setPosicionEspecial(String posicion) {
            this.posicionEspecial = posicion;
            labelPosicion.setText(posicion);
        }
    }

    //Muestra un diálogo con instrucciones del juego
    public void mostrarInstrucciones() {
        String instrucciones = "TEXAS HOLD'EM - INSTRUCCIONES\n\n" +
                "1. Cada jugador recibe 2 cartas privadas.\n" +
                "2. Se reparten 5 cartas comunitarias en tres etapas:\n" +
                "   - Flop: primeras 3 cartas\n" +
                "   - Turn: cuarta carta\n" +
                "   - River: quinta carta\n\n" +
                "3. Acciones disponibles:\n" +
                "   - Pasar: no apostar (solo si nadie ha apostado)\n" +
                "   - Igualar: poner la misma cantidad que la apuesta más alta\n" +
                "   - Apostar/Subir: hacer una nueva apuesta o aumentar la existente\n" +
                "   - Retirarse: abandonar la mano actual\n\n" +
                "4. El objetivo es formar la mejor mano posible de 5 cartas\n" +
                "   usando tus 2 cartas privadas y las 5 comunitarias.\n\n" +
                "5. Jerarquía de manos (de mayor a menor):\n" +
                "   - Escalera Real\n" +
                "   - Escalera de Color\n" +
                "   - Poker (4 cartas iguales)\n" +
                "   - Full House (3 iguales + 2 iguales)\n" +
                "   - Color (5 del mismo palo)\n" +
                "   - Escalera (5 consecutivas)\n" +
                "   - Trío (3 iguales)\n" +
                "   - Doble Par\n" +
                "   - Par\n" +
                "   - Carta Alta";
        JOptionPane.showMessageDialog(this, instrucciones,
                "Instrucciones de Texas Hold'em", JOptionPane.INFORMATION_MESSAGE);
    }
}
