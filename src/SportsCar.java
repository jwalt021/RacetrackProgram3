/**
 * SportsCar
 * ---------
 * CPU car that focuses on speed.
 * Always chooses the lowest-weight reachable destination.
 * Does not avoid collisions, so it crashes often (as required).
 */
public class SportsCar extends Car {

    public SportsCar(char id,
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

                int nr = getRow() + dr;
                int nc = getCol() + dc;

                if (nr < 0 || nr >= track.height() ||
                        nc < 0 || nc >= track.width()) {
                    continue;
                }

                if (track.getTrack(nr, nc) == 'X') continue;

                int w = track.getWeight(nr, nc);
                if (w < bestWeight) {
                    bestWeight = w;
                    bestPos = new Position(nr, nc);
                }
            }
        }

        return bestPos;
    }
}
