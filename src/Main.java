import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String archivo = args[0]; 
        FaseLexica analizadorLexico = new FaseLexica();

        try {
            System.out.println("FASE LEXICA:");
            analizadorLexico.analizarArchivo(archivo);

            // Guardar la tabla de simbolos en un archivo
            String archivoSalida = "tablaDeSimbolos.txt";
            analizadorLexico.getTablaSimbolos().guardarTablaSimbolos(archivoSalida);
            System.out.println("\n" + "Tabla de simbolos guardada en: " + archivoSalida);
            
            //System.out.println("\n" + "FASE SINTACTICA: ");
            // Fase Sintactica del avance 2 sin la implementacion de los nodos AST
            //FaseSintactica analizadorSintactico = new FaseSintactica(analizadorLexico.getTokens());
            //analizadorSintactico.analizar();

            System.out.println("\n" + "FASE SINTACTICA AST:");

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
            System.out.println("Error [Fase Semantica]:" + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error en el main: " + e.getMessage());
        }
    }
}
