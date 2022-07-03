import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
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
                        Color.WHITE));

                tetrahedron.add(new Triangle(new Vertex(200, 200, 200),
                        new Vertex (-200, -200, 200),
                        new Vertex(200, -200, -200),
                        Color.YELLOW));

                tetrahedron.add(new Triangle(new Vertex(-200, 200, -200),
                        new Vertex (200, -200, -200),
                        new Vertex(-200, -200, 200),
                        Color.RED));

                tetrahedron.add(new Triangle(new Vertex(-200, 200, -200),
                        new Vertex (200, -200, -200),
                        new Vertex(200, 200, 200),
                        Color.BLUE));

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

                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB); //rasterize the triangle

                double[] zBuffer = new double[img.getWidth() * img.getHeight()];

                for (Triangle t : tetrahedron) {
                    Vertex v1 = transMatrix.transform(t.v1); //making vertices change according to transformation matrix
                    Vertex v2 = transMatrix.transform(t.v2);
                    Vertex v3 = transMatrix.transform(t.v3);


                    //manual translation of triangle

                    v1.x += getWidth() / 2;
                    v1.y += getHeight() / 2;

                    v2.x += getWidth() / 2;
                    v2.y += getHeight() / 2;

                    v3.x += getWidth() / 2;
                    v3.y += getHeight() / 2;

                    // calculate rectangular bounds for triangle
                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1,
                            Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    int maxY = (int) Math.min(img.getHeight() - 1,
                            Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    double triangleArea =
                            (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                    for (int y = minY; y <= maxY; y++) {
                        for (int x = minX; x <= maxX; x++) {
                            double b1 =
                                    ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                            double b2 =
                                    ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                            double b3 =
                                    ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                            if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                                img.setRGB(x, y, t.color.getRGB());
                            }
                        }
                    }

                }

                g2.drawImage(img, 0, 0, null);
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
