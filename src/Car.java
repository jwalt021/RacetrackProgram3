//update for program 3

import java.util.ArrayList;
import java.util.List;

/**
 * Car (abstract)
 * --------------
 * Base class for all cars.
 *  - Stores position, speed limits, move order, weight at current position.
 *  - Stores path history so the winner's path can be shown.
 *  - Implements shared move() logic with Bresenham + collision detection.
 */
public abstract class Car {

    private final char idNumber;
    private int row;
    private int col;

    private int rowVelocity;
    private int colVelocity;
    private int maxSpeed;

    private boolean isWinner;

    private int moveOrder;
    private int weightPosition;
    public static int nextOrder = 1;

    /** Every grid point this car has actually visited. */
    private List<Position> pathHistory;

    public Car(char idNumber,
               int startRow,
               int startCol,
               int rowVelocity,
               int colVelocity,
               int maxSpeed,
               Racetrack track) {

        this.idNumber = idNumber;
        this.row = startRow;
        this.col = startCol;
        this.rowVelocity = rowVelocity;
        this.colVelocity = colVelocity;
        this.maxSpeed = maxSpeed;
        this.isWinner = false;

        this.moveOrder = nextOrder++;
        this.weightPosition = track.getWeight(startRow, startCol);

        this.pathHistory = new ArrayList<>();
        this.pathHistory.add(new Position(startRow, startCol));
    }

    public char getIdNumber() { return idNumber; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public Position getPosition() { return new Position(row, col); }
    public boolean isWinner() { return isWinner; }
    public void setWinner(boolean winner) { isWinner = winner; }

    public int getRowVelocity() { return rowVelocity; }
    public int getColVelocity() { return colVelocity; }
    public int getMaxSpeed() { return maxSpeed; }
    public int getMoveOrder() { return moveOrder; }
    public int getWeightPosition() { return weightPosition; }
    public void updateWeightPosition(Racetrack track) {
        weightPosition = track.getWeight(row, col);
    }
    public List<Position> getPathHistory() { return pathHistory; }

    protected void setPosition(Position p) {
        this.row = p.getRow();
        this.col = p.getCol();
    }

    /**
     * Subclasses choose a destination each turn.
     */
    public abstract Position chooseDestination(Racetrack track, Car[] cars);

    /**
     * Shared move logic:
     *  - Ask subclass for destination
     *  - Check speed limits
     *  - Get Bresenham path
     *  - Check for boundary, wall, other car, finish
     *  - Update position and pathHistory
     *  - Return professor-style message about what happened
     */
    public String move(Racetrack track, Car[] cars) {
        if (isWinner) {
            return "";
        }

        Position start = getPosition();
        Position dest = chooseDestination(track, cars);

        if (dest == null || dest.equals(start)) {
            return "";
        }

        int dRow = dest.getRow() - start.getRow();
        int dCol = dest.getCol() - start.getCol();

        // Speed limits
        if (Math.abs(dRow) > rowVelocity ||
                Math.abs(dCol) > colVelocity ||
                Math.abs(dRow) + Math.abs(dCol) > maxSpeed) {
            // invalid move silently ignored for CPU cars
            return "";
        }

        List<Position> path = track.getPath(start, dest);
        Position previous = start;

        for (int i = 1; i < path.size(); i++) {
            Position current = path.get(i);

            // boundary
            if (track.isOutOfBounds(current)) {
                setPosition(previous);
                pathHistory.add(previous);
                return formatCollisionMessage("boundary", start, dest, current, previous);
            }

            // wall
            if (track.isWall(current)) {
                setPosition(previous);
                pathHistory.add(previous);
                return formatCollisionMessage("wall", start, dest, current, previous);
            }

            // other car
            Car other = findCarAt(cars, current, this);
            if (other != null) {
                setPosition(previous);
                pathHistory.add(previous);
                return formatCollisionMessage("car " + other.getIdNumber(), start, dest, current, previous);
            }

            // finish line
            if (track.isFinish(current)) {
                setPosition(current);
                pathHistory.add(current);
                isWinner = true;
                return formatWinnerMessage(start, dest, current);
            }

            // normal step
            setPosition(current);
            pathHistory.add(current);
            previous = current;
        }

        return "";
    }

    private Car findCarAt(Car[] cars, Position p, Car self) {
        for (Car car : cars) {
            if (car == null || car == self) continue;
            if (car.getRow() == p.getRow() && car.getCol() == p.getCol()) {
                return car;
            }
        }
        return null;
    }

    private String formatCollisionMessage(String type,
                                          Position start,
                                          Position dest,
                                          Position collision,
                                          Position previous) {

        String carName = "Car " + idNumber;

        return carName + " attempts to move to " + dest + ".\n" +
                "Before reaching the space, " + carName +
                " passes a " + type + " at " + collision + ".\n" +
                "This means " + carName +
                " will not reach its destination and will land on the previous safe spot " +
                previous + ".";
    }

    private String formatWinnerMessage(Position start, Position dest, Position finishPos) {
        String carName = "Car " + idNumber;

        return carName + " attempts to move to " + dest + ".\n" +
                "Before reaching the space, " + carName +
                " passes the finish line at " + finishPos + ".\n" +
                "CAR " + idNumber + " WINS!";
    }
}

