import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Mazo
{
    private ArrayList<Carta> cartas;

    public Mazo() {
        cartas = new ArrayList();
        llenar(1);
    }

    public Mazo(int valorDeInicio) {
        cartas = new ArrayList();
        llenar(valorDeInicio);
    }

//    public void mostrarInverso(){
//        for (int i = cartas.size()-1; i>= 0; i--){
//            System.out.println(cartas.get(i));
//        }
//    }
    public void mostrar(){
        for (int i = 0; i<cartas.size(); i++){
            System.out.println(cartas.get(i));
        }
    }



    private void llenar(int N) {
        //Se itera N veces para generar las N cartas
        for (int i= N; i<5; i++) {
            for (int j = N; j<14 ; j++){
                String color = "";
                String figura = "";
                //Switch Case para asignar la figura y su respectivo color
                switch (i){
                    case 1:
                        figura = "corazones";
                        color = "rojo";
                        break;
                    case 2:
                        figura = "diamantes";
                        color = "rojo";
                        break;
                    case 3:
                        figura = "tréboles";
                        color = "negro";
                        break;
                    case 4:
                        figura = "espadas";
                        color = "negro";
                        break;
                }
                 // Convertir el valor 1 a 14 para el As
                int valorCarta = j;
                if (j == 1) {
                    valorCarta = 14;
                }
                //se crea un nuevo objeto de la clase Carta para posteriormente añadirla al arreglo
                Carta carta = new Carta(j,color,figura);
                cartas.add(carta);
            }
        }
    }

    public void barajear() {
//        Random rnd = new Random();
//        for (int i = 0; i<cartas.size(); i++){
//            int posicion = rnd.nextInt(cartas.size());
//            Carta temp = cartas.get(i);
//            cartas.set(i,cartas.get(posicion));
//            cartas.set(posicion,temp);
//        }
        Collections.shuffle(cartas);
    }

//    public void eliminarPares(){
//        for (int i= 0; i < cartas.size(); i++){
//            Carta carta = cartas.get(i);
//            if (carta.getValor()%2==0) {
//                cartas.remove(i);
//                i--;
//                System.out.println(carta + " eliminada");
//            }
//        }
//    }

//    public void ordenar() { //
//        for (int i= 0; i< cartas.size()-1; i++) {
//            for( int j=i+1; j<cartas.size(); j++) {
//                Carta cartaA = cartas.get(i);
//                Carta cartaB = cartas.get(j);
//                if (cartaA.getValor() > cartaB.getValor()){
//                    Carta temp = cartas.get(i);
//                    cartas.set(i,cartas.get(j));
//                    cartas.set(j,temp);
//                }
//            }
//        }
//    }

//    public void eliminarPorFigura(String figura){
//        for ( int i= 0; i < cartas.size(); i++) {
//            //Carta carta = cartas.get(i)
//            if(cartas.get(i).getFigura().equals(figura)){
//                cartas.remove(i);
//                i--;
//            }
//        }
//    }

    public ArrayList<Carta> sacarNCartas(int numeroCartas) {
        ArrayList<Carta> mano = new ArrayList();

        for(int i = 0; i< numeroCartas; i++){
            Carta carta = cartas.remove(0);
            mano.add(carta);
        }
        return mano;
    }
//    public Mazo getMazoDeTamaño(int N){
//        Mazo nuevoMazo = new Mazo(N);
//        ArrayList<Carta> nuevasCartas = new ArrayList<>();
//        if (N > cartas.size()) {
//            N = cartas.size();
//        }
//        for (int i = 0; i < N; i++) {
//            nuevasCartas.add(cartas.get(i));
//        }
//        nuevoMazo.cartas = nuevasCartas;
//        return nuevoMazo;
//    }
}

