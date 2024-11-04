import java.util.List;

public class NodoPrograma extends NodoAST {
    List<NodoAST> declaraciones;

    public NodoPrograma(List<NodoAST> declaraciones) {
        this.declaraciones = declaraciones;
    }

    @Override
    public void aceptar(VisitanteSemantico visitante) {
        visitante.visitar(this);
    }
}