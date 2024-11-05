abstract class NodoAST {

    public abstract void aceptar(VisitanteSemantico visitante);

        // Método para imprimir el árbol
        public void imprimir(String prefijo) {
            System.out.println(prefijo + this.toString()); // Imprime el tipo de nodo
        }
}

class NodoIdentificador extends NodoAST {
    public String nombre;
    public int linea;  // Campo para la línea del nodo

    public NodoIdentificador(String nombre, int linea) {
        this.nombre = nombre;
        this.linea = linea;
    }

    @Override
    public void aceptar(VisitanteSemantico visitante) {
        visitante.visitar(this);
    }

    @Override
    public String toString() {
        return "NodoIdentificador: " + nombre; // Muestra el nombre del identificador
    }

    @Override
    public void imprimir(String prefijo) {
        super.imprimir(prefijo); // Llama al método de impresión en NodoAST
    }
}

class NodoAsignacion extends NodoAST {
    public String identificador;
    public NodoAST expresion;
    public int linea;  // Campo para almacenar la línea del nodo

    public NodoAsignacion(String identificador, NodoAST expresion, int linea) {
        this.identificador = identificador;
        this.expresion = expresion;
        this.linea = linea;
    }

    @Override
    public String toString() {
        return "NodoAsignacion: " + identificador;
    }

    @Override
    public void imprimir(String prefijo) {
        super.imprimir(prefijo);
        expresion.imprimir(prefijo + "  "); // Imprime la expresión asignada
    }

    @Override
    public void aceptar(VisitanteSemantico visitante) {
        visitante.visitar(this);
    }
}

class NodoOperacionBinaria extends NodoAST {
    NodoAST izquierda;
    NodoAST derecha;
    String operador;
    int linea;

    public NodoOperacionBinaria(NodoAST izquierda, String operador, NodoAST derecha, int linea) {
        this.izquierda = izquierda;
        this.operador = operador;
        this.derecha = derecha;
        this.linea = linea;
    }

    @Override
    public void aceptar(VisitanteSemantico visitante) {
        visitante.visitar(this);
    }

    @Override
    public String toString() {
        return "NodoOperacionBinaria: " + operador; // Muestra el operador
    }

    @Override
    public void imprimir(String prefijo) {
        super.imprimir(prefijo); // Llama al método de impresión en NodoAST
        izquierda.imprimir(prefijo + "  "); // Imprime la subexpresión izquierda
        derecha.imprimir(prefijo + "  "); // Imprime la subexpresión derecha
    }
}

class NodoNumero extends NodoAST {
    int valor;

    public NodoNumero(int valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "NodoNumero: " + valor;
    }    

    @Override
    public void aceptar(VisitanteSemantico visitante) {
        visitante.visitar(this);
    }


}
