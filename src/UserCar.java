import java.util.Scanner;

/**
 * UserCar
 * -------
 * User-controlled car.
 * Each turn, the user chooses vertical and horizontal movement.
 * If the move exceeds speed limits, it is rejected with an explanation.
 */
public class UserCar extends Car {

    /** Scanner used to read keyboard input for user movement. */
    private Scanner in;

    public UserCar(char id,
                   int row,
                   int col,
                   int rowVel,
                   int colVel,
                   int maxSpeed,
                   Racetrack track,
                   Scanner in) {

        super(id, row, col, rowVel, colVel, maxSpeed, track);
        this.in = in;
    }

    @Override
    public Position chooseDestination(Racetrack track, Car[] cars) {

        System.out.println("User Car " + getIdNumber() + " is currently at " + getPosition());

        System.out.print("Enter vertical move (negative = up, positive = down): ");
        int dRow = in.nextInt();

        System.out.print("Enter horizontal move (negative = left, positive = right): ");
        int dCol = in.nextInt();

        boolean rowOK = Math.abs(dRow) <= getRowVelocity();
        boolean colOK = Math.abs(dCol) <= getColVelocity();
        boolean speedOK = Math.abs(dRow) + Math.abs(dCol) <= getMaxSpeed();

        if (!rowOK || !colOK || !speedOK) {
            System.out.println();
            System.out.println("❌ INVALID MOVE!");
            System.out.println("Your attempted move: (" + dRow + ", " + dCol + ")");
            System.out.println("Rules for User Car " + getIdNumber() + ":");
            System.out.println(" - Vertical movement allowed:  -" + getRowVelocity() + " to +" + getRowVelocity());
            System.out.println(" - Horizontal movement allowed: -" + getColVelocity() + " to +" + getColVelocity());
            System.out.println(" - |vertical| + |horizontal| must be ≤ " + getMaxSpeed());
            System.out.println("This move was rejected. Your turn is skipped.");
            System.out.println();

            return getPosition();
        }

        int newRow = getRow() + dRow;
        int newCol = getCol() + dCol;
        return new Position(newRow, newCol);
    }
}
