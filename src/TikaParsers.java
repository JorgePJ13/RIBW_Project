import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Clase para analizar diferentes tipos de archivos utilizando Tika.
 */
public class TikaParsers {

    /**
     * Analiza el archivo dado y devuelve su contenido como texto.
     *
     * @param file El archivo a analizar.
     * @return El contenido del archivo como texto.
     * @throws TikaException Si hay un error durante el análisis.
     * @throws IOException   Si ocurre un error de lectura del archivo.
     * @throws SAXException  Si hay un error SAX durante el análisis.
     */
    public static String parseFile(File file) throws TikaException, IOException, SAXException {

        String extension;
        String text = "";

        extension = getFileExtension(file.getName());

        switch (extension){
            case "txt":
                text = txtParser(file);
                break;
            case "pdf":
                text = pdfParser(file);
                break;
            case "html":
                text = htmlParser(file);
                break;
            default:
                System.out.println("Error, los archivos de tipo " + extension + " no están soportados");
        }

        return text;

    }

    /**
     * Obtiene la extensión de un nombre de archivo.
     *
     * @param filename El nombre del archivo.
     * @return La extensión del archivo.
     */
    public static String getFileExtension(String filename){

        int i = filename.lastIndexOf('.');
        if(i > 0) {
            return filename.substring(i + 1);
        } else {
            return ""; // No hay extensión
        }

    }

    /**
     * Analiza un archivo de texto plano (.txt) y devuelve su contenido como texto.
     *
     * @param file El archivo de texto plano a analizar.
     * @return El contenido del archivo como texto.
     * @throws IOException   Si ocurre un error de lectura del archivo.
     * @throws TikaException Si hay un error durante el análisis.
     * @throws SAXException  Si hay un error SAX durante el análisis.
     */
    public static String txtParser(File file) throws IOException, TikaException, SAXException {

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputStream = new FileInputStream(file);
        ParseContext parseContext = new ParseContext();

        TXTParser txtParser = new TXTParser();
        txtParser.parse(inputStream, handler, metadata, parseContext);

        return handler.toString();

    }

    /**
     * Analiza un archivo PDF (.pdf) y devuelve su contenido como texto.
     *
     * @param file El archivo PDF a analizar.
     * @return El contenido del archivo como texto.
     * @throws IOException   Si ocurre un error de lectura del archivo.
     * @throws TikaException Si hay un error durante el análisis.
     * @throws SAXException  Si hay un error SAX durante el análisis.
     */
    public static String pdfParser(File file) throws IOException, TikaException, SAXException {

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputStream = new FileInputStream(file);
        ParseContext parseContext = new ParseContext();

        PDFParser pdfParser = new PDFParser();
        pdfParser.parse(inputStream, handler, metadata, parseContext);

        return handler.toString();

    }

    /**
     * Analiza un archivo HTML (.html) y devuelve su contenido como texto.
     *
     * @param file El archivo HTML a analizar.
     * @return El contenido del archivo como texto.
     * @throws IOException   Si ocurre un error de lectura del archivo.
     * @throws TikaException Si hay un error durante el análisis.
     * @throws SAXException  Si hay un error SAX durante el análisis.
     */
    public static String htmlParser(File file) throws IOException, TikaException, SAXException {

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputStream = new FileInputStream(file);
        ParseContext parseContext = new ParseContext();

        HtmlParser htmlParser = new HtmlParser();
        htmlParser.parse(inputStream, handler, metadata, parseContext);

        return handler.toString();

    }

}
