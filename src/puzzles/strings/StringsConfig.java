package puzzles.strings;

import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.lang.StringBuffer;

public class StringsConfig implements Configuration {
    private String current;
    private String finish;
    public StringsConfig(String current, String finish) {
        this.current = current;
        this.finish = finish;
    }
    @Override
    public boolean isSolution() {
        if (this.current.equals(finish)) {
            return true;
        }
        return false;
    }

    /**
     * Each character will have two configurations.
     * @return
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();
        StringBuilder str = new StringBuilder(current.substring(0));
        for (int i = 0; i < current.length(); i++) {
            if (str.charAt(i) == 'A') {
                StringBuilder backStr = new StringBuilder(str);
                backStr.setCharAt(i, 'Z');
//                char forwardChar = (char) (str.charAt(i) + 1);
                StringBuilder forwardStr = new StringBuilder(str);
                forwardStr.setCharAt(i, (char) (str.charAt(i) + 1));
                StringsConfig back = new StringsConfig(backStr.toString(), finish);
                StringsConfig forward = new StringsConfig(forwardStr.toString(), finish);
                neighbors.add(back);
                neighbors.add(forward);
            } else if (str.charAt(i) == 'Z') {
                StringBuilder forwardStr = new StringBuilder(str);
                forwardStr.setCharAt(i, 'A');
                StringBuilder backStr = new StringBuilder(str);
                backStr.setCharAt(i, (char) (str.charAt(i) - 1));
                StringsConfig back = new StringsConfig(backStr.toString(), finish);
                StringsConfig forward = new StringsConfig(forwardStr.toString(), finish);
                neighbors.add(back);
                neighbors.add(forward);
            } else {
                StringBuilder backStr = new StringBuilder(str);
                backStr.setCharAt(i, (char) (str.charAt(i) - 1));
                StringBuilder forwardStr = new StringBuilder(str);
                forwardStr.setCharAt(i, (char) (str.charAt(i) + 1));
                StringsConfig back = new StringsConfig(backStr.toString(), finish);
                StringsConfig forward = new StringsConfig(forwardStr.toString(), finish);

                neighbors.add(back);
                neighbors.add(forward);
            }
        }
        return neighbors;
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof StringsConfig) {
            StringsConfig o = (StringsConfig) other;
            result = this.current.equals(o.current);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = this.current.hashCode() + finish.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return current;
    }
}
