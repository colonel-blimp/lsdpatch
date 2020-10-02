package paletteEditor;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class ColorPicker extends JFrame {

    private interface HueListener {
        void onHueChanged(float hue);
    }

    private static class Hue extends JPanel implements MouseListener, MouseMotionListener {
        int selectedPosition = 0;
        final int width = 32;
        final int height = 256;
        private final LinkedList<HueListener> hueListeners = new LinkedList<>();

        Hue() {
            setMinimumSize(new Dimension(width, height));
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void subscribe(HueListener hueListener) {
            hueListeners.add(hueListener);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; ++y) {
                Color color = Color.getHSBColor((float) y / height, 1, 1);
                color = new Color(ColorUtil.colorCorrect(color));
                for (int x = 0; x < width; ++x) {
                    image.setRGB(x, y, color.getRGB());
                }
            }
            g.drawImage(image, 0, 0, null);
            g.setColor(Color.BLACK);
            g.drawRect(0, selectedPosition - 1, width, 2);
        }

        public float hue() {
            float hue = selectedPosition;
            hue /= height;
            assert(hue >= 0);
            assert(hue <= 1);
            return hue;
        }

        boolean mousePressed;

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            mousePressed = true;
            mouseDragged(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePressed = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            selectedPosition = Math.max(0, Math.min(height, e.getY()));
            for (HueListener hueListener : hueListeners) {
                hueListener.onHueChanged(hue());
            }
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    public ColorPicker() {
        Hue hue = new Hue();

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new MigLayout());

        contentPane.add(hue);
        pack();
    }
}
