import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
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
        setTitle("BookUsers · Kevin David Rojas Gutierrez e Ivan Daniel Chacon Otalvarez");
        setSize(1440, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // solo el menu cierra todo el programa
        setLocationRelativeTo(null);

        // CARGAR LOS USUARIOS GUARDADOS: se meten en la MISMA lista y se retoma el contador de id
        Almacen.Datos datos = Almacen.cargar();
        usuarios.addAll(datos.usuarios);
        FormRegistro.setContadorId(datos.siguienteId);

        // ACCIONES DE LOS BOTONES <KEVIN>
        btnRegistrar.addActionListener(e -> {
            new FormRegistro(usuarios).setVisible(true);
        });

        btnVerUsuarios.addActionListener(e -> {
            new FormListado(usuarios).setVisible(true);
        });

        btnCreditos.addActionListener(e -> {
            new FormCreditos().setVisible(true);
        });
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new FormMenu().setVisible(true);
        });
    }

    private void createUIComponents() {
        panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Degradado pastel suave
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(224, 231, 255),
                        getWidth(), getHeight(), new Color(224, 242, 254)
                ));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        btnRegistrar = botonGlass("REGISTRAR", "Assets/btn1.png");
        btnVerUsuarios = botonGlass("VER USUARIOS", "Assets/btn 2.png");
        btnCreditos = botonGlass("CREDITOS", "Assets/btn3.png");
    }

    private ImageIcon cargarIconoEscalado(String ruta, int ancho, int alto) {
        ImageIcon original = new ImageIcon(ruta);
        if (original.getIconWidth() <= 0) {
            return null; // Por si no encuentra la imagen en esa ruta
        }
        Image escalada = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(escalada);
    }

    private JButton botonGlass(String texto, String rutaImagen) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int transparencia = getModel().isRollover() ? 140 : 90;

                g2.setColor(new Color(255, 255, 255, transparencia));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);

                g2.setColor(new Color(255, 255, 255, 210));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);

                super.paintComponent(g);
            }
        };

        ImageIcon icono = cargarIconoEscalado(rutaImagen, 130, 130);
        if (icono != null) {
            btn.setIcon(icono);
        }

        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(new Color(30, 41, 59));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(18);
        return btn;
    }
}