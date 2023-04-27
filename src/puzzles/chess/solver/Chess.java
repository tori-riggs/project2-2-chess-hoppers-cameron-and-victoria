package puzzles.chess.solver;

import puzzles.chess.model.ChessConfig;

import java.io.IOException;
import puzzles.common.solver.Solver;

public class Chess {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Chess filename");
        } else {
            try {
                String filename = args[0];
                ChessConfig start = new ChessConfig(filename);
                System.out.println(start.toString());
                Solver solver = new Solver(start);
                solver.solve();
            } catch (IOException ex) {
                System.err.println("IOException");
            }
        }
    }
}
