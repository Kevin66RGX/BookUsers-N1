import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.util.ArrayList;

public class FormMenu extends JFrame {

    // LA UNICA LISTA DE LA APLICACION: se crea aqui y se le pasa a las demas ventanas <KEVIN>
    private ArrayList<Usuario> usuarios = new ArrayList<>();

    // COMPONENTES: se dibujan en FormMenu.form, aqui solo se declaran (mismo field name) <KEVIN>
    private JPanel panelPrincipal;
    private JButton btnRegistrar;
    private JButton btnVerUsuarios;
    private JButton btnCreditos;
    private JLabel BookUsers;

    public FormMenu() {
        setContentPane(panelPrincipal); // el panel dibujado en el .form

        // CONFIGURACION DE LA VENTANA <KEVIN>
        // TODO: cambia por tus nombres y apellidos completos y los de tu pareja
        setTitle("BookUsers · Kevin Rojas y Nombre Chacón");
        setSize(1440, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // solo el menu cierra todo el programa
        setLocationRelativeTo(null);

        // ACCIONES DE LOS BOTONES <KEVIN>
        // Registrar: abre FormRegistro y le pasa LA MISMA lista
        btnRegistrar.addActionListener(e -> {
            new FormRegistro(usuarios).setVisible(true);
        });

        // Ver usuarios: abre FormListado y le pasa LA MISMA lista
        btnVerUsuarios.addActionListener(e -> {
            new FormListado(usuarios).setVisible(true);
        });

        // TODO: cuando exista FormCreditos, reemplaza el mensaje por:
        // new FormCreditos().setVisible(true);
        btnCreditos.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "FormCreditos todavía no está hecho.");
        });
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new FormMenu().setVisible(true);
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
