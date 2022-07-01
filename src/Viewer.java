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

                double horizontalSliderPosition = Math.toRadians(horizontalRotation.getValue());
                MatrixCalc horizontalTransMatrix = new MatrixCalc(new double[] {
                        //making a transformation matrix based on horizontal slider mouse position
                        Math.cos(horizontalSliderPosition), 0, -Math.sin(horizontalSliderPosition), //first row
                        0, 1, 0, //second row
                        Math.sin(horizontalSliderPosition), 0, Math.cos(horizontalSliderPosition) //third row
                });

                double verticalSliderPosition = Math.toRadians(verticalRotation.getValue());
                MatrixCalc verticalTransMatrix = new MatrixCalc( new double[] {
                        //making a transformation matrix based on vertical slider mouse position
                        1, 0, 0, //first row
                        0, Math.cos(verticalSliderPosition), Math.sin(verticalSliderPosition), //second row
                        0, -Math.sin(verticalSliderPosition), Math.cos(verticalSliderPosition) //third row
                });

                MatrixCalc transMatrix = horizontalTransMatrix.multiply(verticalTransMatrix); //make both rotations work together

                g2.translate((getWidth()/2), (getHeight()/2));
                g2.setColor(Color.WHITE);

                for (Triangle t : tetrahedron) {
                    Vertex v1 = transMatrix.transform(t.v1); //making vertices change according to transformation matrix
                    Vertex v2 = transMatrix.transform(t.v2);
                    Vertex v3 = transMatrix.transform(t.v3);

                    //draw lines to make the list of triangles a tetrahedron
                    Path2D connect = new Path2D.Double();
                    connect.moveTo(v1.x, v1.y); //start at first triangle
                    connect.lineTo(v2.x, v2.y);
                    connect.lineTo(v3.x, v3.y);
                    connect.closePath();
                    g2.draw(connect);
                }
            }
        };
        pane.add(render, BorderLayout.CENTER); //make render in center of frame

        horizontalRotation.addChangeListener(e -> render.repaint()); //make sure program knows where your mouse position is on sliders
        verticalRotation.addChangeListener(e -> render.repaint());

        frame.setSize(800,800); //width and height = both 500 px
        frame.setLocationRelativeTo(null); //make frame centered
        frame.setVisible(true); //make frame visible
    }
}
