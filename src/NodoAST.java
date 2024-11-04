import java.util.List;

abstract class NodoAST {
    public abstract void aceptar(VisitanteSemantico visitante);
}

class NodoPrograma extends NodoAST {
    List<NodoAST> declaraciones;

    public NodoPrograma(List<NodoAST> declaraciones) {
        this.declaraciones = declaraciones;
    }

    @Override
    public void aceptar(VisitanteSemantico visitante) {
        visitante.visitar(this);
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
}

class NodoNumero extends NodoAST {
    int valor;

    public NodoNumero(int valor) {
        this.valor = valor;
    }

    @Override
    public void aceptar(VisitanteSemantico visitante) {
        visitante.visitar(this);
    }
}
