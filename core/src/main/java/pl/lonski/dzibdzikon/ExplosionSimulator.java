package pl.lonski.dzibdzikon;

import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.map.Line;

public class ExplosionSimulator {

    private List<List<Point>> steps;
    private int currentStep = 0;

    public ExplosionSimulator(Point center, int radius, World world) {
        steps = simulateSteps(center, radius, world);
        currentStep = 0;
    }

    public boolean hasMoreSteps() {
        return currentStep < steps.size();
    }

    public List<Point> step() {
        if (currentStep >= steps.size()) {
            return List.of();
        }

        var step = steps.get(currentStep);
        currentStep += 1;

        return step;
    }

    private List<List<Point>> simulateSteps(Point center, int radius, World world) {

        List<List<Point>> steps = new ArrayList<>();

        var points = new ArrayList<Point>();
        PositionUtils.inFilledCircleOf(radius, cp -> points.add(center.add(cp)));

        var lines = new ArrayList<List<Point>>();
        var maxLineLength = 0;
        for (Point point : points) {
            var line = Line.calculate(center, point);
            maxLineLength = Math.max(maxLineLength, line.size());
            lines.add(line);
        }

        for (int step = 0; step < maxLineLength; step++) {
            var stepPoints = new ArrayList<Point>();
            for (List<Point> line : lines) {
                if (step >= line.size()) {
                    continue;
                }
                var linePoint = line.get(step);
                if (world.getCurrentLevel().isObstacle(linePoint, false)) {
                    line.clear();
                } else {
                    stepPoints.add(linePoint);
                }
            }
            steps.add(stepPoints);
        }

        return steps;
    }

    public static List<Point> simulate(Point center, int radius, World world) {

        List<List<Point>> steps = new ArrayList<>();

        var points = new ArrayList<Point>();
        PositionUtils.inFilledCircleOf(radius, cp -> points.add(center.add(cp)));

        var rayCast = new ArrayList<Point>();

        var lines = new ArrayList<List<Point>>();
        for (Point point : points) {
            lines.add(Line.calculate(center, point));
        }

        for (List<Point> line : lines) {
            for (Point linePoint : line) {
                if (world.getCurrentLevel().isObstacle(linePoint, false)) {
                    break;
                }
                rayCast.add(linePoint);
            }
        }

        return rayCast;
    }
}
