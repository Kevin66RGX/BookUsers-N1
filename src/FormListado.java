import javax.swing.*;
import java.util.ArrayList;

public class FormListado extends JFrame {

    // LA LISTA LLEGA POR EL CONSTRUCTOR (es la misma que creo FormMenu)
    private ArrayList<Usuario> usuarios;

    // El modelo guarda lo que la JList muestra
    private DefaultListModel<Usuario> modelo = new DefaultListModel<>();

    // COMPONENTES: se dibujan en FormListado.form, aqui solo se declaran (mismo field name)
    private JPanel panelPrincipal;
    private JList<Usuario> lstUsuarios;
    private JButton btnVerDetalle;

    public FormListado(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
        setContentPane(panelPrincipal); // el panel dibujado en el .form

        // CONFIGURACION DE LA VENTANA
        setTitle("BookUsers · Usuarios registrados");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // cierra solo esta ventana
        setLocationRelativeTo(null);

        // LLENAR LA LISTA: cada renglon usa el toString() de Usuario
        for (Usuario u : this.usuarios) {
            modelo.addElement(u);
        }
        lstUsuarios.setModel(modelo);
        lstUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ACCION DEL BOTON
        btnVerDetalle.addActionListener(e -> verDetalle());
    }

    private void verDetalle() {
        int indice = lstUsuarios.getSelectedIndex();

        // sin seleccion getSelectedIndex() devuelve -1: se avisa y no se abre nada
        if (indice == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario de la lista.");
            return;
        }

        Usuario seleccionado = modelo.get(indice);

        // TODO: cuando exista FormDetalle, reemplaza el mensaje por:
        // new FormDetalle(seleccionado).setVisible(true);
        JOptionPane.showMessageDialog(this, "Seleccionaste: " + seleccionado);
    }
}