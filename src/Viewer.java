import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class Viewer {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        //create slider for horizontal rotation
        JSlider horizontalRotation = new JSlider(0,360,180); //0 = min, 360 = max, value = 180
        horizontalRotation.setOpaque(true);
        horizontalRotation.setBackground(Color.BLACK);
        pane.add(horizontalRotation, BorderLayout.SOUTH);

        //create slider for vertical rotation
        JSlider verticalRotation = new JSlider(SwingConstants.VERTICAL, -90, 90, 0); //orientation = vertical constants, min = -90, max = 90, value = 0
        verticalRotation.setOpaque(true);
        verticalRotation.setBackground(Color.BLACK);
        pane.add(verticalRotation, BorderLayout.EAST);

        //create render panel
        JPanel render = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; //making a Graphics2D object from g to render 2D background
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                //start making a tetrahedron using 4 triangles
                List<Triangle> tetrahedron = new ArrayList<>();
                tetrahedron.add(new Triangle(new Vertex(200, 200, 200),
                        new Vertex (-200, -200, 200),
                        new Vertex(-200, 200, -200),
                        new Color(53,94,59)));

                tetrahedron.add(new Triangle(new Vertex(200, 200, 200),
                        new Vertex (-200, -200, 200),
                        new Vertex(200, -200, -200),
                        new Color(79,121,66)));

                tetrahedron.add(new Triangle(new Vertex(-200, 200, -200),
                        new Vertex (200, -200, -200),
                        new Vertex(-200, -200, 200),
                        new Color(74,93,25)));

                tetrahedron.add(new Triangle(new Vertex(-200, 200, -200),
                        new Vertex (200, -200, -200),
                        new Vertex(200, 200, 200),
                        new Color(85,93,80)));

                g2.translate((getWidth()/2), (getHeight()/2));
                g2.setColor(Color.WHITE);

                for (Triangle t : tetrahedron) {
                    //draw lines to make the list of triangles a tetrahedron
                    Path2D connect = new Path2D.Double();
                    connect.moveTo(t.v1.x, t.v1.y); //start at first triangle
                    connect.lineTo(t.v2.x, t.v2.y);
                    connect.lineTo(t.v3.x, t.v3.y);
                    connect.closePath();
                    g2.draw(connect);
                }
            }
        };
        pane.add(render, BorderLayout.CENTER);

        frame.setSize(800,800); //width and height = both 500 px
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
