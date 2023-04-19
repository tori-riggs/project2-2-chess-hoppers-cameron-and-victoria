package puzzles.clock;

import puzzles.common.solver.Solver;

public class Clock {
    private int hours;
    private int start;
    private int end;

    /**
     * Create the clocks puzzle
     * @param hours the total hours in the clock
     * @param start the starting hour
     * @param end the goal hour
     */
    public Clock(int hours, int start, int end) {
        this.hours = hours;
        this.start = start;
        this.end = end;
    }

    /**
     * Create a Clock puzzle and call the solve function
     * @param args the args used to create the Clock puzzle (hours start stop)
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Clock start stop"));
        } else {
            int hours = Integer.parseInt(args[0]);
            int start = Integer.parseInt(args[1]);
            int end = Integer.parseInt(args[2]);
            Clock clock = new Clock(hours, start, end);
            ClockConfig startConfig = new ClockConfig(hours, start, end);
            ClockConfig goalConfig = new ClockConfig(hours, end, end);
            System.out.println("Hours: " + hours + ", Start: " + start + ", End: " + end);
            Solver solve = new Solver(startConfig, goalConfig);
            solve.solve();
        }
    }
}
