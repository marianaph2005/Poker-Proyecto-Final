import java.util.ArrayList;

/**
 * Implementación del juego de Poker con cinco cartas (Five Card Draw)
 * Extiende la clase abstracta JuegoPoker
 */
public class PokerCincoCartas extends JuegoPoker {

    // Etapas específicas del juego Five Card Draw
    public enum EtapaJuego {
        APUESTAS_INICIALES,
        DESCARTE,
        APUESTAS_FINALES,
        MOSTRAR_CARTAS
    }

    private EtapaJuego etapaActual;
    private int jugadoresQueHanDescartado;
    private boolean rondaDeDescarteTerminada;
    private boolean[] jugadoresTurno; // Para rastrear si cada jugador ha tenido su turno
    private int jugadorInicialRonda; // Para recordar quién comenzó la ronda

    /**
     * Constructor para inicializar el juego con número de jugadores y fichas iniciales
     */
    public PokerCincoCartas(int numeroDeJugadores, int dineroInicial) {
        super(numeroDeJugadores, dineroInicial);
        this.etapaActual = EtapaJuego.APUESTAS_INICIALES;
        this.jugadoresQueHanDescartado = 0;
        this.rondaDeDescarteTerminada = false;
    }

    /**
     * Inicializa el juego con el número de jugadores
     */
    @Override
    public void iniciarJuego(int numJugadores) {
        // Verificar si hay al menos 2 jugadores con fichas para continuar el juego
        int jugadoresConFichas = 0;
        Jugador jugadorGanador = null;

        for (Jugador jugador : jugadores) {
            if (jugador.getFichas() > 0) {
                jugadoresConFichas++;
                jugadorGanador = jugador;
            }
        }

        // Si solo queda un jugador con fichas, declararlo ganador del juego
        if (jugadoresConFichas <= 1 && jugadorGanador != null) {
            mensajeEstado = "¡Juego terminado! " + jugadorGanador.getNombre() +
                    " es el ganador con " + jugadorGanador.getFichas() + " fichas.";
            estadoActual = EstadoJuego.JUEGO_TERMINADO;
            return;
        }

        // Si no hay suficientes jugadores con fichas, no iniciar el juego
        if (jugadoresConFichas < 2) {
            mensajeEstado = "No hay suficientes jugadores con fichas para iniciar una nueva ronda.";
            estadoActual = EstadoJuego.JUEGO_TERMINADO;
            return;
        }

        // Continuar con la inicialización normal
        // Resetear el mazo
        this.mazo = new Mazo();
        this.mazo.barajear();

        // Resetear estado del juego
        this.pozo = 0;
        this.apuestaActual = 0;
        this.rondaTerminada = false;
        this.estadoActual = EstadoJuego.ESPERANDO_INICIO;
        this.mensajeEstado = "Juego de Cinco Cartas inicializado.";
        this.apuestasAcumuladas.clear();

        // Resetear estado específico del Five Card Draw
        this.etapaActual = EtapaJuego.APUESTAS_INICIALES;
        this.jugadoresQueHanDescartado = 0;
        this.rondaDeDescarteTerminada = false;

        this.jugadoresTurno = new boolean[jugadores.size()];
        for (int i = 0; i < jugadoresTurno.length; i++) {
            jugadoresTurno[i] = false;
        }


        // Limpiar manos y activar SOLO jugadores con fichas
        for (Jugador jugador : jugadores) {
            jugador.limpiarMano();
            // Solo activar jugadores con fichas
            if (jugador.getFichas() > 0) {
                jugador.activar();
            } else {
                // Marcar como inactivo a jugadores sin fichas
                jugador.abandonar();
            }
        }


        // Determinar turno inicial (asegurarse que sea un jugador con fichas)
        determinarTurnoInicial();
        this.jugadorInicialRonda = turnoActual;

        // Repartir cartas solo a jugadores activos (con fichas)
        repartirCartas();

        // Establecer estado
        this.estadoActual = EstadoJuego.ESPERANDO_ACCION;
        this.mensajeEstado = "Ronda iniciada. Esperando acción del " + jugadores.get(turnoActual).getNombre();

    }


    /**
     * Reparte las cartas iniciales a cada jugador (5 cartas en este caso)
     */
    @Override
    protected void repartirCartas() {
        // Aseguramos que el mazo está mezclado
        mazo.barajear();

        // Repartimos 5 cartas SOLO a jugadores activos (con fichas)
        for (Jugador jugador : jugadores) {
            if (jugador.estaActivo()) {
                ArrayList<Carta> cartasJugador = mazo.sacarNCartas(5);
                jugador.recibirCartas(cartasJugador);
            }
        }

        mensajeEstado += " Cartas repartidas a todos los jugadores activos.";
    }

    /**
     * Muestra la mano del jugador actual
     */
    @Override
    public void mostrarMano() {
    }

    /**
     * Muestra las manos de todos los jugadores
     */
    @Override
    public void mostrarManos() {
    }

    /**
     * Determina qué jugador comienza la ronda
     */
    @Override
    protected void determinarTurnoInicial() {
        // Encontrar un jugador aleatorio con fichas para comenzar
        boolean encontrado = false;
        int intentos = 0;
        int maxIntentos = jugadores.size();

        while (!encontrado && intentos < maxIntentos) {
            turnoActual = (int) (Math.random() * numeroDeJugadores);
            if (jugadores.get(turnoActual).estaActivo() && jugadores.get(turnoActual).getFichas() > 0) {
                encontrado = true;
            }
            intentos++;
        }

        if (!encontrado) {
            for (int i = 0; i < jugadores.size(); i++) {
                if (jugadores.get(i).estaActivo() && jugadores.get(i).getFichas() > 0) {
                    turnoActual = i;
                    break;
                }
            }
        }
    }


    /**
     * Evalúa las manos de los jugadores y determina el ganador
     */
    @Override
    public int determinarGanador() {
        int mejorValor = -1;
        int indiceGanador = -1;
        int cartaAltaGanador = -1;

        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            if ((jugador.estaActivo() || jugador.isAllIn()) && jugador.getMano() != null) {
                int valorMano = evaluarMano(jugador.getMano());
                int cartaAlta = jugador.getMano().obtenerCartaAlta();

                if (valorMano > mejorValor) {
                    mejorValor = valorMano;
                    indiceGanador = i;
                    cartaAltaGanador = cartaAlta;
                }
                else if (valorMano == mejorValor) {
                    // Empate en el tipo de mano
                    // Desempatar por carta alta
                    if (cartaAlta > cartaAltaGanador) {
                        indiceGanador = i;
                        cartaAltaGanador = cartaAlta;
                    }
                }
            }
        }

        return indiceGanador;
    }

    /**
     * Implementa la lógica de una ronda completa de poker de 5 cartas
     */
    @Override
    public void jugarRonda() {
        if (rondaTerminada) {
            return;
        }

        // Si quedan menos de 2 jugadores activos, finalizar la ronda
        if (contarJugadoresActivos() <= 1) {
            for (Jugador jugador : jugadores) {
                if (jugador.estaActivo()) {
                    finalizarRonda(jugador);
                    break;
                }
            }
            return;
        }

        // Marcar que el jugador actual ha tenido su turno
        jugadoresTurno[turnoActual] = true;
        boolean todosIgualados = true;
        int apuestaMaxima = 0;
        boolean todosHanTenidoTurno = true;
        for (Jugador jugador : jugadores) {
            if (jugador.estaActivo() && !jugador.isAllIn()) {
                int apuestaJugador = apuestasAcumuladas.getOrDefault(jugador, 0);
                apuestaMaxima = Math.max(apuestaMaxima, apuestaJugador);
            }
        }

        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            if (jugador.estaActivo() && !jugador.isAllIn()) {
                if (!jugadoresTurno[i]) {
                    todosHanTenidoTurno = false;
                }
                int apuestaJugador = apuestasAcumuladas.getOrDefault(jugador, 0);
                if (apuestaJugador < apuestaMaxima && jugadoresTurno[i]) {
                    todosIgualados = false;
                }
            }
        }

        // Solo avanzamos de etapa si:
        // 1. Todos han tenido su turno, Y
        // 2. Todos han igualado la apuesta máxima O no hay apuestas
        if (todosHanTenidoTurno && (todosIgualados || apuestaMaxima == 0)) {
            // Verificar que realmente todos los jugadores han completado su acción
            // Solo avanzar si hemos dado una vuelta completa desde el jugador inicial
            boolean completaronVuelta = false;
            if (hayanTenidoTodosTurno()) {
                completaronVuelta = true;
            }

            if (completaronVuelta) {
                avanzarEtapa();
            }
        }
    }
    /**
     * Avanza a la siguiente etapa del juego
     */
    private void avanzarEtapa() {
        switch (etapaActual) {
            case APUESTAS_INICIALES:
                etapaActual = EtapaJuego.DESCARTE;
                mensajeEstado = "Etapa de descarte. Cada jugador puede descartar hasta 3 cartas.";
                apuestaActual = 0;
                jugadoresQueHanDescartado = 0;
                rondaDeDescarteTerminada = false;
                resetearTurno();
                jugadorInicialRonda = turnoActual;
                for (int i = 0; i < jugadoresTurno.length; i++) {
                    jugadoresTurno[i] = false;
                }
                break;

            case DESCARTE:
                // Si todos han descartado, pasamos a la etapa de apuestas finales
                if (rondaDeDescarteTerminada || jugadoresQueHanDescartado >= contarJugadoresActivos()) {
                    etapaActual = EtapaJuego.APUESTAS_FINALES;
                    mensajeEstado = "Etapa de apuestas finales.";
                    apuestaActual = 0;
                    resetearTurno();
                    jugadorInicialRonda = turnoActual;
                    for (int i = 0; i < jugadoresTurno.length; i++) {
                        jugadoresTurno[i] = false;
                    }
                }
                break;

            case APUESTAS_FINALES:
                // Finalizar la ronda y mostrar cartas
                etapaActual = EtapaJuego.MOSTRAR_CARTAS;
                mensajeEstado = "Hora de mostrar las cartas.";


                if (hayJugadoresAllIn()) {
                    procesarResultadosFinales();
                } else {
                    int indiceGanador = determinarGanador();
                    if (indiceGanador >= 0) {
                        finalizarRonda(jugadores.get(indiceGanador));
                    }
                }

                rondaTerminada = true;
                estadoActual = EstadoJuego.RONDA_FINALIZADA;
                break;
            case MOSTRAR_CARTAS:
                break;
        }
    }

    /**
     * Resetea el turno al primer jugador activo
     */
    private void resetearTurno() {
        int inicioTurno = 0;
        turnoActual = inicioTurno;
        while (!jugadores.get(turnoActual).estaActivo() || jugadores.get(turnoActual).isAllIn()) {
            turnoActual = (turnoActual + 1) % numeroDeJugadores;


            if (turnoActual == inicioTurno) {
                break;
            }
        }
    }

    /**
     * Avanza el turno al siguiente jugador activo
     */
    @Override
    protected void avanzarTurno() {
        if (rondaTerminada || contarJugadoresActivos() <= 1) {
            return;
        }

        int turnoAnterior = turnoActual;

        do {
            turnoActual = (turnoActual + 1) % numeroDeJugadores;
        } while (!jugadores.get(turnoActual).estaActivo() ||
                jugadores.get(turnoActual).isAllIn() ||
                jugadores.get(turnoActual).getFichas() <= 0);

        // Si hemos vuelto al jugador inicial y todos han tenido su turno
        if (turnoActual == jugadorInicialRonda && hayanTenidoTodosTurno()) {

        }
    }

    /**
     * Método auxiliar para verificar si todos los jugadores activos han tenido su turno
     */
    private boolean hayanTenidoTodosTurno() {
        for (int i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).estaActivo() && !jugadores.get(i).isAllIn() && !jugadoresTurno[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Método para descartar cartas en la etapa de descarte
     */
    public boolean descartar(int[] posiciones) {
        if (etapaActual != EtapaJuego.DESCARTE) {
            mensajeEstado = "No estamos en la etapa de descarte.";
            return false;
        }

        Jugador jugadorActual = jugadores.get(turnoActual);

        if (!jugadorActual.estaActivo()) {
            mensajeEstado = "Este jugador no está activo en la ronda.";
            return false;
        }

        // Verificar si el jugador tiene un As (para permitir descartar 4 cartas)
        boolean tieneAs = false;
        for (Carta carta : jugadorActual.getMano().getMano()) {
            if (carta.getValor() == 14) {
                tieneAs = true;
                break;
            }
        }

        // Verificar el número máximo de cartas a descartar
        int maxDescartes = tieneAs ? 4 : 3;
        if (posiciones.length > maxDescartes) {
            mensajeEstado = "Solo puedes descartar hasta " + maxDescartes + " cartas.";
            return false;
        }

        // Ordenar posiciones de mayor a menor para no afectar índices al eliminar
        ArrayList<Integer> posicionesOrdenadas = new ArrayList<>();
        for (int pos : posiciones) {
            posicionesOrdenadas.add(pos);
        }
        posicionesOrdenadas.sort((a, b) -> b - a);

        // Eliminar las cartas seleccionadas
        ArrayList<Carta> manoActual = jugadorActual.getMano().getMano();

        for (int pos : posicionesOrdenadas) {
            if (pos >= 1 && pos <= manoActual.size()) {
                manoActual.remove(pos - 1); // Ajuste porque las posiciones empiezan en 1
            }
        }

        // Reponer las cartas descartadas
        int cartasAReponer = posicionesOrdenadas.size();
        for (int i = 0; i < cartasAReponer; i++) {
            ArrayList<Carta> nuevaCarta = mazo.sacarNCartas(1);
            if (!nuevaCarta.isEmpty()) {
                manoActual.add(nuevaCarta.get(0));
            }
        }

        mensajeEstado = jugadorActual.getNombre() + " descartó " + cartasAReponer + " cartas.";
        jugadoresQueHanDescartado++;

        // Verificar si todos los jugadores han descartado
        if (jugadoresQueHanDescartado >= contarJugadoresActivos()) {
            rondaDeDescarteTerminada = true;
        }

        // Avanzar al siguiente jugador
        avanzarTurno();

        // Verificar si debemos avanzar de etapa
        if (rondaDeDescarteTerminada) {
            avanzarEtapa();
        }

        return true;
    }

    /**
     * Método para no descartar cartas
     */
    public boolean noDescartar() {
        if (etapaActual != EtapaJuego.DESCARTE) {
            mensajeEstado = "No estamos en la etapa de descarte.";
            return false;
        }

        Jugador jugadorActual = jugadores.get(turnoActual);

        mensajeEstado = jugadorActual.getNombre() + " decide no descartar cartas.";
        jugadoresQueHanDescartado++;

        // Verificar si todos los jugadores han tomado su decisión de descarte
        if (jugadoresQueHanDescartado >= contarJugadoresActivos()) {
            rondaDeDescarteTerminada = true;
        }

        avanzarTurno();

        if (rondaDeDescarteTerminada) {
            avanzarEtapa();
        }

        return true;
    }

    /**
     * Obtiene información sobre la etapa actual del juego
     */
    public String getEtapaActual() {
        switch (etapaActual) {
            case APUESTAS_INICIALES:
                return "Apuestas iniciales";
            case DESCARTE:
                return "Descarte de cartas";
            case APUESTAS_FINALES:
                return "Apuestas finales";
            case MOSTRAR_CARTAS:
                return "Mostrar cartas";
            default:
                return "Desconocido";
        }
    }

    /**
     * Para obtener la etapa actual como enum (útil para comparaciones)
     */
    public EtapaJuego getEtapaActualEnum() {
        return etapaActual;
    }

    /**
     * Devuelve el nombre de la combinación según su valor
     */
    public String obtenerNombreMano(int valorMano) {
        switch (valorMano) {
            case 23: return "Escalera Real";
            case 22: return "Escalera de Color";
            case 21: return "Poker (Four of a Kind)";
            case 20: return "Full House";
            case 19: return "Color (Flush)";
            case 18: return "Escalera (Straight)";
            case 17: return "Tercia (Three of a Kind)";
            case 16: return "Dos Pares (Two Pair)";
            case 15: return "Un Par (One Pair)";
            default: return "Carta Alta: " + valorMano;
        }
    }

    /**
     * Override del método pasar para restricciones específicas
     */
    @Override
    public boolean pasar() {
        // No se puede pasar si hay apuestas pendientes
        if (apuestaActual > 0) {
            mensajeEstado = "No puedes pasar cuando hay una apuesta activa.";
            return false;
        }

        // En etapa de descarte no se puede pasar
        if (etapaActual == EtapaJuego.DESCARTE) {
            mensajeEstado = "En la etapa de descarte, debes elegir qué cartas descartar o no descartar ninguna.";
            return false;
        }

        mensajeEstado = jugadores.get(turnoActual).getNombre() + " pasa.";
        jugadoresTurno[turnoActual] = true;
        avanzarTurno();
        jugarRonda();
        return true;
    }

    /**
     * Override del método apostar para restricciones específicas
     */
    @Override
    public boolean apostar(int cantidad) {
        // En etapa de descarte no se puede apostar
        if (etapaActual == EtapaJuego.DESCARTE) {
            mensajeEstado = "No puedes apostar durante la etapa de descarte.";
            return false;
        }

        boolean resultado = super.apostar(cantidad);
        if (resultado) {
            jugadoresTurno[turnoActual] = true;
            jugarRonda();
        }
        return resultado;
    }

    /**
     * Override del método igualar para restricciones específicas
     */
    @Override
    public boolean igualar() {
        // En etapa de descarte no se puede igualar
        if (etapaActual == EtapaJuego.DESCARTE) {
            mensajeEstado = "No puedes igualar durante la etapa de descarte.";
            return false;
        }

        boolean resultado = super.igualar();
        if (resultado) {
            jugadoresTurno[turnoActual] = true;
            jugarRonda();
        }
        return resultado;
    }

    /**
     * Override del método subir para restricciones específicas
     */
    @Override
    public boolean subir(int incremento) {
        // En etapa de descarte no se puede subir
        if (etapaActual == EtapaJuego.DESCARTE) {
            mensajeEstado = "No puedes subir durante la etapa de descarte.";
            return false;
        }

        boolean resultado = super.subir(incremento);
        if (resultado) {
            jugadoresTurno[turnoActual] = true;
            jugarRonda();
        }
        return resultado;
    }
    /**
     * Override del método retirarse para restricciones específicas
     */
    @Override
    public boolean retirarse() {
        // En etapa de descarte, retirarse equivale a no descartar y luego retirarse
        if (etapaActual == EtapaJuego.DESCARTE) {
            jugadoresQueHanDescartado++;

            if (jugadoresQueHanDescartado >= contarJugadoresActivos()) {
                rondaDeDescarteTerminada = true;
            }
        }

        boolean resultado = super.retirarse();
        if (resultado) {
            jugadoresTurno[turnoActual] = true;
            jugarRonda();
        }
        return resultado;
    }
}