import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String archivo = args[0]; 
        
        FaseLexica analizadorLexico = new FaseLexica();

        try {
            // Inicio de Fase Lexica
            analizadorLexico.analizarArchivo(archivo);

            System.out.println("FASE LEXICA:");
            analizadorLexico.imprimirTokens();

            // Guardar la tabla de símbolos en un archivo
            String archivoSalida = "tablaDeSimbolos.txt";
            analizadorLexico.getTablaSimbolos().guardarTablaSimbolos(archivoSalida);
            System.out.println("\n" + "Tabla de simbolos guardada en: " + archivoSalida);
            
            System.out.println("\n" + "FASE SINTACTICA: ");
            FaseSintactica analizadorSintactico = new FaseSintactica(analizadorLexico.getTokens());
            analizadorSintactico.analizar();

            analizadorSintactico.eliminarErroresTablaSimbolos("tablaDeSimbolos.txt");
            
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error en la fase sintáctica: " + e.getMessage());
        }
    }
}
