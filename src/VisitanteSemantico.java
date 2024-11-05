/*  Esta interfaz sigue el patron de diseño visitor, este patron de diseño de comportamiento
permite separar algoritmos de los objetos sobre los que operan */

interface VisitanteSemantico {
    void visitar(NodoPrograma nodo);
    void visitar(NodoAsignacion nodo);
    void visitar(NodoOperacionBinaria nodo);
    void visitar(NodoIdentificador nodo);
    void visitar(NodoNumero nodo);
    
}