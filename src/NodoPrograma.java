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
    public void imprimir(String prefijo, boolean esUltimo) {
        super.imprimir(prefijo, esUltimo);
        for (int i = 0; i < declaraciones.size(); i++) {
            declaraciones.get(i).imprimir(prefijo + (esUltimo ? "    " : "|   "), i == declaraciones.size() - 1);
        }
    }

    @Override
    public String toString() {
        return "NodoPrograma";
    }
}