import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FaseSintactica {
    private List<Token> tokens;
    private int indiceActual;
    private int lineaActual;
    private boolean validar_parentesis;
    private boolean validar_numeroSolo;
    private boolean existe_error;
    private List<Character> lista = new ArrayList<>();
    private List<Character> lista_numero = new ArrayList<>();
    List<Integer> errores_tablaSimbolos = new ArrayList<>();
    List<String> errores_tokens = new ArrayList<>(); // Se utiliza para saber que linea eliminar del txt en caso de un error

    public FaseSintactica(List<Token> tokens) {
        this.tokens = tokens;
        this.indiceActual = 0;
        this.lineaActual = 0; // Se utiliza para saber en que linea esta el error encontrado
        lista.add('N'); // N es igual a NO tiene par el parentesis inicial
        lista_numero.add('N'); // N es igual a NO es una asignacion
        this.validar_parentesis = false; // Validacion para el error
        this.validar_numeroSolo = false; // Validacion para el error
        this.existe_error = false; //Se valida que no hayan errores
        
    }

    public void analizar() throws Exception {
        try {
            while (indiceActual < tokens.size() && !existe_error) {
                programa();
            }
        } catch (IndexOutOfBoundsException e) { 
            //cuando detecta un indice fuera de sus limites significa que falta ;
            existe_error = true;
            errores_tablaSimbolos.add(lineaActual+1);
            System.out.println(" contiene un error en su gramatica, falta token ;");

        } catch (Exception e) {
            existe_error = true;
            errores_tablaSimbolos.add(lineaActual+1);
            eliminarErroresTablaSimbolos("tablaDeSimbolos.txt");
            System.out.println("Error [Fase Sintactica]: La linea " + (lineaActual) + e.getMessage());
        }

        if(!existe_error){
            System.out.println("Se logro completar la fase sintactica correctamente");
        }
    }

    // programa -> expresion ; { programa }
    private void programa() throws Exception {

        while (indiceActual < tokens.size()) {
            lineaActual++;
            expresion(); // Analiza una expresión

            if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                lista_numero.add('N');
                siguienteToken();
            }
             else {
                existe_error = true;
                errores_tablaSimbolos.add(lineaActual+1);
                throw new Exception(" contiene un error en su gramatica, falta token ;");
            }
        }
    }

    // expresion -> identificador = expresion | termino { ( +|- ) termino }
    private void expresion() throws Exception {

        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            
            siguienteToken(); // Consume el identificador

            if (tokens.get(indiceActual).getTipo().equals("ASIGNACION")) {
                siguienteToken(); // Consume el signo '='

                // Se verifica que el numero es parte de una asignacion, S significa SI es parte de una asignacion
                lista_numero.add('S'); 
                expresion(); 

            } else {
                // Si no hay una asignación, retrocede para permitir que la expresión continúe
                indiceActual--; 
                termino(); // Comenzar la expresión
                while (indiceActual < tokens.size() && 
                       (tokens.get(indiceActual).getTipo().equals("SUMA") || 
                        tokens.get(indiceActual).getTipo().equals("RESTA"))) {
                    siguienteToken(); // Consume el operador
                    termino(); // Consume el siguiente término
                }
            }
        } else {
            // Si no es un identificador, se comienza con un término
            termino();
                while (indiceActual < tokens.size() && 
                    (tokens.get(indiceActual).getTipo().equals("SUMA") || 
                    tokens.get(indiceActual).getTipo().equals("RESTA"))) {
                siguienteToken(); // Consume el operador
                termino(); // Consume el siguiente término

            }
        }
        
        // ----------- Validacion para revisar si hay parentesis derecho pero no izquierdo
        if ((tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) && (lista.get(lista.size()-1) == 'N')) {
            validar_parentesis = true;
        } 

        if (!validar_parentesis) {
        } else {
            errores_tablaSimbolos.add(lineaActual+1);
            existe_error = true;
            throw new Exception(" contiene un error en su gramatica, falta token '(' (parentesis izquierdo)");
        }

    }

    // termino -> factor { ( * | / ) factor }
    private void termino() throws Exception {

        factor(); // Analiza el primer factor
        while (indiceActual < tokens.size() && 
               (tokens.get(indiceActual).getTipo().equals("MULTIPLICACION") || 
                tokens.get(indiceActual).getTipo().equals("DIVISION"))) {
            siguienteToken(); // Consume el operador
            factor(); // Consume el siguiente factor
        }
    }

    // factor -> identificador | numero | ( expresion )
    private void factor() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            siguienteToken(); // Consume el identificador
    
        } else if (tokens.get(indiceActual).getTipo().equals("NUMERO")) {
            siguienteToken(); // Consume el número
    
            // Validación para detectar números consecutivos sin un operador entre ellos
            if (indiceActual < tokens.size() && tokens.get(indiceActual).getTipo().equals("NUMERO")) {
                existe_error = true;
                errores_tablaSimbolos.add(lineaActual + 1);
                throw new Exception(" contiene números consecutivos sin operador.");
            }
    
            // ------- Validación en caso que haya un número solo (sin estar asignado)
            if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA") && (lista_numero.get(lista_numero.size() - 1) == 'N')) {
                validar_numeroSolo = true;
            }
    
            if (!validar_numeroSolo) {
            } else {
                errores_tablaSimbolos.add(lineaActual + 1);
                existe_error = true;
                throw new Exception(" contiene un número que está solo.");
            }
    
        } else if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            // Verificamos que SI hay un paréntesis izquierdo
            lista.add('S');
            validar_parentesis = false;
    
            siguienteToken(); // Consume '('
            expresion(); 
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // Consume ')'
            } else {
                errores_tablaSimbolos.add(lineaActual + 1);
                existe_error = true;
                throw new Exception(" se esperaba un parentesis derecho.");
            }
    
            // Ya se completó '( )', por ende otra vez NO hay paréntesis izquierdo
            lista.add('N');
        } else {
            errores_tablaSimbolos.add(lineaActual + 1);
            existe_error = true;
            throw new Exception(" se esperaba un identificador, numero o un parentesis.");
        }
    }

    private void siguienteToken() {
        if (indiceActual < tokens.size()) {
            indiceActual++;
        }
    }

public void eliminarErroresTablaSimbolos(String archivoTablaSimbolos) throws IOException {
        // Lista para almacenar las líneas válidas
        List<String> lineasValidas = new ArrayList<>();
        
        // Leer el archivo y filtrar las líneas
        try (BufferedReader br = new BufferedReader(new FileReader(archivoTablaSimbolos))) {
            String linea;
            int numeroLinea = 1;

            while ((linea = br.readLine()) != null) {
                if (!errores_tablaSimbolos.contains(numeroLinea)) {
                    lineasValidas.add(linea); 
                }
                numeroLinea++;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTablaSimbolos))) {
            for (String linea : lineasValidas) {
                bw.write(linea);
                bw.newLine(); 
            }
        }
    }

}
    
    