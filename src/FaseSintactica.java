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
            while (indiceActual < tokens.size() && existe_error == false) {
                programa(); // Comienza con la producción 'programa'
            }
        } catch (Exception e) {
            System.out.println("Error [Fase Sintactica]: La linea " + (lineaActual) + " " + e.getMessage());
        }

        if(existe_error){
            throw new Exception("Error [Fase Sintactica]: Existe uno o mas errores en el proceso de la fase sintactica");
        }
    }

    // programa -> expresion ; { programa }
    private void programa() throws Exception {

        while (indiceActual < tokens.size()) {
            lineaActual++;
            expresion(); // Analiza una expresión

            if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                lista_numero.add('N');
                siguienteToken(); // Consume el punto y coma
            } else {
                //System.out.println("Error [Fase Sintactica]: La linea " + (lineaActual) + " contiene un error en su gramática, falta token ;" );
                existe_error = true;
                errores_tablaSimbolos.add(lineaActual+1);
                throw new Exception("Error [Fase Sintactica]: La linea " + (lineaActual) + " contiene un error en su gramática, falta token ;");
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
                indiceActual--; // Regresar un token para analizar la expresión
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
            // Verifica si hay más operadores
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
            //System.out.println("Error [Fase Sintactica]: La linea " + (lineaActual) + " contiene un error en su gramatica, falta token '(' (parentesis izquierdo)");
            errores_tablaSimbolos.add(lineaActual+1);
            existe_error = true;
            throw new Exception("contiene un error en su gramatica, falta token '(' (parentesis izquierdo)");
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

        // ------- Validacion en caso que haya un numero solo
            if(tokens.get(indiceActual).getTipo().equals("PUNTO_COMA") && (lista_numero.get(lista_numero.size()-1) == 'N')){
                validar_numeroSolo = true;
            }
             
            if (!validar_numeroSolo) {
            } else {
                //System.out.println("Error [Fase Sintactica]: La linea " + (lineaActual) + " contiene un error en su gramática, el numero esta solo");
                errores_tablaSimbolos.add(lineaActual+1);
                existe_error = true;
                throw new Exception("contiene un error en su gramática, el numero esta solo");
            }
            
        } else if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            // Verificamos que SI hay un parentesis izquiero
            lista.add('S');
            validar_parentesis = false;

            siguienteToken(); // Consume '('
            expresion(); // Analiza la expresión dentro del paréntesis
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // Consume ')'
            } else {
                //System.out.println("Error [Fase Sintactica]: La linea " + (lineaActual) + " Se esperaba un parentesis derecho.");               
                errores_tablaSimbolos.add(lineaActual+1);
                existe_error = true;
                throw new Exception("Se esperaba un parentesis derecho.");
            } 
        
            // Ya se completo '( )' por ende otra vez NO hay parentesis izquiero
            lista.add('N');
        } else {
            //System.out.println("Error [Fase Sintactica]: La linea " + (lineaActual) + " Se esperaba un identificador, numero o un parentesis.");               
            errores_tablaSimbolos.add(lineaActual+1);
            existe_error = true;

            throw new Exception("Se esperaba un identificador, numero o un parentesis.");
        }
    }

    private void siguienteToken() {
        if (indiceActual < tokens.size()) {
            indiceActual++;
        }
    }

    public void eliminarErroresTablaSimbolos(String archivoTablaSimbolos) throws IOException {
        // Cargar todas las líneas en una lista
        List<String> lineasValidas = new ArrayList<>();
        
        // Leer el archivo de la tabla de símbolos
        try (BufferedReader br = new BufferedReader(new FileReader(archivoTablaSimbolos))) {
            String linea;
            int numeroLinea = 1;

            // Leer cada línea y agregar las válidas
            while ((linea = br.readLine()) != null) {
                if (!errores_tablaSimbolos.contains(numeroLinea)) {
                    lineasValidas.add(linea); // Agregar línea válida a la lista
                }
                numeroLinea++;
            }
        }

        // Sobrescribir el archivo con las líneas válidas
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTablaSimbolos))) {
            for (String lineaValida : lineasValidas) {
                bw.write(lineaValida);
                bw.newLine();
            }
        }  
        
    }
}
    
    
