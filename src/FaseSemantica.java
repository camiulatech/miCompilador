import java.util.Map;

public class FaseSemantica implements VisitanteSemantico {
    private Map<String, InformacionSimbolo> tablaSimbolos;

    public FaseSemantica(Map<String, InformacionSimbolo> tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
    }

    @Override
    public void visitar(NodoAsignacion nodo) {
        // Verifica que el identificador al que se le asigna esté en la tabla de simbolos
        if (!tablaSimbolos.containsKey(nodo.identificador)) {
            throw new RuntimeException("La linea " + nodo.linea + " contiene un error, no declarado identificador '" + nodo.identificador + "'");        }
        
        nodo.expresion.aceptar(this);
    }

    @Override
    public void visitar(NodoOperacionBinaria nodo) {
        // Recorre los nodos izquierdo y derecho de la operacion binaria
        nodo.izquierda.aceptar(this);
        nodo.derecha.aceptar(this);
                if (nodo.operador.equals("/")) {
            if (nodo.derecha instanceof NodoNumero) {
                NodoNumero derechoNumero = (NodoNumero) nodo.derecha;
                if (derechoNumero.valor == 0) {
                    throw new RuntimeException("La linea " + nodo.linea + " contiene una division entre cero en la expresion.");
                }
            }
        }
    }

    @Override
    public void visitar(NodoNumero nodo) {
        // numeros siempre validos
    }

    @Override
    public void visitar(NodoIdentificador nodo) {
        if (!tablaSimbolos.containsKey(nodo.nombre)) {
            throw new RuntimeException(" La linea " + nodo.linea + 
                                       " contiene un error, no declarado identificador '" + nodo.nombre + "'");
        }
    }

    @Override
    public void visitar(NodoPrograma nodo) {
        // Recorre cada declaracion
        for (NodoAST declaracion : nodo.declaraciones) {
            declaracion.aceptar(this); 
        }

    }
}
