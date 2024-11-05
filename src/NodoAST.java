abstract class NodoAST {

    // Clase abstracta de la cual van a heredar todos los tipos de nodos
    public abstract void aceptar(VisitanteSemantico visitante);

    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "|___ " : "|--- ") + this.toString());
    }
}

// Nodo tipo identificador
class NodoIdentificador extends NodoAST {
    public String nombre;
    public int linea;

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
        return "NodoIdentificador: " + nombre;
    }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        super.imprimir(prefijo, esUltimo);
    }
}

// Nodo tipo asignacion
class NodoAsignacion extends NodoAST {
    public String identificador;
    public NodoAST expresion;
    public int linea;

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
    public void imprimir(String prefijo, boolean esUltimo) {
        super.imprimir(prefijo, esUltimo);
        expresion.imprimir(prefijo + (esUltimo ? "    " : "|   "), true);
    }

    @Override
    public void aceptar(VisitanteSemantico visitante) {
        visitante.visitar(this);
    }
}

// Nodo tipo Operacion Binaria -> +, -, / y *
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
        return "NodoOperacionBinaria: " + operador;
    }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        super.imprimir(prefijo, esUltimo);
        izquierda.imprimir(prefijo + (esUltimo ? "    " : "|   "), false);
        derecha.imprimir(prefijo + (esUltimo ? "    " : "|   "), true);
    }
}

// Nodo de tipo Numero
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

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        super.imprimir(prefijo, esUltimo);
    }
}