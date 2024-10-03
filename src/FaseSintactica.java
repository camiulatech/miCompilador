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
                declaracion();
            }
        } catch (Exception e) {
            System.out.println("Error de análisis sintáctico: " + e.getMessage());
        }
    }

    private void declaracion() throws Exception {
        // Suponiendo que una declaración tiene la forma: IDENTIFICADOR = EXPRESION;
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            Token identificador = tokens.get(indiceActual);
            siguienteToken(); // Consumir el identificador

            if (tokens.get(indiceActual).getTipo().equals("ASIGNACION")) {
                siguienteToken(); // Consumir el signo '='

                // Aquí llamamos a la función para analizar la expresión
                expresion();

                if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                    siguienteToken(); // Consumir el punto y coma
                    System.out.println("Declaración válida: " + identificador.getValor());
                } else {
                    throw new Exception("Se esperaba un punto y coma al final de la declaración.");
                }
            } else {
                throw new Exception("Se esperaba un signo de asignación.");
            }
        } else {
            throw new Exception("Se esperaba un identificador.");
        }
    }

    private void expresion() throws Exception {
        // Suponiendo que una expresión es un término seguido de operadores
        termino();

        while (indiceActual < tokens.size() && 
               (tokens.get(indiceActual).getTipo().equals("SUMA") || tokens.get(indiceActual).getTipo().equals("RESTA"))) {
            siguienteToken(); // Consumir el operador
            termino(); // Consumir el siguiente término
        }
    }

    private void termino() throws Exception {
        // Suponiendo que un término es un número o un identificador
        if (tokens.get(indiceActual).getTipo().equals("NUMERO") || tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            siguienteToken(); // Consumir el número o identificador
        } else {
            throw new Exception("Se esperaba un número o un identificador.");
        }
    }

    private void siguienteToken() {
        if (indiceActual < tokens.size()) {
            indiceActual++;
        }
    }
}
