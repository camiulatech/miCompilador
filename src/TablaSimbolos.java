import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {
    private Map<String, InformacionSimbolo> tabla;

    public TablaSimbolos() {
        tabla = new HashMap<>();
    }

    public void agregarSimbolo(String identificador, InformacionSimbolo info) {
        tabla.put(identificador, info);
    }

    public InformacionSimbolo obtenerSimbolo(String identificador) {
        return tabla.get(identificador);
    }

    public boolean existeSimbolo(String identificador) {
        return tabla.containsKey(identificador);
    }

    // Método para guardar la tabla de símbolos en un archivo
    public void imprimirTablaSimbolos(String nombreArchivo) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo));
        writer.write("Tabla de Simbolos:\n");
        for (Map.Entry<String, InformacionSimbolo> entry : tabla.entrySet()) {
            String id = entry.getKey();
            InformacionSimbolo info = entry.getValue();
            writer.write("Identificador: " + id + ", Informacion: " + info + "\n");
        }
        writer.close();
    }
}
