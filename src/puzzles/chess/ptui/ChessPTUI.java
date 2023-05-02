package puzzles.chess.ptui;

import javafx.geometry.Pos;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;
import puzzles.chess.model.Position;

import java.io.IOException;
import java.util.Scanner;

public class ChessPTUI implements Observer<ChessModel, String> {
    private ChessModel model;
    private String filename;

    public void init(String filename) throws IOException {
        this.filename = filename;
        this.model = new ChessModel(filename);
        this.model.addObserver(this);
        model.load(filename);
        displayHelp();
    }

    @Override
    public void update(ChessModel model, String data) {
        // for demonstration purposes
        System.out.println(data);
        System.out.println(model);
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
        for (; ;) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if (words.length > 0) {
                if (words[0].startsWith( "q" )) {
                    break;
                } else if (words[0].startsWith("h")) {
                    model.hint();
                } else if (words[0].startsWith("l")) {
                    model.load(words[1]);
                } else if (words[0].startsWith("s")) {
                    model.select(Integer.parseInt(words[1]),
                            Integer.parseInt(words[2]));
                } else if (words[0].startsWith("r")) {
                    model.reset();
                } else {
                    displayHelp();
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ChessPTUI filename");
        } else {
            try {
                ChessPTUI ptui = new ChessPTUI();
                ptui.init(args[0]);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}

