import java.io.*;
import java.util.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

@SuppressWarnings("serial")
public class Ocurrencia implements Serializable {

    private Integer FT;
    private TreeMap<String, Integer> FTURL;
    private ArrayList<String> sinonimos;

    /**
     * Constructor de la clase Ocurrencia.
     *
     * @param FT        Frecuencia de la palabra.
     * @param FTURL     Mapa que contiene la frecuencia de la palabra en cada
     *                  archivo.
     * @param sinonimos Lista de sinónimos de la palabra.
     */
    public Ocurrencia(Integer FT, TreeMap<String, Integer> FTURL, ArrayList<String> sinonimos) {

        this.FT = FT;
        this.FTURL = FTURL;
        this.sinonimos = sinonimos;

    }

    /**
     * Constructor de la clase Ocurrencia que inicializa la frecuencia de la palabra
     * y su ubicación en un archivo.
     *
     * @param path      Ruta del archivo en el que se encuentra la palabra.
     * @param sinonimos Lista de sinónimos de la palabra.
     */
    public Ocurrencia(String path, ArrayList<String> sinonimos) {

        this.FT = 1;
        this.FTURL = new TreeMap<String, Integer>();
        this.FTURL.put(path, Integer.valueOf(1));
        this.sinonimos = sinonimos;

    }

    /**
     * Obtiene la frecuencia total de la palabra.
     *
     * @return Frecuencia total de la palabra.
     */
    public Integer getFT() {
        return FT;
    }

    /**
     * Establece la frecuencia total de la palabra.
     *
     * @param FT Frecuencia total de la palabra.
     */
    public void setFT(Integer FT) {
        this.FT = FT;
    }

    /**
     * Obtiene el mapa que contiene la frecuencia de la palabra en cada archivo.
     *
     * @return Mapa que contiene la frecuencia de la palabra en cada archivo.
     */
    public TreeMap<String, Integer> getFTURL() {
        return FTURL;
    }

    /**
     * Establece el mapa que contiene la frecuencia de la palabra en cada archivo.
     *
     * @param FTURL Mapa que contiene la frecuencia de la palabra en cada archivo.
     */
    public void setFTURL(TreeMap<String, Integer> FTURL) {
        this.FTURL = FTURL;
    }

    /**
     * Obtiene la lista de sinónimos de la palabra.
     *
     * @return Lista de sinónimos de la palabra.
     */
    public List<String> getSinonimos() {
        return sinonimos;
    }

    /**
     * Establece la lista de sinónimos de la palabra.
     *
     * @param sinonimos Lista de sinónimos de la palabra.
     */
    public void setSinonimos(ArrayList<String> sinonimos) {
        this.sinonimos = sinonimos;
    }

    /**
     * Aumenta la frecuencia de la palabra en el archivo especificado.
     *
     * @param path Ruta del archivo en el que se encuentra la palabra.
     */
    public void aumentarFrecuencia(String path) {

        Object o = FTURL.get(path);
        Integer cont = (Integer) o;

        FT++;

        if (o == null) {
            FTURL.put(path, Integer.valueOf(1));
        } else {
            FTURL.put(path, Integer.valueOf(cont.intValue() + 1));
        }
    }

    /**
     * Muestra información sobre una palabra consultada.
     *
     * @param palabra Palabra consultada.
     */
    public void mostrar(String palabra) {
        System.out.println("Palabra consultada: " + palabra + " " + sinonimos);
        System.out.println("La frecuencia global del término es: " + FT);

        FTURL.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(10)
                .forEachOrdered(
                        e -> System.out.println("En el archivo " + e.getKey() + " aparece " + e.getValue() + " veces"));
    }

}
