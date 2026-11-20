import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class FormDetalle extends JFrame {

    private static final String ARCHIVO_SIN_FOTO = "sinicon.png";

    private final Usuario usuario;
    private final JPanel tarjeta;

    private static final Color ORO = new Color(205, 174, 125);
    private static final Color ORO_CLARO = new Color(232, 211, 175);
    private static final Color BLANCO = new Color(245, 245, 247);
    private static final Color GRIS = new Color(155, 158, 168);

    public FormDetalle(Usuario usuario) {
        this.usuario = usuario;

        setTitle("BookUsers | " + usuario.getUsername());
        setSize(700, 460);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(new Color(224, 228, 234));
        fondo.setBorder(BorderFactory.createEmptyBorder(24, 28, 14, 28));

        tarjeta = crearTarjeta();
        fondo.add(tarjeta, BorderLayout.CENTER);

        JButton exportar = new JButton("Exportar PDF");
        exportar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exportar.setForeground(new Color(30, 41, 59));
        exportar.setBackground(new Color(250, 250, 252));
        exportar.setFocusPainted(false);
        exportar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exportar.setPreferredSize(new Dimension(115, 34));
        exportar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 174, 125)),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        exportar.addActionListener(e -> exportarPdf());

        JButton cerrar = new JButton("Cerrar");
        cerrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cerrar.setForeground(new Color(40, 42, 48));
        cerrar.setBackground(new Color(250, 250, 252));
        cerrar.setFocusPainted(false);
        cerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cerrar.setPreferredSize(new Dimension(100, 34));
        cerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 213, 219)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        cerrar.addActionListener(e -> dispose());

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        inferior.setOpaque(false);
        inferior.add(exportar);
        inferior.add(cerrar);

        fondo.add(inferior, BorderLayout.SOUTH);

        setContentPane(fondo);
    }

    private void exportarPdf() {
        int w = tarjeta.getWidth();
        int h = tarjeta.getHeight();
        if (w <= 0 || h <= 0) {
            w = 644;
            h = 335;
        }

        JFileChooser selector = new JFileChooser();
        String user = (usuario.getUsername() != null && !usuario.getUsername().isBlank())
                ? usuario.getUsername().trim()
                : "usuario_" + usuario.getId();
        selector.setSelectedFile(new File("credencial_" + user + ".pdf"));

        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File destino = selector.getSelectedFile();
        if (!destino.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            destino = new File(destino.getAbsolutePath() + ".pdf");
        }

        try {
            int escala = 2;
            int imgW = w * escala;
            int imgH = h * escala;

            BufferedImage captura = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = captura.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.scale(escala, escala);
            tarjeta.printAll(g2);
            g2.dispose();

            BufferedImage rgb = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
            Graphics2D gRgb = rgb.createGraphics();
            gRgb.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gRgb.setColor(new Color(18, 19, 23));
            gRgb.fillRect(0, 0, imgW, imgH);

            Shape clipTarjeta = new RoundRectangle2D.Float(
                    2f * escala,
                    2f * escala,
                    (w - 4f) * escala,
                    (h - 6f) * escala,
                    28f * escala,
                    28f * escala
            );
            gRgb.setClip(clipTarjeta);
            gRgb.drawImage(captura, 0, 0, null);
            gRgb.setClip(null);
            gRgb.dispose();

            ByteArrayOutputStream jpgOut = new ByteArrayOutputStream();
            ImageIO.write(rgb, "jpg", jpgOut);
            byte[] jpgBytes = jpgOut.toByteArray();

            escribirPdf(destino, w, h, imgW, imgH, jpgBytes);

            JOptionPane.showMessageDialog(
                    this,
                    "PDF guardado en:\n" + destino.getAbsolutePath(),
                    "Exportar PDF",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al generar el PDF:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void escribirPdf(File destino, int pageW, int pageH, int imgW, int imgH, byte[] jpgBytes)
            throws IOException {
        String streamCmd = String.format(Locale.US, "q %d 0 0 %d 0 0 cm /Im0 Do Q\n", pageW, pageH);
        byte[] streamBytes = streamCmd.getBytes(StandardCharsets.ISO_8859_1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int[] offsets = new int[6];

        writeAscii(out, "%PDF-1.4\n");

        offsets[1] = out.size();
        writeAscii(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        offsets[2] = out.size();
        writeAscii(out, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        offsets[3] = out.size();
        writeAscii(out, String.format(
                Locale.US,
                "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 %d %d] /Resources << /XObject << /Im0 4 0 R >> >> /Contents 5 0 R >>\nendobj\n",
                pageW, pageH
        ));

        offsets[4] = out.size();
        writeAscii(out, String.format(
                Locale.US,
                "4 0 obj\n<< /Type /XObject /Subtype /Image /Width %d /Height %d /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length %d >>\nstream\n",
                imgW, imgH, jpgBytes.length
        ));
        out.write(jpgBytes);
        writeAscii(out, "\nendstream\nendobj\n");

        offsets[5] = out.size();
        writeAscii(out, String.format(Locale.US, "5 0 obj\n<< /Length %d >>\nstream\n", streamBytes.length));
        out.write(streamBytes);
        writeAscii(out, "endstream\nendobj\n");

        int xrefPos = out.size();
        writeAscii(out, "xref\n0 6\n0000000000 65535 f \n");
        for (int i = 1; i <= 5; i++) {
            writeAscii(out, String.format(Locale.US, "%010d 00000 n \n", offsets[i]));
        }

        writeAscii(out, String.format(
                Locale.US,
                "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n%d\n%%%%EOF\n",
                xrefPos
        ));

        try (FileOutputStream fos = new FileOutputStream(destino)) {
            out.writeTo(fos);
        }
    }

    private void writeAscii(ByteArrayOutputStream out, String text) throws IOException {
        out.write(text.getBytes(StandardCharsets.ISO_8859_1));
    }


    // TARJETA
    private JPanel crearTarjeta() {

        JPanel card = new JPanel(new BorderLayout()) {

            //Para que java dibuje la tarjeta con mis peticiones y no con su diseño por defecto
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth();
                int h = getHeight();
                int arc = 28;

                // Sombra
                g2.setColor(new Color(0, 0, 0, 35));
                g2.fillRoundRect(4, 6, w - 8, h - 10, arc, arc);

                // Fondo
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(39, 40, 46),
                        w, h, new Color(14, 15, 19)
                ));
                g2.fillRoundRect(1, 1, w - 3, h - 7, arc, arc);

                // Efectos internos
                g2.setClip(new RoundRectangle2D.Float(1, 1, w - 3, h - 7, arc, arc));

                g2.setPaint(new GradientPaint(
                        0, 0, new Color(255, 255, 255, 20),
                        0, 130, new Color(255, 255, 255, 0)
                ));
                g2.fillRect(0, 0, w, 130);

                g2.setPaint(new GradientPaint(
                        w - 250, 0, new Color(205, 174, 125, 0),
                        w, h, new Color(205, 174, 125, 18)
                ));
                g2.fillRect(w - 250, 0, 250, h);

                // decoracion circular
                int x = w - 80;
                int y = 130;

                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(new Color(205, 174, 125, 22));

                g2.drawOval(x - 95, y - 95, 190, 190);
                g2.drawOval(x - 75, y - 75, 150, 150);
                g2.drawOval(x - 55, y - 55, 110, 110);

                g2.setColor(new Color(255, 255, 255, 10));
                g2.drawOval(x - 115, y - 115, 230, 230);

                // Línea inferior dorada
                int lineaY = h - 27;

                g2.setPaint(new GradientPaint(
                        30, lineaY, new Color(205, 174, 125, 0),
                        w / 2, lineaY, new Color(205, 174, 125, 110)
                ));
                g2.drawLine(30, lineaY, w - 30, lineaY);

                g2.setClip(null);

                // Borde dorado
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(205, 174, 125, 75));
                g2.drawRoundRect(1, 1, w - 3, h - 7, arc, arc);

                g2.dispose();
            }
        };

        card.setOpaque(false);

        // ENCABEZADO
        JPanel header = ENCABEZADO();

        card.add(header, BorderLayout.NORTH);

        // CUERPO
        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(8, 30, 12, 30));

        GridBagConstraints gbc = new GridBagConstraints();

        // FOTO
        JPanel panelFoto = new JPanel();
        panelFoto.setOpaque(false);
        panelFoto.setLayout(new BoxLayout(panelFoto, BoxLayout.Y_AXIS));

        JLabel foto = new JLabel();
        foto.setPreferredSize(new Dimension(112, 132));
        foto.setMinimumSize(new Dimension(112, 132));
        foto.setMaximumSize(new Dimension(112, 132));
        foto.setAlignmentX(Component.CENTER_ALIGNMENT);
        foto.setHorizontalAlignment(SwingConstants.CENTER);
        foto.setVerticalAlignment(SwingConstants.CENTER);

        foto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 174, 125, 170)),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        cargarImagenUsuario(foto, 102, 122);

        JLabel username = new JLabel("@" + valorSeguro(usuario.getUsername()));
        username.setAlignmentX(Component.CENTER_ALIGNMENT);
        username.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        username.setForeground(ORO_CLARO);
        username.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panelFoto.add(foto);
        panelFoto.add(username);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 26);

        cuerpo.add(panelFoto, gbc);

        // DATOS DEL USUARIO
        JPanel datos = new JPanel(new GridBagLayout());
        datos.setOpaque(false);

        GridBagConstraints d = new GridBagConstraints();
        d.gridx = 0;
        d.weightx = 1;
        d.fill = GridBagConstraints.HORIZONTAL;
        d.anchor = GridBagConstraints.WEST;

        JLabel titular = new JLabel("TITULAR");
        titular.setFont(new Font("Segoe UI", Font.BOLD, 9));
        titular.setForeground(ORO);

        d.gridy = 0;
        d.insets = new Insets(0, 0, 4, 0);
        datos.add(titular, d);

        JLabel nombre = new JLabel(valorSeguro(usuario.getNombreCompleto()).toUpperCase());
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 17));
        nombre.setForeground(BLANCO);

        d.gridy = 1;
        d.insets = new Insets(0, 0, 12, 0);
        datos.add(nombre, d);

        // Documento
        agregarCampo(datos, d, 2, "DOCUMENTO",
                limpiarCodigoTipoId(usuario.getTipoIdentificacion()) + "  "
                        + valorSeguro(usuario.getNumeroIdentificacion()));

        // Teléfono
        agregarCampo(datos, d, 4, "TELÉFONO",
                valorSeguro(usuario.getTelefono()));

        // Correo
        agregarCampo(datos, d, 6, "CORREO ELECTRÓNICO",
                valorSeguro(usuario.getCorreo()));

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        cuerpo.add(datos, gbc);

        card.add(cuerpo, BorderLayout.CENTER);

        return card;
    }

    private JPanel ENCABEZADO() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 76));
        header.setBorder(BorderFactory.createEmptyBorder(24, 30, 0, 30));

        JLabel marca = new JLabel("BOOKUSERS");
        marca.setFont(new Font("Segoe UI", Font.BOLD, 17));
        marca.setForeground(BLANCO);

        JLabel id = new JLabel(String.format("ID %03d", usuario.getId()));
        id.setFont(new Font("Consolas", Font.PLAIN, 12));
        id.setForeground(ORO_CLARO);

        header.add(marca, BorderLayout.WEST);
        header.add(id, BorderLayout.EAST);
        return header;
    }

    // CAMPOS DE INFORMACIÓN
    private void agregarCampo(JPanel panel, GridBagConstraints gbc,
                              int fila, String titulo, String valor) {

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 9));
        etiqueta.setForeground(GRIS);

        gbc.gridy = fila;
        gbc.insets = new Insets(0, 0, 2, 0);
        panel.add(etiqueta, gbc);

        JLabel texto = new JLabel(valor);
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        texto.setForeground(new Color(230, 231, 235));

        gbc.gridy = fila + 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(texto, gbc);
    }

    // UTILIDADES
    private String limpiarCodigoTipoId(String tipoId) {

        if (tipoId == null || tipoId.isBlank()) {
            return "ID";
        }

        int guion = tipoId.indexOf('-');

        return guion >= 0
                ? tipoId.substring(guion + 1).trim()
                : tipoId.trim();
    }

    private String valorSeguro(String texto) {
        return texto == null || texto.isBlank()
                ? "No registrado"
                : texto.trim();
    }

    // CARGA DE FOTO
    private void cargarImagenUsuario(JLabel destino, int ancho, int alto) {

        String ruta = usuario.getRutaFoto();

        if (ruta != null && !ruta.isBlank()) {

            File archivo = new File(ruta);

            if (archivo.isFile()) {
                ImageIcon icon = new ImageIcon(archivo.getAbsolutePath());

                if (icon.getIconWidth() > 0) {
                    destino.setIcon(escalar(icon, ancho, alto));
                    return;
                }
            }
        }

        ImageIcon defecto = cargarAvatarPorDefecto(ancho, alto);

        if (defecto != null) {
            destino.setIcon(defecto);
        } else {
            destino.setText("SIN FOTO");
            destino.setFont(new Font("Segoe UI", Font.BOLD, 10));
            destino.setForeground(GRIS);
        }
    }

    private ImageIcon cargarAvatarPorDefecto(int ancho, int alto) {

        String[] rutas = {
                "/Assets/" + ARCHIVO_SIN_FOTO,
                "/" + ARCHIVO_SIN_FOTO,
                "Assets/" + ARCHIVO_SIN_FOTO,
                ARCHIVO_SIN_FOTO
        };

        for (String ruta : rutas) {

            URL url = getClass().getResource(ruta);

            if (url != null) {
                ImageIcon icon = new ImageIcon(url);

                if (icon.getIconWidth() > 0) {
                    return escalar(icon, ancho, alto);
                }
            }
        }

        File archivo = new File("Assets" + File.separator + ARCHIVO_SIN_FOTO);

        if (!archivo.isFile()) {
            archivo = new File(ARCHIVO_SIN_FOTO);
        }

        if (archivo.isFile()) {
            ImageIcon icon = new ImageIcon(archivo.getAbsolutePath());

            if (icon.getIconWidth() > 0) {
                return escalar(icon, ancho, alto);
            }
        }

        return null;
    }

    private ImageIcon escalar(ImageIcon icono, int ancho, int alto) {
        return new ImageIcon(icono.getImage().getScaledInstance(
                ancho, alto, Image.SCALE_SMOOTH
        ));
    }
}