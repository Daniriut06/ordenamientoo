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

    // nuevo
    private static boolean estaOrdenadaPorNombreCompleto() {
        for (int i = 0; i < documentos.size() - 1; i++) {
            if (documentos.get(i).getNombreCompleto().compareTo(documentos.get(i + 1).getNombreCompleto()) > 0) {
                return false;
            }
        }
        return true;
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

    // nuevo

    public static int buscarCoincidencia(String texto) {
        if (documentos.isEmpty())
            return -1;

        // Forzar ordenamiento por nombre completo si no está ordenado
        if (!estaOrdenadaPorNombreCompleto()) {
            ordenarRapido(0); // Criterio 0 = Nombre completo
        }

        return busquedaBinariaRecursiva(texto.toLowerCase(), 0, documentos.size() - 1);
    }

    // nuevo

    private static int busquedaBinariaRecursiva(String texto, int inicio, int fin) {
        if (inicio > fin) {
            return -1; // No encontrado
        }

        int medio = inicio + (fin - inicio) / 2;
        Documento doc = documentos.get(medio);

        // Verificación mejorada de coincidencias
        boolean coincide = doc.getApellido1().toLowerCase().startsWith(texto) ||
                doc.getApellido2().toLowerCase().startsWith(texto) ||
                doc.getNombre().toLowerCase().startsWith(texto) ||
                doc.getNombreCompleto().toLowerCase().contains(texto);

        if (coincide) {
            // Buscar la primera ocurrencia hacia atrás
            int primera = medio;
            while (primera > inicio) {
                Documento anterior = documentos.get(primera - 1);
                if (anterior.getApellido1().toLowerCase().startsWith(texto) ||
                        anterior.getApellido2().toLowerCase().startsWith(texto) ||
                        anterior.getNombre().toLowerCase().startsWith(texto) ||
                        anterior.getNombreCompleto().toLowerCase().contains(texto)) {
                    primera--;
                } else {
                    break;
                }
            }
            return primera;
        }

        // Lógica mejorada de división de búsqueda
        String textoBusqueda = texto.toLowerCase();
        String comparacion1 = doc.getApellido1().toLowerCase();
        String comparacion2 = doc.getApellido2().toLowerCase();
        String comparacion3 = doc.getNombre().toLowerCase();
        String comparacion4 = doc.getNombreCompleto().toLowerCase();

        // Decide qué mitad buscar basado en múltiples campos
        if (textoBusqueda.compareTo(comparacion1) < 0 ||
                textoBusqueda.compareTo(comparacion2) < 0 ||
                textoBusqueda.compareTo(comparacion3) < 0 ||
                textoBusqueda.compareTo(comparacion4) < 0) {
            return busquedaBinariaRecursiva(texto, inicio, medio - 1);
        } else {
            return busquedaBinariaRecursiva(texto, medio + 1, fin);
        }
    }

    private static List<Integer> coincidencias; // Guarda los índices de las coincidencias
    private static int posicionActual; // Índice actual en la lista de coincidencias

    // Método para buscar TODAS las coincidencias (no solo la primera)

    // nuevo
    public static void buscarTodasCoincidencias(String texto) {
        coincidencias = new ArrayList<>();
        texto = texto.toLowerCase();

        // Primera búsqueda binaria para encontrar una coincidencia inicial
        int primeraCoincidencia = buscarCoincidencia(texto);

        if (primeraCoincidencia != -1) {
            // Buscar hacia atrás desde la primera coincidencia
            int i = primeraCoincidencia;
            while (i >= 0) {
                Documento doc = documentos.get(i);
                if (doc.getNombre().toLowerCase().contains(texto) ||
                        doc.getApellido1().toLowerCase().contains(texto) ||
                        doc.getApellido2().toLowerCase().contains(texto) ||
                        doc.getNombreCompleto().toLowerCase().contains(texto)) {
                    coincidencias.add(0, i); // Insertar al inicio para mantener orden
                    i--;
                } else {
                    break;
                }
            }

            // Buscar hacia adelante desde la primera coincidencia
            i = primeraCoincidencia + 1;
            while (i < documentos.size()) {
                Documento doc = documentos.get(i);
                if (doc.getNombre().toLowerCase().contains(texto) ||
                        doc.getApellido1().toLowerCase().contains(texto) ||
                        doc.getApellido2().toLowerCase().contains(texto) ||
                        doc.getNombreCompleto().toLowerCase().contains(texto)) {
                    coincidencias.add(i);
                    i++;
                } else {
                    break;
                }
            }
        }

        posicionActual = coincidencias.isEmpty() ? -1 : 0;
    }

    // Método para obtener la siguiente coincidencia
    public static int siguienteCoincidencia() {
        if (coincidencias == null || coincidencias.isEmpty() || posicionActual == -1) {
            return -1;
        }

        posicionActual++;
        if (posicionActual >= coincidencias.size()) {
            posicionActual = 0; // Vuelve al inicio si llega al final
        }

        return coincidencias.get(posicionActual);
    }

    public static int anteriorCoincidencia() {
        if (coincidencias == null || coincidencias.isEmpty() || posicionActual == -1) {
            return -1;
        }

        posicionActual--;
        if (posicionActual < 0) {
            posicionActual = coincidencias.size() - 1; // Va al final si estaba al inicio
        }

        return coincidencias.get(posicionActual);
    }
}
