import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String archivo = args[0]; // Se pasa el nombre del archivo como argumento
        FaseLexica analizadorLexico = new FaseLexica();

        try {
            // Analizar el archivo y obtener los tokens
            analizadorLexico.analizarArchivo(archivo);

            // Imprimir los tokens
            analizadorLexico.imprimirTokens();

            // Imprimir la tabla de símbolos
            analizadorLexico.getTablaSimbolos().imprimirTablaSimbolos();

            // Llamar a la fase sintáctica
            FaseSintactica analizadorSintactico = new FaseSintactica(analizadorLexico.getTokens());
            analizadorSintactico.analizar();
            
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error en la fase sintáctica: " + e.getMessage());
        }
    }
}
