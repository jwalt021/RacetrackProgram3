import java.util.List;

/**
 * AgileCar
 * --------
 * CPU car that focuses on maneuvering.
 * It only selects moves whose full Bresenham path does NOT collide
 * with walls, boundaries, or other cars.
 */
public class AgileCar extends Car {

    public AgileCar(char id,
                    int row,
                    int col,
                    int rowVel,
                    int colVel,
                    int maxSpeed,
                    Racetrack track) {

        super(id, row, col, rowVel, colVel, maxSpeed, track);
    }

    @Override
    public Position chooseDestination(Racetrack track, Car[] cars) {
        int bestWeight = track.getWeight(getRow(), getCol());
        Position bestPos = getPosition();

        for (int dr = -getRowVelocity(); dr <= getRowVelocity(); dr++) {
            for (int dc = -getColVelocity(); dc <= getColVelocity(); dc++) {

                if (dr == 0 && dc == 0) continue;
                if (Math.abs(dr) + Math.abs(dc) > getMaxSpeed()) continue;

                Position candidate = new Position(getRow() + dr, getCol() + dc);

                if (track.isOutOfBounds(candidate)) continue;
                if (track.isWall(candidate)) continue;

                // Skip any move where the path would collide
                if (wouldCollide(track, cars, candidate)) {
                    continue;
                }

                int w = track.getWeight(candidate.getRow(), candidate.getCol());
                if (w < bestWeight) {
                    bestWeight = w;
                    bestPos = candidate;
                }
            }
        }

        return bestPos;
    }

    /**
     * Simulate path to a candidate destination.
     * Return true if the path would collide with a wall, boundary, or car.
     */
    private boolean wouldCollide(Racetrack track, Car[] cars, Position dest) {
        List<Position> path = track.getPath(getPosition(), dest);

        for (int i = 1; i < path.size(); i++) {
            Position p = path.get(i);

            if (track.isOutOfBounds(p)) return true;
            if (track.isWall(p)) return true;

            for (Car car : cars) {
                if (car == null || car == this) continue;
                if (car.getRow() == p.getRow() && car.getCol() == p.getCol()) {
                    return true;
                }
            }

            // Finish line is safe (AgileCar is happy to win)
            if (track.isFinish(p)) return false;
        }

        return false;
    }
}
