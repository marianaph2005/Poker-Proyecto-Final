import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Panel para visualizar y jugar a Poker Cinco Cartas
 */
public class PanelPokerCincoCartas extends JPanel {
    // Juego y ventana
    private PokerCincoCartas juego;
    private VentanaPoker ventana;

    // Paneles de la interfaz
    private JPanel panelMesa;
    private JPanel panelJugadores;
    private JPanel panelControles;
    private JPanel panelInfo;
    private JPanel panelDescarte;

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
    private JButton btnDescartar;
    private JButton btnNoDescartar;
    private JSpinner spinnerApuesta;

    // Componentes de descarte
    private ArrayList<JCheckBox> checkBoxesDescarte;

    // Componentes visuales para jugadores
    private ArrayList<PanelJugador> panelesJugadores;

    // Constantes para gestionar tamaños
    private static final int ANCHO_CARTA = 63;
    private static final int ALTO_CARTA = 110;
    private static final Color COLOR_MESA = new Color(0, 100, 0);
    private static final Color COLOR_DEALER = new Color(255, 215, 0);

    /**
     * Constructor del panel
     */
    public PanelPokerCincoCartas(PokerCincoCartas juego, VentanaPoker ventana) {
        this.juego = juego;
        this.ventana = ventana;
        this.panelesJugadores = new ArrayList<>();
        this.checkBoxesDescarte = new ArrayList<>();

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

        // Panel de información
        crearPanelInfo();

        // Panel central (mesa)
        crearPanelMesa();

        // Panel de descarte
        crearPanelDescarte();

        // Panel de controles
        crearPanelControles();

        // Añadir componentes al panel principal
        add(panelInfo, BorderLayout.NORTH);
        add(panelMesa, BorderLayout.CENTER);
        add(panelDescarte, BorderLayout.EAST);
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
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    /**
     * Crea el panel principal de la mesa de juego
     */
    private void crearPanelMesa() {
        panelMesa = new JPanel(new BorderLayout(10, 10));
        panelMesa.setBackground(COLOR_MESA);

        // Panel central con instrucciones o logo
        JPanel panelCentral = new JPanel();
        panelCentral.setBackground(COLOR_MESA);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 90, 40), 3),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        try {
            // Cargar la imagen del logo
            ImageIcon logoIcon = new ImageIcon("G:\\4toSemestre\\POO\\Poker-Proyecto-Final\\src\\recursos\\fondo_mesa.png");
            Image imagenRedimensionada = logoIcon.getImage().getScaledInstance(400, 200, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(imagenRedimensionada);

            JLabel logoLabel = new JLabel(logoIcon);
            panelCentral.add(logoLabel);
        } catch (Exception e) {
            JLabel lblInstruccion = new JLabel("FIVE CARD DRAW");
            lblInstruccion.setFont(new Font("Arial", Font.BOLD, 24));
            lblInstruccion.setForeground(Color.WHITE);
            panelCentral.add(lblInstruccion);
        }

        crearPanelJugadores();
        panelMesa.add(panelCentral, BorderLayout.CENTER);
        panelMesa.add(panelJugadores, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel para mostrar a los jugadores
     */
    private void crearPanelJugadores() {
        panelJugadores = new JPanel();

        int numJugadores = juego.getJugadores().size();

        if (numJugadores <= 4) {
            panelJugadores.setLayout(new GridLayout(1, numJugadores, 5, 0));
        } else {
            panelJugadores.setLayout(new GridLayout(2, (numJugadores + 1) / 2, 5, 10));
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
     * Crea el panel de descarte (para seleccionar cartas a descartar)
     */
    private void crearPanelDescarte() {
        panelDescarte = new JPanel();
        panelDescarte.setLayout(new BoxLayout(panelDescarte, BoxLayout.Y_AXIS));
        panelDescarte.setBackground(new Color(80, 80, 80));
        panelDescarte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panelDescarte.setPreferredSize(new Dimension(150, 300));

        // Título del panel
        JLabel lblTitulo = new JLabel("Selección de cartas a descartar");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel para los checkboxes
        JPanel panelCheckBoxes = new JPanel(new GridLayout(5, 1, 5, 10));
        panelCheckBoxes.setBackground(new Color(80, 80, 80));

        // Crear 5 checkboxes para seleccionar cartas
        for (int i = 0; i < 5; i++) {
            JCheckBox check = new JCheckBox("Carta " + (i + 1));
            check.setForeground(Color.WHITE);
            check.setBackground(new Color(80, 80, 80));
            check.setEnabled(false);
            checkBoxesDescarte.add(check);
            panelCheckBoxes.add(check);
        }

        // Botones de descarte
        btnDescartar = crearBoton("Descartar seleccionadas", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descartarCartas();
            }
        });
        btnDescartar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDescartar.setEnabled(false);

        btnNoDescartar = crearBoton("No descartar", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.noDescartar();
                actualizarEstadoJuego();
            }
        });
        btnNoDescartar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNoDescartar.setEnabled(false);

        // Añadir componentes al panel
        panelDescarte.add(Box.createVerticalStrut(10));
        panelDescarte.add(lblTitulo);
        panelDescarte.add(Box.createVerticalStrut(15));
        panelDescarte.add(panelCheckBoxes);
        panelDescarte.add(Box.createVerticalStrut(15));
        panelDescarte.add(btnDescartar);
        panelDescarte.add(Box.createVerticalStrut(10));
        panelDescarte.add(btnNoDescartar);
        panelDescarte.add(Box.createVerticalGlue());
    }

    /**
     * Procesa el descarte de cartas seleccionadas
     */
    private void descartarCartas() {
        ArrayList<Integer> posiciones = new ArrayList<>();

        // Recoger posiciones seleccionadas (posiciones 1-5, no 0-4)
        for (int i = 0; i < checkBoxesDescarte.size(); i++) {
            if (checkBoxesDescarte.get(i).isSelected()) {
                posiciones.add(i + 1);
            }
        }

        if (posiciones.isEmpty()) {
            juego.noDescartar();
            actualizarEstadoJuego();
            return;
        }

        // Convertir a array de int para el método descartar
        int[] posicionesArray = new int[posiciones.size()];
        for (int i = 0; i < posiciones.size(); i++) {
            posicionesArray[i] = posiciones.get(i);
        }

        juego.descartar(posicionesArray);

        actualizarEstadoJuego();
        for (JCheckBox check : checkBoxesDescarte) {
            check.setSelected(false);
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

        // Botón iniciar juego
        btnIniciarJuego = crearBoton("Iniciar Juego", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarJuego();
            }
        });

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
                // Obtener el estado del juego
                JuegoPoker.EstadoJuegoInfo estado = juego.obtenerEstadoJuego();

                // Calcular cuánto debe apostar para igualar
                int apuestaActual = estado.apuestaActual;

                // Calcular la diferencia a igualar
                juego.apostar(apuestaActual);
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

        // Separamos los botones de apostar y subir
        btnApostar = crearBoton("Apostar", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cantidad = (int) spinnerApuesta.getValue();
                juego.apostar(cantidad);
                actualizarEstadoJuego();
            }
        });
        btnApostar.setEnabled(false);
        btnSubir = crearBoton("Subir", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cantidad = (int) spinnerApuesta.getValue();
                // Usar apostar en lugar de subir para evitar el bucle infinito
                juego.apostar(cantidad);
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
                        PanelPokerCincoCartas.this,
                        "¿Seguro que deseas volver al menú principal?\nSe perderá la partida actual.",
                        "Volver al Menú",
                        JOptionPane.YES_NO_OPTION
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    ventana.iniciarMenuPrincipal();
                }
            }
        });

        // Botón instrucciones
        JButton btnInstrucciones = crearBoton("Instrucciones", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarInstrucciones();
            }
        });

        // Añadir componentes al panel
        panelControles.add(btnIniciarJuego);
        panelControles.add(btnPasar);
        panelControles.add(btnIgualar);
        panelControles.add(lblCantidad);
        panelControles.add(spinnerApuesta);
        panelControles.add(btnApostar); // Agregamos el botón de apostar
        panelControles.add(btnSubir);   // Y el de subir por separado
        panelControles.add(btnRetirarse);
        panelControles.add(btnInstrucciones);
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
        juego.iniciarJuego(juego.getJugadores().size());
        actualizarEstadoJuego();
        btnIniciarJuego.setEnabled(false);
        actualizarControlesPorEtapa();
    }

    /**
     * Actualiza toda la interfaz según el estado actual del juego
     */
    private void actualizarEstadoJuego() {
        // Obtener toda la información del estado
        JuegoPoker.EstadoJuegoInfo estado = juego.obtenerEstadoJuego();

        // Actualizar etiquetas de información
        lblEstadoJuego.setText("Estado: " + estado.mensajeEstado);
        lblPozo.setText("Pozo: $" + estado.pozo);
        lblApuestaActual.setText("Apuesta: $" + estado.apuestaActual);
        lblTurnoActual.setText("Turno: " + estado.turnoActualNombre);
        lblEtapa.setText("Etapa: " + juego.getEtapaActual());

        boolean mostrarTodasLasCartas = juego.getEtapaActualEnum() == PokerCincoCartas.EtapaJuego.MOSTRAR_CARTAS;
        // Actualizar información de jugadores
        for (int i = 0; i < panelesJugadores.size(); i++) {
            // Mostrar cartas si es turno del jugador O si estamos en la etapa final
            boolean mostrarCartas = (i == estado.turnoActualIndice) || mostrarTodasLasCartas;
            panelesJugadores.get(i).actualizar(estado.infoJugadores.get(i), mostrarCartas);
        }

        // Actualizar botones según el estado
        actualizarControlesPorEtapa();

        // Marcar visualmente el jugador actual
        marcarJugadorActual(estado.turnoActualIndice);
    }

    /**
     * Actualiza los controles según la etapa del juego
     */
    private void actualizarControlesPorEtapa() {
        // Obtener información del estado del juego
        JuegoPoker.EstadoJuegoInfo estado = juego.obtenerEstadoJuego();
        PokerCincoCartas.EtapaJuego etapaActual = juego.getEtapaActualEnum();

        // Si el juego está finalizado, deshabilitar controles de acción
        if (estado.estadoActual == JuegoPoker.EstadoJuego.RONDA_FINALIZADA ||
                estado.estadoActual == JuegoPoker.EstadoJuego.JUEGO_TERMINADO) {
            btnPasar.setEnabled(false);
            btnIgualar.setEnabled(false);
            btnApostar.setEnabled(false);
            btnSubir.setEnabled(false);
            btnRetirarse.setEnabled(false);
            btnDescartar.setEnabled(false);
            btnNoDescartar.setEnabled(false);
            btnIniciarJuego.setEnabled(true);

            // Deshabilitar checkboxes de descarte
            for (JCheckBox check : checkBoxesDescarte) {
                check.setEnabled(false);
                check.setSelected(false);
            }
            return;
        }

        // Si no ha comenzado, solo habilitar iniciar juego
        if (estado.estadoActual == JuegoPoker.EstadoJuego.ESPERANDO_INICIO) {
            btnPasar.setEnabled(false);
            btnIgualar.setEnabled(false);
            btnApostar.setEnabled(false);
            btnSubir.setEnabled(false);
            btnRetirarse.setEnabled(false);
            btnDescartar.setEnabled(false);
            btnNoDescartar.setEnabled(false);
            btnIniciarJuego.setEnabled(true);


            for (JCheckBox check : checkBoxesDescarte) {
                check.setEnabled(false);
                check.setSelected(false);
            }
            return;
        }

        // Configurar botones según la etapa del juego
        if (etapaActual == PokerCincoCartas.EtapaJuego.DESCARTE) {
            // En etapa de descarte, solo habilitar botones de descarte
            btnPasar.setEnabled(false);
            btnIgualar.setEnabled(false);
            btnApostar.setEnabled(false);
            btnSubir.setEnabled(false);
            btnRetirarse.setEnabled(true);
            btnDescartar.setEnabled(true);
            btnNoDescartar.setEnabled(true);

            // Habilitar checkboxes para la selección de cartas
            Jugador jugadorActual = juego.getJugadores().get(estado.turnoActualIndice);
            boolean habilitarCheckboxes = jugadorActual.estaActivo() && !jugadorActual.isAllIn();

            // Configurar los checkboxes con los nombres de las cartas del jugador actual
            if (habilitarCheckboxes && jugadorActual.getMano() != null) {
                ArrayList<Carta> cartasJugador = jugadorActual.getMano().getMano();
                for (int i = 0; i < checkBoxesDescarte.size(); i++) {
                    if (i < cartasJugador.size()) {
                        checkBoxesDescarte.get(i).setText("Carta " + (i+1) + ": " + cartasJugador.get(i).toString());
                        checkBoxesDescarte.get(i).setEnabled(true);
                    } else {
                        checkBoxesDescarte.get(i).setText("Carta " + (i+1));
                        checkBoxesDescarte.get(i).setEnabled(false);
                    }
                }
            } else {
                // Deshabilitar checkboxes si no es el turno del jugador
                for (JCheckBox check : checkBoxesDescarte) {
                    check.setEnabled(false);
                }
            }
        } else {
            // En otras etapas, habilitar botones de apuesta y deshabilitar descarte
            btnDescartar.setEnabled(false);
            btnNoDescartar.setEnabled(false);

            // Deshabilitar checkboxes de descarte
            for (JCheckBox check : checkBoxesDescarte) {
                check.setEnabled(false);
                check.setSelected(false);
            }

            if (estado.estadoActual == JuegoPoker.EstadoJuego.ESPERANDO_ACCION) {
                btnIniciarJuego.setEnabled(false);

                // El botón de pasar solo se habilita si no hay apuesta activa
                btnPasar.setEnabled(estado.apuestaActual == 0);

                // El botón de igualar solo se habilita si hay apuesta activa
                btnIgualar.setEnabled(estado.apuestaActual > 0);

                // El botón de apostar solo se habilita si no hay apuesta activa
                btnApostar.setEnabled(estado.apuestaActual == 0);

                // El botón de subir solo se habilita si hay apuesta activa
                btnSubir.setEnabled(estado.apuestaActual > 0);

                // Siempre se puede retirar
                btnRetirarse.setEnabled(true);

                // Ajustar el spinner según la apuesta actual o mínima
                int minApuesta = Math.max(10, estado.apuestaActual);
                SpinnerNumberModel model = (SpinnerNumberModel) spinnerApuesta.getModel();
                model.setMinimum(minApuesta);

                // Si hay una apuesta activa, la cantidad mínima para subir debe ser al menos esa apuesta
                if (estado.apuestaActual > 0) {
                    model.setValue(estado.apuestaActual);
                } else {
                    model.setValue(10);
                }
            }
        }
    }

    /**
     * Marca visualmente el jugador que tiene el turno actual
     */
    private void marcarJugadorActual(int indiceJugador) {
        // Primero, resetear todos los paneles
        for (PanelJugador panel : panelesJugadores) {
            panel.setEsTurnoActual(false);
        }

        // Marcar el jugador actual
        if (indiceJugador >= 0 && indiceJugador < panelesJugadores.size()) {
            panelesJugadores.get(indiceJugador).setEsTurnoActual(true);
        }
    }

    /**
     * Clase interna para representar visualmente a un jugador
     */
    private class PanelJugador extends JPanel {
        private Jugador jugador;
        private int indice;
        private JLabel lblNombre;
        private JLabel lblFichas;
        private JLabel lblEstado;
        private JPanel panelCartas;
        private ArrayList<JLabel> cartasLabels;
        private boolean esTurnoActual;
        private ImageIcon imagenReverso;

        public PanelJugador(Jugador jugador, int indice) {
            this.jugador = jugador;
            this.indice = indice;
            this.esTurnoActual = false;
            this.cartasLabels = new ArrayList<>();

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

            lblEstado = new JLabel("Activo");
            lblEstado.setFont(new Font("Arial", Font.ITALIC, 12));
            lblEstado.setForeground(Color.GREEN);
            lblEstado.setHorizontalAlignment(SwingConstants.RIGHT);

            panelSuperior.add(lblNombre, BorderLayout.WEST);
            panelSuperior.add(lblEstado, BorderLayout.EAST);

            // Panel de cartas
            panelCartas = new JPanel(new GridLayout(1, 5, 2, 0));
            panelCartas.setOpaque(false);

            // Crear 5 espacios para cartas
            for (int i = 0; i < 5; i++) {
                JLabel cartaLabel = new JLabel();
                cartaLabel.setPreferredSize(new Dimension(ANCHO_CARTA / 3, ALTO_CARTA / 3));
                cartaLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                cartaLabel.setBackground(new Color(200, 200, 200));
                cartaLabel.setOpaque(true);
                cartaLabel.setHorizontalAlignment(SwingConstants.CENTER);
                cartaLabel.setFont(new Font("Arial", Font.BOLD, 10));

                cartasLabels.add(cartaLabel);
                panelCartas.add(cartaLabel);
            }
            // Panel inferior con fichas
            JPanel panelInferior = new JPanel(new BorderLayout(5, 0));
            panelInferior.setOpaque(false);
            lblFichas = new JLabel("$" + jugador.getFichas());
            lblFichas.setFont(new Font("Arial", Font.BOLD, 14));
            lblFichas.setForeground(new Color(255, 255, 150));
            panelInferior.add(lblFichas, BorderLayout.WEST);
            add(panelSuperior, BorderLayout.NORTH);
            add(panelCartas, BorderLayout.CENTER);
            add(panelInferior, BorderLayout.SOUTH);
        }

        /**
         * Actualiza la información del jugador
         * El parámetro mostrarCartas controla si se muestran las cartas (solo para el jugador activo)
         */
        public void actualizar(Jugador.Info info, boolean mostrarCartas) {
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

            // Actualizar cartas
            if (info.tieneCartas && info.cartas != null) {
                if (mostrarCartas) {
                    // Mostrar cartas del jugador activo con imágenes
                    for (int i = 0; i < cartasLabels.size(); i++) {
                        if (i < info.cartas.size()) {
                            // Usar el método getImagen de la clase Carta para obtener la imagen correspondiente
                            Carta carta = info.cartas.get(i);
                            cartasLabels.get(i).setIcon(carta.getImagen(ANCHO_CARTA, ALTO_CARTA));
                            cartasLabels.get(i).setText(""); // Quitar cualquier texto
                            cartasLabels.get(i).setOpaque(false); // Hacer transparente para mostrar solo la imagen
                        } else {
                            // Si hay menos de 5 cartas, mostrar espacios vacíos
                            cartasLabels.get(i).setIcon(null);
                            cartasLabels.get(i).setText("");
                            cartasLabels.get(i).setBackground(new Color(200, 200, 200));
                            cartasLabels.get(i).setOpaque(true);
                        }
                    }
                } else {
                    // Para jugadores que no están en su turno, mostrar reverso de cartas
                    for (JLabel cartaLabel : cartasLabels) {
                        if (info.tieneCartas) {
                            // Mostrar el reverso de la carta
                            cartaLabel.setIcon(imagenReverso);
                            cartaLabel.setText("");
                            cartaLabel.setOpaque(false);
                        } else {
                            cartaLabel.setIcon(null);
                            cartaLabel.setText("");
                            cartaLabel.setBackground(new Color(200, 200, 200));
                            cartaLabel.setOpaque(true);
                        }
                    }
                }
            } else {
                // Si el jugador no tiene cartas, mostrar espacios vacíos
                for (JLabel cartaLabel : cartasLabels) {
                    cartaLabel.setIcon(null);
                    cartaLabel.setText("");
                    cartaLabel.setBackground(new Color(200, 200, 200));
                    cartaLabel.setOpaque(true);
                }
            }

            // Actualizar el borde según si es el turno actual
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

        /**
         * Establece si este jugador tiene el turno actual
         */
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
    }

    /**
     * Muestra un diálogo con instrucciones del juego
     */
    public void mostrarInstrucciones() {
        String instrucciones = "FIVE CARD DRAW - INSTRUCCIONES\n\n" +
                "1. Cada jugador recibe 5 cartas privadas.\n" +
                "2. Se realizan dos rondas de apuestas con un descarte en medio:\n" +
                "   - Primera ronda de apuestas\n" +
                "   - Descarte (cada jugador puede descartar hasta 3 cartas, o 4 si tiene un As)\n" +
                "   - Segunda ronda de apuestas\n\n" +
                "3. Acciones disponibles:\n" +
                "   - Pasar: no apostar (solo si nadie ha apostado)\n" +
                "   - Igualar: poner la misma cantidad que la apuesta más alta\n" +
                "   - Apostar/Subir: hacer una nueva apuesta o aumentar la existente\n" +
                "   - Retirarse: abandonar la mano actual\n\n" +
                "4. Jerarquía de manos (de mayor a menor):\n" +
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
                "Instrucciones de Five Card Draw", JOptionPane.INFORMATION_MESSAGE);
    }
}