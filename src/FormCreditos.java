import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URL;

public class FormCreditos extends JFrame {

    private static final String ARCHIVO_SIN_FOTO = "sinicon.png";

    private static final String NOMBRE_1 = "Kevin David Rojas Gutierrez";
    private static final String ROL_1 = "Lógica - Diseño - Diseño de interfaces";
    private static final String GITHUB_1 = "https://github.com/Kevin66RGX";
    private static final String FOTO_1 = "KR.png";

    private static final String NOMBRE_2 = "Ivan Daniel Chacon Otalvarez";
    private static final String ROL_2 = "Lógica - Diseño de interfaces";
    private static final String GITHUB_2 = "https://github.com/ivanchaconota-lang";
    private static final String FOTO_2 = "IC.png";

    private static final String MENSAJE_PROFESOR =
            "Agradecimiento especial al profesor Hermes por las clases del semestre de POO y sus enseñanzas. "
                    + "¡Éxitos y los mejores deseos!";

    public FormCreditos() {
        setTitle("CREDITOS");
        setSize(860, 560);
        setMinimumSize(new Dimension(740, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel fondo = crearPanelFondo();
        fondo.setLayout(new BorderLayout(0, 14));
        fondo.setBorder(BorderFactory.createEmptyBorder(20, 24, 18, 24));

        JLabel lblTitulo = new JLabel("CREDITOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(15, 23, 42));
        fondo.add(lblTitulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);

        JPanel dosColumnas = new JPanel(new GridLayout(1, 2, 22, 0));
        dosColumnas.setOpaque(false);

        dosColumnas.add(crearTarjetaAutor(NOMBRE_1, ROL_1, GITHUB_1, FOTO_1));
        dosColumnas.add(crearTarjetaAutor(NOMBRE_2, ROL_2, GITHUB_2, FOTO_2));

        centro.add(dosColumnas, BorderLayout.CENTER);
        centro.add(crearPanelAgradecimiento(), BorderLayout.SOUTH);

        fondo.add(centro, BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pie.setOpaque(false);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.putClientProperty("JComponent.roundRect", true);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrar.setForeground(new Color(30, 41, 59));
        btnCerrar.setBackground(new Color(240, 247, 255));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.setPreferredSize(new Dimension(105, 34));
        btnCerrar.addActionListener(e -> dispose());

        pie.add(btnCerrar);
        fondo.add(pie, BorderLayout.SOUTH);

        setContentPane(fondo);
    }

    private JPanel crearPanelFondo() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(248, 250, 255),
                        getWidth(), getHeight(), new Color(219, 234, 254)
                ));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
    }

    private JPanel crearTarjetaAutor(String nombre, String rol, String urlGithub, String archivoFoto) {
        JPanel tarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth();
                int h = getHeight();
                int arc = 22;

                g2.setPaint(new GradientPaint(
                        0, 0, new Color(255, 255, 255, 195),
                        0, h, new Color(239, 246, 255, 155)
                ));
                g2.fillRoundRect(1, 1, w - 2, h - 2, arc, arc);

                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(new Color(191, 219, 254, 210));
                g2.drawRoundRect(1, 1, w - 2, h - 2, arc, arc);

                g2.dispose();
            }
        };

        tarjeta.setOpaque(false);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 18, 20));

        Image imagenOriginal = cargarImagenOriginal(archivoFoto);
        if (imagenOriginal == null) {
            imagenOriginal = cargarImagenOriginal(ARCHIVO_SIN_FOTO);
        }
        final Image fotoFinal = imagenOriginal;

        JPanel marcoFoto = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, 20, 20);

                if (fotoFinal != null && fotoFinal.getWidth(null) > 0 && fotoFinal.getHeight(null) > 0) {
                    int pad = 8;
                    int maxW = w - pad * 2;
                    int maxH = h - pad * 2;
                    int origW = fotoFinal.getWidth(null);
                    int origH = fotoFinal.getHeight(null);

                    double escala = Math.min((double) maxW / origW, (double) maxH / origH);
                    int drawW = Math.max(1, (int) Math.round(origW * escala));
                    int drawH = Math.max(1, (int) Math.round(origH * escala));
                    int x = (w - drawW) / 2;
                    int y = (h - drawH) / 2;

                    g2.drawImage(fotoFinal, x, y, drawW, drawH, null);
                } else {
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                    g2.setColor(new Color(30, 58, 138));
                    FontMetrics fm = g2.getFontMetrics();
                    String ini = iniciales(nombre);
                    g2.drawString(ini, (w - fm.stringWidth(ini)) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
                }

                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(new Color(147, 197, 253));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);

                g2.dispose();
            }
        };
        marcoFoto.setOpaque(false);
        marcoFoto.setPreferredSize(new Dimension(160, 160));
        marcoFoto.setMinimumSize(new Dimension(160, 160));
        marcoFoto.setMaximumSize(new Dimension(160, 160));
        marcoFoto.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNombre = new JLabel(nombre, SwingConstants.CENTER);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNombre.setForeground(new Color(15, 23, 42));

        JLabel lblRol = new JLabel(rol, SwingConstants.CENTER);
        lblRol.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRol.setForeground(new Color(71, 85, 105));

        JLabel lblGithub = new JLabel("<html><u>" + escaparHtml(urlGithub) + "</u></html>", SwingConstants.CENTER);
        lblGithub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblGithub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblGithub.setForeground(new Color(29, 78, 216));
        lblGithub.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblGithub.setToolTipText("Abrir en el navegador");
        lblGithub.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirUrl(urlGithub);
            }
        });

        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(marcoFoto);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 12)));
        tarjeta.add(lblNombre);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 4)));
        tarjeta.add(lblRol);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 10)));
        tarjeta.add(lblGithub);
        tarjeta.add(Box.createVerticalGlue());

        return tarjeta;
    }

    private JPanel crearPanelAgradecimiento() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth();
                int h = getHeight();
                int arc = 18;

                g2.setPaint(new GradientPaint(
                        0, 0, new Color(255, 255, 255, 200),
                        w, h, new Color(239, 246, 255, 165)
                ));
                g2.fillRoundRect(1, 1, w - 2, h - 2, arc, arc);

                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(new Color(191, 219, 254, 210));
                g2.drawRoundRect(1, 1, w - 2, h - 2, arc, arc);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel texto = new JLabel(
                "<html><div style='text-align:center;'>" + escaparHtml(MENSAJE_PROFESOR) + "</div></html>",
                SwingConstants.CENTER
        );
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        texto.setForeground(new Color(30, 41, 59));

        panel.add(texto, BorderLayout.CENTER);
        return panel;
    }

    private Image cargarImagenOriginal(String nombreArchivo) {
        String[] rutasDisco = {
                "Assets" + File.separator + nombreArchivo,
                "Assets/" + nombreArchivo,
                "src" + File.separator + "Assets" + File.separator + nombreArchivo,
                nombreArchivo
        };
        for (String ruta : rutasDisco) {
            File f = new File(ruta);
            if (f.isFile()) {
                ImageIcon icon = new ImageIcon(f.getAbsolutePath());
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    return icon.getImage();
                }
            }
        }

        String[] rutasClasspath = {
                "/Assets/" + nombreArchivo,
                "/" + nombreArchivo
        };
        for (String ruta : rutasClasspath) {
            URL url = getClass().getResource(ruta);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    return icon.getImage();
                }
            }
        }

        return null;
    }

    private String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "BU";
        }
        String[] partes = nombre.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, partes.length); i++) {
            if (!partes[i].isEmpty()) {
                sb.append(Character.toUpperCase(partes[i].charAt(0)));
            }
        }
        return sb.length() > 0 ? sb.toString() : "BU";
    }

    private String escaparHtml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void abrirUrl(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                JOptionPane.showMessageDialog(this, "Enlace de GitHub:\n" + url);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enlace de GitHub:\n" + url);
        }
    }
}