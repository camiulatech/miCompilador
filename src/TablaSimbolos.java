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

    public void imprimirTablaSimbolos() {
        System.out.println("Tabla de Simbolos:");
        for (Map.Entry<String, InformacionSimbolo> entry : tabla.entrySet()) {
            String id = entry.getKey();
            InformacionSimbolo info = entry.getValue();
            System.out.println("Identificador: " + id + ", Informacion: " + info);
        }
        
    }
}
