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

    private String obtenerValorDeLaLinea(String linea) {
        String[] partes = linea.split("=");
        if (partes.length == 2) {
            return partes[1].trim().replace(";", ""); // Se elimina el ;
        }
        return ""; 
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

            int inicial = i; 

            if (Character.isLetter(actual)) {
                StringBuilder identificador = new StringBuilder();
                boolean contieneNumero = false; // Para detectar si tiene un número
                boolean contieneMayuscula = false; // Para detectar si tiene una mayuscula
                int contieneMasCaracteres = 0; // Para detectar si el identificador tiene menos o igual a 12 letras 

                while (i < chars.length && (Character.isLetterOrDigit(chars[i]))) {
                    if (Character.isUpperCase(chars[i])) {
                        contieneMayuscula = true;
                    }
                    if (Character.isDigit(chars[i])) {
                        contieneNumero = true;
                    }
                    identificador.append(chars[i]);
                    i++;
                }

                contieneMasCaracteres = i - inicial;
                // Validar que no sea mayor a 12 letras
                if (contieneMasCaracteres > 12) {
                    System.out.println("Error [Fase Lexica]: La linea " + lineaActual + " contiene un error, identificador no valido, mayor a 12 letras: " + identificador.toString());
                }
                // Validar que no tenga numero
                if (contieneNumero) {
                    System.out.println("Error [Fase Lexica]: La linea " + lineaActual + " contiene un error, identificador no valido, contiene un digito: " + identificador.toString());
                }
                // Validar que no tenga mayusculas
                if (contieneMayuscula) {
                    System.out.println("Error [Fase Lexica]: La linea " + lineaActual + " contiene un error, identificador no valido, contiene una mayuscula: " + identificador.toString());
                }
                if (contieneMasCaracteres <= 12 && contieneNumero == false && contieneMayuscula == false) {
                    String id = identificador.toString();
                    
                    // Verificar si el identificador ya existe en la tabla de simbolos
                    if (!tablaSimbolos.existeSimbolo(id)) {
                        String valor = obtenerValorDeLaLinea(linea); 
                        InformacionSimbolo info = new InformacionSimbolo(lineaActual, valor); 
                        tablaSimbolos.agregarSimbolo(id, info); // Agregar a la tabla de simbolos
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

            // Se buscan de otros tokens 
            if (actual == '=') {
                tokens.add(new Token("=", "ASIGNACION"));
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
            } else if (actual == ';') {
                tokens.add(new Token(";", "PUNTO_COMA"));
                i++;
                continue;
            }

            // En caso que no se haya reconocido
            System.out.println("Error [Fase Lexica]: La linea " + lineaActual + " contiene un error, lexema no reconocido: " + actual);
            i++;
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Mi Compilador");
            return;
        }

        String archivo = args[0];
        FaseLexica analizador = new FaseLexica();

        try {
            analizador.analizarArchivo(archivo);
            for (Token token : analizador.getTokens()) {
                System.out.println(token);
            }
            analizador.tablaSimbolos.imprimirTablaSimbolos();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}
