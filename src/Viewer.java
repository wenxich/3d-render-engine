import javax.swing.*;
import java.awt.*;

public class Viewer {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        JSlider horizontalRotation = new JSlider(0,360,180); //0 = min, 360 = max, value = 180
        horizontalRotation.setOpaque(true);
        horizontalRotation.setBackground(Color.BLACK);
        horizontalRotation.setSize(495,5);
        pane.add(horizontalRotation, BorderLayout.SOUTH);

        JSlider verticalRotation = new JSlider(SwingConstants.VERTICAL, -90, 90, 0); //orientation = vertical constants, min = -90, max = 90, value = 0
        verticalRotation.setOpaque(true);
        verticalRotation.setBackground(Color.BLACK);
        verticalRotation.setSize(5,495);
        pane.add(verticalRotation, BorderLayout.EAST);

        JPanel render = new JPanel() { //render panel
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; //making a Graphics2D object from g to render 2D background
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pane.add(render, BorderLayout.CENTER);

        frame.setSize(500,500); //width and height = both 500 px
        frame.setVisible(true);
    }
}
