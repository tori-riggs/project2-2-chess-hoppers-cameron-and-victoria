package puzzles.common.solver;

import java.util.*;
import java.util.LinkedList;

public class Solver {
    private Configuration start;
    private Configuration end;

    /**
     * Create solver for the clock and strings puzzle
     * @param start the starting node
     * @param end the goal node
     */
    public Solver(Configuration start, Configuration end) {
        this.start = start;
        this.end = end;
    }

    public void solve() {
        List<Configuration> queue = new LinkedList<>();
        Map<Configuration, Configuration> predecessors = new HashMap<>();
        int totalConfig = 0;
        queue.add(start);
        predecessors.put(start, start);

        totalConfig++;
        while (!queue.isEmpty()) {
            Configuration current = queue.remove(0);

            if (current.equals(end)) {
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

        int step = 0;
        System.out.println("Total configs: " + totalConfig);
        System.out.println("Unique configs: " + predecessors.size());
        List<String> path = constructPath(predecessors, start, end);
        if (path.size() == 0) {
            System.out.println("No solution.");
        } else {
            for (String s : path) {
                System.out.println("Step " + step + ": " + s);
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
