import java.io.*;
import java.util.*;

public class Crawler {

  public static void main(String[] args) throws Exception {

    if (args.length < 2) {
      System.out.println("ERROR. Ejecutar: >java Crawler.java directorio fichero_salida");
      return;
    }

    String directorio = args[0];
    String ficheroSalida = args[1];

    crawl(directorio, ficheroSalida);

  }

  public static void crawl(String directorioRaiz, String ficheroSalida) throws Exception {

    File raiz = new File(directorioRaiz);
    if (!raiz.exists() || !raiz.canRead()) {
      System.out.println("ERROR. No se puede leer el directorio: " + directorioRaiz);
      return;
    }

    Map<String, Integer> diccionarioTerminos = new TreeMap<String, Integer>();

    if (raiz.isDirectory()) {
      List<File> listaDirectorios = new LinkedList<File>();
      listaDirectorios.add(raiz);
      listIt(listaDirectorios, diccionarioTerminos);
    } else {
      fichContPalabras(raiz, diccionarioTerminos);
    }
    escribirFicheroSalida(diccionarioTerminos, ficheroSalida);
  }

  public static void listIt(List<File> listaDirectorios, Map<String, Integer> diccionarioTerminos) throws Exception {

    while (!listaDirectorios.isEmpty()) {
      File directorio = listaDirectorios.remove(0);
      File[] ficheros = directorio.listFiles();
      for (File f : ficheros) {
        if (f.isDirectory()) {
          listaDirectorios.add(f);
        } else {
          fichContPalabras(f, diccionarioTerminos);
        }
      }
    }
  }

  public static void fichContPalabras(File fichero, Map<String, Integer> diccionarioTerminos)
      throws Exception {

    BufferedReader br = new BufferedReader(new FileReader(fichero));
    String linea;

    while ((linea = br.readLine()) != null) {
      StringTokenizer st = new StringTokenizer(linea, " ,.:;(){}!°?\t''%/|[]<=>&#+*$-¨^~"); // Separadores
      while (st.hasMoreTokens()) {
        String s = st.nextToken();
        Object o = diccionarioTerminos.get(s);
        if (o == null)
          // diccionarioTerminos.put(s, new Integer(1));
          diccionarioTerminos.put(s, Integer.valueOf(1));
        else {
          Integer cont = (Integer) o;
          // diccionarioTerminos.put(s, new Integer(cont.intValue() + 1));
          diccionarioTerminos.put(s, Integer.valueOf(cont.intValue() + 1));
        }
      }
    }
    br.close();
  }

  public static void escribirFicheroSalida(Map<String, Integer> diccionarioTerminos, String ficheroSalida)
      throws Exception {

    List<String> claves = new ArrayList<String>(diccionarioTerminos.keySet());
    Collections.sort(claves);

    PrintWriter pr = new PrintWriter(new FileWriter(ficheroSalida));
    Iterator<String> i = claves.iterator();
    while (i.hasNext()) {
      Object k = i.next();
      pr.println(k + " : " + diccionarioTerminos.get(k));
    }
    pr.close();
  }
}
