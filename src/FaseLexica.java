import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FaseLexica {
    private List<Token> tokens = new ArrayList<>();
    private TablaSimbolos tablaSimbolos = new TablaSimbolos();
    private int lineaActual = 1;

    public void analizarArchivo(String archivo) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        String linea;

        while ((linea = reader.readLine()) != null) {
            analizarLinea(linea);
            lineaActual++;
        }
        reader.close();
    }

    private void analizarLinea(String linea) {
        char[] chars = linea.toCharArray();
        int i = 0;

        while (i < chars.length) {
            char actual = chars[i];

            if (Character.isWhitespace(actual)) {
                i++;
                continue;
            }

            if (Character.isLetter(actual)) {
                StringBuilder identificador = new StringBuilder();
                boolean contieneNumero = false; // Para detectar si el identificador tiene un número

                while (i < chars.length && (Character.isLetterOrDigit(chars[i]))) {
                    if (Character.isDigit(chars[i])) {
                        contieneNumero = true;
                    }
                    identificador.append(chars[i]);
                    i++;
                }

                if (contieneNumero) {
                    System.out.println("Error [Fase Lexica]: La linea " + lineaActual + " contiene un error, identificador no válido: " + identificador.toString());
                } else {
                    String id = identificador.toString();
                    
                    // Verificar si el identificador ya existe en la tabla de símbolos
                    if (!tablaSimbolos.existeSimbolo(id)) {
                        InformacionSimbolo info = new InformacionSimbolo(lineaActual); // Crear nueva información del símbolo
                        tablaSimbolos.agregarSimbolo(id, info); // Agregar a la tabla de símbolos
                    }

                    tokens.add(new Token(id, "IDENTIFICADOR"));
                }
                continue;
            }

            if (Character.isDigit(actual)) {
                StringBuilder numero = new StringBuilder();
                while (i < chars.length && Character.isDigit(chars[i])) {
                    numero.append(chars[i]);
                    i++;
                }
                tokens.add(new Token(numero.toString(), "NUMERO"));
                continue;
            }

            // Manejo de otros tokens (., =, +, -, *, /, etc.)
            if (actual == '=') {
                tokens.add(new Token("=", "ASIGNACION"));
                i++;
                continue;
            } else if (actual == ';') {
                tokens.add(new Token(";", "PUNTO_COMA"));
                i++;
                continue;
            } else if (actual == '+') {
                tokens.add(new Token("+", "SUMA"));
                i++;
                continue;
            } else if (actual == '-') {
                tokens.add(new Token("-", "RESTA"));
                i++;
                continue;
            } else if (actual == '*') {
                tokens.add(new Token("*", "MULTIPLICACION"));
                i++;
                continue;
            } else if (actual == '/') {
                tokens.add(new Token("/", "DIVISION"));
                i++;
                continue;
            } else if (actual == '(') {
                tokens.add(new Token("(", "PARENTESIS_IZQ"));
                i++;
                continue;
            } else if (actual == ')') {
                tokens.add(new Token(")", "PARENTESIS_DER"));
                i++;
                continue;
            }

            // Si llegamos aquí, el carácter no se ha reconocido
            System.out.println("Error [Fase Lexica]: La linea " + lineaActual + " contiene un error, lexema no reconocido: " + actual);
            i++;
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java FaseLexica [ARCHIVO DE ENTRADA]");
            return;
        }

        String archivo = args[0];
        FaseLexica analizador = new FaseLexica();

        try {
            analizador.analizarArchivo(archivo);
            for (Token token : analizador.getTokens()) {
                System.out.println(token);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}
