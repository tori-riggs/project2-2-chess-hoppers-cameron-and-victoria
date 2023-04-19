package puzzles.hoppers.model;

import puzzles.common.solver.Configuration;

import java.io.IOException;
import java.util.Collection;

// TODO: implement your HoppersConfig for the common solver

public class HoppersConfig implements Configuration{
    public HoppersConfig(String filename) throws IOException {
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
