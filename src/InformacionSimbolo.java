public class InformacionSimbolo {
    private int lineaDeclaracion;  // Línea donde se declaró el identificador
    private String valor;          // Valor asociado al identificador

    public InformacionSimbolo(int lineaDeclaracion, String valor) {
        this.lineaDeclaracion = lineaDeclaracion;
        this.valor = valor;
    }

    public int getLineaDeclaracion() {
        return lineaDeclaracion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Linea: " + lineaDeclaracion + ", Valor: " + valor;
    }
}
