
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Random;

public class ALife1Dim extends JComponent implements Runnable {

    int imageWidth, imageHeight;
    static double a, b;
    static double Pi = Math.PI;
    static double rho = 0.001;

//  static Random randNumGen = new Random(5);
    ArrayList<BufferedImage> images = new ArrayList<>();
    Graphics2D bufImageGraphic;
    double[] cells = new double[400];
    int numImages = 1;

    public ALife1Dim(int w, int h) {
        imageWidth = w;
        imageHeight = h;
        //xLowerLeft =  xLL; yLowerLeft =  yLL;
        //xUpperRight = xUR; yUpperRight = yUR;

        //pixelWidth  = (xUR - xLL)/((double)w); 
        //invPixelWidth  = ((double)w)/(xUR - xLL);
        //pixelHeight = (yUR - yLL)/((double)h); 
        //invPixelHeight = ((double)h)/(yUR - yLL);
        setSize(w, h);
        setPreferredSize(new Dimension(w, h));

        System.out.println(imageWidth + "     " + imageHeight);
        //System.out.println(pixelWidth + "     " + pixelHeight);

        BufferedImage bufImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        bufImageGraphic = bufImage.createGraphics();
        //bufImageGraphic.setPaint(Color.white);
        bufImageGraphic.setPaint(Color.black);
        bufImageGraphic.fillRect(0, 0, w, h);
        images.add(bufImage);
        //bufImageGraphic.setPaint(Color.red);
        /*
    bufImageGraphic.setStroke(
      //new BasicStroke((float)pixelHeight) 
      new BasicStroke(
            (float)pixelHeight, 
            BasicStroke.CAP_ROUND, 
            BasicStroke.JOIN_ROUND
      ) 
    );
         */

 /* 
    AffineTransform at = new AffineTransform(invPixelWidth, 
                                             0, 0, -invPixelHeight,
                                             -invPixelWidth*xLL, 
                                             invPixelHeight*yUR      
                                            ); 
         */
        //bufImageGraphic.setTransform(at);
        Thread t = new Thread(this);
        t.start();
    }

    public void timeStep(int j) {
//     Dimension d = getPreferredSize();
//     if (d.height < j)
//     {
//         setPreferredSize(new Dimension(d.width, j));
//     }
        int i;
        cells = stateTransition(cells);
        for (i = 0; i < 400; ++i) {
            Color c;
            float rgb1, rgb2, rgb3;
            double c1, c2, c3;
            //c1 = 0.5 - 0.5*Math.cos(24*Pi*cells[i]);
            //c2 = c1*c1*c1*c1;
            //c3 = 0.5 + 0.5*Math.cos(30*Pi*cells[i]);
            //rgb1 = (float) c1;
            rgb1 = (float) cells[i];
            //rgb2 = (float) c2;
            rgb2 = (float) cells[i];
            //rgb3 = (float) c3;
            rgb3 = (float) cells[i];
            c = new Color(rgb1, rgb2, rgb3);
            bufImageGraphic.setPaint(c);
            //int s = 16;
            int s = 2;
            bufImageGraphic.fillRect(i * s, j * s, s, s);
        }
    }

    public void run() {
        try {
            int i, j;
            Color c;
            float rgb1, rgb2, rgb3;
            double c1, c2, c3;
            Random randGen = new Random((long) 1);
            for (i = 0; i < 400; i++) {
                //cells[i] = binarize(randGen.nextDouble());
                cells[i] = randGen.nextBoolean() ? 1 : 0;
                //cells[i] = 0;
                System.out.print((int) cells[i]);
            }
            cells[200] = 1;
            for (j = 0; j < 6400; ++j) { //LOOPBODY:
                if ((j * 2) >= (imageHeight * numImages)) {
                    ++numImages;
                    BufferedImage bufImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
                    bufImageGraphic = bufImage.createGraphics();
                    bufImageGraphic.setPaint(Color.black);
                    bufImageGraphic.fillRect(0, 0, imageWidth, imageHeight);
                    images.add(bufImage);
                    setPreferredSize(new Dimension(getPreferredSize().width, imageHeight * numImages));
                    revalidate();
                }
                timeStep(j % (imageHeight / 2));
                repaint();
//                Thread.sleep(2048);
                Thread.sleep(100);
            }
        } catch (InterruptedException ie) {
        }
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        /* g2.setRenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON); */
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        rh.put(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        g2.setRenderingHints(rh);
        for (int i = 0; i < images.size(); ++i) {
            g2.drawImage(images.get(i), 0, i * imageHeight, this);
        }
    }

    static double[] stateTransition(double[] x) {
        //assumes x.length >= 2;
        int len = x.length;
        int last = len - 1;
        double y[] = new double[len];
        y[0] = poly(x[len - 1], x[0], x[1]);
        y[last] = poly(x[last - 1], x[last], x[0]);
        for (int i = 1; i < last; i++) {
            y[i] = poly(x[i - 1], x[i], x[i + 1]);
        }
        return y;
    }

    static double binarize(double x) {
        double acc = 0;
        double den = 0.5;
        for (int k = 0; k < 14; ++k) {
            if (acc + den <= x) {
                acc += den;
            }
            den *= 0.5;
        }
        return acc;
    }

    static double poly(double u, double x, double v) {
        return binarize(
                // 0.5 - 0.5*Math.cos(Pi*(a + (a+b)*u + (a-b)*v + b*u*v - 2*u*x*v))
                0.5 - 0.5 * Math.cos(Pi * (x + v - x * v - u * x * v))
        );
    }

    public static void main(String[] args) {
        //a = Double.valueOf(args[0]).doubleValue();
        //b = Double.valueOf(args[1]).doubleValue();
        JFrame frame = new JFrame("Artificial Life");
        frame.setBackground(Color.white);
        frame.setSize(840, 140);
        frame.setLocation(10, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //ALife1Dim plot = new ALife1Dim(800, 1024);
        ALife1Dim plot = new ALife1Dim(800, 96);
        JScrollPane jsp = new JScrollPane(plot);
        frame.add(jsp);
//    frame.getContentPane().add(plot);
        frame.setVisible(true);
    }

}
