import java.util.List;

public class FaseSintactica {
    private List<Token> tokens;
    private int indiceActual;

    public FaseSintactica(List<Token> tokens) {
        this.tokens = tokens;
        this.indiceActual = 0;
    }

    public void analizar() {
        try {
            while (indiceActual < tokens.size()) {
                programa(); // Comienza con la producción 'programa'
            }
        } catch (Exception e) {
            System.out.println("Error de análisis sintáctico: " + e.getMessage());
        }
    }

    private void programa() throws Exception {
        // La estructura es 'expresion ; { programa }'
        while (indiceActual < tokens.size()) {
            expresion(); // Analiza una expresión

            if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                siguienteToken(); // Consume el punto y coma
            } else {
                throw new Exception("Se esperaba un punto y coma al final de la expresión.");
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
        } else if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // Consume '('
            expresion(); // Analiza la expresión dentro del paréntesis
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // Consume ')'
            } else {
                throw new Exception("Se esperaba un paréntesis derecho.");
            }
        } else {
            throw new Exception("Se esperaba un identificador, número o un paréntesis.");
        }
    }

    private void siguienteToken() {
        if (indiceActual < tokens.size()) {
            indiceActual++;
        }
    }
}