interface VisitanteSemantico {

    void visitar(NodoPrograma nodo);
    void visitar(NodoAsignacion nodo);
    void visitar(NodoOperacionBinaria nodo);
    void visitar(NodoIdentificador nodo);
    void visitar(NodoNumero nodo);
    
}