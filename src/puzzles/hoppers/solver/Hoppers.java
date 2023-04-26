package puzzles.hoppers.solver;

import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;

import java.io.IOException;

public class Hoppers {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Hoppers filename");
        }

        System.out.println("File: " + args[0]);
        HoppersConfig initialConfig = new HoppersConfig(args[0]);
        Solver solver = new Solver(initialConfig);

        System.out.println(initialConfig);

        solver.solveAndPrint();
    }
}
