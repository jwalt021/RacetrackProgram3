/**
 * Position
 * --------
 * Simple helper class that stores a row and column on the racetrack.
 * Used throughout the program to represent locations.
 */
public class Position {

    /** Row index on the racetrack grid. */
    private final int row;

    /** Column index on the racetrack grid. */
    private final int col;

    /**
     * Construct a Position with the given row and column.
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /** @return row index */
    public int getRow() {
        return row;
    }

    /** @return column index */
    public int getCol() {
        return col;
    }

    /**
     * Print the position in the form (row,col).
     */
    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }

    /**
     * Two positions are equal if they have the same row and column.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
