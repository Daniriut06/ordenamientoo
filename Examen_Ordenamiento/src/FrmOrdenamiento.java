import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

import entidades.Documento;
import servicios.ServicioDocumento;
import servicios.Util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class FrmOrdenamiento extends JFrame {

    private JButton btnOrdenarBurbuja;
    private JButton btnOrdenarRapido;
    private JButton btnOrdenarInsercion;
    private JToolBar tbOrdenamiento;
    private JComboBox cmbCriterio;
    private JTextField txtTiempo;
    private JButton btnBuscar;
    private JTextField txtBuscar;

    private JTable tblDocumentos;
    private JButton btnAnterior;
    private JButton btnSiguiente;

    public FrmOrdenamiento() {

        tbOrdenamiento = new JToolBar();
        btnOrdenarBurbuja = new JButton();
        btnOrdenarInsercion = new JButton();
        btnOrdenarRapido = new JButton();
        cmbCriterio = new JComboBox();
        txtTiempo = new JTextField();

        btnBuscar = new JButton();
        txtBuscar = new JTextField();

        tblDocumentos = new JTable();

        setSize(600, 400);
        setTitle("Ordenamiento Documentos");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Declara los nuevos botones como atributos de la clase

        // Dentro del constructor, después de agregar los demás botones:
        btnAnterior = new JButton();
        btnSiguiente = new JButton();

        // Configura los botones (las imágenes las agregarás tú)
        btnAnterior.setIcon(new ImageIcon(getClass().getResource("/iconos/FlechaIzquierda.png"))); // Ajusta la ruta
        btnAnterior.setToolTipText("Anterior coincidencia");
        btnAnterior.addActionListener(e -> btnAnteriorClick());

        btnSiguiente.setIcon(new ImageIcon(getClass().getResource("/iconos/FlechaDerecha.png"))); // Ajusta la ruta
        btnSiguiente.setToolTipText("Siguiente coincidencia");
        btnSiguiente.addActionListener(e -> btnSiguienteClick());

        // Agrega los botones a la barra de herramientas
        tbOrdenamiento.add(btnAnterior);
        tbOrdenamiento.add(btnSiguiente);

        btnOrdenarBurbuja.setIcon(new ImageIcon(getClass().getResource("/iconos/Ordenar.png")));
        btnOrdenarBurbuja.setToolTipText("Ordenar Burbuja");
        btnOrdenarBurbuja.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnOrdenarBurbujaClick(evt);
            }
        });
        tbOrdenamiento.add(btnOrdenarBurbuja);

        btnOrdenarRapido.setIcon(new ImageIcon(getClass().getResource("/iconos/OrdenarRapido.png")));
        btnOrdenarRapido.setToolTipText("Ordenar Rapido");
        btnOrdenarRapido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnOrdenarRapidoClick(evt);
            }
        });
        tbOrdenamiento.add(btnOrdenarRapido);

        btnOrdenarInsercion.setIcon(new ImageIcon(getClass().getResource("/iconos/OrdenarInsercion.png")));
        btnOrdenarInsercion.setToolTipText("Ordenar Inserción");
        btnOrdenarInsercion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnOrdenarInsercionClick(evt);
            }
        });
        tbOrdenamiento.add(btnOrdenarInsercion);

        cmbCriterio.setModel(new DefaultComboBoxModel(
                new String[] { "Nombre Completo, Tipo de Documento", "Tipo de Documento, Nombre Completo" }));
        tbOrdenamiento.add(cmbCriterio);
        tbOrdenamiento.add(txtTiempo);

        btnBuscar.setIcon(new ImageIcon(getClass().getResource("/iconos/Buscar.png")));
        btnBuscar.setToolTipText("Buscar");
        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnBuscar(evt);
            }
        });
        tbOrdenamiento.add(btnBuscar);
        tbOrdenamiento.add(txtBuscar);

        var spDocumentos = new JScrollPane(tblDocumentos);

        getContentPane().add(tbOrdenamiento, BorderLayout.NORTH);
        getContentPane().add(spDocumentos, BorderLayout.CENTER);

        String nombreArchivo = System.getProperty("user.dir")
                + "/src/datos/Datos.csv";

        ServicioDocumento.cargar(nombreArchivo);
        ServicioDocumento.mostrar(tblDocumentos);
    }

    private void btnOrdenarBurbujaClick(ActionEvent evt) {
        if (cmbCriterio.getSelectedIndex() >= 0) {
            Util.iniciarCronometro();
            ServicioDocumento.ordenarBurbuja(cmbCriterio.getSelectedIndex());
            txtTiempo.setText(Util.getTextoTiempoCronometro());
            ServicioDocumento.mostrar(tblDocumentos);
        }
    }

    private void btnOrdenarRapidoClick(ActionEvent evt) {
        if (cmbCriterio.getSelectedIndex() >= 0) {
            Util.iniciarCronometro();
            ServicioDocumento.ordenarRapido(cmbCriterio.getSelectedIndex());
            txtTiempo.setText(Util.getTextoTiempoCronometro());
            ServicioDocumento.mostrar(tblDocumentos);
        }
    }

    private void btnOrdenarInsercionClick(ActionEvent evt) {
        if (cmbCriterio.getSelectedIndex() >= 0) {
            Util.iniciarCronometro();
            ServicioDocumento.ordenarInsercionRecursivo(cmbCriterio.getSelectedIndex());
            txtTiempo.setText(Util.getTextoTiempoCronometro());
            ServicioDocumento.mostrar(tblDocumentos);
        }
    }

    /*
     * private List<Integer> coincidencias; // Declara como atributo de la clase
     * private int posicionCoincidencia; // Para rastrear la posición actual
     

    private void btnBuscar(ActionEvent evt) {
        String texto = txtBuscar.getText().trim();

        if (!texto.isEmpty()) {
            Util.iniciarCronometro();
            ServicioDocumento.buscarTodasCoincidencias(texto);
            txtTiempo.setText(Util.getTextoTiempoCronometro());
            mostrarSiguienteCoincidencia();
        }
    }*/

    private void btnBuscar(ActionEvent evt) {
    String texto = txtBuscar.getText().trim();

    if (!texto.isEmpty()) {
        // Verificar si la lista está ordenada por nombre completo (criterio 0)
        if (!ServicioDocumento.estaOrdenadaPorNombreCompleto()) {
            JOptionPane.showMessageDialog(
                this, 
                "La lista no está ordenada. Por favor, ordénala primero.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return; // Detener la búsqueda si no está ordenada
        }

        Util.iniciarCronometro();
        ServicioDocumento.buscarTodasCoincidencias(texto);
        txtTiempo.setText(Util.getTextoTiempoCronometro());
        mostrarSiguienteCoincidencia();
    }
}

    private void mostrarSiguienteCoincidencia() {
        int indice = ServicioDocumento.siguienteCoincidencia();
        if (indice != -1) {
            Util.iniciarCronometro();
            tblDocumentos.setRowSelectionInterval(indice, indice);
            tblDocumentos.scrollRectToVisible(tblDocumentos.getCellRect(indice, 0, true));
            txtTiempo.setText(Util.getTextoTiempoCronometro());
        } else {
            JOptionPane.showMessageDialog(this, "No se encontraron coincidencias", "Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void btnAnteriorClick() {
        int indice = ServicioDocumento.anteriorCoincidencia();
        if (indice != -1) {
            Util.iniciarCronometro();
            tblDocumentos.setRowSelectionInterval(indice, indice);
            tblDocumentos.scrollRectToVisible(tblDocumentos.getCellRect(indice, 0, true));
            txtTiempo.setText(Util.getTextoTiempoCronometro());
        } else {
            JOptionPane.showMessageDialog(this, "No hay más coincidencias", "Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void btnSiguienteClick() {
        int indice = ServicioDocumento.siguienteCoincidencia();
        if (indice != -1) {
            tblDocumentos.setRowSelectionInterval(indice, indice);
            tblDocumentos.scrollRectToVisible(tblDocumentos.getCellRect(indice, 0, true));
        } else {
            JOptionPane.showMessageDialog(this, "No hay más coincidencias", "Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
