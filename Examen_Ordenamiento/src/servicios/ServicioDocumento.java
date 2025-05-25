package servicios;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import entidades.Documento;

public class ServicioDocumento {

    private static List<Documento> documentos = new ArrayList<>();

    public static void cargar(String nombreArchivo) {
        var br = Archivo.abrirArchivo(nombreArchivo);
        if (br != null) {
            try {
                var linea = br.readLine();
                linea = br.readLine();
                while (linea != null) {
                    var textos = linea.split(";");
                    var documento = new Documento(textos[0], textos[1], textos[2], textos[3]);
                    documentos.add(documento);
                    linea = br.readLine();
                }
            } catch (Exception ex) {

            }
        }
    }

    public static void mostrar(JTable tbl) {
        String[] encabezados = new String[] { "#", "Primer Apellido", "Segundo Apellido", "Nombres", "Documento" };
        String[][] datos = new String[documentos.size()][encabezados.length];

        int fila = 0;
        for (var documento : documentos) {
            datos[fila][0] = String.valueOf(fila + 1);
            datos[fila][1] = documento.getApellido1();
            datos[fila][2] = documento.getApellido2();
            datos[fila][3] = documento.getNombre();
            datos[fila][4] = documento.getDocumento();
            fila++;
        }

        var dtm = new DefaultTableModel(datos, encabezados);
        tbl.setModel(dtm);

    }

    private static boolean esMayor(Documento d1, Documento d2, int criterio) {
        if (criterio == 0) {
            return d1.getNombreCompleto().compareTo(d2.getNombreCompleto()) > 0 ||
                    (d1.getNombreCompleto().equals(d2.getNombreCompleto()) &&
                            d1.getDocumento().compareTo(d2.getDocumento()) > 0);
        } else {
            return d1.getDocumento().compareTo(d2.getDocumento()) > 0 ||
                    (d1.getDocumento().equals(d2.getDocumento()) &&
                            d1.getNombreCompleto().compareTo(d2.getNombreCompleto()) > 0);
        }
    }

    private static void intercambiar(int origen, int destino) {
        if (0 <= origen && origen < documentos.size() &&
                0 <= destino && destino < documentos.size()) {
            var temporal = documentos.get(origen);
            documentos.set(origen, documentos.get(destino));
            documentos.set(destino, temporal);
        }
    }

    public static void ordenarBurbuja(int criterio) {
        for (int i = 0; i < documentos.size() - 1; i++) {
            for (int j = i + 1; j < documentos.size(); j++) {
                System.out.println(
                        "d[i]=" + documentos.get(i).getNombreCompleto() + " " + documentos.get(i).getDocumento());
                System.out.println(
                        "d[j]=" + documentos.get(j).getNombreCompleto() + " " + documentos.get(j).getDocumento());

                if (esMayor(documentos.get(i), documentos.get(j), criterio)) {
                    intercambiar(i, j);
                }
            }
        }
    }

    private static int getPivote(int inicio, int fin, int criterio) {
        var pivote = inicio;
        var documentoPivote = documentos.get(pivote);

        for (int i = inicio + 1; i <= fin; i++) {
            if (esMayor(documentoPivote, documentos.get(i), criterio)) {
                pivote++;
                if (i != pivote) {
                    intercambiar(i, pivote);
                }
            }
        }
        if (inicio != pivote) {
            intercambiar(inicio, pivote);
        }

        return pivote;
    }

    private static void ordenarRapido(int inicio, int fin, int criterio) {
        if (fin > inicio) {
            var pivote = getPivote(inicio, fin, criterio);
            ordenarRapido(inicio, pivote - 1, criterio);
            ordenarRapido(pivote + 1, fin, criterio);
        }
    }

    public static void ordenarRapido(int criterio) {
        ordenarRapido(0, documentos.size() - 1, criterio);
    }

    public static void ordenarInsercion(int criterio) {
        for (int i = 1; i < documentos.size(); i++) {
            var documentoActual = documentos.get(i);
            // mover los documentos mayores que el actual
            int j = i - 1;
            while (j >= 0 && esMayor(documentos.get(j), documentoActual, criterio)) {
                documentos.set(j + 1, documentos.get(j));
                j--;
            }
            // insertar el documento
            documentos.set(j + 1, documentoActual);
        }
    }

    private static void ordenarInsercionRecursivo(int posicion, int criterio) {
        if (posicion == 0) {
            return;
        }
        ordenarInsercionRecursivo(posicion - 1, criterio);

        var documentoActual = documentos.get(posicion);
        // System.out.println(documentoActual.getNombreCompleto());
        // mover los documentos mayores que el actual
        int j = posicion - 1;
        while (j >= 0 && esMayor(documentos.get(j), documentoActual, criterio)) {
            // System.out.println(documentos.get(j));
            documentos.set(j + 1, documentos.get(j));
            j--;
        }
        // insertar el documento
        documentos.set(j + 1, documentoActual);
    }

    public static void ordenarInsercionRecursivo(int criterio) {
        ordenarInsercionRecursivo(documentos.size() - 1, criterio);
    }

    public static List<Documento> getDocumentos() {
        return documentos;
    }

    // Agrega este nuevo método para búsqueda parcial
    public static List<Documento> buscarParcial(String texto) {
        List<Documento> resultados = new ArrayList<>();
        texto = texto.toLowerCase().trim();

        for (Documento doc : documentos) {
            if (doc.getApellido1().toLowerCase().contains(texto) ||
                    doc.getApellido2().toLowerCase().contains(texto) ||
                    doc.getNombre().toLowerCase().contains(texto) ||
                    doc.getNombreCompleto().toLowerCase().contains(texto)) {
                resultados.add(doc);
            }
        }
        return resultados;
    }

    public static int buscarCoincidencia(String texto) {
        if (documentos.isEmpty()) {
            return -1;
        }
        texto = texto.toLowerCase();
        // Asegúrate de que la lista esté ordenada por nombre completo (criterio 0)

        return busquedaBinariaRecursiva(texto, 0, documentos.size() - 1);
    }

    private static int busquedaBinariaRecursiva(String texto, int inicio, int fin) {
        if (inicio > fin) {
            return -1; // No encontrado
        }

        int medio = inicio + (fin - inicio) / 2;
        Documento doc = documentos.get(medio);

        // Busca en nombre, apellido1 y apellido2
        String nombreCompleto = doc.getNombreCompleto().toLowerCase();
        String apellido1 = doc.getApellido1().toLowerCase();
        String apellido2 = doc.getApellido2().toLowerCase();
        String nombre = doc.getNombre().toLowerCase();

        // Verifica si el texto coincide con el inicio de cualquier campo
        if (nombreCompleto.startsWith(texto) ||
                apellido1.startsWith(texto) ||
                apellido2.startsWith(texto) ||
                nombre.startsWith(texto)) {

            // Retrocede para encontrar la primera coincidencia (por si hay duplicados)
            int primeraOcurrencia = medio;
            while (primeraOcurrencia > inicio) {
                Documento docAnterior = documentos.get(primeraOcurrencia - 1);
                String nombreCompletoAnterior = docAnterior.getNombreCompleto().toLowerCase();
                String apellido1Anterior = docAnterior.getApellido1().toLowerCase();
                String apellido2Anterior = docAnterior.getApellido2().toLowerCase();
                String nombreAnterior = docAnterior.getNombre().toLowerCase();

                if (nombreCompletoAnterior.startsWith(texto) ||
                        apellido1Anterior.startsWith(texto) ||
                        apellido2Anterior.startsWith(texto) ||
                        nombreAnterior.startsWith(texto)) {
                    primeraOcurrencia--;
                } else {
                    break;
                }
            }
            return primeraOcurrencia;
        }

        // Compara con nombreCompleto para decidir la búsqueda en mitades
        if (texto.compareTo(nombreCompleto) < 0) {
            return busquedaBinariaRecursiva(texto, inicio, medio - 1); // Mitad izquierda
        } else {
            return busquedaBinariaRecursiva(texto, medio + 1, fin); // Mitad derecha
        }
    }

    private static List<Integer> coincidencias; // Guarda los índices de las coincidencias
    private static int posicionActual; // Índice actual en la lista de coincidencias

    // Método para buscar TODAS las coincidencias (no solo la primera)
public static void buscarTodasCoincidencias(String texto) {
    coincidencias = new ArrayList<>();
    texto = texto.toLowerCase();
    for (int i = 0; i < documentos.size(); i++) {
        Documento doc = documentos.get(i);
        if (doc.getNombreCompleto().toLowerCase().startsWith(texto)) {
            coincidencias.add(i);
        }
    }
    posicionActual = 0; // Inicia en la primera coincidencia
}

    // Método para obtener la siguiente coincidencia
    public static int siguienteCoincidencia() {
        if (coincidencias == null || coincidencias.isEmpty())
            return -1;
        posicionActual = (posicionActual + 1) % coincidencias.size(); // Avanza circularmente
        return coincidencias.get(posicionActual);
    }

    // Método para obtener la anterior coincidencia
    public static int anteriorCoincidencia() {
        if (coincidencias == null || coincidencias.isEmpty())
            return -1;
        posicionActual = (posicionActual - 1 + coincidencias.size()) % coincidencias.size(); // Retrocede circularly
        return coincidencias.get(posicionActual);
    }
}
