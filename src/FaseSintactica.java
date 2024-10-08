import java.util.ArrayList;
import java.util.List;


public class FaseSintactica {
    private List<Token> tokens;
    private int indiceActual;
    private int lineaActual;
    private boolean validar_parentesis;
    private boolean validar_numeroSolo;
    private List<Character> lista = new ArrayList<>();
    private List<Character> lista_numero = new ArrayList<>();

    public FaseSintactica(List<Token> tokens) {
        this.tokens = tokens;
        this.indiceActual = 0;
        this.lineaActual = 0;
        lista.add('N'); // N es igual a NO tiene par el parentesis inicial
        lista_numero.add('N'); // N es igual a NO es una asignacion
        this.validar_parentesis = false;
        this.validar_numeroSolo = false;
    }

    public void analizar() {
        try {
            while (indiceActual < tokens.size()) {
                programa(); // Comienza con la producción 'programa'
            }
        } catch (Exception e) {
            System.out.println("Error [Fase Sintactica]: La linea " + (lineaActual) + " " + e.getMessage());
        }
    }

    private void programa() throws Exception {
        // La estructura es 'expresion ; { programa }'
        while (indiceActual < tokens.size()) {
            lineaActual++;
            expresion(); // Analiza una expresión

            if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                lista_numero.add('N');
                siguienteToken(); // Consume el punto y coma
            } else {
                throw new Exception("Error [Fase Sintactica]: La linea " + (lineaActual) + " contiene un error en su gramática, falta token ;");
            }
        }
    }

    private void expresion() throws Exception {
        // La producción 'expresion -> identificador = expresion | termino { ( +|- ) termino }'

        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            Token identificador = tokens.get(indiceActual);
            siguienteToken(); // Consume el identificador

            if (tokens.get(indiceActual).getTipo().equals("ASIGNACION")) {
                siguienteToken(); // Consume el signo '='
                // Analiza la expresión del lado derecho
                lista_numero.add('S'); // Se verifica que el numero es parte de una asignacion
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
                // Aquí es donde analizamos el siguiente término
                termino(); // Consume el siguiente término

            }
        }
        
        //---------------------VALIDACION PARENTESIS DER-----------------------------------
        //System.out.println(lista);
        System.out.println(tokens.get(indiceActual).getTipo());

        if ((tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) && (lista.get(lista.size()-1) == 'N')) {
            validar_parentesis = true;
        } 

        if (!validar_parentesis) {
        } else {
            throw new Exception("contiene un error en su gramatica, falta token ( (parentesis izquierdo)");
        }

        
    }

    private void termino() throws Exception {
        // La producción 'termino -> factor { ( * | / ) factor }'
        factor(); // Analiza el primer factor
        while (indiceActual < tokens.size() && 
               (tokens.get(indiceActual).getTipo().equals("MULTIPLICACION") || 
                tokens.get(indiceActual).getTipo().equals("DIVISION"))) {
            siguienteToken(); // Consume el operador
            factor(); // Consume el siguiente factor
        }
    }

    private void factor() throws Exception {
        // La producción 'factor -> identificador | numero | ( expresion )'
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            siguienteToken(); // Consume el identificador

        } else if (tokens.get(indiceActual).getTipo().equals("NUMERO")) {
            siguienteToken(); // Consume el número

// -----------------------VALIDACION NUMERO SOLITO-----------------------
            //System.out.println(lista_numero);
            //System.out.println("VALIDAR NUMERO ="+ tokens.get(indiceActual));
            if(tokens.get(indiceActual).getTipo().equals("PUNTO_COMA") && (lista_numero.get(lista_numero.size()-1) == 'N')){
                validar_numeroSolo = true;
            }
             
            if (!validar_numeroSolo) {
            } else {
                throw new Exception("contiene un error en su gramática, el numero esta solo");
            }
            
        } else if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            lista.add('S');
            validar_parentesis = false;
            siguienteToken(); // Consume '('
            expresion(); // Analiza la expresión dentro del paréntesis
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // Consume ')'
            } else {
                throw new Exception("Se esperaba un parentesis derecho.");
            } 
            lista.add('N');
        } else {
            throw new Exception("Se esperaba un identificador, numero o un parentesis.");
        }
    }

    private void siguienteToken() {
        if (indiceActual < tokens.size()) {
            indiceActual++;
        }
    }
}