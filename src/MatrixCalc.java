public class MatrixCalc {
    // shout-out to linear algebra for this one

    //we can represent points as 3x1 vectors
    //so that transforming them = multiplying by a 3x3 matrix

    double[] entries;
    public MatrixCalc(double[] entries) {
        this.entries = entries;
    }

    public MatrixCalc multiply(MatrixCalc matrix) { //matrix multiplication
        double[] product = new double[9];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                for (int i = 0; i < 3; i ++) {
                    product[row * 3 + col] +=
                            this.entries[row * 3 + i] * matrix.entries[i * 3 + col];
                }
            }
        }
        return new MatrixCalc(product);
    }

    public Vertex transform(Vertex vertex) { //transformation
        return new Vertex(
            (vertex.x * entries[0]) + vertex.y * entries[3] + vertex.z * entries[6],
            (vertex.x * entries[1]) + vertex.y * entries[4] + vertex.z * entries[7],
            (vertex.x * entries[2]) + vertex.y * entries[5] + vertex.z * entries[8]
        );
    }
}
