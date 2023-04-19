package puzzles.chess.model;

import puzzles.common.solver.Configuration;

import java.io.IOException;
import java.util.Collection;

// TODO: implement your ChessConfig for the common solver

public class ChessConfig implements Configuration {
    public ChessConfig(String filename) throws IOException {
    }

    @Override
    public boolean isSolution() {
        return false;
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        return null;
    }
}
