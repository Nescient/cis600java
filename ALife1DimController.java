/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cis600;

import javafx.scene.paint.Color;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author me
 */
public class ALife1DimController implements Initializable {

    final int CELL_SIZE = 2;
    double[] cells = new double[400];

//    @FXML
//    private Label label;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ScrollPane scrollPane;

//    @FXML
//    private void handleButtonAction(ActionEvent event) {
//        System.out.println("You clicked me!");
//        label.setText("Hello World!");
//        render_row(0, cells);
//    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Random randGen = new Random((long) 1);
        for (int i = 0; i < cells.length; ++i) {
            cells[i] = randGen.nextBoolean() ? 1 : 0;
        }
        cells[200] = 1;
        render_row(0, cells);
        scrollPane.setVvalue(1.0);

        Task task = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                for (int j = 1; j < 6400; ++j) {
                    timeStep(j);
//                    Thread.sleep(2048);
                    //Thread.sleep(1);
                    Thread.sleep(100);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void timeStep(int j) {
        cells = stateTransition(cells);
        render_row(j, cells);
    }

    private void render_row(int index, double[] cells) {
        final int y_index = index * CELL_SIZE;
        for (int i = 0; i < cells.length; ++i) {
            int x_index = i * CELL_SIZE;
//            double rgb1 = cells[i];
//            double rgb2 = cells[i];
//            double rgb3 = cells[i];
//            Color c = new Color(rgb1, rgb2, rgb3, 0/*opacity*/);
            Rectangle r = new Rectangle(CELL_SIZE, CELL_SIZE, new Color(cells[i], cells[i], cells[i], 1/*opacity*/));
            r.setX(x_index);
            r.setY(y_index);
//            r.setWidth(CELL_SIZE);
//            r.setHeight(CELL_SIZE);
//            r.setFill(Color.TRANSPARENT);
//            r.setStroke(cells[i] == 1 ? Color.BLACK : Color.WHITE);
//            r.setStroke(new Color(cells[i], 0, 1-cells[i], 1/*opacity*/));
//            anchorPane.getChildren().add(r);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    anchorPane.getChildren().add(r);
                }
            });
        }
        if ((cells.length * CELL_SIZE) > anchorPane.getMinWidth()) {
            anchorPane.setMinWidth(cells.length * CELL_SIZE);
        }
        if (y_index > anchorPane.getMinHeight()) {
            anchorPane.setMinHeight(y_index);
            scrollPane.setVvalue(1.0);  // auto scroll to bottom
        }
    }

    static double[] stateTransition(double[] x) {
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
        return binarize(0.5 - 0.5 * Math.cos(Math.PI * (x + v - x * v - u * x * v)));
    }
}
