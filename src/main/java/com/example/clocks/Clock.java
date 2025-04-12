package com.example.clocks;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;

public class Clock extends Application {

    private static final double WIDTH = 700;
    private static final double HEIGHT = 500;
    private static final double CLOCK_RADIUS = 150;
    private static final double ELLIPSE_Y_OFFSET = 0;

    // Регулируемые параметры эллипса
    private static double ellipseRadiusX = CLOCK_RADIUS * 1.2;
    private static double ellipseRadiusY = CLOCK_RADIUS * 0.8;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        StackPane clockPane = new StackPane();
        root.setCenter(clockPane);
        BorderPane.setAlignment(clockPane, Pos.CENTER);
        BorderPane.setMargin(clockPane, new Insets(20));

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        clockPane.getChildren().add(canvas);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> drawClock(gc))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        drawClock(gc);

        // Дата
        LocalDate today = LocalDate.now();
        String dateString = today.getDayOfMonth() + " " + getMonthName(today.getMonthValue());

        // Метка для даты
        Label dateLabel = new Label(dateString);
        dateLabel.setFont(Font.font("Arial", 16));
        dateLabel.setTextFill(Color.BLACK);
        dateLabel.setAlignment(Pos.CENTER);

        // Установка даты
        BorderPane.setAlignment(dateLabel, Pos.CENTER);
        BorderPane.setMargin(dateLabel, new Insets(0, 0, 30, 0));
        root.setBottom(dateLabel);

        // Метка для года
        Label yearLabel = new Label(String.valueOf(today.getYear()));
        yearLabel.setFont(Font.font("Arial", 16));
        yearLabel.setTextFill(Color.BLACK);
        yearLabel.setAlignment(Pos.CENTER);

        // Установка года
        BorderPane.setAlignment(yearLabel, Pos.CENTER);
        BorderPane.setMargin(yearLabel, new Insets(0, 0, 0, 0));
        root.setTop(yearLabel);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Analog Clock");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawClock(GraphicsContext gc) {
        double centerX = WIDTH / 2;
        double centerY = HEIGHT / 2 + 25;

        gc.clearRect(0, 0, WIDTH, HEIGHT);

        // Рисуем внешний эллипс
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeOval(
                centerX - ellipseRadiusX,
                centerY - ellipseRadiusY + ELLIPSE_Y_OFFSET,
                ellipseRadiusX * 2,
                ellipseRadiusY * 2
        );

        // Основной циферблат
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(centerX - CLOCK_RADIUS, centerY - CLOCK_RADIUS, CLOCK_RADIUS * 2, CLOCK_RADIUS * 2);

        // Римские цифры
        gc.setFont(Font.font("Arial", 16));
        gc.setFill(Color.BLACK);
        drawRomanNumerals(gc, centerX, centerY, CLOCK_RADIUS);

        // Время и стрелки
        LocalTime now = LocalTime.now();
        int hour = now.getHour() % 12;
        hour = hour == 0 ? 12 : hour;
        int minute = now.getMinute();
        int second = now.getSecond();

        // Часовая стрелка с кружком
        drawHourHand(gc, centerX, centerY,
                (hour * 30 + minute * 0.5),
                CLOCK_RADIUS * 0.5,
                1,
                Color.BLACK);

        // Минутная стрелка с треугольным наконечником
        drawMinuteSecondHand(gc, centerX, centerY,
                (minute * 6 + second * 0.1),
                CLOCK_RADIUS * 0.8,
                1,
                Color.BLACK,
                12,
                Color.BLACK);

        // Секундная стрелка с треугольным наконечником
        drawMinuteSecondHand(gc, centerX, centerY,
                second * 6,
                CLOCK_RADIUS * 0.9,
                1,
                Color.BLACK,
                16,
                Color.WHITE);

        // Центральный кружок
        gc.setFill(Color.WHITE);
        gc.fillOval(centerX - 5, centerY - 5, 10, 10);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(centerX - 5, centerY - 5, 10, 10);

        // Зеленые кружки
        drawSideCircles(gc, centerX);
    }

    private void drawHourHand(GraphicsContext gc, double centerX, double centerY,
                              double angleDeg, double length,
                              double lineWidth, Color color) {
        // Рисуем основную линию
        double angle = Math.toRadians(angleDeg - 90);
        double endX = centerX + Math.cos(angle) * length;
        double endY = centerY + Math.sin(angle) * length;

        gc.setStroke(color);
        gc.setLineWidth(lineWidth);
        gc.strokeLine(centerX, centerY, endX, endY);

        // Рисуем кружок на конце
        double circleRadius = 8;
        double distanceFromEnd = 20;
        double circleX = endX - Math.cos(angle) * distanceFromEnd;
        double circleY = endY - Math.sin(angle) * distanceFromEnd;

        gc.setFill(Color.WHITE);
        gc.fillOval(circleX - circleRadius, circleY - circleRadius,
                circleRadius*2, circleRadius*2);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(circleX - circleRadius, circleY - circleRadius,
                circleRadius*2, circleRadius*2);
    }

    private void drawMinuteSecondHand(GraphicsContext gc, double centerX, double centerY,
                                      double angleDeg, double length,
                                      double lineWidth, Color lineColor,
                                      double arrowSize, Color arrowColor) {
        // Рисуем основную линию
        double angle = Math.toRadians(angleDeg - 90);
        double endX = centerX + Math.cos(angle) * length;
        double endY = centerY + Math.sin(angle) * length;

        gc.setStroke(lineColor);
        gc.setLineWidth(lineWidth);
        gc.strokeLine(centerX, centerY, endX, endY);

        // Рисуем треугольный наконечник
        double angleLeft = angle + Math.toRadians(150);
        double angleRight = angle - Math.toRadians(150);

        double[] xPoints = {
                endX,
                endX + Math.cos(angleLeft) * arrowSize,
                endX + Math.cos(angleRight) * arrowSize
        };

        double[] yPoints = {
                endY,
                endY + Math.sin(angleLeft) * arrowSize,
                endY + Math.sin(angleRight) * arrowSize
        };

        gc.setFill(arrowColor);
        gc.fillPolygon(xPoints, yPoints, 3);
        gc.setStroke(Color.BLACK);
        gc.strokePolygon(xPoints, yPoints, 3);
    }

    private void drawSideCircles(GraphicsContext gc, double centerX) {
        double circleRadius = 15;
        double yPosition = HEIGHT - 70;
        double xOffset = ellipseRadiusX * 0.7;

        // Левый кружок
        gc.setFill(Color.rgb(154, 205, 50));
        gc.fillOval(
                centerX - xOffset - circleRadius,
                yPosition - circleRadius,
                circleRadius * 2,
                circleRadius * 2
        );
        gc.strokeOval(
                centerX - xOffset - circleRadius,
                yPosition - circleRadius,
                circleRadius * 2,
                circleRadius * 2
        );

        // Правый кружок
        gc.fillOval(
                centerX + xOffset - circleRadius,
                yPosition - circleRadius,
                circleRadius * 2,
                circleRadius * 2
        );
        gc.strokeOval(
                centerX + xOffset - circleRadius,
                yPosition - circleRadius,
                circleRadius * 2,
                circleRadius * 2
        );
    }

    private void drawRomanNumerals(GraphicsContext gc, double centerX, double centerY, double radius) {
        for (int i = 1; i <= 12; i++) {
            String romanNumeral = toRoman(i);
            double angle = Math.PI / 6 * (i - 3);
            double x = centerX + Math.cos(angle) * (radius * 0.9);
            double y = centerY + Math.sin(angle) * (radius * 0.9);

            double textWidth = getTextWidth(gc.getFont(), romanNumeral, 0.0);
            double textHeight = getTextHeight(gc.getFont());

            gc.fillText(romanNumeral, x - textWidth / 2, y + textHeight / 4);
        }
    }

    private String toRoman(int num) {
        switch (num) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            case 11: return "XI";
            case 12: return "XII";
            default: return "";
        }
    }

    private double getTextWidth(Font font, String text, double wrappingWidth) {
        javafx.scene.text.Text t = new javafx.scene.text.Text(text);
        t.setFont(font);
        if (wrappingWidth > 0) {
            t.setWrappingWidth(wrappingWidth);
        }
        return t.getLayoutBounds().getWidth();
    }

    private double getTextHeight(Font font) {
        javafx.scene.text.Text t = new javafx.scene.text.Text("test");
        t.setFont(font);
        return t.getLayoutBounds().getHeight();
    }

    private String getMonthName(int month) {
        String[] months = {
                "Января", "Февраля", "Марта", "Апреля", "Мая",
                "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"
        };
        return months[month - 1];
    }

    public static void main(String[] args) {
        ellipseRadiusX = 300;
        ellipseRadiusY = 200;
        launch(args);
    }
}