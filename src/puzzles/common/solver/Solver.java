package puzzles.common.solver;

import java.util.*;
import java.util.LinkedList;

public class Solver {
    private Configuration start;
    private Configuration end;
    private int totalConfig = 0;
    private int uniqueConfig = 0;

    /**
     * Create solver for the clock and strings puzzle
     * @param start the starting node
     */
    public Solver(Configuration start) {
        this.start = start;
    }
    public List<String> solve() {
        List<Configuration> queue = new LinkedList<>();
        Map<Configuration, Configuration> predecessors = new HashMap<>();
        queue.add(start);
        predecessors.put(start, start);

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
        List<String> path = constructPath(predecessors, start, end);
        if (path.size() == 0) {
            path.add("No solution.");
        }
        return path;
    }

    public void solveAndPrint() {
        List<String> path = solve();
        System.out.println("Total configs: " + totalConfig);
        System.out.println("Unique configs: " + uniqueConfig);

        int step = 0;
        for (String s : path) {
            if (!s.equals("No solution.")) {
                System.out.println("Step " + step + ": " + s.toString());
                step++;
            } else {
                System.out.println(s);
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
    public List<String> constructPath(Map<Configuration, Configuration> predMap,
                                      Configuration start, Configuration end) {
        List<String> path = new LinkedList<>();

        if (predMap.containsKey(end)) {
            Configuration curr = end;
            while (!curr.equals(start)) {
                path.add(0, curr.toString());
                curr = predMap.get(curr);
            }
            path.add(0, start.toString());
        }
        return path;
    }
}
