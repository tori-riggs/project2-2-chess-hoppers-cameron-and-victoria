package puzzles.hoppers.ptui;

import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersModel;

import java.io.IOException;
import java.util.Scanner;

public class HoppersPTUI implements Observer<HoppersModel, String> {
    private HoppersModel model;
    private boolean initialized = false;

    public void init(String filename) throws IOException {
        this.model = new HoppersModel(filename);
        this.model.addObserver(this);
        this.initialized = true;
        this.model.load(filename);
        displayHelp();
    }

    @Override
    public void update(HoppersModel model, String data) {
        if (!initialized) return;
        System.out.println(data);
        System.out.println(model.getCurrentConfig().prettyToString());
    }

    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    public void run() {
        Scanner in = new Scanner( System.in );
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if (words.length > 0) {
                if (words[0].startsWith( "q" )) {
                    break;
                } else if (words[0].startsWith("l")) {
                    model.load(words[1]);
                } else if (words[0].startsWith("h")) {
                    model.getHint();
                } else if (words[0].startsWith("s")) {
                    model.select(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                } else if (words[0].startsWith("r")) {
                    model.reset();
                }
                else {
                    displayHelp();
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            try {

                HoppersPTUI ptui = new HoppersPTUI();
                ptui.init(args[0]);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}
