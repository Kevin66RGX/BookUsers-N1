import javax.swing.*;
import java.awt.*;

public class FormRegistro extends JPanel {

    // COMPONENTES <KEVIN>
    private int id;
    private JComboBox<String> TipoIdentificacion;
    private JTextField NumeroIdentificacion;
    private JTextField PrimerNombre;
    private JTextField SeguntoNombre;
    private JTextField PrimerApellido;
    private JTextField SegundoApellido;
    private JLabel UserName;
    private JPasswordField Password;
    private JTextField Telefono;
    private JTextField Correo;

    private JButton CargarFoto;
    private JLabel RutaFoto;
    private JButton GuardarFoto;

    public FormRegistro(){
        setLayout(new GridLayout(12, 2, 5, 5));

        // TIPO DE IDENTIFICACION <KEVIN>
        add(new JLabel("Tipo de Identificación"));
        TipoIdentificacion = new JComboBox<>(new String[]{"Cedula de ciudadania", "Tarjeta de identidad" , "Pasaporte" , "Cedula de Extranjeria"});
        add(TipoIdentificacion);

        // NUMERO DE IDENTIFICACION <KEVIN>
        add(new JLabel("Numeri de identificacion:"));
        NumeroIdentificacion = new JTextField();
        add(NumeroIdentificacion);

        //NOMBRE COMPLETO
        add(new JLabel("Primer nombre"));
        PrimerNombre = new JTextField();
        add(PrimerNombre);

        //COMPLETA CHACON

        //NOMBRE DE USUARIO
        add(new JLabel("Nombre de usuario (GENERADO):"));
        UserName = new JLabel("Se genera automaticamente");
        add(UserName);

        //CONTRASEÑA
        add(new JPasswordField("Contraseña"));
        Password = new JPasswordField();
        add(Password);

        // TELEFONO
        add(new JLabel("Telefono:"));
        Telefono = new JTextField();
        add (Telefono);

        //CORREO
        add (new JLabel("Correo electronico: "));
        Correo = new JTextField();
        add (Correo);

        //FOTO
        add(new JLabel("Fotografía:"));
        JPanel panelFoto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        CargarFoto = new JButton("Buscar Foto");
        RutaFoto = new JLabel("Ningún archivo seleccionado");
        panelFoto.add(CargarFoto);
        panelFoto.add(RutaFoto);
        add(panelFoto);
        
        //BOTON GUARDADO
        add(new JLabel("")); // Espacio vacío para alinear
        GuardarFoto = new JButton("Registrar Usuario");
        add(GuardarFoto);
    }

}
