import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
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


        //create sliders for color adjustment
        JSlider redSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        JSlider greenSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        JSlider blueSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);

        JLabel redLabel = new JLabel("RED = 0");
        JLabel greenLabel = new JLabel("GREEN = 0");
        JLabel blueLabel = new JLabel("BLUE = 0");

        //make the text visible against a black bg
        redLabel.setForeground(Color.WHITE);
        greenLabel.setForeground(Color.WHITE);
        blueLabel.setForeground(Color.WHITE);

        //make the text centered
        redLabel.setHorizontalAlignment(SwingConstants.CENTER);
        greenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        blueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new GridLayout(2,3,1,1));
        colorPanel.setBackground(Color.BLACK);

        colorPanel.add(redSlider); //row 1 col 1
        colorPanel.add(greenSlider); //row 1 col 2
        colorPanel.add(blueSlider); //row 1 col 3
        colorPanel.add(redLabel); //row 2 col 1
        colorPanel.add(greenLabel); //row 2 col 2
        colorPanel.add(blueLabel); //row 2 col 3

        pane.add(colorPanel, BorderLayout.NORTH);

        //create render panel
        JPanel render = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; //making a Graphics2D object from g to render 2D background
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                int redPosition = redSlider.getValue();
                int greenPosition = greenSlider.getValue();
                int bluePosition = blueSlider.getValue();

                redLabel.setText("RED = " + redPosition);
                greenLabel.setText("GREEN = " + greenPosition);
                blueLabel.setText("BLUE = " + bluePosition);

                Color tetrahedronColor = new Color(redPosition, greenPosition, bluePosition);

                //start making a tetrahedron using 4 triangles
                List<Triangle> tetrahedron = new ArrayList<>();
                tetrahedron.add(new Triangle(new Vertex(200, 200, 200),
                        new Vertex (-200, -200, 200),
                        new Vertex(-200, 200, -200),
                        tetrahedronColor));

                tetrahedron.add(new Triangle(new Vertex(200, 200, 200),
                        new Vertex (-200, -200, 200),
                        new Vertex(200, -200, -200),
                        tetrahedronColor));

                tetrahedron.add(new Triangle(new Vertex(-200, 200, -200),
                        new Vertex (200, -200, -200),
                        new Vertex(-200, -200, 200),
                        tetrahedronColor));

                tetrahedron.add(new Triangle(new Vertex(-200, 200, -200),
                        new Vertex (200, -200, -200),
                        new Vertex(200, 200, 200),
                        tetrahedronColor));

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

                Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY); //initialize array w/ far-away z-values

                for (Triangle t : tetrahedron) {
                    Vertex v1 = transMatrix.transform(t.v1); //making vertices change according to transformation matrix
                    v1.x += getWidth() / 2.0;
                    v1.y += getHeight() / 2.0;

                    Vertex v2 = transMatrix.transform(t.v2);
                    v2.x += getWidth() / 2.0;
                    v2.y += getHeight() / 2.0;

                    Vertex v3 = transMatrix.transform(t.v3);
                    v3.x += getWidth() / 2.0;
                    v3.y += getHeight() / 2.0;

                    Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
                    Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
                    Vertex norm = new Vertex(
                            ab.y * ac.z - ab.z * ac.y,
                            ab.z * ac.x - ab.x * ac.z,
                            ab.x * ac.y - ab.y * ac.x
                    );
                    double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
                    norm.x /= normalLength;
                    norm.y /= normalLength;
                    norm.z /= normalLength;

                    double angleCos = Math.abs(norm.z);

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
                                //set z-value for each rasterized pixel
                                double depth = (b1 * v1.z) + (b2 * v2.z) + (b3 * v3.z);
                                int zIndex = y * img.getWidth() + x;
                                if (zBuffer[zIndex] < depth) {
                                    img.setRGB(x, y, ShadeCalc.getShade(t.color, angleCos).getRGB());
                                    zBuffer[zIndex] = depth;
                                }
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
        redSlider.addChangeListener(e -> render.repaint());
        greenSlider.addChangeListener(e -> render.repaint());
        blueSlider.addChangeListener(e -> render.repaint());

        frame.setSize(800,800); //width and height = both 500 px
        frame.setLocationRelativeTo(null); //make frame centered
        frame.setVisible(true); //make frame visible
    }
}