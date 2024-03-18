package paquete;

import java.io.*;
import java.text.Normalizer;
import java.util.*;

public class Crawler {

	private static TreeMap<String, Ocurrencia> diccionarioTerminos;
	private static TreeMap<String, ArrayList<String>> thesauro;
	private ArrayList<String> urls;

	public static void main(String[] args) throws Exception {

		File thesauri = new File("thesauro.ser");
		thesauro = new TreeMap<String, ArrayList<String>>();

		if (thesauri.exists()) {
			thesauro = cargarObjeto(thesauri, thesauro);
		} else {
			crawlThesauro("Thesaurus_es_ES.txt");
			salvarObjeto("thesauro.ser", thesauro);
		}

		File diccionario = new File("diccionario.ser");
		diccionarioTerminos = new TreeMap<String, Ocurrencia>();

		if (diccionario.exists()) {
			diccionarioTerminos = cargarObjeto(diccionario, diccionarioTerminos);
		} else {
			crawl("test");
			salvarObjeto("diccionario.ser", diccionarioTerminos);
		}

		// Consultar diccionario
		consultarDiccionario();

	}

	@SuppressWarnings("unchecked")
	public static <K, V> TreeMap<K, V> cargarObjeto(File fichero, TreeMap<K, V> object) {

		try {
			FileInputStream fis = new FileInputStream(fichero);
			ObjectInputStream ois = new ObjectInputStream(fis);
			object = (TreeMap<K, V>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return object;
	}

	public static void crawl(String directorioRaiz) throws Exception {

		File raiz = new File(directorioRaiz);
		if (!raiz.exists() || !raiz.canRead()) {
			System.out.println("ERROR. No se puede leer el directorio: " + directorioRaiz);
			return;
		}

		if (raiz.isDirectory()) {
			List<File> listaDirectorios = new LinkedList<File>();
			listaDirectorios.add(raiz);
			listIt(listaDirectorios);
		} else {
			fichContPalabras(raiz);
		}
	}

	public static void listIt(List<File> listaDirectorios) throws Exception {

		while (!listaDirectorios.isEmpty()) {
			File directorio = listaDirectorios.remove(0);
			File[] ficheros = directorio.listFiles();
			for (File f : ficheros) {
				if (f.isDirectory()) {
					listaDirectorios.add(f);
				} else {
					fichContPalabras(f);
				}
			}
		}
	}

	public static void fichContPalabras(File fichero) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(fichero));
		String linea;

		Ocurrencia ocurrencia;
		ArrayList<String> sinonimos = new ArrayList<>();
		String path = fichero.getAbsolutePath();
		System.out.println("HOLAAAAAAAAA FICHERO EN: " + path);

		while ((linea = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(linea, " ,.:;(){}!°?\t''%/|[]<=>&#+*$-¨^~"); // Separadores
			while (st.hasMoreTokens()) {
				String s = st.nextToken().toLowerCase();
				s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

				Object o = diccionarioTerminos.get(s);
				if (o == null) {
					// diccionarioTerminos.put(s, new Integer(1));
					// diccionarioTerminos.put(s, Integer.valueOf(1));
					sinonimos = thesauro.get(s);
					ocurrencia = new Ocurrencia(path, sinonimos);
					diccionarioTerminos.put(s, ocurrencia);
				}

				else {
					// Integer cont = (Integer) o;
					// diccionarioTerminos.put(s, new Integer(cont.intValue() + 1));
					// diccionarioTerminos.put(s, Integer.valueOf(cont.intValue() + 1));
					ocurrencia = (Ocurrencia) o;
					ocurrencia.aumentarFrecuencia(path);
				}
			}
		}
		br.close();

	}

	public static <K, V> void salvarObjeto(String nombreFichero, TreeMap<K, V> object) {

		try {
			FileOutputStream fos = new FileOutputStream(nombreFichero);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void consultarDiccionario() {

		Scanner teclado = new Scanner(System.in);

		while (true) {
			System.out.println("Introduzca palabra a buscar en el diccionario: (000 para salir)");
			String palabra = teclado.nextLine();
			if (palabra.equals("000")) {
				break;
			}
			if (diccionarioTerminos.containsKey(palabra)) {
				Object valor = diccionarioTerminos.get(palabra);
				if (valor instanceof Ocurrencia) {
					Ocurrencia ocurrencia = (Ocurrencia) valor;
					System.out.println("La palabra " + palabra + " aparece " + ocurrencia.getFT() + " veces");
					System.out.println("Sinónimos de " + palabra + ": " + ocurrencia.getSinonimos());
				} else {
					System.out.println("El valor asociado con la palabra " + palabra + " no es una instancia de Ocurrencia");
				}
			} else {
				System.out.println("La palabra " + palabra + " no aparece en el diccionario");
			}

		}
		teclado.close();
	}

	public static void crawlThesauro(String archivo) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(archivo));
			String linea;
			while ((linea = br.readLine()) != null) {
				if (!linea.startsWith("#")) {
					StringTokenizer st = new StringTokenizer(linea, ";"); // Separadores
					String palabra = st.nextToken();
					ArrayList<String> sinonimos = new ArrayList<>();
					while (st.hasMoreTokens()) {
						sinonimos.add(st.nextToken());
					}
					thesauro.put(palabra, sinonimos);
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
