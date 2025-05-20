import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementación de Texas Hold'em
 */
public class TexasHoldem extends JuegoPoker{
    // Cartas comunitarias
    private ArrayList<Carta> cartasComunitarias;

    // Posiciones
    private int posicionDealer;
    private int posicionSmallBlind;
    private int posicionBigBlind;

    // Valores de las ciegas
    private int smallBlindValue;
    private int bigBlindValue;

    // Etapas del juego
    public enum Etapa {
        CIEGAS,      // Pago de ciegas
        PRE_FLOP,    // Reparto de cartas privadas y primera ronda de apuestas
        FLOP,        // Tres primeras cartas comunitarias y apuestas
        TURN,        // Cuarta carta comunitaria y apuestas
        RIVER,       // Quinta carta comunitaria y apuestas
        SHOWDOWN     // Mostrar cartas y determinar ganador
    }

    private Etapa etapaActual;

    // Indica si la ronda de apuestas ha sido completada
    private boolean rondaApuestasCompleta;

    // Último jugador que subió la apuesta
    private int ultimoJugadorQueSubio;

    // Indica si algún jugador ha realizado una acción en esta ronda
    private boolean accionRealizadaEnRonda;

    //Constructor que recibe número de jugadores y dinero inicial
    public TexasHoldem(int numeroDeJugadores, int dineroInicial) {
        super(numeroDeJugadores, dineroInicial);
        this.cartasComunitarias = new ArrayList<>();
        this.posicionDealer = 0;
        this.smallBlindValue = 5; // Valor predeterminado
        this.bigBlindValue = 10;  // Valor predeterminado
        this.etapaActual = Etapa.CIEGAS;
        this.rondaApuestasCompleta = false;
        this.ultimoJugadorQueSubio = -1;
        this.accionRealizadaEnRonda = false;
    }

    //Constructor con valores específicos para ciegas
    public TexasHoldem(int numeroDeJugadores, int dineroInicial, int smallBlind, int bigBlind) {
        this(numeroDeJugadores, dineroInicial);
        this.smallBlindValue = smallBlind;
        this.bigBlindValue = bigBlind;
    }

    @Override
    protected void repartirCartas() {
        // Barajar el mazo
        mazo = new Mazo();
        mazo.barajear();

        // Limpiar estado
        cartasComunitarias.clear();
        apuestasAcumuladas.clear();
        rondaApuestasCompleta = false;
        accionRealizadaEnRonda = false;

        // Limpiar estado de los jugadores
        for (Jugador jugador : jugadores) {
            jugador.limpiarMano();
            if (jugador.getFichas() > 0) {
                jugador.activar();
            }
        }

        // Establecer posiciones
        establecerPosiciones();

        // Repartir 2 cartas a cada jugador
        for (Jugador jugador : jugadores) {
            if (jugador.estaActivo()) {
                ArrayList<Carta> cartasJugador = mazo.sacarNCartas(2);
                jugador.recibirCartas(cartasJugador);
            }
        }

        // Establecer ciegas
        establecerCiegas();

        // Pasar a la etapa de Pre-Flop
        etapaActual = Etapa.PRE_FLOP;
        estadoActual = EstadoJuego.ESPERANDO_ACCION;
        mensajeEstado = "Cartas repartidas. Comienza la apuesta inicial (Pre-Flop).";

        // Establecer el turno inicial (jugador después del big blind)
        turnoActual = (posicionBigBlind + 1) % numeroDeJugadores;

        // Verificar si el jugador actual está activo
        while (!jugadores.get(turnoActual).estaActivo() || jugadores.get(turnoActual).isAllIn()) {
            turnoActual = (turnoActual + 1) % numeroDeJugadores;
            // Si dimos la vuelta completa sin encontrar jugador activo
            if (turnoActual == (posicionBigBlind + 1) % numeroDeJugadores) {
                finalizarRondaApuestas();
                break;
            }
        }
    }

    //Establece las posiciones de dealer, small blind y big blind
    private void establecerPosiciones() {
        int jugadoresActivos = contarJugadoresConFichas();

        // Si hay menos de 2 jugadores activos con fichas no se puede jugar
        if (jugadoresActivos < 2) {
            mensajeEstado = "No hay suficientes jugadores con fichas para jugar.";
            rondaTerminada = true;
            return;
        }

        // El dealer avanza 1 posición cada ronda
        posicionDealer = (posicionDealer + 1) % numeroDeJugadores;

        // Asegurarse que las posiciones correspondan a jugadores activos con fichas
        while (!jugadores.get(posicionDealer).estaActivo() || jugadores.get(posicionDealer).getFichas() <= 0) {
            posicionDealer = (posicionDealer + 1) % numeroDeJugadores;
        }

        // Establecer small blind
        posicionSmallBlind = (posicionDealer + 1) % numeroDeJugadores;
        while (!jugadores.get(posicionSmallBlind).estaActivo() || jugadores.get(posicionSmallBlind).getFichas() <= 0) {
            posicionSmallBlind = (posicionSmallBlind + 1) % numeroDeJugadores;
        }

        // Establecer big blind
        posicionBigBlind = (posicionSmallBlind + 1) % numeroDeJugadores;
        while (!jugadores.get(posicionBigBlind).estaActivo() || jugadores.get(posicionBigBlind).getFichas() <= 0
                || posicionBigBlind == posicionSmallBlind) {
            posicionBigBlind = (posicionBigBlind + 1) % numeroDeJugadores;
        }

        // En caso de que solo haya 2 jugadores, el dealer es el small blind
        if (jugadoresActivos == 2) {
            posicionSmallBlind = posicionDealer;
            posicionBigBlind = (posicionDealer + 1) % numeroDeJugadores;
            while (!jugadores.get(posicionBigBlind).estaActivo() || jugadores.get(posicionBigBlind).getFichas() <= 0) {
                posicionBigBlind = (posicionBigBlind + 1) % numeroDeJugadores;
            }
        }
    }

    //Cuenta el número de jugadores activos con fichas
    private int contarJugadoresConFichas() {
        int contador = 0;
        for (Jugador jugador : jugadores) {
            if (jugador.estaActivo() && jugador.getFichas() > 0) {
                contador++;
            }
        }
        return contador;
    }

    //Establece las apuestas forzadas
    private void establecerCiegas() {
        // Small Blind
        Jugador jugadorSmallBlind = jugadores.get(posicionSmallBlind);
        int smallBlindActual = Math.min(smallBlindValue, jugadorSmallBlind.getFichas());
        jugadorSmallBlind.restarFichas(smallBlindActual);
        apuestasAcumuladas.put(jugadorSmallBlind, smallBlindActual);

        // Big Blind
        Jugador jugadorBigBlind = jugadores.get(posicionBigBlind);
        int bigBlindActual = Math.min(bigBlindValue, jugadorBigBlind.getFichas());
        jugadorBigBlind.restarFichas(bigBlindActual);
        apuestasAcumuladas.put(jugadorBigBlind, bigBlindActual);

        // Actualizar pozo y apuesta actual
        pozo = smallBlindActual + bigBlindActual;
        apuestaActual = bigBlindActual;

        // Verificar si algún jugador quedó all-in con las ciegas
        if (smallBlindActual == jugadorSmallBlind.getFichas()) {
            jugadorSmallBlind.setAllIn(true);
        }
        if (bigBlindActual == jugadorBigBlind.getFichas()) {
            jugadorBigBlind.setAllIn(true);
        }

        // Definir el último jugador que "subió" como el big blind
        ultimoJugadorQueSubio = posicionBigBlind;

        mensajeEstado = jugadorSmallBlind.getNombre() + " pone small blind de $" + smallBlindActual +
                " y " + jugadorBigBlind.getNombre() + " pone big blind de $" + bigBlindActual;
    }

    //Avanza el juego a la siguiente etapa
    public void avanzarEtapa() {
        // Verificar que todos hayan igualado o se hayan retirado
        if (!rondaApuestasCompleta && contarJugadoresActivos() > 1) {
            mensajeEstado = "No se puede avanzar hasta que todos los jugadores hayan igualado la apuesta más alta.";
            return;
        }

        // Determinar la siguiente etapa
        switch (etapaActual) {
            case PRE_FLOP:
                // Quemar una carta y repartir el flop (3 cartas)
                mazo.sacarNCartas(1); // Quemar carta
                cartasComunitarias.addAll(mazo.sacarNCartas(3));
                etapaActual = Etapa.FLOP;
                mensajeEstado = "Flop repartido: " + mostrarCartasComunitarias();
                iniciarNuevaRondaApuestas();
                break;

            case FLOP:
                // Quemar una carta y repartir el turn (4ta carta)
                mazo.sacarNCartas(1); // Quemar carta
                cartasComunitarias.add(mazo.sacarNCartas(1).get(0));
                etapaActual = Etapa.TURN;
                mensajeEstado = "Turn repartido: " + mostrarCartasComunitarias();
                iniciarNuevaRondaApuestas();
                break;

            case TURN:
                // Quemar una carta y repartir el river (5ta carta)
                mazo.sacarNCartas(1); // Quemar carta
                cartasComunitarias.add(mazo.sacarNCartas(1).get(0));
                etapaActual = Etapa.RIVER;
                mensajeEstado = "River repartido: " + mostrarCartasComunitarias();
                iniciarNuevaRondaApuestas();
                break;

            case RIVER:
                // Pasar al showdown - mostrar cartas y determinar ganador
                etapaActual = Etapa.SHOWDOWN;
                mensajeEstado = "Showdown! Se muestran las cartas.";
                procesarResultadosFinales();
                break;

            case SHOWDOWN:
                // Iniciar nueva ronda
                iniciarNuevaRonda();
                break;

            default:
                break;
        }
    }

    //Inicia una nueva ronda de apuestas después de avanzar de etapa
    private void iniciarNuevaRondaApuestas() {
        // Resetear la apuesta actual
        apuestaActual = 0;
        rondaApuestasCompleta = false;
        accionRealizadaEnRonda = false;
        ultimoJugadorQueSubio = -1;

        // Después del flop, comienza el primer jugador activo a la izquierda del dealer
        turnoActual = (posicionDealer + 1) % numeroDeJugadores;

        // Verificar si el jugador está activo
        while (!jugadores.get(turnoActual).estaActivo() || jugadores.get(turnoActual).isAllIn()) {
            turnoActual = (turnoActual + 1) % numeroDeJugadores;
            // Si dimos la vuelta completa sin encontrar jugador activo
            if (turnoActual == (posicionDealer + 1) % numeroDeJugadores) {
                finalizarRondaApuestas();
                avanzarEtapa(); // Avanzar automáticamente si no hay jugadores activos
                return;
            }
        }
    }

    //String con cartas comunitarias
    private String mostrarCartasComunitarias() {
        StringBuilder sb = new StringBuilder();
        for (Carta carta : cartasComunitarias) {
            sb.append(carta.toString()).append(" ");
        }
        return sb.toString();
    }

    //Determina si el jugador actual es el último que debe actuar
    private boolean esUltimoEnActuar() {
        // Si no ha habido acción en esta ronda, el último es el big blind
        if (!accionRealizadaEnRonda && etapaActual == Etapa.PRE_FLOP) {
            return turnoActual == posicionBigBlind;
        }

        // Si nadie ha subido, el último jugador es el que está antes del dealer
        if (ultimoJugadorQueSubio == -1) {
            // En rondas posteriores al preflop, el último jugador será el dealer
            if (etapaActual != Etapa.PRE_FLOP) {
                return turnoActual == posicionDealer;
            }

            // En preflop, si nadie ha subido, el último jugador es el big blind
            return turnoActual == posicionBigBlind;
        }

        // Si alguien subió, el último jugador es el último jugador activo antes del que subió
        return turnoActual == obtenerJugadorAnteriorActivo(ultimoJugadorQueSubio);
    }

    //Obtiene el jugador activo anterior a una posición dada
    private int obtenerJugadorAnteriorActivo(int posicion) {
        int jugadorAnterior = posicion;

        do {
            jugadorAnterior = (jugadorAnterior - 1 + numeroDeJugadores) % numeroDeJugadores;

            // Si encontramos un jugador activo que no está all-in
            if (jugadores.get(jugadorAnterior).estaActivo() && !jugadores.get(jugadorAnterior).isAllIn()) {
                return jugadorAnterior;
            }

            // Si dimos la vuelta completa, significa que no hay más jugadores activos
            if (jugadorAnterior == posicion) {
                break;
            }
        } while (true);

        // Si no encontramos ningún jugador activo, devolvemos la posición original
        return posicion;
    }

    //Iniciar una nueva ronda de juego
    private void iniciarNuevaRonda() {
        // Resetear el estado del juego
        for (Jugador jugador : jugadores) {
            jugador.limpiarMano();
            // Solo activar jugadores que tengan fichas
            if (jugador.getFichas() > 0) {
                jugador.activar();
            }
        }

        cartasComunitarias.clear();
        pozo = 0;
        apuestaActual = 0;
        rondaTerminada = false;
        repartirCartas();
    }

    @Override
    public int determinarGanador() {
        int mejorIndice = -1;
        int mejorValor = -1;

        // Si solo hay un jugador activo, es el ganador
        int jugadoresElegibles = 0;
        for (int i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).estaActivo() || jugadores.get(i).isAllIn()) {
                jugadoresElegibles++;
                mejorIndice = i;
            }
        }

        if (jugadoresElegibles == 1) {
            return mejorIndice;
        }

        // Evaluamos la mejor mano para cada jugador
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            if (jugador.estaActivo() || jugador.isAllIn()) {
                // Combinamos las cartas del jugador con las comunitarias
                ArrayList<Carta> todasLasCartas = new ArrayList<>(jugador.getMano().getMano());
                todasLasCartas.addAll(cartasComunitarias);

                // Encontrar la mejor mano posible
                Mano mejorMano = encontrarMejorMano(todasLasCartas);
                jugador.setMano(mejorMano);

                int valorMano = evaluarMano(mejorMano);

                if (valorMano > mejorValor) {
                    mejorValor = valorMano;
                    mejorIndice = i;
                } else if (valorMano == mejorValor && mejorIndice != -1) {
                    // Desempatar por cartas altas
                    Mano manoActual = jugador.getMano();
                    Mano mejorManoActual = jugadores.get(mejorIndice).getMano();

                    if (desempatar(manoActual, mejorManoActual) > 0) {
                        mejorIndice = i;
                    }
                }
            }
        }

        return mejorIndice;
    }

    /**
     * Encuentra la mejor mano de 5 cartas entre las 7 disponibles
     */
    public Mano encontrarMejorMano(ArrayList<Carta> cartas) {
        // Si no hay suficientes cartas
        if (cartas.size() < 5) {
            return new Mano(new ArrayList<>(cartas));
        }

        // Si hay exactamente 5 cartas
        if (cartas.size() == 5) {
            return new Mano(new ArrayList<>(cartas));
        }

        // Generar todas las combinaciones posibles de 5 cartas
        List<List<Carta>> combinaciones = generarCombinaciones(cartas, 5);

        // Evaluar cada combinación
        Mano mejorMano = null;
        int mejorValor = -1;

        for (List<Carta> combo : combinaciones) {
            Mano manoActual = new Mano(new ArrayList<>(combo));
            int valorActual = evaluarMano(manoActual);

            if (valorActual > mejorValor) {
                mejorValor = valorActual;
                mejorMano = manoActual;
            } else if (valorActual == mejorValor && mejorMano != null) {
                // Desempatar por cartas altas
                if (desempatar(manoActual, mejorMano) > 0) {
                    mejorMano = manoActual;
                }
            }
        }

        return mejorMano;
    }

    //Genera todas las combinaciones posibles de r elementos de la lista
    private List<List<Carta>> generarCombinaciones(List<Carta> lista, int r) {
        List<List<Carta>> combinaciones = new ArrayList<>();
        generarCombinacionesRecursivo(lista, r, 0, new ArrayList<>(), combinaciones);
        return combinaciones;
    }

    //Método recursivo para generar combinaciones
    private void generarCombinacionesRecursivo(List<Carta> lista, int r, int inicio,
                                               List<Carta> combinacionActual,
                                               List<List<Carta>> resultado) {
        // Si la combinación actual tiene el tamaño deseado
        if (combinacionActual.size() == r) {
            resultado.add(new ArrayList<>(combinacionActual));
            return;
        }

        // Recorrer los elementos disponibles
        for (int i = inicio; i < lista.size(); i++) {
            combinacionActual.add(lista.get(i));

            generarCombinacionesRecursivo(lista, r, i + 1, combinacionActual, resultado);

            // Eliminar el último elemento para probar con el siguiente
            combinacionActual.remove(combinacionActual.size() - 1);
        }
    }

    //Compara dos manos para desempate
    private int desempatar(Mano mano1, Mano mano2) {
        ArrayList<Carta> cartas1 = mano1.getMano();
        ArrayList<Carta> cartas2 = mano2.getMano();

        // Ordenar por valor descendente
        cartas1.sort(Comparator.comparing(Carta::getValor).reversed());
        cartas2.sort(Comparator.comparing(Carta::getValor).reversed());

        // Comparar cada carta
        for (int i = 0; i < Math.min(cartas1.size(), cartas2.size()); i++) {
            int comp = Integer.compare(cartas1.get(i).getValor(), cartas2.get(i).getValor());
            if (comp != 0) {
                return comp;
            }
        }

        return 0;
    }

    @Override
    public void mostrarMano() {
        // Mostrar cartas comunitarias
        System.out.println("Cartas comunitarias:");
        for (Carta carta : cartasComunitarias) {
        }

        // Mostrar cartas de cada jugador
        for (Jugador jugador : jugadores) {
            if (jugador.estaActivo() || jugador.isAllIn()) {
                System.out.println("Jugador: " + jugador.getNombre());
                jugador.mostrarMano();
            }
        }
    }

    @Override
    public void jugarRonda() {
        // Iniciar con repartir cartas
        repartirCartas();

        mensajeEstado = "Ronda iniciada. Ciegas establecidas. " +
                "Turno del jugador " + jugadores.get(turnoActual).getNombre();
    }

    @Override
    protected void determinarTurnoInicial() {
        // En Pre-Flop: jugador después del big blind
        if (etapaActual == Etapa.PRE_FLOP) {
            turnoActual = (posicionBigBlind + 1) % numeroDeJugadores;
        }
        // En otras etapas: jugador después del dealer
        else {
            turnoActual = (posicionDealer + 1) % numeroDeJugadores;
        }

        // Verificar si el jugador está activo
        while (!jugadores.get(turnoActual).estaActivo() || jugadores.get(turnoActual).isAllIn()) {
            turnoActual = (turnoActual + 1) % numeroDeJugadores;
            // Si dimos la vuelta completa
            if ((etapaActual == Etapa.PRE_FLOP && turnoActual == (posicionBigBlind + 1) % numeroDeJugadores) ||
                    (etapaActual != Etapa.PRE_FLOP && turnoActual == (posicionDealer + 1) % numeroDeJugadores)) {
                // No hay jugadores activos
                finalizarRondaApuestas();
                break;
            }
        }
    }

    @Override
    public void iniciarJuego(int numJugadores) {
        // Reiniciar el estado del juego
        this.numeroDeJugadores = numJugadores;
        jugadores.clear();

        // Inicializar jugadores
        for (int i = 0; i < numJugadores; i++) {
            jugadores.add(new Jugador("Jugador " + (i + 1), dineroInicial));
        }
        pozo = 0;
        apuestaActual = 0;
        rondaTerminada = false;
        posicionDealer = 0;
        cartasComunitarias.clear();
        etapaActual = Etapa.CIEGAS;
        estadoActual = EstadoJuego.ESPERANDO_INICIO;
        mensajeEstado = "Juego inicializado con " + numJugadores + " jugadores.";
    }

    @Override
    public boolean pasar() {
        if (apuestaActual > 0) {
            mensajeEstado = "No puedes pasar cuando hay una apuesta activa. Debes igualar, subir o retirarte.";
            return false;
        }
        Jugador jugadorActual = jugadores.get(turnoActual);
        mensajeEstado = jugadorActual.getNombre() + " pasa.";
        accionRealizadaEnRonda = true;

        if (esUltimoEnActuar()) {
            finalizarRondaApuestas();
            avanzarEtapa();
        } else {
            avanzarTurno();
        }
        return true;
    }

    @Override
    public boolean apostar(int cantidad) {
        // Verificación básica: no se puede apostar si ya hay una apuesta
        if (apuestaActual > 0) {
            mensajeEstado = "No puedes apostar cuando ya hay una apuesta. Debes igualar, subir o retirarte.";
            return false;
        }

        if (cantidad <= 0) {
            mensajeEstado = "La cantidad debe ser mayor que cero.";
            return false;
        }

        // La cantidad debe ser al menos la ciega grande
        if (cantidad < bigBlindValue) {
            mensajeEstado = "La apuesta mínima es la ciega grande (" + bigBlindValue + ").";
            return false;
        }

        Jugador jugadorActual = jugadores.get(turnoActual);

        // Si el jugador no tiene suficiente, va all-in
        int cantidadFinal = Math.min(cantidad, jugadorActual.getFichas());

        // Realizar la apuesta
        jugadorActual.restarFichas(cantidadFinal);
        pozo += cantidadFinal;

        // Actualizar apuesta acumulada
        int apuestaAnterior = apuestasAcumuladas.getOrDefault(jugadorActual, 0);
        apuestasAcumuladas.put(jugadorActual, apuestaAnterior + cantidadFinal);

        // Actualizar apuesta actual y últimos datos
        apuestaActual = cantidadFinal;
        ultimoJugadorQueSubio = turnoActual;
        rondaApuestasCompleta = false;
        accionRealizadaEnRonda = true;

        // Mensaje según el tipo de apuesta
        if (cantidadFinal < cantidad) {
            // Caso all-in
            jugadorActual.setAllIn(true);
            mensajeEstado = jugadorActual.getNombre() + " va ALL-IN con " + cantidadFinal + " fichas!";
        } else {
            mensajeEstado = jugadorActual.getNombre() + " apuesta " + cantidadFinal + " fichas.";
        }
        avanzarTurno();
        return true;
    }

    @Override
    public boolean igualar() {
        if (apuestaActual <= 0) {
            mensajeEstado = "No hay apuesta que igualar.";
            return false;
        }
        Jugador jugadorActual = jugadores.get(turnoActual);

        // Cuánto debe igualar
        int apuestaJugador = apuestasAcumuladas.getOrDefault(jugadorActual, 0);
        int cantidadAIgualar = apuestaActual - apuestaJugador;

        // Si ya ha igualado
        if (cantidadAIgualar <= 0) {
            mensajeEstado = "Ya has igualado la apuesta actual.";
            return false;
        }

        // Si no tiene suficiente, va all-in
        int cantidadFinal = Math.min(cantidadAIgualar, jugadorActual.getFichas());

        // Realizar la apuesta
        jugadorActual.restarFichas(cantidadFinal);
        pozo += cantidadFinal;

        // Actualizar apuesta acumulada
        apuestasAcumuladas.put(jugadorActual, apuestaJugador + cantidadFinal);
        accionRealizadaEnRonda = true;

        // Mensaje según el caso
        if (cantidadFinal < cantidadAIgualar) {
            jugadorActual.setAllIn(true);
            mensajeEstado = jugadorActual.getNombre() + " va ALL-IN con " + cantidadFinal + " fichas!";
        } else {
            mensajeEstado = jugadorActual.getNombre() + " iguala la apuesta de " + apuestaActual + " fichas.";
        }

        // Verificar si es el último jugador en actuar
        if (esUltimoEnActuar()) {
            finalizarRondaApuestas();
            avanzarEtapa();
        } else {
            avanzarTurno();
        }

        return true;
    }

    //Método para subir una apuesta existente
    @Override
    public boolean subir(int incremento) {
        // Verificación básica: debe haber una apuesta para poder subir
        if (apuestaActual <= 0) {
            mensajeEstado = "No hay apuesta que subir. Debes apostar primero.";
            return false;
        }

        if (incremento <= 0) {
            mensajeEstado = "El incremento debe ser mayor que cero.";
            return false;
        }

        Jugador jugadorActual = jugadores.get(turnoActual);

        // La subida mínima debe ser al menos igual a la apuesta actual
        if (incremento < apuestaActual) {
            mensajeEstado = "La subida mínima es igual a la apuesta actual (" + apuestaActual + ").";
            return false;
        }

        // Calcular cuánto debe igualar primero
        int apuestaJugador = apuestasAcumuladas.getOrDefault(jugadorActual, 0);
        int cantidadAIgualar = apuestaActual - apuestaJugador;

        // La cantidad total a apostar es lo que le falta para igualar + el incremento
        int cantidadTotal = cantidadAIgualar + incremento;

        // Si no tiene suficiente, va all-in
        int cantidadFinal = Math.min(cantidadTotal, jugadorActual.getFichas());

        // Realizar la apuesta
        jugadorActual.restarFichas(cantidadFinal);
        pozo += cantidadFinal;

        // Actualizar apuestas acumuladas
        apuestasAcumuladas.put(jugadorActual, apuestaJugador + cantidadFinal);

        // Calcular nueva apuesta total
        int nuevaApuestaTotal = apuestaJugador + cantidadFinal;

        // Solo si la nueva apuesta total supera la apuesta actual, actualizamos
        if (nuevaApuestaTotal > apuestaActual) {
            apuestaActual = nuevaApuestaTotal;
            ultimoJugadorQueSubio = turnoActual;
            rondaApuestasCompleta = false;
        }

        accionRealizadaEnRonda = true;

        // Mensaje según el caso
        if (cantidadFinal < cantidadTotal) {
            jugadorActual.setAllIn(true);
            mensajeEstado = jugadorActual.getNombre() + " va ALL-IN con " + cantidadFinal + " fichas!";
        } else {
            mensajeEstado = jugadorActual.getNombre() + " sube la apuesta a " + nuevaApuestaTotal + " fichas.";
        }

        avanzarTurno();
        return true;
    }
    //Método para manejar cuando un jugador se retira
    @Override
    public boolean retirarse() {
        Jugador jugadorActual = jugadores.get(turnoActual);
        mensajeEstado = jugadorActual.getNombre() + " se retira.";
        jugadorActual.abandonar();

        accionRealizadaEnRonda = true;

        // Verificar si solo queda un jugador activo
        int jugadoresActivos = contarJugadoresActivos();
        int jugadoresAllIn = contarJugadoresAllIn();

        if (jugadoresActivos == 0 && jugadoresAllIn > 0) {
            // Avanzar hasta el showdown para determinar el ganador
            mensajeEstado += " No quedan jugadores activos. Se avanza al showdown.";
            while (etapaActual != Etapa.SHOWDOWN) {
                finalizarRondaApuestas();
                avanzarEtapa();
            }
            return true;
        } else if (jugadoresActivos == 0 && jugadoresAllIn == 0) {
            mensajeEstado += " Error: No quedan jugadores en el juego.";
            rondaTerminada = true;
            return false;
        } else if (jugadoresActivos == 1 && jugadoresAllIn == 0) {
            for (Jugador jugador : jugadores) {
                if (jugador.estaActivo()) {
                    mensajeEstado += " Todos los demás jugadores se han retirado.";
                    finalizarRonda(jugador);
                    rondaTerminada = true;
                    etapaActual = Etapa.SHOWDOWN;
                    estadoActual = EstadoJuego.RONDA_FINALIZADA;
                    return true;
                }
            }
        } else if (jugadoresActivos == 1 && jugadoresAllIn > 0) {
            if (esUltimoEnActuar()) {
                mensajeEstado += " Se termina la ronda de apuestas.";
                finalizarRondaApuestas();
                // Continuar hasta el showdown
                while (etapaActual != Etapa.SHOWDOWN) {
                    avanzarEtapa();
                }
                return true;
            }
        }
        if (esUltimoEnActuar() && jugadoresActivos > 1) {
            finalizarRondaApuestas();
            avanzarEtapa();
        } else {
            avanzarTurno();
        }

        return true;
    }

    //Método para avanzar el turno al siguiente jugador
    @Override
    protected void avanzarTurno() {
        if (rondaTerminada) {
            return;
        }

        int activos = contarJugadoresActivos();

        if (activos <= 1 && !hayJugadoresAllIn()) {
            // Finalizar la ronda si solo queda un jugador
            if (activos == 1) {
                for (Jugador jugador : jugadores) {
                    if (jugador.estaActivo()) {
                        finalizarRonda(jugador);
                        rondaTerminada = true;
                        etapaActual = Etapa.SHOWDOWN;
                        estadoActual = EstadoJuego.RONDA_FINALIZADA;
                        return;
                    }
                }
            }
            return;
        }

        if (activos == 0 && hayJugadoresAllIn()) {
            finalizarRondaApuestas();
            avanzarEtapa();
            return;
        }

        // Guardar el turno inicial para detectar si damos una vuelta completa
        int turnoInicial = turnoActual;

        // Buscar el siguiente jugador activo
        do {
            turnoActual = (turnoActual + 1) % numeroDeJugadores;

            if (turnoActual == turnoInicial) {
                if (contarJugadoresActivos() == 0) {
                    finalizarRondaApuestas();
                    avanzarEtapa();
                    return;
                }
                break;
            }
        } while (!jugadores.get(turnoActual).estaActivo() || jugadores.get(turnoActual).isAllIn());
    }

    //Cuenta el número de jugadores en estado all-in
    private int contarJugadoresAllIn() {
        int contador = 0;
        for (Jugador jugador : jugadores) {
            if (jugador.isAllIn()) {
                contador++;
            }
        }
        return contador;
    }

    //Finaliza la ronda de apuestas actual
    private void finalizarRondaApuestas() {
        rondaApuestasCompleta = true;
    }

    //Métodos getter
    public ArrayList<Carta> getCartasComunitarias() {
        return cartasComunitarias;
    }

    public Etapa getEtapaActual() {
        return etapaActual;
    }

    public int getPosicionDealer() {
        return posicionDealer;
    }

    public int getPosicionSmallBlind() {
        return posicionSmallBlind;
    }

    public int getPosicionBigBlind() {
        return posicionBigBlind;
    }

    public int getBigBlindValue() {
        return bigBlindValue;
    }

    public void setSmallBlindValue(int smallBlindValue) {
        this.smallBlindValue = smallBlindValue;
    }

    public void setBigBlindValue(int bigBlindValue) {
        this.bigBlindValue = bigBlindValue;
    }

    public String getNombreEtapa() {
        switch (etapaActual) {
            case CIEGAS:
                return "Ciegas";
            case PRE_FLOP:
                return "Pre-Flop";
            case FLOP:
                return "Flop";
            case TURN:
                return "Turn";
            case RIVER:
                return "River";
            case SHOWDOWN:
                return "Showdown";
            default:
                return "Desconocida";
        }
    }
}