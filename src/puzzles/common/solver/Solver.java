package puzzles.common.solver;

import puzzles.chess.model.ChessConfig;
import puzzles.chess.solver.Chess;

import java.util.*;
import java.util.LinkedList;

public class Solver {
    private Configuration start;
    private Configuration end;
    private int totalConfig;
    private int uniqueConfig;

    /**
     * Create solver for the clock and strings puzzle
     * @param start the starting node
     */
    public Solver(Configuration start) {
        this.start = start;
        this.totalConfig = 0;
        this.uniqueConfig = 0;
    }

    public List<Configuration> solve() {
        List<Configuration> queue = new LinkedList<>();
        Map<Configuration, Configuration> predecessors = new HashMap<>();
        queue.add(start);
        predecessors.put(start, null);

        totalConfig++;
        while (!queue.isEmpty()) {
            Configuration current = queue.remove(0);

            if (current.isSolution()) {
                end = current;
                break;
            }

            for (Configuration neighbors : current.getNeighbors()) {
                totalConfig++;
                if (!predecessors.containsKey(neighbors)) {
                    predecessors.put(neighbors, current);
                    queue.add(neighbors);
                }
            }
        }

        uniqueConfig = predecessors.size();
        return constructPath(predecessors, start, end);
    }

    public void solveAndPrint() {
        List<Configuration> path = solve();
        System.out.println("Total configs: " + totalConfig);
        System.out.println("Unique configs: " + uniqueConfig);

        int step = 0;
        if (path.isEmpty()) {
            System.out.println("No solution.");
        } else {
            for (Configuration s : path) {
                System.out.println("Step " + step + ": " + s.toString());
                step++;
            }
        }
    }

    /**
     * Construct the path to get from the start to the end goal
     * using the predecessor map
     * @param predMap the map of predecessors
     * @param start the starting config
     * @param end the goal config
     * @return a path of strings for each config to get to the goal config.
     */
    public List<Configuration> constructPath(Map<Configuration, Configuration> predMap,
                                      Configuration start, Configuration end) {
        List<Configuration> path = new LinkedList<>();
        if (end == null) {
            return path;
        }
//            path.add(end);
//        } else {
//            return path;
//        }
        if (predMap.containsKey(end)) {
            Configuration curr = end;
            while (!curr.equals(start)) {
                path.add(0, curr);
                curr = predMap.get(curr);
            }
            path.add(0, start);
        }
        return path;
    }
}
