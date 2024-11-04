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
        
            //Inicio Fase Sintactica
            //FaseSintactica analizadorSintactico = new FaseSintactica(analizadorLexico.getTokens());
            //analizadorSintactico.analizar();

            //Pruebas con la nueva fase sintactica
            FaseSintacticaAST faseSintacticaAST = new FaseSintacticaAST(analizadorLexico.getTokens());
            NodoAST ast = faseSintacticaAST.analizar();

            System.out.println("\n" + "FASE SEMANTICA: ");
            FaseSemantica faseSemantica = new FaseSemantica(analizadorLexico.getTablaSimbolos().getTabla());

            ast.aceptar(faseSemantica);

            System.out.println("\nImpresion Arbol AST:");
            ast.imprimir("");

            
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Error en la fase semantica: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error en la fase sintactica: " + e.getMessage());
        }
    }
}
