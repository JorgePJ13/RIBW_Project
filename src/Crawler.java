import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class Crawler {

	// Declaración de variables globales
	private static TreeMap<String, Ocurrencia> diccionarioTerminos;
	private static TreeMap<String, ArrayList<String>> thesauro;

	public static void main(String[] args) throws Exception {

		File thesauri = new File("thesauro.ser");
		thesauro = new TreeMap<String, ArrayList<String>>();

		// Carga o creación del Thesauro
		if (thesauri.exists()) {
			thesauro = cargarObjeto(thesauri, thesauro);
		} else {
			crawlThesauro("Thesaurus_es_ES.txt");
			salvarObjeto("thesauro.ser", thesauro);
		}

		File diccionario = new File("diccionario.ser");
		diccionarioTerminos = new TreeMap<String, Ocurrencia>();

		// Carga o creación del Diccionario de Términos
		if (diccionario.exists()) {
			diccionarioTerminos = cargarObjeto(diccionario, diccionarioTerminos);
		} else {
			parsing("test");
			salvarObjeto("diccionario.ser", diccionarioTerminos);
		}

		// Consulta al Diccionario de Términos.
		consultarDiccionario();
	}

	/**
	 * Carga un objeto desde un archivo.
	 *
	 * @param fichero Archivo desde el cual cargar el objeto.
	 * @param object  TreeMap donde se almacenará el objeto cargado.
	 * @param <K>     Tipo de la clave del TreeMap.
	 * @param <V>     Tipo del valor del TreeMap.
	 * @return TreeMap con el objeto cargado desde el archivo.
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> TreeMap<K, V> cargarObjeto(File fichero, TreeMap<K, V> object)
			throws IOException, ClassNotFoundException {

		try {
			FileInputStream fis = new FileInputStream(fichero);
			ObjectInputStream ois = new ObjectInputStream(fis);
			object = (TreeMap<K, V>) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error al cargar el objeto desde el archivo: " + e.getMessage());
		}
		return object;
	}

	/**
	 * Explora un directorio y sus subdirectorios.
	 *
	 * @param directorioRaiz Directorio raíz desde el cual comenzar la exploración.
	 */
	public static void parsing(String directorioRaiz) throws Exception {

		// Verifica si el directorio raíz existe y es legible
		File raiz = new File(directorioRaiz);
		if (!raiz.exists() || !raiz.canRead()) {
			System.out.println("ERROR. No se puede leer el directorio: " + directorioRaiz);
			return;
		}

		// Si es un directorio, crea una lista de directorios para procesar
		if (raiz.isDirectory()) {
			List<File> listaDirectorios = new LinkedList<File>();
			listaDirectorios.add(raiz);
			listIt(listaDirectorios);
			// Si es un archivo, analiza su contenido
		} else {
			String text = TikaParsers.parseFile(raiz);
			fichContPalabras(raiz, text);
		}
	}

	/**
	 * Recorre una lista de directorios y sus subdirectorios.
	 *
	 * @param listaDirectorios Lista de directorios a recorrer.
	 */
	public static void listIt(List<File> listaDirectorios) throws Exception {

		while (!listaDirectorios.isEmpty()) {
			File directorio = listaDirectorios.remove(0);
			File[] ficheros = directorio.listFiles();
			for (File f : ficheros) {
				// Si es un directorio, lo agrega a la lista para procesarlo posteriormente
				if (f.isDirectory()) {
					listaDirectorios.add(f);
					// Si es un archivo, analiza su contenido
				} else {
					String text = TikaParsers.parseFile(f);
					fichContPalabras(f, text);
				}
			}
		}
	}

	/**
	 * Analiza un archivo de texto para contar las ocurrencias de palabras y actualizar el diccionario de términos.
	 *
	 * @param fichero Archivo de texto a analizar.
	 * @param text    El texto extraído del archivo.
	 * @throws IOException Si ocurre un error al leer el archivo.
	 */
	public static void fichContPalabras(File fichero, String text) throws IOException {

		BufferedReader br = new BufferedReader(new StringReader(text));
		String linea;

		// Información sobre la ocurrencia de palabras
		Ocurrencia ocurrencia;
		ArrayList<String> sinonimos = new ArrayList<>();
		String path = fichero.getAbsolutePath();

		while ((linea = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(linea, " ,.:;(){}!°?\t'%/|[]<=>&#+*$-¨^~"); // Separadores
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				s = normalizarPalabra(s);

				// Verifica si la palabra existe en el thesauro
				if (thesauro.containsKey(s)) {
					Object o = diccionarioTerminos.get(s);
					// Si la palabra no existe en el diccionario, crea una nueva ocurrencia
					if (o == null) {
						// diccionarioTerminos.put(s, new Integer(1));
						// diccionarioTerminos.put(s, Integer.valueOf(1));
						sinonimos = thesauro.get(s);
						ocurrencia = new Ocurrencia(path, sinonimos);
						diccionarioTerminos.put(s, ocurrencia);
					}
					// Si la palabra ya existe, aumenta su frecuencia
					else {
						// Integer cont = (Integer) o;
						// diccionarioTerminos.put(s, new Integer(cont.intValue() + 1));
						// diccionarioTerminos.put(s, Integer.valueOf(cont.intValue() + 1));
						ocurrencia = (Ocurrencia) o;
						ocurrencia.aumentarFrecuencia(path);
					}

				}
			}
		}
		br.close();
	}

	/**
	 * Guarda un objeto en un archivo utilizando serialización.
	 *
	 * @param nombreFichero Nombre del archivo donde se guardará el objeto.
	 * @param object        Objeto a guardar en el archivo.
	 * @param <K>           Tipo de la clave del objeto (para TreeMap).
	 * @param <V>           Tipo del valor del objeto (para TreeMap).
	 */
	public static <K, V> void salvarObjeto(String nombreFichero, TreeMap<K, V> object) {

		try {
			FileOutputStream fos = new FileOutputStream(nombreFichero);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
		} catch (Exception e) {
			System.out.println("Error al guardar el objeto en el archivo: " + e.getMessage());
		}
	}

	/**
	 * Consulta el diccionario de términos. Permite buscar una palabra y muestra
	 * información relacionada si existe en el diccionario.
	 */
	public static void consultarDiccionario() {

		Scanner teclado = new Scanner(System.in);

		while (true) {
			System.out.println("Introduzca palabra a buscar en el diccionario: (000 para salir)");
			String palabra = teclado.nextLine();
			if (palabra.equals("000")) {
				break;
			}

			palabra = normalizarPalabra(palabra);

			if (diccionarioTerminos.containsKey(palabra)) {
				Object valor = diccionarioTerminos.get(palabra);
				if (valor instanceof Ocurrencia) {
					Ocurrencia ocurrencia = (Ocurrencia) valor;
					ocurrencia.mostrar(palabra);
				} else {
					System.out.println(
							"El valor asociado con la palabra " + palabra + " no es una instancia de Ocurrencia");
				}
			} else {
				System.out.println("La palabra " + palabra + " no aparece en el diccionario");
			}
		}
		teclado.close();
	}

	/**
	 * Carga sinónimos desde un archivo y los almacena en el thesauro.
	 *
	 * @param archivo Archivo que contiene los sinónimos en formato específico.
	 */
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
					palabra = normalizarPalabra(palabra);
					thesauro.put(palabra, sinonimos);
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Normaliza una palabra eliminando acentos y caracteres especiales.
	 *
	 * @param palabra Palabra a normalizar.
	 * @return Palabra normalizada en minúsculas sin acentos ni caracteres
	 *         especiales.
	 */
	public static String normalizarPalabra(String palabra) {

		String palabraNormalizada = palabra.toLowerCase();
		palabraNormalizada = Normalizer.normalize(palabraNormalizada, Normalizer.Form.NFD).replaceAll("[^\\p{IsLatin}]",
				"");

		return palabraNormalizada;
	}

}
