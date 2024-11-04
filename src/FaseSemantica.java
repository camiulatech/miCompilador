import java.util.Map;

public class FaseSemantica implements VisitanteSemantico {
    private Map<String, InformacionSimbolo> tablaSimbolos;

    public FaseSemantica(Map<String, InformacionSimbolo> tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
    }

    @Override
    public void visitar(NodoAsignacion nodo) {
        // Verifica que el identificador al que se le asigna esté en la tabla de símbolos
        if (!tablaSimbolos.containsKey(nodo.identificador)) {
            throw new RuntimeException("Error: Identificador " + nodo.identificador + " no declarado.");
        }
        
        // Recorre la expresión para verificar que todos los identificadores dentro estén definidos
        nodo.expresion.aceptar(this);
    }

    @Override
    public void visitar(NodoOperacionBinaria nodo) {
        // Recorre los nodos izquierdo y derecho de la operación binaria
        nodo.izquierda.aceptar(this);
        nodo.derecha.aceptar(this);
        
        // Verifica si la operación es una división por cero
        if (nodo.operador.equals("/")) {
            if (nodo.derecha instanceof NodoNumero) {
                NodoNumero derechoNumero = (NodoNumero) nodo.derecha;
                if (derechoNumero.valor == 0) {
                    throw new RuntimeException("Error: División por cero en expresión.");
                }
            }
        }
    }

    @Override
    public void visitar(NodoNumero nodo) {
        // No se requiere verificación para números; siempre son válidos.
    }

    @Override
    public void visitar(NodoIdentificador nodo) {
        // Verifica que el identificador esté en la tabla de símbolos
        if (!tablaSimbolos.containsKey(nodo.nombre)) {
            throw new RuntimeException("Error: Identificador " + nodo.nombre + " no declarado.");
        }
    }

    @Override
    public void visitar(NodoPrograma nodo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitar'");
    }
}
