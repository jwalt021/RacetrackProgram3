import java.util.Scanner;

/**
 * RaceGame
 * --------
 * Main driver:
 *  - Builds racetrack from track1.txt
 *  - Places cars at highest-weight starting positions
 *  - Runs race until a car wins
 *  - Displays the winning car's path at the end.
 */
public class RaceGame {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        // Make sure track1.txt is in the project root (same folder as src).
        String fileName = "track1.txt";

        Racetrack track = new Racetrack(fileName);

        boolean[][] used = new boolean[track.height()][track.width()];

        Position p1 = track.findHighestWeightStart(used);
        used[p1.getRow()][p1.getCol()] = true;

        Position p2 = track.findHighestWeightStart(used);
        used[p2.getRow()][p2.getCol()] = true;

        Position p3 = track.findHighestWeightStart(used);
        used[p3.getRow()][p3.getCol()] = true;

        int rowVel = 3;
        int colVel = 3;
        int maxSpeed = 5;

        Car[] cars = new Car[3];
        cars[0] = new SportsCar('1', p1.getRow(), p1.getCol(), rowVel, colVel, maxSpeed, track);
        cars[1] = new AgileCar ('2', p2.getRow(), p2.getCol(), rowVel, colVel, maxSpeed, track);
        cars[2] = new UserCar  ('3', p3.getRow(), p3.getCol(), rowVel, colVel, maxSpeed, track, in);

        Car winner = null;
        boolean raceOver = false;

        System.out.println("Starting Race!");
        track.displayTrack(cars);

        while (!raceOver) {

            // update weights at current positions
            for (Car c : cars) {
                c.updateWeightPosition(track);
            }

            // sort cars by order, then by weightPosition
            sortCars(cars);

            // each car moves in order
            for (Car c : cars) {
                String msg = c.move(track, cars);
                if (!msg.isEmpty()) {
                    System.out.println(msg);
                }

                if (c.isWinner()) {
                    winner = c;
                    raceOver = true;
                    break;
                }
            }

            track.displayTrack(cars);
        }

        if (winner != null) {
            track.printWithWinningPath(winner.getPathHistory(), cars, winner);
        }

        in.close();
    }

    /**
     * Selection sort on cars:
     * 1. moveOrder (lower first)
     * 2. weightPosition (lower first)
     */
    private static void sortCars(Car[] cars) {
        int n = cars.length;
        for (int i = 0; i < n - 1; i++) {
            int bestIndex = i;
            for (int j = i + 1; j < n; j++) {
                Car a = cars[j];
                Car b = cars[bestIndex];

                if (a.getMoveOrder() < b.getMoveOrder()) {
                    bestIndex = j;
                } else if (a.getMoveOrder() == b.getMoveOrder() &&
                        a.getWeightPosition() < b.getWeightPosition()) {
                    bestIndex = j;
                }
            }
            Car temp = cars[i];
            cars[i] = cars[bestIndex];
            cars[bestIndex] = temp;
        }
    }
}
