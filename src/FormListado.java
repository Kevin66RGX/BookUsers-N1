import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.util.ArrayList;

public class FormListado extends JFrame {

    // LA LISTA LLEGA POR EL CONSTRUCTOR (es la misma que creo FormMenu)[cite: 12]
    private ArrayList<Usuario> usuarios;

    // El modelo guarda lo que la JList muestra[cite: 12]
    private DefaultListModel<Usuario> modelo = new DefaultListModel<>();

    // COMPONENTES: se dibujan en FormListado.form, aqui solo se declaran (mismo field name)[cite: 12]
    private JPanel panelPrincipal;
    private JList<Usuario> lstUsuarios;
    private JButton btnVerDetalle;
    private JButton btnEditar;
    private JButton btnBorrar;

    public FormListado(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
        setContentPane(panelPrincipal); // el panel dibujado en el .form[cite: 12]

        // CONFIGURACION DE LA VENTANA[cite: 12]
        setTitle("BookUsers · Usuarios registrados");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // cierra solo esta ventana[cite: 12]
        setLocationRelativeTo(null);

        lstUsuarios.setModel(modelo);
        lstUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recargarLista();

        // ESTETICA BLANCO CON DEGRADADOS AZULES SUAVES (SIN MARGENES EN PANEL PRINCIPAL)[cite: 12]
        estilizarVentana();

        // COMO SE DIBUJA CADA RENGLON: dos lineas con mas informacion (ver abajo)[cite: 12]
        lstUsuarios.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                // el metodo de la clase padre ya pone colores y seleccion[cite: 12]
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario u) {
                    setText(armarTexto(u, isSelected));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8)); // espacio interno del renglón[cite: 12]
                return this;
            }
        });

        // ACCIONES DE LOS BOTONES[cite: 12]
        btnVerDetalle.addActionListener(e -> verDetalle());
        btnEditar.addActionListener(e -> editarUsuario());
        btnBorrar.addActionListener(e -> borrarUsuario());
    }

    // Refresca la JList manteniendo la selección si es posible[cite: 12]
    public void recargarLista() {
        int indiceAnterior = lstUsuarios.getSelectedIndex();
        modelo.clear();
        for (Usuario u : this.usuarios) {
            modelo.addElement(u);
        }
        if (indiceAnterior >= 0 && indiceAnterior < modelo.getSize()) {
            lstUsuarios.setSelectedIndex(indiceAnterior);
        }
    }

    private void hacerPanelesHijosTransparentes(Container contenedor) {
        for (Component comp : contenedor.getComponents()) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).setOpaque(false);
            }
            if (comp instanceof JScrollPane scroll) {
                scroll.setOpaque(false);
                if (scroll.getViewport() != null) {
                    scroll.getViewport().setOpaque(false);
                }
            }
            if (comp instanceof Container) {
                hacerPanelesHijosTransparentes((Container) comp);
            }
        }
    }

    private void estilizarVentana() {
        if (panelPrincipal != null) {
            hacerPanelesHijosTransparentes(panelPrincipal);
        }

        Color textoOscuro = new Color(30, 41, 59);
        Color rojoBorrar = new Color(185, 28, 28);
        Color blancoAzulSuave = new Color(240, 247, 255);
        Color seleccionAzulSuave = new Color(219, 234, 254);

        Font fuenteLista = new Font("Segoe UI", Font.PLAIN, 13);
        Font fuenteBoton = new Font("Segoe UI", Font.BOLD, 12);

        if (lstUsuarios != null) {
            lstUsuarios.putClientProperty("JComponent.roundRect", true);
            lstUsuarios.setFont(fuenteLista);
            lstUsuarios.setBackground(Color.WHITE);
            lstUsuarios.setForeground(textoOscuro);
            lstUsuarios.setSelectionBackground(seleccionAzulSuave);
            lstUsuarios.setSelectionForeground(textoOscuro);
        }

        JButton[] botones = { btnVerDetalle, btnEditar, btnBorrar };
        for (JButton btn : botones) {
            if (btn != null) {
                btn.putClientProperty("JComponent.roundRect", true);
                btn.setFont(fuenteBoton);
                btn.setBackground(blancoAzulSuave);
                btn.setForeground(textoOscuro);
                btn.setFocusPainted(false);
            }
        }

        if (btnBorrar != null) {
            btnBorrar.setForeground(rojoBorrar);
        }
    }

    // ARMA EL TEXTO DE UN RENGLON con HTML: nombre en negrita y debajo los datos en gris.
    // No incluye la contraseña. El color cambia si el renglon esta seleccionado.[cite: 12]
    private String armarTexto(Usuario u, boolean seleccionado) {
        String colorGris = seleccionado ? "#475569" : "#666666";
        return "<html><b>" + u.getId() + " - " + html(u.getNombreCompleto()) + "</b>"
                + " &nbsp;(" + html(u.getUsername()) + ")<br>"
                + "<span style='color:" + colorGris + "'>"
                + html(u.getTipoIdentificacion()) + ": " + html(u.getNumeroIdentificacion())
                + " &nbsp;-&nbsp; " + html(u.getCorreo())
                + " &nbsp;-&nbsp; Tel: " + html(u.getTelefono())
                + "</span></html>";
    }

    // Evita que un texto con &, < o > rompa el HTML del renglon[cite: 12]
    private String html(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private Usuario obtenerUsuarioSeleccionado(String accion) {
        int indice = lstUsuarios.getSelectedIndex();
        if (indice == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario de la lista para " + accion + ".");
            return null;
        }
        return modelo.get(indice);
    }

    private void verDetalle() {
        Usuario seleccionado = obtenerUsuarioSeleccionado("ver su detalle");
        if (seleccionado == null) {
            return;
        }

        new FormDetalle(seleccionado).setVisible(true);
    }

    private void editarUsuario() {
        Usuario seleccionado = obtenerUsuarioSeleccionado("editar");
        if (seleccionado == null) {
            return;
        }

        FormRegistro formEditar = new FormRegistro(usuarios, seleccionado, this::recargarLista);
        formEditar.setVisible(true);
    }

    private void borrarUsuario() {
        Usuario seleccionado = obtenerUsuarioSeleccionado("borrar");
        if (seleccionado == null) {
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas borrar al usuario #" + seleccionado.getId()
                        + " (" + seleccionado.getNombreCompleto() + ")?",
                "Confirmar borrado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String fotoGuardada = seleccionado.getRutaFoto();
        usuarios.remove(seleccionado);
        modelo.removeElement(seleccionado);
        Almacen.guardar(usuarios, FormRegistro.getContadorId());

        if (fotoGuardada != null && !fotoGuardada.isBlank()) {
            File archivoFoto = new File(fotoGuardada);
            if (archivoFoto.exists() && archivoFoto.isFile()) {
                archivoFoto.delete();
            }
        }

        JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
    }

    private void createUIComponents() {
        panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Degradado blanco con azules suaves que combina con la interfaz[cite: 12]
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(255, 255, 255),
                        getWidth(), getHeight(), new Color(219, 234, 254)
                ));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }
}