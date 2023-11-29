import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;
import java.util.RandomAccess;

public class Serializador {

    // Directorio del archivo principal
    private final String directorio = "src/friendContact.txt";
    // Directorio del archivo temporal
    private final String directorioTemp = "src/tempfile.txt";

    /**
     * Agrega un nuevo amigo al archivo.
     *
     * @param nombre Nombre del amigo.
     * @param numero Número del amigo.
     * @return true si se agrega correctamente, false si ya existe.
     * @throws IOException Si hay un problema de entrada/salida.
     */
    public boolean agregarAmigo(String nombre, String numero) throws IOException {
        // Definimos las variables que usaremos para acceder al archivo
        String linea, nuevaLinea;
        File archivo = new File(directorio);

        // Comprobamos si el archivo existe, sino se crea
        crearArchivo(archivo);

        // Creamos un acceso al archivo en modo escritura y lectura
        RandomAccessFile accesoArchivo = new RandomAccessFile(archivo, "rw");

        // Comprobamos si el registro existe y retornamos falso, ya que no se puede crear
        if (existeRegistro(nombre, numero, accesoArchivo)) {
            return false;
        }

        // Creamos el registro y lo almacenamos en el archivo para luego cerrar el acceso
        nuevaLinea = nombre + "!" + numero;
        accesoArchivo.writeBytes(nuevaLinea);
        accesoArchivo.writeBytes(System.lineSeparator());
        accesoArchivo.close();
        return true;
    }

    /**
     * Elimina a un amigo del archivo.
     *
     * @param nombre Nombre del amigo a eliminar.
     * @return true si se elimina correctamente, false si no existe.
     * @throws IOException Si hay un problema de entrada/salida.
     */
    public boolean eliminarAmigo(String nombre) throws IOException {
        String linea;

        File archivo = new File(directorio);
        // Comprobamos si el archivo existe, sino se crea
        crearArchivo(archivo);

        // Creamos un acceso al archivo en modo escritura y lectura
        RandomAccessFile accesoArchivo = new RandomAccessFile(archivo, "rw");

        // Si no existe el amigo, cerramos el acceso y retornamos falso
        if (!existeRegistro(nombre, "", accesoArchivo)) {
            accesoArchivo.close();
            return false;
        }

        // Creamos el archivo temporal
        File archivoTemporal = new File(directorioTemp);
        // Comprobamos si el archivo temporal existe, sino se crea
        crearArchivo(archivoTemporal);

        // Creamos un acceso al archivo temporal en modo escritura y lectura
        RandomAccessFile accesoTemporal = new RandomAccessFile(archivoTemporal, "rw");

        // Iteramos sobre el archivo principal
        while (accesoArchivo.getFilePointer() < accesoArchivo.length()) {
            linea = accesoArchivo.readLine();

            String[] partesLinea = linea.split("!");

            // Si la línea coincide con el amigo a eliminar, la omitimos
            if (partesLinea.length == 2 && nombre.equals(partesLinea[0])) {
                continue;
            }

            // Escribimos la línea en el archivo temporal con un carácter de nueva línea
            accesoTemporal.writeBytes(linea + System.lineSeparator());
        }

        // Cerramos ambos archivos
        accesoArchivo.close();
        accesoTemporal.close();

        // Eliminamos el archivo original
        if (!archivo.delete()) {
            // Manejamos el caso donde no se pudo eliminar el archivo original
            return false;
        }

        // Renombramos el archivo temporal al nombre del archivo original
        if (!archivoTemporal.renameTo(archivo)) {
            // Manejamos el caso donde no se pudo renombrar el archivo temporal
            return false;
        }

        return true;
    }

    /**
     * Obtiene la lista de amigos almacenados en el archivo.
     *
     * @return Una lista de arrays de String donde cada array representa un amigo con sus datos.
     * @throws IOException Si hay un problema de entrada/salida.
     */
    public ArrayList<String[]> listarAmigos() throws IOException {
        ArrayList<String[]> amigos = new ArrayList<>();
        String linea;
        File archivo = new File(directorio);
        crearArchivo(archivo);
        RandomAccessFile accesoArchivo  = new RandomAccessFile(archivo, "r");

        // Iteramos sobre el archivo principal
        while (accesoArchivo.getFilePointer() < accesoArchivo.length()) {
            linea = accesoArchivo.readLine();
            amigos.add(linea.split("!"));
        }

        accesoArchivo.close();
        return amigos;
    }

    /**
     * Actualiza la información de un amigo en el archivo.
     *
     * @param nombreAntiguo Nombre antiguo del amigo.
     * @param nuevoNombre Nuevo nombre del amigo.
     * @param nuevoNumero Nuevo número del amigo.
     * @return true si la actualización se realiza correctamente, false si el amigo no existe.
     * @throws IOException Si hay un problema de entrada/salida.
     */
    public boolean actualizarAmigo(String nombreAntiguo, String nuevoNombre, String nuevoNumero) throws IOException {
        String linea;

        File archivo = new File(directorio);
        crearArchivo(archivo);

        RandomAccessFile accesoArchivo = new RandomAccessFile(archivo, "rw");

        // Creamos el archivo temporal
        File archivoTemporal = new File(directorioTemp);
        crearArchivo(archivoTemporal);

        RandomAccessFile accesoTemporal = new RandomAccessFile(archivoTemporal, "rw");

        // Iteramos sobre el archivo principal
        while (accesoArchivo.getFilePointer() < accesoArchivo.length()) {
            linea = accesoArchivo.readLine();

            String[] partesLinea = linea.split("!");

            // Si la línea coincide con el amigo a actualizar, la modificamos
            if (partesLinea.length == 2 && nombreAntiguo.equals(partesLinea[0])) {
                // Actualizamos la información del amigo con el nuevo nombre y número
                String nuevaLinea = nuevoNombre + "!" + nuevoNumero;
                accesoTemporal.writeBytes(nuevaLinea+ System.lineSeparator());
            } else {
                // Escribimos la línea original en el archivo temporal con un carácter de nueva línea
                accesoTemporal.writeBytes(linea + System.lineSeparator());
            }
        }

        // Cerramos ambos archivos
        accesoArchivo.close();
        accesoTemporal.close();

        // Eliminamos el archivo original
        if (!archivo.delete()) {
            return false;
        }

        // Renombramos el archivo temporal al nombre del archivo original
        if (!archivoTemporal.renameTo(archivo)) {
            return false;
        }

        return true;
    }

    /**
     * Crea el archivo si no existe.
     *
     * @param archivo Archivo a verificar y crear si es necesario.
     * @throws IOException Si hay un problema de entrada/salida.
     */
    private void crearArchivo(File archivo) throws IOException {
        if (!archivo.exists()) {
            archivo.createNewFile();
        }
    }

    /**
     * Verifica si un registro (amigo) existe en el archivo.
     *
     * @param nombre Nombre del amigo.
     * @param numero Número del amigo.
     * @param accesoArchivo Acceso al archivo para realizar la verificación.
     * @return true si el registro existe, false si no.
     * @throws IOException Sí hay un problema de entrada/salida.
     */
    private boolean existeRegistro(String nombre, String numero, RandomAccessFile accesoArchivo) throws IOException {
        String linea;

        while (accesoArchivo.getFilePointer() < accesoArchivo.length()) {
            linea = accesoArchivo.readLine();

            String[] partesLinea = linea.split("!");

            if (partesLinea.length == 2 && (nombre.equals(partesLinea[0]) || numero.equals(partesLinea[1]))) {
                return true;
            }
        }
        return false;
    }

    public String obtenerTelefono(String nombre) {
        try {
            File archivo = new File("src/friendContact.txt");
            RandomAccessFile accesoArchivo = new RandomAccessFile(archivo, "r");
            String linea;
            while ((linea = accesoArchivo.readLine()) != null) {
                String[] datos = linea.split("!");
                if (datos.length >= 2 && datos[0].equalsIgnoreCase(nombre)) {
                    accesoArchivo.close();
                    return datos[1];
                }
            }
            accesoArchivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
