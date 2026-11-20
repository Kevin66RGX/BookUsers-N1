import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;

public class FormRegistro extends JFrame {

    // --- CONSTANTES Y ESTADO COMPARTIDO ---
    private static final String CARPETA_FOTOS = "fotos";
    private static final String ARCHIVO_SIN_FOTO = "sinicon.png";
    private static final int TAM_VISTA_FOTO = 120;

    private static int contadorId = 1;

    private final ArrayList<Usuario> usuarios;
    private final Usuario usuarioEditando;
    private final Runnable onGuardado;

    private String rutaFoto = "";

    //  COMPONENTES DEL FORMULARIO (.form)
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

    // ACCESO AL CONTADOR DE ID
    public static void setContadorId(int valor) {
        contadorId = valor;
    }

    public static int getContadorId() {
        return contadorId;
    }

    // CONSTRUCTORES
    public FormRegistro(ArrayList<Usuario> usuarios) {
        this(usuarios, null, null);
    }

    public FormRegistro(ArrayList<Usuario> usuarios, Usuario usuarioEditar, Runnable onGuardado) {
        this.usuarios = usuarios;
        this.usuarioEditando = usuarioEditar;
        this.onGuardado = onGuardado;

        setContentPane(panelPrincipal);

        boolean modoEditar = (this.usuarioEditando != null);
        configurarVentana(modoEditar);
        cargarTiposIdentificacion();
        estilizarEsteticaGlass();

        if (modoEditar) {
            configurarModoEditar();
        }

        registrarEventos();
    }

    // CONFIGURACIÓN INICIAL
    private void configurarVentana(boolean modoEditar) {
        setTitle(modoEditar ? "BookUsers · Editar usuario" : "BookUsers · Registrar usuario");
        setSize(700, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void cargarTiposIdentificacion() {
        cmbTipoId.addItem("11 - Registro civil de nacimiento");
        cmbTipoId.addItem("12 - Tarjeta de identidad");
        cmbTipoId.addItem("13 - Cédula de ciudadanía");
        cmbTipoId.addItem("21 - Tarjeta de extranjería");
        cmbTipoId.addItem("22 - Cédula de extranjería");
        cmbTipoId.addItem("41 - Pasaporte");
        cmbTipoId.addItem("42 - Documento de identificación extranjero");
        cmbTipoId.addItem("47 - Permiso Especial de Permanencia (PEP)");
        cmbTipoId.addItem("91 - NUIP");
        cmbTipoId.setSelectedIndex(2);
    }

    private void registrarEventos() {
        DocumentListener oyenteUsername = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarUsername();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarUsername();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarUsername();
            }
        };

        txtPrimerNombre.getDocument().addDocumentListener(oyenteUsername);
        txtPrimerApellido.getDocument().addDocumentListener(oyenteUsername);
        txtSegundoApellido.getDocument().addDocumentListener(oyenteUsername);

        btnCargarFoto.addActionListener(e -> cargarFoto());
        btnGuardar.addActionListener(e -> guardarUsuario());
    }

    private void configurarModoEditar() {
        cambiarEncabezadoFormulario("Editar");

        if (btnGuardar != null) {
            btnGuardar.setText("Guardar cambios");
        }

        if (usuarioEditando.getTipoIdentificacion() != null) {
            cmbTipoId.setSelectedItem(usuarioEditando.getTipoIdentificacion());
        }

        txtNumeroId.setText(usuarioEditando.getNumeroIdentificacion());
        txtPrimerNombre.setText(usuarioEditando.getPrimerNombre());
        txtSegundoNombre.setText(usuarioEditando.getSegundoNombre());
        txtPrimerApellido.setText(usuarioEditando.getPrimerApellido());
        txtSegundoApellido.setText(usuarioEditando.getSegundoApellido());
        txtPassword.setText(usuarioEditando.getPassword());
        txtTelefono.setText(usuarioEditando.getTelefono());
        txtCorreo.setText(usuarioEditando.getCorreo());

        String userActual = usuarioEditando.getUsername();
        if (userActual != null && !userActual.isBlank()) {
            lblUsername.setText(userActual);
        }

        String fotoActual = usuarioEditando.getRutaFoto();
        if (fotoActual != null && !fotoActual.isBlank()) {
            File archivoActual = new File(fotoActual);
            if (archivoActual.exists() && archivoActual.isFile()) {
                lblRutaFoto.setText(archivoActual.getName());
                mostrarImagenEnVista(new ImageIcon(archivoActual.getAbsolutePath()));
            } else {
                lblRutaFoto.setText("Sin foto guardada");
                mostrarAvatarPorDefecto();
            }
        } else {
            lblRutaFoto.setText("Ningún archivo seleccionado");
            mostrarAvatarPorDefecto();
        }
    }

    //  ESTÉTICA VISUAL
    private void estilizarEsteticaGlass() {
        if (panelPrincipal != null) {
            hacerPanelesHijosTransparentes(panelPrincipal);
            panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 48, 20, 32));
        }

        Color textoOscuro = new Color(30, 41, 59);
        Color textoSuave = new Color(71, 85, 105);
        Color acentoIndigo = new Color(79, 70, 229);

        Font fuenteCampo = new Font("Segoe UI", Font.PLAIN, 13);
        Font fuenteBoton = new Font("Segoe UI", Font.BOLD, 13);

        JComponent[] camposRedondeados = {
                cmbTipoId, txtNumeroId, txtPrimerNombre, txtSegundoNombre,
                txtPrimerApellido, txtSegundoApellido, txtPassword,
                txtTelefono, txtCorreo, btnCargarFoto
        };

        for (JComponent campo : camposRedondeados) {
            if (campo != null) {
                campo.putClientProperty("JComponent.roundRect", true);
                campo.setFont(fuenteCampo);
            }
        }

        if (btnCargarFoto != null) {
            btnCargarFoto.setFont(fuenteBoton);
            btnCargarFoto.setForeground(textoOscuro);
            btnCargarFoto.setBackground(new Color(255, 255, 255, 210));
            btnCargarFoto.setFocusPainted(false);
        }

        if (btnGuardar != null) {
            btnGuardar.putClientProperty("JComponent.roundRect", true);
            btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnGuardar.setBackground(acentoIndigo);
            btnGuardar.setForeground(Color.WHITE);
            btnGuardar.setFocusPainted(false);
        }

        if (lblUsername != null) {
            lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblUsername.setForeground(acentoIndigo);
        }

        if (lblRutaFoto != null) {
            lblRutaFoto.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblRutaFoto.setForeground(textoSuave);
        }

        if (lblVistaFoto != null) {
            lblVistaFoto.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblVistaFoto.setForeground(textoSuave);
            lblVistaFoto.setHorizontalAlignment(SwingConstants.CENTER);
            lblVistaFoto.setPreferredSize(new Dimension(130, 130));
            lblVistaFoto.setMinimumSize(new Dimension(TAM_VISTA_FOTO, TAM_VISTA_FOTO));
            mostrarAvatarPorDefecto();
        }
    }

    private void cambiarEncabezadoFormulario(String nuevoTitulo) {
        if (panelPrincipal == null) {
            return;
        }
        for (Component comp : panelPrincipal.getComponents()) {
            if (comp instanceof JLabel lbl && "Añadir registro".equals(lbl.getText())) {
                lbl.setText(nuevoTitulo);
                break;
            }
        }
    }

    private void hacerPanelesHijosTransparentes(Container contenedor) {
        for (Component comp : contenedor.getComponents()) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).setOpaque(false);
            }
            if (comp instanceof Container) {
                hacerPanelesHijosTransparentes((Container) comp);
            }
        }
    }

    // --- MANEJO DE IMÁGENES ---
    private void cargarFoto() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes (png, jpg)", "png", "jpg", "jpeg"));

        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            rutaFoto = archivo.getAbsolutePath();
            lblRutaFoto.setText(archivo.getName());
            mostrarImagenEnVista(new ImageIcon(rutaFoto));
        }
    }

    private void mostrarImagenEnVista(ImageIcon icon) {
        if (lblVistaFoto == null) {
            return;
        }
        if (icon != null && icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            lblVistaFoto.setText("");
            lblVistaFoto.setIcon(escalarAltaCalidad(icon.getImage(), TAM_VISTA_FOTO, TAM_VISTA_FOTO));
        } else {
            mostrarAvatarPorDefecto();
        }
    }

    private void mostrarAvatarPorDefecto() {
        if (lblVistaFoto == null) {
            return;
        }
        ImageIcon iconoSinFoto = cargarRecursoPorDefecto(TAM_VISTA_FOTO, TAM_VISTA_FOTO);
        if (iconoSinFoto != null) {
            lblVistaFoto.setText("");
            lblVistaFoto.setIcon(iconoSinFoto);
        } else {
            lblVistaFoto.setIcon(null);
            lblVistaFoto.setText("Sin foto");
        }
        lblVistaFoto.revalidate();
        lblVistaFoto.repaint();
    }

    private ImageIcon cargarRecursoPorDefecto(int maxAncho, int maxAlto) {
        String[] rutasClasspath = {
                "/Assets/" + ARCHIVO_SIN_FOTO,
                "/" + ARCHIVO_SIN_FOTO,
                "Assets/" + ARCHIVO_SIN_FOTO,
                ARCHIVO_SIN_FOTO
        };
        for (String ruta : rutasClasspath) {
            URL url = getClass().getResource(ruta);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    return escalarAltaCalidad(icon.getImage(), maxAncho, maxAlto);
                }
            }
        }

        String[] rutasArchivo = {
                "Assets" + File.separator + ARCHIVO_SIN_FOTO,
                "src" + File.separator + "Assets" + File.separator + ARCHIVO_SIN_FOTO,
                "src" + File.separator + "main" + File.separator + "resources" + File.separator + "Assets" + File.separator + ARCHIVO_SIN_FOTO,
                ARCHIVO_SIN_FOTO
        };
        for (String ruta : rutasArchivo) {
            File f = new File(ruta);
            if (f.exists() && f.isFile()) {
                ImageIcon icon = new ImageIcon(f.getAbsolutePath());
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    return escalarAltaCalidad(icon.getImage(), maxAncho, maxAlto);
                }
            }
        }

        return null;
    }

    private ImageIcon escalarAltaCalidad(Image original, int maxAncho, int maxAlto) {
        int origW = original.getWidth(null);
        int origH = original.getHeight(null);
        if (origW <= 0 || origH <= 0) {
            return new ImageIcon(original);
        }

        double escala = Math.min((double) maxAncho / origW, (double) maxAlto / origH);
        int nuevoW = Math.max(1, (int) Math.round(origW * escala));
        int nuevoH = Math.max(1, (int) Math.round(origH * escala));

        BufferedImage destino = new BufferedImage(nuevoW, nuevoH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = destino.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(original, 0, 0, nuevoW, nuevoH, null);
        g2.dispose();

        return new ImageIcon(destino);
    }

    private String copiarFoto(int id) throws IOException {
        File origen = new File(rutaFoto);
        String nombreOriginal = origen.getName();
        int punto = nombreOriginal.lastIndexOf('.');
        String extension = (punto >= 0) ? nombreOriginal.substring(punto).toLowerCase() : ".jpg";

        File carpeta = new File(CARPETA_FOTOS);
        carpeta.mkdirs();

        File destino = new File(carpeta, id + extension);
        Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return destino.getPath();
    }

    // GENERACIÓN DE USERNAME
    private String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        String limpio = texto.trim();
        limpio = Normalizer.normalize(limpio, Normalizer.Form.NFD);
        limpio = limpio.replaceAll("\\p{M}", "");
        limpio = limpio.toLowerCase();
        limpio = limpio.replace(" ", "");
        return limpio;
    }

    private String generarUsername() {
        String nombre = limpiarTexto(txtPrimerNombre.getText());
        String apellido1 = limpiarTexto(txtPrimerApellido.getText());
        String apellido2 = limpiarTexto(txtSegundoApellido.getText());
        String doc = txtNumeroId.getText() == null ? "" : txtNumeroId.getText().trim();

        // Si falta primer nombre o primer apellido, aún no se puede armar la base
        if (nombre.isEmpty() || apellido1.isEmpty()) {
            return "";
        }

        // 1. Primera letra del primer apellido
        String username = apellido1.substring(0, 1);

        // 2. Primera letra del segundo apellido (si existe)
        if (!apellido2.isEmpty()) {
            username = username + apellido2.substring(0, 1);
        }

        // 3. Primer nombre completo ya normalizado
        username = username + nombre;

        // 4. Últimos dos caracteres del documento de identidad (si tiene al menos 2)
        if (doc.length() >= 2) {
            username = username + doc.substring(doc.length() - 2).toLowerCase();
        }

        return username;
    }

    private void actualizarUsername() {
        String username = generarUsername();
        lblUsername.setText(username.isEmpty() ? "Se genera automáticamente" : username);
    }

    // --- VALIDACIÓN Y GUARDADO ---
    private boolean esNombreValido(String valor) {
        return valor.matches("[\\p{L}]+([ '\\-][\\p{L}]+)*");
    }

    private void mostrarError(String mensaje, JComponent campo) {
        JOptionPane.showMessageDialog(this, mensaje, "Campo inválido", JOptionPane.WARNING_MESSAGE);
        if (campo != null) {
            campo.requestFocusInWindow();
        }
    }

    private void guardarUsuario() {
        String tipoId = (String) cmbTipoId.getSelectedItem();
        String numeroId = txtNumeroId.getText().trim();
        String primerNombre = txtPrimerNombre.getText().trim();
        String segundoNombre = txtSegundoNombre.getText().trim();
        String primerApellido = txtPrimerApellido.getText().trim();
        String segundoApellido = txtSegundoApellido.getText().trim();
        String password = new String(txtPassword.getPassword());
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (tipoId == null || tipoId.trim().isEmpty()) {
            mostrarError("Debes seleccionar un tipo de identificación.", cmbTipoId);
            return;
        }

        if (numeroId.isEmpty()) {
            mostrarError("El número de identificación es obligatorio.", txtNumeroId);
            return;
        }
        boolean aceptaLetras = tipoId.startsWith("41") || tipoId.startsWith("42");
        if (!aceptaLetras && !numeroId.matches("\\d+")) {
            mostrarError("El número de identificación solo debe contener números.", txtNumeroId);
            return;
        }
        if (aceptaLetras && !numeroId.matches("[A-Za-z0-9\\-]+")) {
            mostrarError("El documento extranjero/pasaporte solo debe contener letras y números.", txtNumeroId);
            return;
        }
        if (numeroId.length() < 4 || numeroId.length() > 20) {
            mostrarError("El número de identificación debe tener entre 4 y 20 caracteres.", txtNumeroId);
            return;
        }

        if (primerNombre.isEmpty()) {
            mostrarError("El primer nombre es obligatorio.", txtPrimerNombre);
            return;
        }
        if (!esNombreValido(primerNombre)) {
            mostrarError("El primer nombre no debe contener números ni símbolos inválidos.", txtPrimerNombre);
            return;
        }

        if (!segundoNombre.isEmpty() && !esNombreValido(segundoNombre)) {
            mostrarError("El segundo nombre no debe contener números ni símbolos inválidos.", txtSegundoNombre);
            return;
        }

        if (primerApellido.isEmpty()) {
            mostrarError("El primer apellido es obligatorio.", txtPrimerApellido);
            return;
        }
        if (!esNombreValido(primerApellido)) {
            mostrarError("El primer apellido no debe contener números ni símbolos inválidos.", txtPrimerApellido);
            return;
        }

        if (!segundoApellido.isEmpty() && !esNombreValido(segundoApellido)) {
            mostrarError("El segundo apellido no debe contener números ni símbolos inválidos.", txtSegundoApellido);
            return;
        }

        String usernameGenerado = generarUsername();
        if (usernameGenerado.isEmpty()) {
            mostrarError("No se pudo generar el nombre de usuario. Revisa el primer nombre y el primer apellido.", txtPrimerNombre);
            return;
        }

        if (password.isEmpty()) {
            mostrarError("La contraseña es obligatoria.", txtPassword);
            return;
        }
        if (password.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres.", txtPassword);
            return;
        }

        if (telefono.isEmpty()) {
            mostrarError("El número de teléfono es obligatorio.", txtTelefono);
            return;
        }
        if (!telefono.matches("\\d+")) {
            mostrarError("El número de teléfono solo debe contener números.", txtTelefono);
            return;
        }
        if (telefono.length() < 7 || telefono.length() > 15) {
            mostrarError("El número de teléfono debe tener entre 7 y 15 dígitos.", txtTelefono);
            return;
        }

        if (correo.isEmpty()) {
            mostrarError("El correo electrónico es obligatorio.", txtCorreo);
            return;
        }
        if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarError("Ingresa un correo electrónico válido (ejemplo: usuario@correo.com).", txtCorreo);
            return;
        }

        boolean modoEditar = (usuarioEditando != null);
        int idObjetivo = modoEditar ? usuarioEditando.getId() : contadorId;

        String rutaFotoGuardada = modoEditar ? usuarioEditando.getRutaFoto() : "";
        if (!rutaFoto.isEmpty()) {
            try {
                rutaFotoGuardada = copiarFoto(idObjetivo);
            } catch (IOException ex) {
                mostrarError("No se pudo copiar la foto: " + ex.getMessage(), btnCargarFoto);
                return;
            }
        }

        if (modoEditar) {
            usuarioEditando.setTipoIdentificacion(tipoId);
            usuarioEditando.setNumeroIdentificacion(numeroId);
            usuarioEditando.setPrimerNombre(primerNombre);
            usuarioEditando.setSegundoNombre(segundoNombre);
            usuarioEditando.setPrimerApellido(primerApellido);
            usuarioEditando.setSegundoApellido(segundoApellido);
            usuarioEditando.setUsername(usernameGenerado);
            usuarioEditando.setPassword(password);
            usuarioEditando.setTelefono(telefono);
            usuarioEditando.setCorreo(correo);
            usuarioEditando.setRutaFoto(rutaFotoGuardada);

            Almacen.guardar(usuarios, contadorId);
            if (onGuardado != null) {
                onGuardado.run();
            }
            JOptionPane.showMessageDialog(this, "Usuario actualizado: " + usuarioEditando.getUsername());
            dispose();
        } else {
            Usuario nuevo = new Usuario(contadorId, tipoId, numeroId,
                    primerNombre, segundoNombre, primerApellido, segundoApellido,
                    usernameGenerado, password, telefono, correo, rutaFotoGuardada);
            contadorId++;
            usuarios.add(nuevo);
            Almacen.guardar(usuarios, contadorId);

            JOptionPane.showMessageDialog(this, "Usuario registrado: " + nuevo.getUsername());
            limpiarFormulario();
        }
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
        mostrarAvatarPorDefecto();
    }

    //  CREACIÓN DE COMPONENTES PERSONALIZADOS (.form)
    private void createUIComponents() {
        panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                g2.setPaint(new GradientPaint(
                        0, 0, new Color(224, 231, 255),
                        getWidth(), getHeight(), new Color(224, 242, 254)
                ));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }
}