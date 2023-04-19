package puzzles.clock;

import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;

public class ClockConfig implements Configuration {
    /** The current node for the config */
    private int hours;
    private int current;
    private int end;

    public ClockConfig(int hours, int current, int end) {
        this.hours = hours;
        this.current = current;
        this.end = end;
    }


    @Override
    public boolean isSolution() {
        if (this.current == end) {
            return true;
        }
        return false;
    }

    /**
     * Get the neighbors of the current node.
     * You can only go forwards or backwards by one
     * @return a list of the neighbors of the current hour
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();
        if (current == 1) {
            // going back an hour will make the current equal
            // to the number of hours
            // Ex. At 1, going back an hour will make the current hour 12
            Configuration back = new ClockConfig(hours, hours, this.end);
//            forward = current++;
            Configuration forward = new ClockConfig(hours, current + 1, this.end);
            neighbors.add(back);
            neighbors.add(forward);
        } else if (current == hours) {
            // Ex: If current is at 12, going forward an hour will make it 1.
            Configuration back = new ClockConfig(hours, current - 1, this.end);
            Configuration forward = new ClockConfig(hours, 1, this.end);
//            forward = 1;
            neighbors.add(back);
            neighbors.add(forward);
        } else {
            Configuration back = new ClockConfig(hours, current - 1, this.end);
            Configuration forward = new ClockConfig(hours, current + 1, this.end);
            neighbors.add(back);
            neighbors.add(forward);
        }
        return neighbors;
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof ClockConfig) {
            ClockConfig o = (ClockConfig) other;
            result = this.current == o.current;
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = this.current + end;
        return result;
    }

    @Override
    public String toString() {
        return (String.valueOf(current));
    }
}
