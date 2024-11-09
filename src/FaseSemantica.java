import java.util.Map;

public class FaseSemantica implements VisitanteSemantico {
    private Map<String, InformacionSimbolo> tablaSimbolos;
    private boolean error;

    public FaseSemantica(Map<String, InformacionSimbolo> tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
        this.error = false;
    }

    public boolean correcto(){
        if (!error) {
            System.out.println("Se completo la Fase Semantica correctamente");
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void visitar(NodoAsignacion nodo) {
        try {
            // Verifica que el identificador al que se le asigna este en la tabla de simbolos
            if (!tablaSimbolos.containsKey(nodo.identificador)) {
                throw new RuntimeException("La linea " + nodo.linea + " contiene un error: identificador no declarado '" + nodo.identificador + "'");
            }
            nodo.expresion.aceptar(this);
        } catch (RuntimeException e) {
            error = true;
            System.out.println("Error [Fase Semantica]: " + e.getMessage());
        }
    }
    
    @Override
    public void visitar(NodoOperacionBinaria nodo) {
        try {
            // Recorre los nodos izquierdo y derecho de la operación binaria
            nodo.izquierda.aceptar(this);
            nodo.derecha.aceptar(this);
    
            if (nodo.operador.equals("/")) {
                if (nodo.derecha instanceof NodoNumero) {
                    NodoNumero derechoNumero = (NodoNumero) nodo.derecha;
                    if (derechoNumero.valor == 0) {
                        throw new RuntimeException("La linea " + nodo.linea + " contiene una division entre cero.");
                    }
                } else if (nodo.derecha instanceof NodoIdentificador) {
                    NodoIdentificador derechoIdentificador = (NodoIdentificador) nodo.derecha;
                    InformacionSimbolo infoSimbolo = tablaSimbolos.get(derechoIdentificador.nombre);
                    
                    if (infoSimbolo != null && infoSimbolo.getValor().equals("0")) {
                        throw new RuntimeException("La linea " + nodo.linea + " contiene una division entre cero usando el identificador '" + derechoIdentificador.nombre + "'.");
                    }
                }
            }
        } catch (RuntimeException e) {
            error = true;
            System.out.println("Error [Fase Semantica]: " + e.getMessage());
        }
    }
    

    @Override
    public void visitar(NodoNumero nodo) {
        // numeros siempre validos
    }

    @Override
    public void visitar(NodoIdentificador nodo) {
        try {
            if (!tablaSimbolos.containsKey(nodo.nombre)) {
                throw new RuntimeException("La linea " + nodo.linea + " contiene un error: identificador no declarado '" + nodo.nombre + "'");
            }
        } catch (RuntimeException e) {
            error = true;
            System.out.println("Error [Fase Semantica]: " + e.getMessage());
        }
    }

    @Override
    public void visitar(NodoPrograma nodo) {
        try {
            // Recorre cada declaración
            for (NodoAST declaracion : nodo.declaraciones) {
                declaracion.aceptar(this);
            }
        } catch (RuntimeException e) {
            error = true;
            System.out.println("Error [Fase Semantica]: " + e.getMessage());
        }
    }
}
