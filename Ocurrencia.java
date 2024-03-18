package paquete;

import java.io.*;
import java.util.*;

public class Ocurrencia implements Serializable {

    private Integer FT;
    private TreeMap<String, Integer> FTURL;
    private ArrayList<String> sinonimos;

    public Ocurrencia(Integer FT, TreeMap<String, Integer> FTURL, ArrayList<String> sinonimos){

        this.FT = FT;
        this.FTURL = FTURL;
        this.sinonimos = sinonimos;

    }

    public Ocurrencia(String path, ArrayList<String> sinonimos){

        this.FT = 1;
        this.FTURL.put(path, Integer.valueOf(1));
        this.sinonimos = sinonimos;

    }

    public Integer getFT() {
        return FT;
    }

    public void setFT(Integer FT) {
        this.FT = FT;
    }

    public TreeMap<String, Integer> getFTURL() {
        return FTURL;
    }

    public void setFTURL(TreeMap<String, Integer> FTURL) {
        this.FTURL = FTURL;
    }

    public List<String> getSinonimos() {
        return sinonimos;
    }

    public void setSinonimos(ArrayList<String> sinonimos) {
        this.sinonimos = sinonimos;
    }

    public void aumentarFrecuencia(String path){

        Object o = FTURL.get(path);
        Integer cont = (Integer) o;

        if (o == null) {
            FTURL.put(path, Integer.valueOf(1));
        }
        else{
            FTURL.put(path, Integer.valueOf(cont.intValue() + 1));
        }
    }

}
