import java.util.ArrayList;

/**
 * Representa al jugador
 */
public class Jugador {
    private String nombre;
    private int fichas;
    private Mano mano;
    private boolean activo;
    private boolean allIn;


      /**
       * Constructor que inicializa un jugador
       */
    public Jugador(String nombre, int fichasIniciales) {
        this.nombre = nombre;
        this.fichas = fichasIniciales;
        this.activo = true;
        this.allIn = false;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFichas() {
        return fichas;
    }

    public void setFichas(int fichas) {
        this.fichas = fichas;
    }

    public Mano getMano() {
        return mano;
    }

    public void setMano(Mano mano) {
        this.mano = mano;
    }

    public boolean estaActivo() {
        return activo;
    }

    public boolean isAllIn() {
        return allIn;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }


    /**
     * Agrega fichas al jugador (cuando gana)
     */
    public void agregarFichas(int cantidad) {
        if (cantidad > 0) {
            fichas += cantidad;
        }
    }

    /**
     * Resta fichas al jugador (cuando apuesta)
     */
    public boolean restarFichas(int cantidad) {
        if (cantidad > 0 && cantidad <= fichas) {
            fichas -= cantidad;
            return true;
        }
        return false;
    }

    /**
     * Marca al jugador como retirado de la ronda actual
     */
    public void abandonar() {
        this.activo = false;
    }

    /**
     * Activa al jugador para una nueva ronda
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Limpia la mano del jugador y resetea su estado para una nueva ronda
     */
    public void limpiarMano() {
        this.mano = null;
        this.activo = true;
        this.allIn = false;
    }

    /**
     * Asigna cartas al jugador creando una nueva mano
     */
    public void recibirCartas(ArrayList<Carta> cartas) {
        this.mano = new Mano(cartas);
    }


    public void mostrarMano() {
        if (mano != null) {
            mano.mostrar();
        }
    }

    /**
     * Clase interna que da la información del jugador para la interfaz grafica
     */
    public class Info {
        public String nombre;
        public int fichas;
        public boolean activo;
        public boolean allIn;
        public boolean esTurnoActual;
        public boolean tieneCartas;
        public ArrayList<Carta> cartas;

        /**
         * Constructor privado que inicializa los datos desde el jugador
         */
        private Info(boolean esTurnoActual) {
            this.nombre = Jugador.this.nombre;
            this.fichas = Jugador.this.fichas;
            this.activo = Jugador.this.activo;
            this.allIn = Jugador.this.allIn;
            this.esTurnoActual = esTurnoActual;

            if (Jugador.this.mano != null) {
                this.tieneCartas = true;
                this.cartas = Jugador.this.mano.getMano();
            } else {
                this.tieneCartas = false;
                this.cartas = null;
            }
        }
    }

    /**
     * Obtiene la informacion del jugador para mostrar en la interfaz grafica
     */
    public Info obtenerInformacion(boolean esTurnoActual) {
        return new Info(esTurnoActual);
    }
}