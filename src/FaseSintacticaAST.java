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
    List<Integer> errores_tablaSimbolos = new ArrayList<>(); 

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
                System.out.println("Se completo la fase sintactica correctamente");
            }
            return new NodoPrograma(declaraciones);
        } catch (IndexOutOfBoundsException e) { 
            eliminarErroresTablaSimbolos("tablaDeSimbolos.txt");
            existe_error = true;
            errores_tablaSimbolos.add(lineaActual+1);
            throw new RuntimeException(" contiene un error en su gramatica, falta token ;");

        } catch (Exception e) {
            existe_error = true;
            eliminarErroresTablaSimbolos("tablaDeSimbolos.txt");
            throw new RuntimeException("Error [Fase Sintactica]: La linea " + (lineaActual) + e.getMessage()); 
        }
    }

    public NodoAST programa() throws Exception {
        List<NodoAST> declaraciones = new ArrayList<>();
        while (indiceActual < tokens.size()) { 
            lineaActual++;
            NodoAST nodoExpresion = expresion(); // Analiza una expresion
            declaraciones.add(nodoExpresion); // Agrega la expresion al arbol
    
            if (indiceActual < tokens.size() && tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                lista_numero.add('N');
                siguienteToken();
            } else {
                existe_error = true; 
                errores_tablaSimbolos.add(lineaActual+1);
                throw new RuntimeException(" contiene un error en su gramatica, falta token ;");
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
                return new NodoAsignacion(identificadorToken.getValor(), nodoExpresion, lineaActual);
            } else {
                indiceActual--;
            }
        }
        
        nodoIzquierdo = termino();
        while (indiceActual < tokens.size() && 
               (tokens.get(indiceActual).getTipo().equals("SUMA") || tokens.get(indiceActual).getTipo().equals("RESTA"))) {
            String operador = tokens.get(indiceActual).getValor();
            siguienteToken();
            NodoAST nodoDerecho = termino();
            nodoIzquierdo = new NodoOperacionBinaria(nodoIzquierdo, operador, nodoDerecho, lineaActual);
        }
        
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER") && (lista.get(lista.size() - 1) == 'N')) {
            validar_parentesis = true;
        }

        if (validar_parentesis) {
            errores_tablaSimbolos.add(lineaActual + 1);
            existe_error = true;
            throw new RuntimeException(" falta token '(' (parentesis izquierdo)");
        }

        return nodoIzquierdo;
    }

    private NodoAST termino() throws Exception {
        // Construye el nodo izquierdo al llamar al método factor()
        NodoAST nodoIzquierdo = factor();
        
        while (indiceActual < tokens.size() && 
            (tokens.get(indiceActual).getTipo().equals("MULTIPLICACION") || 
                tokens.get(indiceActual).getTipo().equals("DIVISION"))) {
            
            String operador = tokens.get(indiceActual).getValor();
            siguienteToken();
            
            if (indiceActual >= tokens.size()) {
                existe_error = true;
                errores_tablaSimbolos.add(lineaActual + 1);
                throw new RuntimeException("Error [Fase Sintactica]: La linea " + (lineaActual + 1) + " contiene un operador sin un termino despues de el.");
            }

            String tipoSiguienteToken = tokens.get(indiceActual).getTipo();
            if (!tipoSiguienteToken.equals("IDENTIFICADOR") && 
                !tipoSiguienteToken.equals("NUMERO") && 
                !tipoSiguienteToken.equals("PARENTESIS_IZQ")) {
                existe_error = true;
                errores_tablaSimbolos.add(lineaActual + 1);
                throw new RuntimeException("Error [Fase Sintactica]: La linea " + (lineaActual + 1) + " contiene un operador sin un termino valido despues de el.");
            }

            // Construye el nodo derecho llamando nuevamente a factor()
            NodoAST nodoDerecho = factor();
            
            // Crea un nodo binario para la operación y lo asigna como el nuevo nodo izquierdo
            nodoIzquierdo = new NodoOperacionBinaria(nodoIzquierdo, operador, nodoDerecho, lineaActual);
        }
        
        // Retorna el nodo izquierdo, que representa la raiz del subarbol de este termino
        return nodoIzquierdo;
    }


    private NodoAST factor() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            Token identificadorToken = tokens.get(indiceActual);
            siguienteToken();
            return new NodoIdentificador(identificadorToken.getValor(), lineaActual); // Devuelve un nodo identificador

        } else if (tokens.get(indiceActual).getTipo().equals("NUMERO")) {
            Token numeroToken = tokens.get(indiceActual);
            siguienteToken();

            if (indiceActual < tokens.size() && tokens.get(indiceActual).getTipo().equals("NUMERO")) {
                existe_error = true;
                errores_tablaSimbolos.add(lineaActual + 1);
                throw new RuntimeException(" contiene numeros consecutivos sin operador.");
            }

            if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA") && (lista_numero.get(lista_numero.size() - 1) == 'N')) {
                validar_numeroSolo = true;
            }

            if (validar_numeroSolo) {
                errores_tablaSimbolos.add(lineaActual + 1);
                existe_error = true;
                throw new RuntimeException(" contiene un numero que esta solo.");
            } 

            return new NodoNumero(Integer.parseInt(numeroToken.getValor())); // Devuelve un nodo numero

        } else if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            lista.add('S');
            validar_parentesis = false;
            siguienteToken();

            NodoAST nodoExpresion = expresion(); // Analiza una expresion

            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken();
            } else {
                errores_tablaSimbolos.add(lineaActual + 1);
                existe_error = true;
                throw new RuntimeException(" se esperaba un parentesis derecho.");
            }
            lista.add('N');
            return nodoExpresion; // devuelve el nodo expresion
        } else {
            errores_tablaSimbolos.add(lineaActual + 1);
            existe_error = true;
            throw new RuntimeException(" se esperaba un identificador, numero o un parentesis.");
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
