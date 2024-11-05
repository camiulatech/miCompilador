import java.util.List;

// Nodo inicial tipo programa
public class NodoPrograma extends NodoAST {
    List<NodoAST> declaraciones;

    public NodoPrograma(List<NodoAST> declaraciones) {
        this.declaraciones = declaraciones;
    }

    @Override
    public void aceptar(VisitanteSemantico visitante) {
        visitante.visitar(this);
    }

    @Override
    public void imprimir(String prefijo) {
        super.imprimir(prefijo);
        for (NodoAST declaracion : declaraciones) {
            declaracion.imprimir(prefijo + "  "); 
        }
    }

    @Override
    public String toString() {
        return "NodoPrograma";
    }
}