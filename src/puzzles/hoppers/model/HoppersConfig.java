package puzzles.hoppers.model;

import puzzles.common.Coordinates;
import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * A configuration for the Hoppers puzzle
 *
 * @author Cameron Wilson
 */
public class HoppersConfig implements Configuration{
    private final static char EMPTY = '.';
    private final static char RED_FROG = 'R';
    private final static char GREEN_FROG = 'G';
    private final int rows;
    private final int columns;
    private final char[][] grid;
    private Set<Coordinates> frogPositions = new HashSet<>();

    /**
     * Creates a HoppersConfig with a specified input filename
     *
     * @param filename the name of the file which contains a Hoppers puzzle
     * @throws IOException
     */
    public HoppersConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String[] dimensionLine = in.readLine().split("\\s+");
            rows = Integer.parseInt(dimensionLine[0]);
            columns = Integer.parseInt(dimensionLine[1]);
            grid = new char[rows][columns];

            for (int i = 0; i < rows; i++) {
                String[] gridLine = in.readLine().split("\\s+");
                for (int j = 0; j < columns; j++) {
                    char current = gridLine[j].charAt(0);
                    if (current == RED_FROG || current == GREEN_FROG) {
                        frogPositions.add(new Coordinates(i, j));
                    }
                    grid[i][j] = gridLine[j].charAt(0);
                }
            }
        }
    }

    /**
     * Copy constructor, does a copy of all values of another config
     * @param other the HopperConfig to copy from
     */
    private HoppersConfig(HoppersConfig other) {
        this.rows = other.rows;
        this.columns = other.columns;
        this.frogPositions = new HashSet<>(other.frogPositions);
        this.grid = new char[other.rows][other.columns];
        for (int i = 0; i < other.rows; i++) {
            this.grid[i] = Arrays.copyOf(other.grid[i], columns);
        }
    }

    /**
     * Returns whether this config is a solution or not
     *
     * @return true if the only remaining frog is the red one, false otherwise
     */
    @Override
    public boolean isSolution() {
        boolean isRedFrog = false;
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (grid[i][j] == GREEN_FROG) {
                    return false;
                } else if (grid[i][j] == RED_FROG) {
                    isRedFrog = true;
                }
            }
        }
        return isRedFrog;
    }

    /**
     * Checks whether this object and another are equal
     * @param obj the object to check if it is equal
     * @return true if obj is a HoppersConfig and the rows, column, grid, and frogPositions are the same,
     * false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HoppersConfig otherConfig) {
            return otherConfig.rows == rows && otherConfig.columns == columns &&
                    Arrays.deepEquals(otherConfig.grid, grid) && otherConfig.frogPositions.equals(frogPositions);
        }
        return false;
    }

    /**
     * Generates a hash code for this object
     * @return the hashcode made by adding the integers rows and columns to the grid's hashcode and the frog positions
     * set hashcode
     */
    @Override
    public int hashCode() {
        return rows + columns + Arrays.deepHashCode(grid) + frogPositions.hashCode();
    }

    /**
     * returns a string representing this config
     *
     * @return A string representing the grid of the config
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (char[] arr : grid) {
            builder.append(arr);
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Generates all neighbors/successors of this config
     *
     * @return successors of this config
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> neighbors = new ArrayList<>();
        // Go through each frog's coordinates
        for (Coordinates coord : frogPositions) {
            // Go to the neighbors (within bounds)
            for (int i = Math.max(0, coord.row() - 1); i <= Math.min(coord.row() + 1, rows - 1); i++) {
                for (int j = Math.max(0, coord.col() - 1); j <= Math.min(coord.col() + 1, columns - 1); j++) {
                    // Create integers for a row further and col further in the direction of the neighbor
                    int rowFurther = 2 * i - coord.row();
                    int colFurther = 2 * j - coord.col();
                    //Make sure that this is not the cell you are checking the neighbors of
                    if ((i != coord.row() || j != coord.col())) {
                        //Make sure that the space after the neighbor is not out of bounds
                        if (rowFurther >= 0 &&
                                rowFurther < rows && colFurther >= 0 && colFurther < columns) {
                            //If this is a horizontal or vertical neighbor, make sure that there is a frog to jump and
                            //this is an even row
                            if ((coord.col() == j || coord.row() == i) && grid[rowFurther][colFurther] == GREEN_FROG &&
                                    coord.row() % 2 == 0) {
                                /*
                                If this is an even row, and the neighbor is horizontal or vertical, and there is a
                                green frog at the row further than the neighbor,
                                create integers for the next valid space, which is three rows further than the neighbor
                                */
                                int threeRowsFurther = 2 * (2 * rowFurther - i) - rowFurther;
                                int threeColsFurther = 2 * (2 * colFurther - j) - colFurther;
                                //Make sure this space is within bounds
                                if (threeRowsFurther >= 0 &&
                                        threeRowsFurther < rows && threeColsFurther >= 0 && threeColsFurther < columns){
                                    //If it is also empty, then create and add a child config to make the move
                                    if (grid[threeRowsFurther][threeColsFurther] == EMPTY) {
                                        HoppersConfig child = new HoppersConfig(this);
                                        child.grid[coord.row()][coord.col()] = EMPTY;
                                        child.grid[rowFurther][colFurther] = EMPTY;
                                        child.frogPositions.remove(coord);
                                        child.frogPositions.add(new Coordinates(threeRowsFurther, threeColsFurther));
                                        child.frogPositions.remove(new Coordinates(rowFurther, colFurther));
                                        child.grid[threeRowsFurther][threeColsFurther] = grid[coord.row()][coord.col()];
                                        neighbors.add(child);
                                    }
                                }
                                //This else is for all diagonal neighbors
                            } else {
                                //Make sure there is an empty space beyond the neighbor and that there is a frog at the
                                //neighbor
                                if (grid[rowFurther][colFurther] == EMPTY && grid[i][j] == GREEN_FROG) {
                                    HoppersConfig child = new HoppersConfig(this);
                                    child.grid[coord.row()][coord.col()] = EMPTY;
                                    child.grid[i][j] = EMPTY;
                                    child.frogPositions.remove(coord);
                                    child.frogPositions.add(new Coordinates(rowFurther, colFurther));
                                    child.frogPositions.remove(new Coordinates(i, j));
                                    child.grid[rowFurther][colFurther] = grid[coord.row()][coord.col()];
                                    neighbors.add(child);
                                }
                            }
                        }
                    }
                }
            }
        }
        return neighbors;
    }
}
