public class InformacionSimbolo {
    private int lineaDeclaracion;  // Línea donde se declaró el identificador

    public InformacionSimbolo(int lineaDeclaracion) {
        this.lineaDeclaracion = lineaDeclaracion;
    }

    public int getLineaDeclaracion() {
        return lineaDeclaracion;
    }

    @Override
    public String toString() {
        return "Linea: " + lineaDeclaracion;
    }
}
