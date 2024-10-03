import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String archivo = args[0]; // Se pasa el nombre del archivo como argumento
        FaseLexica analizador = new FaseLexica();

        try {
            analizador.analizarArchivo(archivo);
            
            // Imprimir los tokens
            analizador.imprimirTokens();

            // Imprimir la tabla de símbolos
            analizador.getTablaSimbolos().imprimirTablaSimbolos();
            
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}
