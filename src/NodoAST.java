abstract class NodoAST {

    public abstract void aceptar(VisitanteSemantico visitante);

        // Método para imprimir el árbol
        public void imprimir(String prefijo) {
            System.out.println(prefijo + this.toString()); // Imprime el tipo de nodo
        }
}

class NodoIdentificador extends NodoAST {
    String nombre;

    public NodoIdentificador(String nombre) {
        this.nombre = nombre;
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
    String identificador;
    NodoAST expresion;

    public NodoAsignacion(String identificador, NodoAST expresion) {
        this.identificador = identificador;
        this.expresion = expresion;
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

    public NodoOperacionBinaria(NodoAST izquierda, String operador, NodoAST derecha) {
        this.izquierda = izquierda;
        this.operador = operador;
        this.derecha = derecha;
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
