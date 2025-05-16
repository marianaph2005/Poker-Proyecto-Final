
public class Carta
{
    private int valor;
    private String color;
    private String figura;

    public Carta(int valor, String color, String figura){
        this.valor = valor;
        this.color = color;
        this.figura = figura;
    }

    public int getValor(){
        return valor;
    }

    public String getColor(){
        return color;
    }

    public String getFigura(){
        return figura;
    }


    public boolean esIgualA(Carta otraCarta) {
        if(tieneElMismoValor(otraCarta)&&
                tieneLaMismaFigura(otraCarta) && tieneMismoColor(otraCarta)){
            return true;
        }
        return false;
    }


    public boolean tieneElMismoValor(Carta otraCarta) {
        if (valor == otraCarta.valor){
            return true;
        }
        return false;
    }


    public boolean tieneLaMismaFigura(Carta otraCarta) {
        if (figura == otraCarta.figura){
            return true;
        }
        return false;
    }

    public boolean tieneMismoColor(Carta otraCarta) {
        if (color == otraCarta.color){
            return true;
        }
        return false;
    }

    public String toString(){
        String laCarta;
        switch (valor) {
            case 0:
                laCarta ="";
                break;
            case 1:
                laCarta = "A";
                break;
            case 11:
                laCarta = "J";
                break;
            case 12:
                laCarta = "Q";
                break;
            case 13:
                laCarta ="K";
                break;
            default:
                laCarta = String.valueOf(valor);

        }

        return laCarta +" " + figura ;
    }
}