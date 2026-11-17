import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class AppPrincipal extends JFrame {

    public AppPrincipal() {

        // CONFIGURACION DE LA VENTANA <KEVIN>
        setTitle("Sistema de Registros - Kevin y Chacón");
        setSize(1440, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // TITULO <KEVIN>
        setLayout(new BorderLayout());
        JLabel Lbtitulo = new JLabel("BOOK USERS", SwingConstants.CENTER);
        Lbtitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        Lbtitulo.setForeground(new Color(13, 110, 253));
        add(Lbtitulo, BorderLayout.NORTH);;

        // BOTONES <KEVIN>
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(1, 3, 10, 10));

        //BOTON 1 <IVAN>
        JButton btnRegistrar = new JButton("<html><div style='text-aling: center; padding: 10px'>" +
                "<span style='font-size: 16px; font-weight: bold;'>REGISTRAR UN NUEVO USUSARIO</span><br><br>" +
                "<span style='font-size: 13px; color: #555555; '>Permite ingresar y almacenar la informacion de un nuevo usuario en la base de datos del sistema.</span>" +
                "</div></html>");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));



        //BOTON 2 <IVAN>
        JButton btnVerUsuarios = new JButton("<html><div style='text-aling: center; padding: 10px'>" +
                "<span style='font-size: 16px; font-weight: bold; '>VISUALIZAR Y EDITAR REGISTROS</span><br><br>" +
                "<span style='font-size: 13px; color : #555555; '>Permite consultar los usuarios registrados, actualizar sus datos o eliminar registros existentes.</span>" +
                "</div></html>" );
        btnVerUsuarios.setFont(new Font("Segoe UI", Font.BOLD, 14));

        //BOTON 3 <IVAN>
        JButton btnCreditos = new JButton("<html><div syle0'text-aling: center; padding: 10px'>" +
                "<span style='font-size: 16px; font-wight: bold; '>CREDITOS</span><br><br>" +
                "<span style='font-size: 13px; color : #555555; '>Muestra nformacion sobre los desarrolladores del proyecto, la version del sistema y los agradecimientos correspondientes.</span>" +
                "</div></html>");
        btnCreditos.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnVerUsuarios);
        panelBotones.add(btnCreditos);

        add(panelBotones, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new AppPrincipal().setVisible(true);
        });
    }
}