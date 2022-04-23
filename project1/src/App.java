import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        HBox vBox = new HBox();
        Timeline[] timelines = new Timeline[2];

        for (int i = 0; i < timelines.length; i++) {
            final int index = i;
            // Car pane
            CarPane carPane = new CarPane(400, 100, index);
            timelines[i] = new Timeline(new KeyFrame(Duration.millis(5), e -> carPane.moveCar(1, index)));
            timelines[i].setCycleCount(carPane.getOneCycleDuration());
            timelines[i].play();

            vBox.getChildren().add(carPane);
        }

        stage.setScene(new Scene(vBox));
        stage.setTitle("Car Accident");
        stage.show();
    }

    public static void main(String args[]) {
        launch();
    }
}

class CarPane extends Pane {
    private double w; // pane width
    private double h; // pane height

    // left Tire x,y and radius
    private double leftTireX; // left tire is the car's drawing start point
    private double leftTireY;
    private double tireRadius;

    Circle[] tires = new Circle[2]; // Index 0, 1 = left and right tire
    Polyline cover = new Polyline();
    ObservableList<Double> points;
    Rectangle base;

    CarPane(double width, double height, int f) {
        // Get width height and measurements for the left tire (starting point)
        w = width;
        h = height;
        leftTireX = w * 0.45;
        leftTireY = h * 0.9;
        tireRadius = h * 0.1;

        // set MIN and MAX width
        setMinWidth(w);
        setMinHeight(h);
        setMaxWidth(w);
        setMaxHeight(h);

        reset(f); // draws and adjusts car to starting position
    }

    public void reset(int f) {
        if (points != null)
            points.clear();
        getChildren().clear();
        drawCar();
        moveCar(tireRadius * 13 * -1, f);
    }

    public void drawCar() {
        for (int i = 0; i < tires.length; i++) {
            tires[i] = new Circle(leftTireX + (i * 4 * tireRadius), leftTireY, tireRadius);
            tires[i].setStroke(Color.BLACK);
            tires[i].setFill(Color.BLACK);
        }

        double baseX = tires[0].getCenterX() - tires[0].getRadius() * 3;
        double baseY = tires[0].getCenterY() - tires[0].getRadius() * 3;
        base = new Rectangle(baseX, baseY, tireRadius * 10, tireRadius * 2);
        Stop[] stops1 = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.BLUE) };
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops1);
        base.setFill(lg1);
        base.setStroke(Color.BLACK);

        // draw the car's top cover
        double startX = base.getX() + tireRadius * 2;
        double startY = base.getY();
        double currentX = startX;
        double currentY = startY;

        points = cover.getPoints();

        double distance = tireRadius * 2;
        points.addAll(currentX, currentY); // start point

        currentX += distance;
        currentY -= distance;
        points.addAll(currentX, currentY); // up right

        currentX += distance;
        points.addAll(currentX, currentY); // right

        currentX += distance;
        currentY += distance;
        points.addAll(currentX, currentY); // down right

        points.addAll(startX, startY); // connect to starting point
        Stop[] stops2 = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.RED) };
        LinearGradient lg2 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops2);
        cover.setFill(lg2);

        getChildren().addAll(tires);
        getChildren().add(base);
        getChildren().add(cover);
    }

    void moveCar(double distance, int f) {
        for (Circle c : tires) {
            if (f == 1)
                c.setCenterX(c.getCenterX() - distance + 0.45 * distance);
            else
                c.setCenterX(c.getCenterX() + distance - 0.45 * distance);
        }
        if (f == 1)
            base.setX(base.getX() - distance + 0.45 * distance);
        else
            base.setX(base.getX() + distance - 0.45 * distance);

        for (int i = 0; i < points.size(); i += 2) {
            if (f == 1)
                points.set(i, points.get(i) - distance + 0.45 * distance);
            else
                points.set(i, points.get(i) + distance - 0.45 * distance);
        }
    }

    public int getOneCycleDuration() {
        return (int) w;
    }
}