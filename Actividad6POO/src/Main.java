import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        // Crear una instancia de Serializador
        Serializador serializador = new Serializador();

        // Agregar dos amigos
        boolean amigo1Agregado = serializador.agregarAmigo("Amigo1", "123");
        boolean amigo2Agregado = serializador.agregarAmigo("Amigo2", "456");

        if (amigo1Agregado) {
            System.out.println("Amigo1 agregado correctamente.");
        } else {
            System.out.println("Amigo1 ya existe.");
        }

        if (amigo2Agregado) {
            System.out.println("Amigo2 agregado correctamente.");
        } else {
            System.out.println("Amigo2 ya existe.");
        }


        // Actualizar un amigo
        boolean amigoActualizado = serializador.actualizarAmigo("Amigo2", "NuevoNombre", "789");

        if (amigoActualizado) {
            System.out.println("Amigo2 actualizado correctamente.");
        } else {
            System.out.println("Amigo2 no encontrado o no pudo ser actualizado.");
        }

        // Listar amigos
        ArrayList<String[]> listaAmigos = serializador.listarAmigos();

        System.out.println("Lista de amigos:");
        for (String[] amigo : listaAmigos) {
            System.out.println("Nombre: " + amigo[0] + ", Número: " + amigo[1]);
        }
    }
}
