import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

// GUARDA Y CARGA LOS USUARIOS EN UN ARCHIVO JSON
// Se guarda la lista Y el siguiente id, para que los ids sigan en cadena aunque se cierre el programa.
public class Almacen {

    // El archivo queda en la carpeta del proyecto
    private static final File ARCHIVO = new File("usuarios.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Lo que se guarda en el archivo: el siguiente id y todos los usuarios
    public static class Datos {
        public int siguienteId = 1;
        public ArrayList<Usuario> usuarios = new ArrayList<>();
    }

    // GUARDAR: escribe la lista y el contador en usuarios.json
    public static void guardar(ArrayList<Usuario> usuarios, int siguienteId) {
        Datos datos = new Datos();
        datos.siguienteId = siguienteId;
        datos.usuarios = usuarios;
        try {
            Files.writeString(ARCHIVO.toPath(), GSON.toJson(datos), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("No se pudo guardar usuarios.json: " + e.getMessage());
        }
    }

    // CARGAR: lee usuarios.json. Si no existe o esta dañado, devuelve datos vacios (empieza en id 1)
    public static Datos cargar() {
        if (!ARCHIVO.exists()) {
            return new Datos();
        }
        try {
            String json = Files.readString(ARCHIVO.toPath(), StandardCharsets.UTF_8);
            Datos datos = GSON.fromJson(json, Datos.class);
            if (datos == null || datos.usuarios == null) {
                return new Datos();
            }

            // seguridad: el siguiente id nunca puede ser menor o igual al id mas alto que ya existe
            int mayorId = 0;
            for (Usuario u : datos.usuarios) {
                mayorId = Math.max(mayorId, u.getId());
            }
            if (datos.siguienteId <= mayorId) {
                datos.siguienteId = mayorId + 1;
            }
            return datos;
        } catch (Exception e) {
            System.err.println("No se pudo leer usuarios.json: " + e.getMessage());
            return new Datos();
        }
    }
}