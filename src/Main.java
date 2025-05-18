import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Mazo mazo = new Mazo();
        mazo.mostrar();
        mazo.barajear();
        mazo.mostrar();
        System.out.println("Mano");
        ArrayList<Carta> mano = mazo.sacarNCartas(5);
        Mano mano1 = new Mano(mano);
        mano1.mostrar();
        System.out.println(mano1.obtenerCartaAlta());

    }
}
