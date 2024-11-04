import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FaseSintacticaAST {
    private List<Token> tokens;
    private int indiceActual;
    private int lineaActual;
    private boolean validar_parentesis;
    private boolean validar_numeroSolo;
    private boolean existe_error;
    private List<Character> lista = new ArrayList<>();
    private List<Character> lista_numero = new ArrayList<>();
    List<Integer> errores_tablaSimbolos = new ArrayList<>(); // Para saber qué línea eliminar en caso de error

    public FaseSintacticaAST(List<Token> tokens) {
        this.tokens = tokens;
        this.indiceActual = 0;
        this.lineaActual = 0;
        lista.add('N'); 
        lista_numero.add('N'); 
        this.validar_parentesis = false;
        this.validar_numeroSolo = false;
        this.existe_error = false;
    }

    
    public NodoPrograma analizar() throws Exception {
        List<NodoAST> declaraciones = new ArrayList<>();
        try {
            while (indiceActual < tokens.size() && !existe_error) {
                declaraciones.add(programa());
            }
            if (!existe_error) {
                System.out.println("Se completó la fase sintáctica correctamente");
            }
            return new NodoPrograma(declaraciones);
        } catch (Exception e) {
            existe_error = true;
            eliminarErroresTablaSimbolos("tablaDeSimbolos.txt");
            System.out.println("Error [Fase Sintactica AST]: La línea " + (lineaActual) + " " + e.getMessage());
            return null;
        }
    }

    public NodoAST programa() throws Exception {
        List<NodoAST> declaraciones = new ArrayList<>();
        while (indiceActual < tokens.size()) { // Se recorre toda la línea de tokens
            lineaActual++;
            NodoAST nodoExpresion = expresion(); // Analiza una expresión
            declaraciones.add(nodoExpresion); // Agrega la expresión al árbol
    
            // Verifica que el token actual sea un punto y coma
            if (indiceActual < tokens.size() && tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                siguienteToken(); // Mueve al siguiente token
            } else {
                existe_error = true; 
                errores_tablaSimbolos.add(lineaActual);
                throw new Exception(" contiene un error en su gramática, falta token ;");
            }
        }
        return new NodoPrograma(declaraciones);
    }


    private NodoAST expresion() throws Exception {
        NodoAST nodoIzquierdo;
        
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            Token identificadorToken = tokens.get(indiceActual);
            siguienteToken();

            if (tokens.get(indiceActual).getTipo().equals("ASIGNACION")) {
                siguienteToken();
                lista_numero.add('S'); 

                NodoAST nodoExpresion = expresion();
                return new NodoAsignacion(identificadorToken.getValor(), nodoExpresion);
            } else {
                indiceActual--;
                nodoIzquierdo = termino();
                while (indiceActual < tokens.size() && (tokens.get(indiceActual).getTipo().equals("SUMA") || tokens.get(indiceActual).getTipo().equals("RESTA"))) {
                    String operador = tokens.get(indiceActual).getValor();
                    siguienteToken();
                    NodoAST nodoDerecho = termino();
                    nodoIzquierdo = new NodoOperacionBinaria(nodoIzquierdo, operador, nodoDerecho);
                }
            }
        } else {
            nodoIzquierdo = termino();
            while (indiceActual < tokens.size() && (tokens.get(indiceActual).getTipo().equals("SUMA") || tokens.get(indiceActual).getTipo().equals("RESTA"))) {
                String operador = tokens.get(indiceActual).getValor();
                siguienteToken();
                NodoAST nodoDerecho = termino();
                nodoIzquierdo = new NodoOperacionBinaria(nodoIzquierdo, operador, nodoDerecho);
            }
        }

        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER") && (lista.get(lista.size() - 1) == 'N')) {
            validar_parentesis = true;
        }

        if (validar_parentesis) {
            errores_tablaSimbolos.add(lineaActual + 1);
            existe_error = true;
            throw new Exception(" falta token '(' (paréntesis izquierdo)");
        }

        return nodoIzquierdo;
    }

    private NodoAST termino() throws Exception {
        NodoAST nodoIzquierdo = factor();
        while (indiceActual < tokens.size() && (tokens.get(indiceActual).getTipo().equals("MULTIPLICACION") || tokens.get(indiceActual).getTipo().equals("DIVISION"))) {
            String operador = tokens.get(indiceActual).getValor();
            siguienteToken();
            NodoAST nodoDerecho = factor();
            nodoIzquierdo = new NodoOperacionBinaria(nodoIzquierdo, operador, nodoDerecho);
        }
        return nodoIzquierdo;
    }

    private NodoAST factor() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            System.out.println("IDENTIFICADOR = " +tokens.get(indiceActual).getTipo());            
            Token identificadorToken = tokens.get(indiceActual);
            siguienteToken();
            return new NodoIdentificador(identificadorToken.getValor());

        } else if (tokens.get(indiceActual).getTipo().equals("NUMERO")) {
            Token numeroToken = tokens.get(indiceActual);
            siguienteToken();

            if (indiceActual < tokens.size() && tokens.get(indiceActual).getTipo().equals("NUMERO")) {
                existe_error = true;
                errores_tablaSimbolos.add(lineaActual + 1);
                throw new Exception(" contiene números consecutivos sin operador.");
            }

            if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA") && (lista_numero.get(lista_numero.size() - 1) == 'N')) {
                System.out.println("EN EL IF DE PUNTO Y COMA"+tokens.get(indiceActual).getTipo());
                System.out.println("CAE AQUI PRIMERO");
                validar_numeroSolo = true;
            }

            
            if (validar_numeroSolo) {
                errores_tablaSimbolos.add(lineaActual + 1);
                existe_error = true;
                System.out.println("CAE AQUI");
                throw new Exception(" contiene un número que está solo.");
            } 

            return new NodoNumero(Integer.parseInt(numeroToken.getValor()));

        } else if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            lista.add('S');
            validar_parentesis = false;
            siguienteToken();
            NodoAST nodoExpresion = expresion();
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken();
            } else {
                errores_tablaSimbolos.add(lineaActual + 1);
                existe_error = true;
                throw new Exception(" se esperaba un paréntesis derecho.");
            }
            lista.add('N');
            return nodoExpresion;
        } else {
            errores_tablaSimbolos.add(lineaActual + 1);
            existe_error = true;
            throw new Exception(" se esperaba un identificador, número o un paréntesis.");
        }
    }

    private void siguienteToken() {
        if (indiceActual < tokens.size()) {
            indiceActual++;
        }
    }

    public void eliminarErroresTablaSimbolos(String archivoTablaSimbolos) throws IOException {
        List<String> lineasValidas = new ArrayList<>();
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
