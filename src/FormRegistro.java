import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;

public class FormRegistro extends JFrame {

    // CONTADOR DE ID: solo crece, nunca se calcula con usuarios.size() <KEVIN>
    // Es static para que lo compartan todas las ventanas de registro que se abran
    private static int contadorId = 1;

    // LA LISTA LLEGA POR EL CONSTRUCTOR (es la misma que creo FormMenu) <KEVIN>
    private ArrayList<Usuario> usuarios;

    // RUTA DE LA FOTO ELEGIDA <KEVIN>
    private String rutaFoto = "";

    // COMPONENTES <KEVIN>
    private JPanel panelPrincipal;
    private JComboBox<String> cmbTipoId;
    private JTextField txtNumeroId;
    private JTextField txtPrimerNombre;
    private JTextField txtSegundoNombre;
    private JTextField txtPrimerApellido;
    private JTextField txtSegundoApellido;
    private JLabel lblUsername;
    private JPasswordField txtPassword;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JButton btnCargarFoto;
    private JLabel lblRutaFoto;
    private JLabel lblVistaFoto;
    private JButton btnGuardar;

    public FormRegistro(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;

        // CONFIGURACION DE LA VENTANA <KEVIN>
        setTitle("BookUsers · Registrar usuario");
        setSize(750, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // cierra solo esta ventana
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        panelPrincipal = new JPanel(new GridLayout(12, 2, 5, 5));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // TIPO DE IDENTIFICACION (tabla DIAN) <KEVIN>
        panelPrincipal.add(new JLabel("Tipo de identificación:"));
        cmbTipoId = new JComboBox<>(new String[]{
                "11 - Registro civil de nacimiento",
                "12 - Tarjeta de identidad",
                "13 - Cédula de ciudadanía",
                "21 - Tarjeta de extranjería",
                "22 - Cédula de extranjería",
                "41 - Pasaporte",
                "42 - Documento de identificación extranjero",
                "47 - Permiso Especial de Permanencia (PEP)",
                "91 - NUIP"
        });
        cmbTipoId.setSelectedIndex(2); // arranca en Cédula de ciudadanía
        panelPrincipal.add(cmbTipoId);

        // NUMERO DE IDENTIFICACION <KEVIN>
        panelPrincipal.add(new JLabel("Número de identificación:"));
        txtNumeroId = new JTextField();
        panelPrincipal.add(txtNumeroId);

        // NOMBRE COMPLETO <KEVIN>
        panelPrincipal.add(new JLabel("Primer nombre:"));
        txtPrimerNombre = new JTextField();
        panelPrincipal.add(txtPrimerNombre);

        panelPrincipal.add(new JLabel("Segundo nombre (opcional):"));
        txtSegundoNombre = new JTextField();
        panelPrincipal.add(txtSegundoNombre);

        panelPrincipal.add(new JLabel("Primer apellido:"));
        txtPrimerApellido = new JTextField();
        panelPrincipal.add(txtPrimerApellido);

        panelPrincipal.add(new JLabel("Segundo apellido (opcional):"));
        txtSegundoApellido = new JTextField();
        panelPrincipal.add(txtSegundoApellido);

        // NOMBRE DE USUARIO (GENERADO) <KEVIN>
        panelPrincipal.add(new JLabel("Nombre de usuario (GENERADO):"));
        lblUsername = new JLabel("Se genera automáticamente");
        panelPrincipal.add(lblUsername);

        // CONTRASEÑA <KEVIN>
        panelPrincipal.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panelPrincipal.add(txtPassword);

        // TELEFONO <KEVIN>
        panelPrincipal.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panelPrincipal.add(txtTelefono);

        // CORREO <KEVIN>
        panelPrincipal.add(new JLabel("Correo electrónico:"));
        txtCorreo = new JTextField();
        panelPrincipal.add(txtCorreo);

        // FOTO <KEVIN>
        panelPrincipal.add(new JLabel("Fotografía:"));
        JPanel panelFoto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCargarFoto = new JButton("Buscar foto");
        lblRutaFoto = new JLabel("Ningún archivo seleccionado");
        panelFoto.add(btnCargarFoto);
        panelFoto.add(lblRutaFoto);
        panelPrincipal.add(panelFoto);

        // BOTON GUARDAR <KEVIN>
        panelPrincipal.add(new JLabel("")); // espacio vacio para alinear
        btnGuardar = new JButton("Registrar usuario");
        panelPrincipal.add(btnGuardar);

        add(panelPrincipal, BorderLayout.CENTER);

        // VISTA PREVIA DE LA FOTO (a la derecha) <KEVIN>
        lblVistaFoto = new JLabel("Sin foto", SwingConstants.CENTER);
        lblVistaFoto.setPreferredSize(new Dimension(140, 140));
        lblVistaFoto.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JPanel panelVista = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        panelVista.add(lblVistaFoto);
        add(panelVista, BorderLayout.EAST);

        // USERNAME EN VIVO: el DocumentListener avisa cada vez que cambia el texto <KEVIN>
        DocumentListener oyenteUsername = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { actualizarUsername(); }
            @Override
            public void removeUpdate(DocumentEvent e) { actualizarUsername(); }
            @Override
            public void changedUpdate(DocumentEvent e) { actualizarUsername(); }
        };
        txtPrimerNombre.getDocument().addDocumentListener(oyenteUsername);
        txtPrimerApellido.getDocument().addDocumentListener(oyenteUsername);
        txtSegundoApellido.getDocument().addDocumentListener(oyenteUsername);

        // ACCIONES DE LOS BOTONES <KEVIN>
        btnCargarFoto.addActionListener(e -> cargarFoto());
        btnGuardar.addActionListener(e -> guardarUsuario());
    }

    // QUITA TILDES, Ñ, ESPACIOS Y PASA A MINUSCULAS <KEVIN>
    private String limpiarTexto(String texto) {
        String limpio = texto.trim();
        // NFD separa la letra de su tilde (ñ queda como n + ~); luego borramos las tildes sueltas
        limpio = Normalizer.normalize(limpio, Normalizer.Form.NFD);
        limpio = limpio.replaceAll("\\p{M}", "");
        limpio = limpio.toLowerCase();
        limpio = limpio.replace(" ", "");
        return limpio;
    }

    // GENERA EL USERNAME: 1ra letra apellido1 + 1ra letra apellido2 + primer nombre <KEVIN>
    private String generarUsername() {
        String nombre = limpiarTexto(txtPrimerNombre.getText());
        String apellido1 = limpiarTexto(txtPrimerApellido.getText());
        String apellido2 = limpiarTexto(txtSegundoApellido.getText());

        // si falta el nombre o el primer apellido todavia no se puede armar
        if (nombre.isEmpty() || apellido1.isEmpty()) {
            return "";
        }

        String username = apellido1.substring(0, 1);
        if (!apellido2.isEmpty()) {
            username = username + apellido2.substring(0, 1);
        }
        username = username + nombre;
        return username;
    }

    private void actualizarUsername() {
        String username = generarUsername();
        if (username.isEmpty()) {
            lblUsername.setText("Se genera automáticamente");
        } else {
            lblUsername.setText(username);
        }
    }

    // CARGAR FOTO: selector de archivos + vista previa escalada <KEVIN>
    private void cargarFoto() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes (png, jpg)", "png", "jpg", "jpeg"));

        int resultado = selector.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            rutaFoto = archivo.getAbsolutePath();
            lblRutaFoto.setText(archivo.getName());

            // escalamos la foto a 120x120 para que quepa en la ventana
            ImageIcon original = new ImageIcon(rutaFoto);
            Image escalada = original.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            lblVistaFoto.setText("");
            lblVistaFoto.setIcon(new ImageIcon(escalada));
        }
    }

    // GUARDAR: valida, crea el Usuario, lo agrega a la lista y limpia <KEVIN>
    private void guardarUsuario() {
        String tipoId = (String) cmbTipoId.getSelectedItem();
        String numeroId = txtNumeroId.getText().trim();
        String primerNombre = txtPrimerNombre.getText().trim();
        String segundoNombre = txtSegundoNombre.getText().trim();
        String primerApellido = txtPrimerApellido.getText().trim();
        String segundoApellido = txtSegundoApellido.getText().trim();
        // JPasswordField no tiene getText(): se usa getPassword() que devuelve char[]
        String password = new String(txtPassword.getPassword());
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        // VALIDACIONES <KEVIN>
        if (numeroId.isEmpty() || primerNombre.isEmpty() || primerApellido.isEmpty()
                || password.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Faltan campos obligatorios.");
            return;
        }

        // solo dígitos, excepto pasaporte (41) y documento extranjero (42)
        boolean aceptaLetras = tipoId.startsWith("41") || tipoId.startsWith("42");
        if (!aceptaLetras && !numeroId.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El número de identificación solo debe tener dígitos.");
            return;
        }

        if (!correo.contains("@")) {
            JOptionPane.showMessageDialog(this, "El correo debe contener @.");
            return;
        }

        // CREAR EL USUARIO con el id que asigna el programa <KEVIN>
        Usuario nuevo = new Usuario(contadorId, tipoId, numeroId,
                primerNombre, segundoNombre, primerApellido, segundoApellido,
                generarUsername(), password, telefono, correo, rutaFoto);
        contadorId++;
        usuarios.add(nuevo);

        JOptionPane.showMessageDialog(this, "Usuario registrado: " + nuevo.getUsername());
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        cmbTipoId.setSelectedIndex(2);
        txtNumeroId.setText("");
        txtPrimerNombre.setText("");
        txtSegundoNombre.setText("");
        txtPrimerApellido.setText("");
        txtSegundoApellido.setText("");
        txtPassword.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        rutaFoto = "";
        lblRutaFoto.setText("Ningún archivo seleccionado");
        lblVistaFoto.setIcon(null);
        lblVistaFoto.setText("Sin foto");
        // lblUsername se reinicia solo: al vaciar los campos se dispara el DocumentListener
    }
}