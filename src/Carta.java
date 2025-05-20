import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class Carta {
    private int valor;
    private String color;
    private String figura;
    private ImageIcon imagenOriginal;
    private ImageIcon imagenEscalada;
    private static final String RUTA_IMAGENES = "G:\\4toSemestre\\POO\\Poker-Proyecto-Final\\src\\recursos\\";

    public Carta(int valor, String color, String figura){
        this.valor = valor == 1 ? 14 : valor;
        this.color = color;
        this.figura = figura;
        cargarImagen();
    }

    public ImageIcon getImagen(int ancho, int alto) {

        if (imagenOriginal == null || imagenOriginal.getIconWidth() <= 0) {
            System.err.println("Error: la imagen original no se cargó correctamente para " + this);
            return new ImageIcon(); // Devuelve un icono vacío
        }
        if (imagenEscalada == null ||
                imagenEscalada.getIconWidth() != ancho ||
                imagenEscalada.getIconHeight() != alto) {

            Image img = imagenOriginal.getImage();
            Image imgEscalada = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            imagenEscalada = new ImageIcon(imgEscalada);
        }
        return imagenEscalada;
    }

    private void cargarImagen() {
        try {
            // Construye el nombre del archivo basado en los atributos de la carta
            String nombreArchivo = obtenerNombreArchivoImagen();
            System.out.println("Intentando cargar: " + RUTA_IMAGENES + nombreArchivo);

            // Verifica si el archivo existe físicamente
            File archivoImagen = new File(RUTA_IMAGENES + nombreArchivo);
            if (!archivoImagen.exists()) {
                System.err.println("ADVERTENCIA: El archivo no existe: " + archivoImagen.getAbsolutePath());
            }

            imagenOriginal = new ImageIcon(RUTA_IMAGENES + nombreArchivo);

            // Verifica si la imagen se cargó correctamente
            if (imagenOriginal.getIconWidth() <= 0) {
                System.err.println("La imagen se cargó pero tiene un tamaño inválido: " + nombreArchivo);
            } else {
                System.out.println("Imagen cargada exitosamente: " + nombreArchivo);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen para carta: " + this + ". Error: " + e.getMessage());
            // Imagen por defecto si no se encuentra la específica
            try {
                System.out.println("Intentando cargar imagen de respaldo: " + RUTA_IMAGENES + "back.png");
                imagenOriginal = new ImageIcon(RUTA_IMAGENES + "back.png");
            } catch (Exception ex) {
                System.err.println("No se pudo cargar ni siquiera la imagen de respaldo");
                imagenOriginal = new ImageIcon(); // Icono vacío como último recurso
            }
        }
    }

        private String obtenerNombreArchivoImagen() {
            // Ejemplo: "2_de_diamantes.png", "as_de_corazones.png", etc.
            String nombreValor;

            switch(valor) {
                case 14: nombreValor = "as"; break;
                case 11: nombreValor = "sota"; break;
                case 12: nombreValor = "reina"; break;
                case 13: nombreValor = "rey"; break;
                default: nombreValor = String.valueOf(valor);
            }

            // Asegúrate de que figura sea exactamente lo que necesitas (diamantes, corazones, etc.)
            // y que esté en minúsculas para coincidir con los nombres de archivo
            String figuraFormateada = figura.toLowerCase();

            String nombreArchivo = nombreValor + "_de_" + figuraFormateada + ".png";
            System.out.println("Nombre de archivo generado: " + nombreArchivo);
            return nombreArchivo;
        }
        //getters
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
        return tieneElMismoValor(otraCarta)
                && tieneLaMismaFigura(otraCarta)
                && tieneMismoColor(otraCarta);
    }


    public boolean tieneElMismoValor(Carta otraCarta) {
        if (valor == otraCarta.valor){
            return true;
        }
        return false;
    }


    public boolean tieneLaMismaFigura(Carta otraCarta) {
        return figura.equals(otraCarta.figura);
    }

    public boolean tieneMismoColor(Carta otraCarta) {
        return color.equals(otraCarta.color);
    }


    public String toString(){
        String laCarta;
        switch (valor) {
            case 0:
                laCarta ="";
                break;
            case 14:
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