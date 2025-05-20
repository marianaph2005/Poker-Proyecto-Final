import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Clase abstracta para que cualquier tipo de póker la herede
public abstract class JuegoPoker {
    // Atributos
    protected int numeroDeJugadores;
    protected int dineroInicial;
    protected ArrayList<Jugador> jugadores;
    protected Mazo mazo;
    protected int turnoActual;

    protected int pozo;
    protected int apuestaActual;
    protected boolean rondaTerminada;
    protected String mensajeEstado;
    protected Map<Jugador, Integer> apuestasAcumuladas;

    // Enumeración para los posibles estados del juego
    public enum EstadoJuego {
        ESPERANDO_INICIO,
        REPARTIENDO_CARTAS,
        ESPERANDO_ACCION,
        RONDA_FINALIZADA,
        JUEGO_TERMINADO
    }

    protected EstadoJuego estadoActual;


    //Constructor base que recibe cantidad de jugadores y las fichas con
    //las que inciarán el juego
    public JuegoPoker(int numeroDeJugadores, int dineroInicial) {
        this.numeroDeJugadores = numeroDeJugadores;
        this.dineroInicial = dineroInicial;
        this.jugadores = new ArrayList<>();
        this.mazo = new Mazo();
        this.pozo = 0;
        this.apuestaActual = 0;
        this.rondaTerminada = false;
        this.estadoActual = EstadoJuego.ESPERANDO_INICIO;
        this.mensajeEstado = "Juego inicializado.";
        this.apuestasAcumuladas = new HashMap<>();

        // Inicializar jugadores con sus nombres y dinero inicial
        for (int i = 0; i < numeroDeJugadores; i++) {
            jugadores.add(new Jugador("Jugador " + (i + 1), dineroInicial));
        }
    }


    //Reparte las cartas iniciales según las reglas de cada variante
    protected abstract void repartirCartas();


    //Evalúa las manos de los jugadores y determina el ganador
    public abstract int determinarGanador();


    //Muestra las cartas de un jugador según cada variante
    public abstract void mostrarMano();


    //Implementa la lógica de una ronda según la variante de póker
    public abstract void jugarRonda();

    //Determina qué jugador comienza la ronda
    protected abstract void determinarTurnoInicial();


    //Inicializa el juego con el número de jugadores
    public abstract void iniciarJuego(int numJugadores);


    //Muestra las manos de todos los jugadores
    public void mostrarManos() {
        // Este método se implementa por las clases hijas
    }


    //Permite a un jugador pasar su turno sin realizar apuesta
    public boolean pasar() {
        if (apuestaActual > 0) {
            mensajeEstado = "No puedes pasar cuando hay una apuesta activa.";
            return false;
        }

        mensajeEstado = jugadores.get(turnoActual).getNombre() + " pasa.";
        avanzarTurno();
        return true;
    }

    //Permite al jugador actual realizar una apuesta
    //Recibe como párametro la cantidad de fichas a apostar
    public boolean apostar(int cantidad) {
        return realizarApuesta(cantidad, true);
    }


    //Permite al jugador actual igualar la apuesta existente
    public boolean igualar() {
        if (apuestaActual <= 0) {
            mensajeEstado = "No hay apuesta que igualar.";
            return false;
        }

        return realizarApuesta(apuestaActual, false);
    }

    //Permite al jugador actual subir la apuesta existente
    //Recibe como parametro la cantidad adicional a la apuesta actual
    public boolean subir(int incremento) {
        if (incremento <= 0) {
            mensajeEstado = "El incremento debe ser mayor que cero.";
            return false;
        }

        return realizarApuesta(apuestaActual + incremento, true);
    }


    //Permite al jugador actual retirarse de la ronda
    public boolean retirarse() {
        Jugador jugadorActual = jugadores.get(turnoActual);
        mensajeEstado = jugadorActual.getNombre() + " se retira.";
        jugadorActual.abandonar();

        // Verificar si solo queda un jugador activo
        if (contarJugadoresActivos() == 1) {
            // Solo queda un jugador, terminar la ronda
            for (Jugador jugador : jugadores) {
                if (jugador.estaActivo()) {
                    mensajeEstado += " Todos los demás jugadores se han retirado.";
                    finalizarRonda(jugador);
                    rondaTerminada = true;
                    estadoActual = EstadoJuego.RONDA_FINALIZADA;
                    return true;
                }
            }
        }

        avanzarTurno();
        return true;
    }

    //Clase para encapsular info del estado del juego para la interfaz
    public class EstadoJuegoInfo {
        public int pozo;
        public int apuestaActual;
        public int turnoActualIndice;
        public String turnoActualNombre;
        public EstadoJuego estadoActual;
        public String mensajeEstado;
        public ArrayList<Jugador.Info> infoJugadores;
    }

    // Devuelve toda la info necesaria para mostrar el estado del juego
    public EstadoJuegoInfo obtenerEstadoJuego() {
        EstadoJuegoInfo estado = new EstadoJuegoInfo();
        estado.pozo = this.pozo;
        estado.apuestaActual = this.apuestaActual;
        estado.turnoActualIndice = this.turnoActual;
        estado.turnoActualNombre = jugadores.get(turnoActual).getNombre();
        estado.estadoActual = this.estadoActual;
        estado.mensajeEstado = this.mensajeEstado;

        estado.infoJugadores = new ArrayList<>();
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            estado.infoJugadores.add(jugador.obtenerInformacion(i == turnoActual));
        }

        return estado;
    }

    //Cambia los nombres de los jugadores (opcional)
    public void establecerNombresJugadores(String[] nombres) {
        for (int i = 0; i < nombres.length && i < jugadores.size(); i++) {
            jugadores.get(i).setNombre(nombres[i]);
        }
    }

    // Getters simples para acceder a atributos importantes
    public String getMensajeEstado() {
        return mensajeEstado;
    }

    public EstadoJuego getEstadoActual() {
        return estadoActual;
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    public int getPozo() {
        return pozo;
    }

    public int getApuestaActual() {
        return apuestaActual;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }


    // Método privado para procesar cualquier tipo de apuesta
    // incluyendo all-in
    private boolean realizarApuesta(int cantidadRequerida, boolean esApuestaInicial) {
        if (cantidadRequerida <= 0) {
            mensajeEstado = "La cantidad debe ser mayor que cero.";
            return false;
        }

        Jugador jugadorActual = jugadores.get(turnoActual);

        // Si el jugador no tiene suficiente, va all-in con lo que tenga
        int cantidadFinal = Math.min(cantidadRequerida, jugadorActual.getFichas());

        // Realizar la apuesta
        //Se quitan fichas al jugador y se suman al pozo
        jugadorActual.restarFichas(cantidadFinal);
        pozo += cantidadFinal;

        // Rastrear la apuesta acumulada de este jugador
        int apuestaAnterior = apuestasAcumuladas.getOrDefault(jugadorActual, 0);
        apuestasAcumuladas.put(jugadorActual, apuestaAnterior + cantidadFinal);

        // Actualizar la apuesta actual si es mayor y es una apuesta inicial
        if (esApuestaInicial && cantidadFinal > apuestaActual) {
            apuestaActual = cantidadFinal;
        }

        // Determinar mensaje según el tipo de apuesta
        if (cantidadFinal < cantidadRequerida) {
            // Caso all-in
            jugadorActual.setAllIn(true);
            mensajeEstado = jugadorActual.getNombre() + " va ALL-IN con " + cantidadFinal + " fichas!";
        } else if (esApuestaInicial) {
            // Apuesta nueva
            mensajeEstado = jugadorActual.getNombre() + " apuesta " + cantidadFinal + " fichas.";
        } else {
            // Iguala apuesta
            mensajeEstado = jugadorActual.getNombre() + " iguala la apuesta de " + apuestaActual + " fichas.";
        }

        avanzarTurno();
        return true;
    }

    // Termina la ronda y entrega el pozo al ganador
    public void finalizarRonda(Jugador ganador) {
        if (ganador != null) {
            mensajeEstado = ganador.getNombre() + " ha ganado la ronda! Gana " + pozo + " fichas.";
            ganador.agregarFichas(pozo);
            pozo = 0;
        }

        rondaTerminada = true;
        estadoActual = EstadoJuego.RONDA_FINALIZADA;

        // Reiniciar las apuestas para la siguiente ronda
        apuestasAcumuladas.clear();
    }

    // Avanza el turno al siguiente jugador activo
    protected void avanzarTurno() {
        if (rondaTerminada || contarJugadoresActivos() <= 1) {
            return;
        }

        do {
            turnoActual = (turnoActual + 1) % numeroDeJugadores;
        } while (!jugadores.get(turnoActual).estaActivo() || jugadores.get(turnoActual).isAllIn());
    }

    // Cuenta cuántos jugadores siguen activos (no retirados)
    protected int contarJugadoresActivos() {
        int contador = 0;
        for (Jugador jugador : jugadores) {
            if (jugador.estaActivo() && !jugador.isAllIn()) {
                contador++;
            }
        }
        return contador;
    }

    //Determina si hay algún jugador en estado all-in
    protected boolean hayJugadoresAllIn() {
        for (Jugador jugador : jugadores) {
            if (jugador.isAllIn()) {
                return true;
            }
        }
        return false;
    }

    //Crea los pozos laterales cuando hay jugadores all-in
    protected ArrayList<PozoLateral> crearPozosLaterales() {
        ArrayList<PozoLateral> pozosLaterales = new ArrayList<>();

        //Obtener jugadores participantes
        ArrayList<Jugador> jugadoresParticipantes = new ArrayList<>();
        for (Jugador jugador : jugadores) {
            if (jugador.estaActivo() || jugador.isAllIn()) {
                jugadoresParticipantes.add(jugador);
            }
        }

        //Ordenar por cantidad apostada (menor a mayor)
        jugadoresParticipantes.sort((j1, j2) ->
                apuestasAcumuladas.getOrDefault(j1, 0) -
                        apuestasAcumuladas.getOrDefault(j2, 0));

        // Crear pozos laterales para cada nivel de apuesta
        int apuestaAnterior = 0;

        for (Jugador jugador : jugadoresParticipantes) {
            if (jugador.isAllIn()) {
                int apuestaJugador = apuestasAcumuladas.getOrDefault(jugador, 0);

                if (apuestaJugador > apuestaAnterior) {
                    int diferencia = apuestaJugador - apuestaAnterior;
                    int tamañoPozo = 0;
                    ArrayList<Jugador> elegibles = new ArrayList<>();
                    //Calcula la contribución de cada jugador al pozo lateral
                    for (Jugador j : jugadoresParticipantes) {
                        int apuestaTotal = apuestasAcumuladas.getOrDefault(j, 0);
                        if (apuestaTotal >= apuestaJugador) {
                            tamañoPozo += diferencia;
                            elegibles.add(j);
                        } else if (apuestaTotal > apuestaAnterior) {
                            tamañoPozo += (apuestaTotal - apuestaAnterior);
                        }
                    }

                    //Crea el pozo lateral si hay más de un jugador elegible
                    if (elegibles.size() > 1) {
                        PozoLateral pozoLateral = new PozoLateral(tamañoPozo);
                        for (Jugador j : elegibles) {
                            pozoLateral.agregarJugador(j);
                        }
                        pozosLaterales.add(pozoLateral);
                    } else if (elegibles.size() == 1) {
                        //Devuelve fichas no igualadas si solo hay un jugador elegible
                        Jugador unicoElegible = elegibles.get(0);
                        unicoElegible.agregarFichas(tamañoPozo);
                        mensajeEstado += " " + unicoElegible.getNombre() +
                                " recupera " + tamañoPozo + " fichas no igualadas.";
                    }

                    apuestaAnterior = apuestaJugador;
                }
            }
        }

        // Calcula el pozo principal descontando los pozos laterales
        int pozoPrincipal = pozo;
        for (PozoLateral pl : pozosLaterales) {
            pozoPrincipal -= pl.getCantidad();
        }
        //Crea el pozo principal con los jugadores activos restantes
        if (pozoPrincipal > 0) {
            ArrayList<Jugador> elegiblesPrincipal = new ArrayList<>();
            for (Jugador j : jugadoresParticipantes) {
                if (j.estaActivo()) {
                    elegiblesPrincipal.add(j);
                }
            }

            if (elegiblesPrincipal.size() > 0) {
                PozoLateral pozoFinal = new PozoLateral(pozoPrincipal);
                for (Jugador j : elegiblesPrincipal) {
                    pozoFinal.agregarJugador(j);
                }
                pozosLaterales.add(pozoFinal);
            }
        }

        return pozosLaterales;
    }

    //Procesa los resultados finales considerando los pozos laterales
    protected void procesarResultadosFinales() {
        // Si no hay jugadores all-in, usar el método normal
        if (!hayJugadoresAllIn()) {
            int indiceGanador = determinarGanador();
            if (indiceGanador >= 0 && indiceGanador < jugadores.size()) {
                finalizarRonda(jugadores.get(indiceGanador));
            }
            return;
        }

        // Crear pozos laterales
        ArrayList<PozoLateral> pozosLaterales = crearPozosLaterales();
        StringBuilder resultadoFinal = new StringBuilder("Resultados finales: ");

        // Evalúa cada pozo lateral para determinar ganadores y repartir fichas
        for (PozoLateral pozo : pozosLaterales) {
            // Si solo hay un jugador elegible, ya se le devolvieron sus fichas
            if (pozo.getJugadoresElegibles().size() <= 1) {
                continue;
            }

            //Busca la mejor mano entre los jugadores elegibles
            int mejorValor = -1;
            ArrayList<Jugador> ganadores = new ArrayList<>();

            for (Jugador jugador : pozo.getJugadoresElegibles()) {
                if (jugador.getMano() != null) {
                    int valorMano = evaluarMano(jugador.getMano());

                    if (valorMano > mejorValor) {
                        //Nueva mejor mano
                        mejorValor = valorMano;
                        ganadores.clear();
                        ganadores.add(jugador);
                    } else if (valorMano == mejorValor) {
                        //Empate
                        ganadores.add(jugador);
                    }
                }
            }

            //Reparte el pozo entre los ganadores y actualiza el resultado
            if (!ganadores.isEmpty()) {
                int cantidadPorJugador = pozo.getCantidad() / ganadores.size();
                int resto = pozo.getCantidad() % ganadores.size();

                for (Jugador ganador : ganadores) {
                    int ganancia = cantidadPorJugador;
                    if (resto > 0) {
                        ganancia++;
                        resto--;
                    }

                    ganador.agregarFichas(ganancia);
                    resultadoFinal.append(ganador.getNombre())
                            .append(" gana ")
                            .append(ganancia)
                            .append(" fichas");

                    if (ganadores.size() > 1) {
                        resultadoFinal.append(" (empate). ");
                    } else {
                        resultadoFinal.append(". ");
                    }
                }
            }
        }
        //Actualiza estado final y resetea el pozo
        mensajeEstado = resultadoFinal.toString();
        pozo = 0; // El pozo ya ha sido distribuido
        rondaTerminada = true;
        estadoActual = EstadoJuego.RONDA_FINALIZADA;
    }

    //Evalúa una mano de póker para determinar su valor
    //Si la evaluación es común puede usarse este método base
    protected int evaluarMano(Mano mano) {
        if (mano.esEscaleraReal()) {
            return 23;
        } else if (mano.esEscaleraColor()) {
            return 22;
        } else if (mano.esPoker()) {
            return 21;
        } else if (mano.hayFullHouse()) {
            return 20;
        } else if (mano.sonDelMismoPalo()) {
            return 19;
        } else if (mano.esEscalera()) {
            return 18;
        } else if (mano.hayTercia()) {
            return 17;
        } else if (mano.hayDosPares()) {
            return 16;
        } else if (mano.hayUnPar()) {
            return 15;
        } else {
            int valor = mano.obtenerCartaAlta();
            return valor;
        }
    }

    //Clase interna para representar un pozo lateral en situaciones de all-in
    protected class PozoLateral {
        private int cantidad;
        private ArrayList<Jugador> jugadoresElegibles;

        public PozoLateral(int cantidad) {
            this.cantidad = cantidad;
            this.jugadoresElegibles = new ArrayList<>();
        }

        //Agrega un jugador a la lista de elegibles si no está ya incluido
        public void agregarJugador(Jugador jugador) {
            if (!jugadoresElegibles.contains(jugador)) {
                jugadoresElegibles.add(jugador);
            }
        }

        public int getCantidad() {
            return cantidad;
        }

        public ArrayList<Jugador> getJugadoresElegibles() {
            return jugadoresElegibles;
        }
    }
}