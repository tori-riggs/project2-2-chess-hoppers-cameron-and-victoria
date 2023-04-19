package puzzles.strings;

import puzzles.common.solver.Solver;

public class Strings {
    private String start;
    private String finish;

    /**
     * Create a Strings puzzle
     * @param start the starting string
     * @param finish the goal string
     */
    public Strings(String start, String finish) {
        this.start = start;
        this.finish = finish;
    }

    /**
     * Create a Strings puzzle from the command line args
     * Call the solve function
     * @param args the args used to create the Strings puzzle (start finish)
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Strings start finish"));
        } else {
            String start = args[0];
            String finish = args[1];
            Strings str = new Strings(start, finish);
            System.out.println("Start: " + start + ", End: " + finish);
            StringsConfig startConfig = new StringsConfig(start, finish);
            StringsConfig goalConfig = new StringsConfig(finish, finish);
            Solver solve = new Solver(startConfig, goalConfig);
            solve.solve();
        }
    }
}
