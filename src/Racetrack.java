import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Racetrack
 * ---------
 * Responsible for:
 *  - Loading the track from a text file (Format B: no dimension header)
 *  - Computing weights using all 8 neighbors
 *  - Providing helpers for walls, finish, bounds
 *  - Hiding 'T' when displaying
 *  - Generating Bresenham paths
 *  - Rendering the winner’s path at the end.
 */
public class Racetrack {

    private static final int WALL_WEIGHT = 9999;

    private static final char WALL = 'X';
    private static final char TRACK = 'T';
    private static final char FINISH = 'F';

    /** Raw track characters loaded from file or default. */
    private char[][] track;

    /** Weight grid storing distance from the finish line. */
    private int[][] weights;

    /**
     * Construct a racetrack from a file. If anything goes wrong,
     * fall back to a built-in default track.
     */
    public Racetrack(String fileName) {
        try {
            loadFromFile(fileName);
        } catch (Exception e) {
            System.out.println("Could not load track file. Using default track.");
            useDefaultTrack();
        }
        computeWeights();
    }

    /**
     * Load track from text file using Format B:
     * - No "rows cols" header
     * - Each non-empty line is a row
     * - All rows must have the same length
     */
    private void loadFromFile(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));
        List<String> lines = new ArrayList<>();

        while (in.hasNextLine()) {
            String line = in.nextLine().trim();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        in.close();

        if (lines.isEmpty()) {
            throw new RuntimeException("Track file is empty.");
        }

        int rows = lines.size();
        int cols = lines.get(0).length();

        track = new char[rows][cols];

        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);
            if (line.length() != cols) {
                throw new RuntimeException("All rows in track file must have the same length.");
            }
            for (int c = 0; c < cols; c++) {
                track[r][c] = line.charAt(c);
            }
        }
    }

    /**
     * Default track used if the file cannot be loaded.
     */
    private void useDefaultTrack() {
        track = new char[][] {
                {'X','X','X','X','X','X','X','X','X','X'},
                {'X','T','T','T','T','T','T','T','F','X'},
                {'X','T','X','X','X','T','X','T','T','X'},
                {'X','T','T','T','T','T','X','T','T','X'},
                {'X','T','X','X','X','T','T','T','T','X'},
                {'X','T','T','T','T','T','X','T','T','X'},
                {'X','T','X','T','X','T','T','T','T','X'},
                {'X','T','T','T','T','T','T','T','F','X'},
                {'X','T','T','T','T','T','T','T','T','X'},
                {'X','X','X','X','X','X','X','X','X','X'}
        };
    }

    /**
     * Compute weight grid:
     *  - Finish cells: 0
     *  - Track cells: distance to nearest finish
     *  - Walls: large weight (WALL_WEIGHT)
     *
     * Uses 8-neighbor relaxation until no more changes.
     */
    private void computeWeights() {
        int rows = track.length;
        int cols = track[0].length;

        weights = new int[rows][cols];

        // Initial values
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (track[r][c] == WALL) {
                    weights[r][c] = WALL_WEIGHT;
                } else if (track[r][c] == FINISH) {
                    weights[r][c] = 0;
                } else {
                    weights[r][c] = WALL_WEIGHT;
                }
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {

                    if (track[r][c] == WALL) continue;

                    int current = weights[r][c];
                    int best = current;

                    // Check 8 neighbors
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            if (dr == 0 && dc == 0) continue;

                            int nr = r + dr;
                            int nc = c + dc;
                            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;

                            if (weights[nr][nc] + 1 < best) {
                                best = weights[nr][nc] + 1;
                            }
                        }
                    }

                    if (best < current) {
                        weights[r][c] = best;
                        changed = true;
                    }
                }
            }
        }
    }

    // -------- helpers --------

    public int height() { return track.length; }
    public int width() { return track[0].length; }
    public int getWeight(int r, int c) { return weights[r][c]; }
    public char getTrack(int r, int c) { return track[r][c]; }

    public boolean isOutOfBounds(Position p) {
        int r = p.getRow();
        int c = p.getCol();
        return r < 0 || r >= height() || c < 0 || c >= width();
    }

    public boolean isWall(Position p) {
        if (isOutOfBounds(p)) return true;
        return track[p.getRow()][p.getCol()] == WALL;
    }

    public boolean isFinish(Position p) {
        if (isOutOfBounds(p)) return false;
        return track[p.getRow()][p.getCol()] == FINISH;
    }

    /**
     * Character for display: hide 'T' as space to make track cleaner.
     */
    public char getDisplayChar(int r, int c) {
        char ch = track[r][c];
        if (ch == TRACK) return ' ';
        return ch;
    }

    /**
     * Display the current track with cars overlaid.
     */
    public void displayTrack(Car[] cars) {
        System.out.println();
        for (int r = 0; r < height(); r++) {
            for (int c = 0; c < width(); c++) {
                char ch = getDisplayChar(r, c);
                for (Car car : cars) {
                    if (car != null && car.getRow() == r && car.getCol() == c) {
                        ch = car.getIdNumber();
                    }
                }
                System.out.print(ch + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Find highest-weight unused track cell to place cars at start.
     */
    public Position findHighestWeightStart(boolean[][] used) {
        int best = -1;
        Position bestPos = null;

        for (int r = 0; r < height(); r++) {
            for (int c = 0; c < width(); c++) {
                if (track[r][c] != TRACK) continue;
                if (used[r][c]) continue;

                int w = weights[r][c];
                if (w < WALL_WEIGHT && w > best) {
                    best = w;
                    bestPos = new Position(r, c);
                }
            }
        }
        return bestPos;
    }

    /**
     * Bresenham's line: returns all grid points between start and end (inclusive).
     */
    public List<Position> getPath(Position start, Position end) {
        List<Position> path = new ArrayList<>();

        int x0 = start.getRow();
        int y0 = start.getCol();
        int x1 = end.getRow();
        int y1 = end.getCol();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;
        int err = dx - dy;

        int x = x0;
        int y = y0;

        while (true) {
            path.add(new Position(x, y));
            if (x == x1 && y == y1) break;

            int e2 = err * 2;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }

        return path;
    }

    /**
     * Final display with the winning car's path drawn onto the track.
     */
    public void printWithWinningPath(List<Position> path, Car[] cars, Car winner) {
        System.out.println();
        System.out.println("Final Track – CAR " + winner.getIdNumber() + " WINS!");
        System.out.println();

        char[][] temp = new char[height()][width()];

        // base
        for (int r = 0; r < height(); r++) {
            for (int c = 0; c < width(); c++) {
                temp[r][c] = getDisplayChar(r, c);
            }
        }

        // path
        if (path != null) {
            for (Position p : path) {
                if (!isOutOfBounds(p) && !isWall(p)) {
                    temp[p.getRow()][p.getCol()] = winner.getIdNumber();
                }
            }
        }

        // cars
        for (Car car : cars) {
            if (car != null) {
                Position p = car.getPosition();
                if (!isOutOfBounds(p)) {
                    temp[p.getRow()][p.getCol()] = car.getIdNumber();
                }
            }
        }

        // print
        for (int r = 0; r < height(); r++) {
            for (int c = 0; c < width(); c++) {
                System.out.print(temp[r][c] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
