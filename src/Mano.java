import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Mano {
    private ArrayList<Carta> mano;

    public Mano(ArrayList<Carta> cartas) {
        this.mano = cartas;
    }

    ArrayList<Carta> getMano() {
        return mano;
    }

    public Carta eliminarCartaDePosicion(int posicion) {
        if (posicion < 0 || posicion >= mano.size()) {
            return null;
        }
        return mano.remove(posicion - 1);
    }

    public void eliminarCarta(Carta laCartaAEliminar) {
        for (int i = 0; i < mano.size(); i++) {
            if (mano.get(i).esIgualA(laCartaAEliminar)) {
                mano.remove(i);
                return;
            }
        }
    }

    public void agregarUnaCarta(ArrayList<Carta> cartas,
                                Carta otraCarta){
        cartas.add(otraCarta);
        this.mano = cartas;
    }

    public int[] contarValores() {
        int[] contadorFrecuencias = new int[15]; // hasta el 14

        for (Carta carta : mano) {
            int valor = carta.getValor();
            contadorFrecuencias[valor]++;
        }

        return contadorFrecuencias;
    }

//valor mayor

     public boolean esEscalera() {
        ordenar();

        // Verificar específicamente la escalera A-2-3-4-5 (aquí A es 14)
        boolean tieneAs = false;
        boolean tiene2 = false;
        boolean tiene3 = false;
        boolean tiene4 = false;
        boolean tiene5 = false;

        for (Carta carta : mano) {
            int valor = carta.getValor();
            if (valor == 14) tieneAs = true;
            else if (valor == 2) tiene2 = true;
            else if (valor == 3) tiene3 = true;
            else if (valor == 4) tiene4 = true;
            else if (valor == 5) tiene5 = true;
        }

        // Si tenemos A-2-3-4-5, es una escalera
        if (tieneAs && tiene2 && tiene3 && tiene4 && tiene5) {
            return true;
        }

        // Verificación de escalera normal
        int[] contadorFrecuencias = contarValores();
        int consecutivos = 0;

        for (int i = 2; i <= 14; i++) {  // Comienza desde 2 ya que el As ahora es 14
            if (contadorFrecuencias[i] >= 1) {
                consecutivos++;
                if (consecutivos == 5) {
                    return true;
                }
            } else {
                consecutivos = 0;
            }
        }

        return false;
    }
    public boolean esEscaleraColor() {
        return esEscalera() && sonDelMismoPalo();
    }

    public boolean esEscaleraReal() {
        ordenar();
        return esEscaleraColor() && mano.get(0).getValor() == 10;
    }

    public int obtenerCartaAlta(){
        int mayorValor = 0;
        for(Carta carta : mano){
            if(carta.getValor() == 1){
                mayorValor = 14;
            }
            if(carta.getValor() > mayorValor){
                mayorValor = carta.getValor();
            }
        }
        return mayorValor;
    }

    public boolean hayUnPar() {
        int[] contadorFrecuencias = contarValores();
        for (int i = 2; i <= 14; i++) {
            if (contadorFrecuencias[i] == 2) {
                return true;
            }
        }
        return false;
    }

    public boolean hayDosPares() {
        int[] contadorFrecuencias = contarValores();
        int pares = 0;

        for (int i = 2; i <= 14; i++) {
            if (contadorFrecuencias[i] == 2) {
                pares++;
            }
        }
        return pares == 2;
    }

    public boolean hayTercia() {
        int[] contadorFrecuencias = contarValores();

        for (int i = 2; i <= 14; i++) {
            if (contadorFrecuencias[i] == 3) {
                return true;
            }
        }
        return false;
    }

    public boolean esPoker(){
        int[] contadorFrecuencias = contarValores();
        for (int i = 2; i <= 14; i++) {
            if (contadorFrecuencias[i] == 4) {
                return true;
            }
        }
        return false;
    }


    public int cuantasHayCon(String figura) {
        int cuantasHay = 0;
        for (int i = 0; i < mano.size(); i++) {
            if (mano.get(i).getFigura().equals(figura)) {
                cuantasHay++;
            }
        }
        return cuantasHay;
    }

    public boolean sonDelMismoPalo() {
        String figura = mano.get(0).getFigura();
        return mano.stream().allMatch(c -> figura.equals(c.getFigura()));
    }

    public boolean hayFullHouse(){
        return hayUnPar() && hayTercia();
    }

    public boolean tienenNDelMismoValor(int N) {
        for (int i = 0; i < mano.size(); i++) {
            int cartasMismoValor = 0;
            for (int j = 0; j < mano.size(); j++) {
                if (mano.get(i).getValor() == mano.get(j).getValor()) {
                    cartasMismoValor++;
                }
            }
            if (cartasMismoValor >= N) {
                return true;
            }
        }
        return false;
    }

    public void mostrar() {
        for (int i = 0; i < mano.size(); i++) {
            System.out.println(mano.get(i));

        }
    }

    public void ordenar() { //
        for (int i = 0; i < mano.size() - 1; i++) {
            for (int j = i + 1; j < mano.size(); j++) {
                Carta cartaA = mano.get(i);
                Carta cartaB = mano.get(j);
                if (cartaA.getValor() > cartaB.getValor()) {
                    Carta temp = mano.get(i);
                    mano.set(i, mano.get(j));
                    mano.set(j, temp);
                }
            }
        }
    }
}
