
import menupackage.MenuAnimation;
import gamepackage.AnimationRunner;
import gamepackage.GameFlow;
import menupackage.HighScoresTable;
import gamepackage.LevelSpecificationReader;
import menupackage.KeyPressStoppableAnimation;
import menupackage.HighScoresAnimation;


import interfaces.LevelInformation;
import interfaces.Menu;
import interfaces.Task;

import biuoop.GUI;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Class Name: Ass6Game.
 */
public class Ass7Game {
    /**
     * Function Name: main.
     * Function Operation: run the game with the levels in the
     * the args array, if args is null the level games
     * are 1, 2, 3 and 4.
     *
     * @param args - the levels of the game.
     */
    public static void main(String[] args) {
        boolean bool = true;
        GUI gui = new GUI("Game_Initialize", 800, 600);
        int fps = 60;
        double dt = 1 / (double) (fps);
        AnimationRunner ar = new AnimationRunner(gui, fps);
        //add keyboard sensor to the game
        biuoop.KeyboardSensor keyboard = gui.getKeyboardSensor();

        GameFlow gameFlow = new GameFlow(ar, keyboard, gui);
        gameFlow.setDt(dt);
        File file = new File("highscores.txt");
        HighScoresTable highScoresTable = HighScoresTable.loadFromFile(file);


        Menu<Task<Void>> levelSetsMenu = new MenuAnimation<Task<Void>>("Level Sets", ar, keyboard);

        Menu<Task<Void>> menu = new MenuAnimation<Task<Void>>("Space Invaders", ar, keyboard);

        menu.addSelection("s", "Start Game", new Task<Void>() {
            @Override
            public Void run() {
                List<LevelInformation> levelInformations =
                        null;
                try {
                    levelInformations = new LevelSpecificationReader()
                            .fromFile("definitions/enemy_level_definitions.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    gameFlow.runLevels(levelInformations);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        menu.addSelection("h", "High Scores Table", new Task<Void>() {
            @Override
            public Void run() {
                try {
                    highScoresTable.load(file);
                } catch (IOException e) {
                    e.getMessage();
                }
                ar.run(new KeyPressStoppableAnimation(keyboard, "space",
                        new HighScoresAnimation(highScoresTable, "space", keyboard)));
                return null;
            }
        });
        menu.addSelection("e", "Exit", new Task<Void>() {
            public Void run() {
                System.exit(0);
                return null;
            }
        });

        while (true) {
            ar.run(menu);
            Task<Void> task = menu.getStatus();
            task.run();
            menu.resetGame();
        }

    }
}
