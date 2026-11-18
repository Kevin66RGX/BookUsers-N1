import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FormMenu extends JFrame {

    // LA UNICA LISTA DE LA APLICACION: se crea aqui y se le pasa a las demas ventanas <KEVIN>
    private ArrayList<Usuario> usuarios = new ArrayList<>();

    // COMPONENTES <KEVIN>
    private JLabel lblTitulo;
    private JPanel panelBotones;
    private JButton btnRegistrar;
    private JButton btnVerUsuarios;
    private JButton btnCreditos;

    public FormMenu() {

        // CONFIGURACION DE LA VENTANA <KEVIN>
        // TODO: cambia por tus nombres y apellidos completos y los de tu pareja
        setTitle("BookUsers · Kevin Rojas y Nombre Chacón");
        setSize(1440, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // solo el menu cierra todo el programa
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // TITULO <KEVIN>
        lblTitulo = new JLabel("BOOK USERS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(13, 110, 253));
        add(lblTitulo, BorderLayout.NORTH);

        // BOTONES <KEVIN>
        panelBotones = new JPanel(new GridLayout(1, 3, 10, 10));

        btnRegistrar = crearBoton("REGISTRAR UN NUEVO USUARIO",
                "Permite ingresar y almacenar la información de un nuevo usuario en la base de datos del sistema.");
        btnVerUsuarios = crearBoton("VISUALIZAR Y EDITAR REGISTROS",
                "Permite consultar los usuarios registrados, actualizar sus datos o eliminar registros existentes.");
        btnCreditos = crearBoton("CREDITOS",
                "Muestra información sobre los desarrolladores del proyecto, la versión del sistema y los agradecimientos correspondientes.");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnVerUsuarios);
        panelBotones.add(btnCreditos);
        add(panelBotones, BorderLayout.CENTER);

        // ACCIONES DE LOS BOTONES <KEVIN>
        // Registrar: abre FormRegistro y le pasa LA MISMA lista
        btnRegistrar.addActionListener(e -> {
            new FormRegistro(usuarios).setVisible(true);
        });

        // TODO: cuando exista FormListado, reemplaza el mensaje por:
        // new FormListado(usuarios).setVisible(true);
        btnVerUsuarios.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "FormListado todavía no está hecho.");
        });

        // TODO: cuando exista FormCreditos, reemplaza el mensaje por:
        // new FormCreditos().setVisible(true);
        btnCreditos.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "FormCreditos todavía no está hecho.");
        });
    }

    // METODO AYUDANTE: crea un boton con titulo y descripcion (asi no repetimos el HTML 3 veces) <KEVIN>
    private JButton crearBoton(String titulo, String descripcion) {
        JButton boton = new JButton("<html><div style='text-align: center; padding: 10px'>" +
                "<span style='font-size: 16px; font-weight: bold;'>" + titulo + "</span><br><br>" +
                "<span style='font-size: 13px; color: #555555;'>" + descripcion + "</span>" +
                "</div></html>");
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return boton;
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new FormMenu().setVisible(true);
        });
    }
}