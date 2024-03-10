import java.io.*;
import java.text.Normalizer;
import java.util.*;

public class Crawler {

	public static void main(String[] args) throws Exception {

		File diccionario = new File("diccionario.ser");
		TreeMap<String, Integer> dicc = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);

		if (diccionario.exists()) {
			dicc = cargarObjeto(diccionario, dicc);
		} else {
			crawl("test", dicc);
			salvarObjeto("diccionario.ser", dicc);
		}

		File thesauro = new File("thesauro.ser");
		TreeMap<String, List<String>> thes = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

		if (thesauro.exists()) {
			thes = cargarObjeto(thesauro, thes);
		} else {
			crawlThesauro("Thesaurus_es_ES.txt", thes);
			salvarObjeto("thesauro.ser", thes);
		}

		// Consultar diccionario y thesauro
		consultarDiccionario(dicc);
		consultarThesauro(thes);

	}

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

	public static void crawl(String directorioRaiz, TreeMap<String, Integer> diccionarioTerminos) throws Exception {

		File raiz = new File(directorioRaiz);
		if (!raiz.exists() || !raiz.canRead()) {
			System.out.println("ERROR. No se puede leer el directorio: " + directorioRaiz);
			return;
		}

		if (raiz.isDirectory()) {
			List<File> listaDirectorios = new LinkedList<File>();
			listaDirectorios.add(raiz);
			listIt(listaDirectorios, diccionarioTerminos);
		} else {
			fichContPalabras(raiz, diccionarioTerminos);
		}
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

	public static void fichContPalabras(File fichero, Map<String, Integer> diccionarioTerminos) throws Exception {

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

	public static void consultarDiccionario(TreeMap<String, Integer> dicc) {

		Scanner teclado = new Scanner(System.in);

		while (true) {
			System.out.println("Introduzca palabra a buscar en el diccionario: (000 para salir)");
			String palabra = teclado.nextLine();
			if (palabra.equals("000")) {
				break;
			}

			String palabraNormalizada = eliminarTildes(palabra);

			if (dicc.containsKey(palabraNormalizada)) {
				System.out.println("La palabra " + palabra + " aparece " + dicc.get(palabraNormalizada) + " veces");
			} else {
				System.out.println("La palabra " + palabra + " no aparece en el diccionario");
			}
		}
	}

	public static void crawlThesauro(String archivo, TreeMap<String, List<String>> thes) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(archivo));
			String linea;
			while ((linea = br.readLine()) != null) {
				if (!linea.startsWith("#")) {
					StringTokenizer st = new StringTokenizer(linea, ";"); // Separadores
					String palabra = st.nextToken();
					List<String> sinonimos = new ArrayList<>();
					while (st.hasMoreTokens()) {
						sinonimos.add(st.nextToken());
					}
					thes.put(palabra, sinonimos);
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void consultarThesauro(TreeMap<String, List<String>> thes) {

		Scanner teclado = new Scanner(System.in);

		while (true) {
			System.out.println("Introduzca palabra a buscar en el Thesauro: (000 para salir)");
			String palabra = teclado.nextLine();
			if (palabra.equals("000")) {
				break;
			}

			String palabraNormalizada = eliminarTildes(palabra);

			if (thes.containsKey(palabraNormalizada)) {
				List<String> sinonimos = thes.get(palabraNormalizada);
				System.out.println("Sinónimos de " + palabra + ": " + sinonimos);
			} else {
				System.out.println("La palabra " + palabra + " no aparece en el thesauro");
			}
		}
		teclado.close();
	}

	// Método que elimina las tildes
	public static String eliminarTildes(String texto) {
		return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
				.toLowerCase();
	}

}
