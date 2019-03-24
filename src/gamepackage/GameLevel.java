package gamepackage;


import biuoop.DialogManager;
import indicators.LivesIndicator;
import indicators.ScoreIndicator;
import interfaces.Animation;
import interfaces.LevelInformation;
import interfaces.Sprite;
import menupackage.HighScoresTable;
import menupackage.EndGame;
import menupackage.HighScoresAnimation;
import menupackage.KeyPressStoppableAnimation;
import menupackage.PauseScreen;
import sprites.Ball;
import sprites.Block;
import sprites.Paddle;
import sprites.SpriteCollection;
import biuoop.DrawSurface;
import biuoop.GUI;
import biuoop.KeyboardSensor;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import interfaces.Collidable;
import collisiondetection.GameEnvironment;
import geomitryprimitives.Point;
import geomitryprimitives.Rectangle;
import geomitryprimitives.Velocity;
import hitlisteners.BallRemover;
import hitlisteners.BlockRemover;
import hitlisteners.ScoreTrackingListener;

/**
 * Class Name: GameLevel.
 */
public class GameLevel implements Animation {
    private LevelInformation levelInformation;
    private SpriteCollection sprites;
    private GameEnvironment environment;
    private AnimationRunner runner;
    private boolean running;
    private boolean firstTime;
    private int startSpeed;
    private int levelNo;

    //magic numbers
    private int widthGui = 800;
    private int heightGui = 600;
    private int frameblockHeight = 25;
    private int frameblockWidth = 40;
    private int blockHeight = 20;
    private int blockWidth = 50;
    private int padHeight = 10;


    //start position to create blocks
    private int startX = 800;
    private int startY = 150;
    private int blockPerRow = 12;

    private int ballNumber = 3;
    private boolean keepRun = true;
    private List<Ball> balls = new ArrayList<Ball>();
    private Paddle pad;
    private double dt;
    private long currentTime = System.currentTimeMillis();
    private long stopTime = currentTime + 350;
    private long currentTime2 = System.currentTimeMillis();
    private long stopTime2 = currentTime + 500;


    //GUI gui = new GUI("Game_Initialize", widthGui, heightGui);
    private GUI gui;
    //add keyboard sensor to the game
    private biuoop.KeyboardSensor keyboard;
    private CountdownAnimation cda;


    private BlockRemover br = new BlockRemover(this, null);
    private BallRemover ballRemover;
    private ScoreTrackingListener stl;
    private LivesIndicator li;
    private Counter score;
    private Counter lives;
    private boolean last;
    private HighScoresTable scores;
    private boolean restartLevel;
    private List<Point> startPos = new ArrayList<Point>();


    /**
     * Function Name: GameLevel.
     * Function Operation: create game environment and sprite collection.
     *
     * @param le             - level type
     * @param ks             - keyboard sensor
     * @param runner         - animation runner
     * @param gui            - the gui
     * @param stl            - score tracking listener
     * @param livesIndicator - live indicator
     * @param last           - if last level or not
     */
    public GameLevel(LevelInformation le, KeyboardSensor ks, AnimationRunner runner,
                     GUI gui, ScoreTrackingListener stl, LivesIndicator livesIndicator, boolean last) {
        this.runner = runner;
        this.environment = new GameEnvironment();
        this.sprites = new SpriteCollection();
        this.sprites.setTime(runner.getFramesPerSecond());
        this.levelInformation = le;
        this.keyboard = ks;
        this.gui = gui;
        this.stl = stl;
        this.li = livesIndicator;
        this.score = this.stl.getCurrentScore();
        this.lives = this.li.getLives();
        this.last = last;
        this.restartLevel = false;
    }

    /**
     * Function Name: returnEnviroment.
     *
     * @return the game environment
     */
    public GameEnvironment returnEnviroment() {
        return this.environment;
    }

    /**
     * Function Name: addCollidable.
     * Function Operation: add collidable to the game environment.
     *
     * @param c - the collidable
     */
    public void addCollidable(Collidable c) {
        this.environment.addCollidable(c);
    }

    /**
     * Function Name: removeCollidable.
     * Function Operation: remove collidable to the game environment.
     *
     * @param c - the collidable
     */
    public void removeCollidable(Collidable c) {
        this.environment.removeCollidable(c);
    }

    /**
     * Function Name: addSprite.
     * Function Operation: add sprite to the game environment.
     *
     * @param s - the sprite
     */
    public void addSprite(Sprite s) {
        this.sprites.addSprite(s);
    }

    /**
     * Function Name: removeSprite.
     * Function Operation: remove sprite to the game environment.
     *
     * @param s - the sprite
     */
    public void removeSprite(Sprite s) {
        this.sprites.removeSprite(s);
    }

    /**
     * Function Name: setGui.
     * Function Operation: set the gui of the game.
     *
     * @param guiGame - the gui of the game.
     */
    public void setGui(GUI guiGame) {
        this.gui = guiGame;
    }

    /**
     * Function Name: getGui.
     *
     * @return the gui of the game
     */
    public GUI getGui() {
        return this.gui;
    }

    /**
     * Function Name: initialize.
     * Function Operation: Initialize a new game: create the Blocks and Ball (and Paddle)
     * and add them to the game.
     */
    public void initialize() {
        ballRemover = new BallRemover(this, new Counter(this.levelInformation.numberOfBalls()));
        //create the gui
        //add frame blocks to the game
        this.sprites.addSprite(this.levelInformation.getBackground());
        indicationStartPoint();
        createEnemies();
        creatingShieldBlocks();
        this.firstTime = false;

        //this.runner = new AnimationRunner(this.gui,60);

    }

    /**
     * Function name: run.
     * Function Operation: Run the game -- start the animation loop.
     */
    public void playOneTurn() {
        this.creatingIndicators();
        createPaddle();
        this.pad.setBalls(balls);
        this.running = true;
        cda = new CountdownAnimation(2000, 3, sprites);
        this.runner.run(cda);
        this.runner.run(this);
    }

    /**
     * Function Name: creatingShieldBlocks.
     * Function Operation: create shield blocks.
     */
    public void creatingShieldBlocks() {
        //creating right shield block
        startX = 50;
        startY = 500;
        for (int i = 0; i < 15; i = i + 5) {
            for (int j = 0; j < 150; j = j + 5) {
                Block b = new Block(new Rectangle(new Point(startX, startY),
                        5, 5));
                b.setHitNum(1);
                b.setShield(true);
                b.setVelocity(new Velocity(0, 0));
                b.addHitListener(br);
                b.addHitListener(stl);
                b.addHitListener(ballRemover);
                b.addToGame(this);
                startX = startX + 5;
            }
            startX = 50;
            startY = startY + 5;
        }

        //creating middle shield block
        startX = 325;
        startY = 500;
        for (int i = 0; i < 15; i = i + 5) {
            for (int j = 0; j < 150; j = j + 5) {
                Block b = new Block(new Rectangle(new Point(startX, startY),
                        5, 5));
                b.setHitNum(1);
                b.setShield(true);
                b.setVelocity(new Velocity(0, 0));
                b.addHitListener(br);
                b.addHitListener(stl);
                b.addHitListener(ballRemover);
                b.addToGame(this);
                startX = startX + 5;
            }
            startX = 325;
            startY = startY + 5;
        }

        //creating left shield block
        startX = 600;
        startY = 500;
        for (int i = 0; i < 15; i = i + 5) {
            for (int j = 0; j < 150; j = j + 5) {
                Block b = new Block(new Rectangle(new Point(startX, startY),
                        5, 5));
                b.setHitNum(1);
                b.setShield(true);
                b.setVelocity(new Velocity(0, 0));
                b.addHitListener(br);
                b.addHitListener(stl);
                b.addHitListener(ballRemover);
                b.addToGame(this);
                startX = startX + 5;
            }
            startX = 600;
            startY = startY + 5;
        }

    }

    /**
     * Function Name: createBallsOnTopOfPaddle.
     * Function Operation: create the balls of the game.
     */
    public void createBallsOnTopOfPaddle() {
        Random rand = new Random();
        balls = new ArrayList<Ball>();
        ballRemover.setRemainingBalls(new Counter(1));
        for (int i = 0; i < 1; i++) {
            Ball ball = new Ball(pad.leftUpPoint().getX() + this.levelInformation.paddleWidth() / 2,
                    this.pad.leftUpPoint().getY() - 15, 3, Color.WHITE.darker());
            ball.setHitPoints(1);
            ball.setBallOfEnemy(false);
            Velocity v = Velocity.fromAngleAndSpeed(0, 8);
            v.setSpeed(8);
            if (i == this.levelInformation.numberOfBalls() - 1) {
                this.firstTime = true;
            }
            ball.setVelocity(v);
            ball.setGameEnvironment(this.returnEnviroment());
            ball.addHitListener(ballRemover);
            ball.addToGame(this);
            balls.add(ball);
        }
    }

    /**
     * Function Name: createPaddle.
     * Function Operation: create the paddle of the game.
     */
    public void createPaddle() {
        pad = null;
        pad = new Paddle(new Rectangle(new Point(widthGui / 2 - this.levelInformation.paddleWidth() / 2,
                heightGui - 2 * frameblockHeight),
                this.levelInformation.paddleWidth(), padHeight), keyboard);
        pad.getGuiWidth(widthGui);
        pad.setGameLevel(this);
        //pad.setBalls(ball1, ball2);
        pad.setSideFrameBlockWidth(frameblockHeight);
        pad.setSpeed(this.levelInformation.paddleSpeed() * this.dt);
        pad.addToGame(this);
    }

    /**
     * Function Name: indicationStartPoint.
     * Function Operation: start point of each enemy.
     */
    public void indicationStartPoint() {
        int colNum = 1;
        int rowNum = 1;
        for (int i = 0; i < 50; i++) {
            if (colNum == 11) {
                colNum = 1;
                rowNum++;
            }
            startPos.add(new Point((double) (colNum - 1) * 50 + 25, (double) (rowNum - 1) * 40 + 45));
            colNum++;
        }
    }

    /**
     * Function Name: createEnemies.
     * Function Operation: create the blocks of the game.
     */
    public void createEnemies() {
        int countblocks = 0;
        int colNum = 1;
        int rowNum = 1;
        for (int i = 0; i < 50; i++) {
            if (colNum == 11) {
                colNum = 1;
                rowNum++;
            }
            Block b = this.levelInformation.blocks().get(i);
            b.setUpLefPoint(new Point((double) (colNum - 1) * 50 + 25, (double) (rowNum - 1) * 40 + 45));
            b.setVelocity(new Velocity((int) (startSpeed * this.dt), 0));
            b.setLimit(800);
            b.setEnemyNumber(i);
            b.setColNumber(colNum);
            b.addHitListener(br);
            b.addHitListener(ballRemover);
            if (levelNo == 1) {
                b.addHitListener(stl);
            }
            b.addToGame(this);
            countblocks++;
            colNum++;
        }
        br.setRemainingBlocks(new Counter(countblocks));
    }

    /**
     * Function Name: creatingIndicators.
     * Function Operation: create the live and score indicators of the game.
     */
    public void creatingIndicators() {
        ScoreIndicator sco = new ScoreIndicator(this.score);
        sco.addToGame(this);
        LivesIndicator live = new LivesIndicator(this.lives);
        live.addToGame(this);
    }

    /**
     * Function  Name: shouldStop.
     *
     * @return true if the game should stop and false otherwise.
     */
    public boolean shouldStop() {
        List<Block> allblocks = new ArrayList<Block>();
        for (int i = 0; i < sprites.getLength(); i++) {
            if (sprites.getI(i).isEnemyBlock()) {
                allblocks.add((Block) sprites.getI(i));
            }
        }
        if (allblocks.size() == 0) {
            return this.running;
        }
        Block lowestBlock = getLowestBlock(allblocks);
        if ((restartLevel && lives.getValue() != 0)
                || (lowestBlock.getUpLeftPoint().getY() + lowestBlock.getHeight() >= 500)) {
            this.lives.decrease(1);
            restartLevel = false;
            for (int i = 0; i < sprites.getLength(); i++) {
                if (sprites.getI(i).isBall()) {
                    Ball b = (Ball) sprites.getI(i);
                    b.removeFromGame(this);
                    i--;
                }
            }
            balls = new ArrayList<Ball>();
            List<Block> blocks = new ArrayList<Block>();
            for (int i = 0; i < sprites.getLength(); i++) {
                if (sprites.getI(i).isEnemyBlock()) {
                    Block b = (Block) sprites.getI(i);
                    b.setUpLefPoint(startPos.get(b.getEnemyNumber()));
                    b.setVelocity(new Velocity((int) (startSpeed * this.dt), 0));
                }
            }
            this.pad.removeFromGame(this);
            if (lives.getValue() != 0) {
                return this.running;
            }
        }
        if (lives.getValue() == 0) {
            Animation end = new EndGame(this.keyboard, this.lives.getValue(), this.score.getValue());
            Animation high = new HighScoresAnimation(this.scores, "space", keyboard);
            Animation aEnd = new KeyPressStoppableAnimation(keyboard, "space", end);
            Animation aHigh = new KeyPressStoppableAnimation(keyboard, "space", high);
            runner.run(aEnd);
            if (scores.getRank(this.score.getValue()) < 10) {
                DialogManager dialog = gui.getDialogManager();
                String name = dialog.showQuestionDialog("Name", "What is your name?", "");
                scores.add(new ScoreInfo(name, this.score.getValue()));
            }
            runner.run(aHigh);
            try {
                scores.save(new File("highscores.txt"));
            } catch (IOException e) {
                e.getMessage();
            }
            //  gui.close();
            return this.running;
        }
        return !this.running;
    }

    /**
     * Function Name: doOneFrame.
     * Function Operation: draw the game with all the sprites and indicators
     *
     * @param d   - the surface of the game
     * @param dts - 1/fps
     */
    public void doOneFrame(DrawSurface d, double dts) {
        this.levelInformation.getBackground().drawOn(d);
        stl.setRemainingBlocks(br.getRemainingBlocks());
        this.score = stl.getCurrentScore();
        this.lives = li.getLives();
        this.sprites.drawAllOn(d);
        this.sprites.notifyAllTimePassed();
        d.setColor(Color.BLACK);
        d.drawText(widthGui - 300, 13, "Level Name: Battle no." + this.levelNo, 15);
        if (this.keyboard.isPressed("p")) {
            Animation pause = new PauseScreen(this.keyboard);
            Animation aPause = new KeyPressStoppableAnimation(this.keyboard, "space", pause);
            runner.run(aPause);
        }
        if (this.keyboard.isPressed("space")) {

            if (currentTime >= stopTime) {
                createBallsOnTopOfPaddle();
                stopTime = currentTime + 350;
            } else {
                currentTime = System.currentTimeMillis();
            }
        }
        for (int i = 0; i < sprites.getLength(); i++) {
            if (sprites.getI(i).isPaddle()) {
                Paddle b = (Paddle) sprites.getI(i);
                restartLevel = b.isHitPaddle();
                if (restartLevel) {
                    break;
                }
            }
        }
        getLeftEnemies();
    }

    /**
     * Function Name: returnLives.
     *
     * @return the remaining life of the player
     */
    public int returnLives() {
        return this.lives.getValue();
    }

    /**
     * Function Name: returnBlocks.
     *
     * @return the remaining blocks in the game.
     */
    public int returnBlocks() {
        return br.getRemainingBlocks().getValue();
    }

    /**
     * Function Name: returnScore.
     *
     * @return the score in the game.
     */
    public int returnScore() {
        return this.score.getValue();
    }

    /**
     * Function Name:setDt.
     * Function Operation: set the dt
     *
     * @param dts - 1/fps
     */
    public void setDt(double dts) {
        this.dt = dts;
    }

    /**
     * Function Name: SetScore.
     *
     * @param scor - high score table
     */
    public void setScore(HighScoresTable scor) {
        this.scores = scor;
    }

    /**
     * Function name:getLeftEnemies.
     * Function Operation: get the left enemies.
     */
    public void getLeftEnemies() {
        boolean flag = false;
        double xAdd = 0;
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < this.sprites.getLength(); i++) {
            if (sprites.getI(i).isEnemyBlock()) {
                blocks.add((Block) this.sprites.getI(i));
            }
        }
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).hitLeftEdge()) {
                xAdd = -1;
                flag = true;
                break;
            }
            if (blocks.get(i).hitrightEdge()) {
                xAdd = 1;
                flag = true;
                break;
            }
        }
        if (flag) {
            for (int i = 0; i < blocks.size(); i++) {
                blocks.get(i).removeFromGame(this);
            }
            for (int i = 0; i < blocks.size(); i++) {
                if (xAdd == 1) {
                    blocks.get(i).setUpLefPoint(new Point(blocks.get(i).getUpLeftPoint().getX(),
                            blocks.get(i).getUpLeftPoint().getY() + 20));
                } else {
                    blocks.get(i).setUpLefPoint(new Point(blocks.get(i).getUpLeftPoint().getX(),
                            blocks.get(i).getUpLeftPoint().getY() + 20));
                }
                blocks.get(i).setVelocity(new Velocity(blocks.get(i).getVelocity().getDX() * (-1.1),
                        blocks.get(i).getVelocity().getDY()));
                blocks.get(i).addToGame(this);
            }
        }
        if (currentTime2 >= stopTime2) {
            divideToColumns();
        } else {
            currentTime2 = System.currentTimeMillis();
        }
    }

    /**
     * Function Name: divideToColumns.
     * Function Operation: divide enemies to columns.
     */
    public void divideToColumns() {
        List<Block> firstCol = new ArrayList<Block>();
        List<Block> secCol = new ArrayList<Block>();
        List<Block> thirdCol = new ArrayList<Block>();
        List<Block> fourthCol = new ArrayList<Block>();
        List<Block> fifthCol = new ArrayList<Block>();
        List<Block> sixthCol = new ArrayList<Block>();
        List<Block> sevCol = new ArrayList<Block>();
        List<Block> eightCol = new ArrayList<Block>();
        List<Block> ninthCol = new ArrayList<Block>();
        List<Block> tenthCol = new ArrayList<Block>();
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < this.sprites.getLength(); i++) {
            if (sprites.getI(i).isEnemyBlock()) {
                Block b = (Block) sprites.getI(i);
                if (b.getColNumber() == 1) {
                    firstCol.add(b);
                }
                if (b.getColNumber() == 2) {
                    secCol.add(b);
                }
                if (b.getColNumber() == 3) {
                    thirdCol.add(b);
                }
                if (b.getColNumber() == 4) {
                    fourthCol.add(b);
                }
                if (b.getColNumber() == 5) {
                    fifthCol.add(b);
                }
                if (b.getColNumber() == 6) {
                    sixthCol.add(b);
                }
                if (b.getColNumber() == 7) {
                    sevCol.add(b);
                }
                if (b.getColNumber() == 8) {
                    eightCol.add(b);
                }
                if (b.getColNumber() == 9) {
                    ninthCol.add(b);
                }
                if (b.getColNumber() == 10) {
                    tenthCol.add(b);
                }

            }
        }
        Block b1 = getLowestBlock(firstCol);
        Block b2 = getLowestBlock(secCol);
        Block b3 = getLowestBlock(thirdCol);
        Block b4 = getLowestBlock(fourthCol);
        Block b5 = getLowestBlock(fifthCol);
        Block b6 = getLowestBlock(sixthCol);
        Block b7 = getLowestBlock(sevCol);
        Block b8 = getLowestBlock(eightCol);
        Block b9 = getLowestBlock(ninthCol);
        Block b10 = getLowestBlock(tenthCol);
        List<Block> blockss = new ArrayList<Block>();
        if (b1 != null) {
            blockss.add(b1);
        }
        if (b2 != null) {
            blockss.add(b2);
        }
        if (b3 != null) {
            blockss.add(b3);
        }
        if (b4 != null) {
            blockss.add(b4);
        }
        if (b5 != null) {
            blockss.add(b5);
        }
        if (b6 != null) {
            blockss.add(b6);
        }
        if (b7 != null) {
            blockss.add(b7);
        }
        if (b8 != null) {
            blockss.add(b8);
        }
        if (b9 != null) {
            blockss.add(b9);
        }
        if (b10 != null) {
            blockss.add(b10);
        }
        Random rand = new Random();
        int num = rand.nextInt(blockss.size());
        dropEnemyBall(blockss.get(num));
    }


    /**
     * Function Name:dropEnemyBall.
     *
     * @param block - the block
     */
    public void dropEnemyBall(Block block) {
        ballRemover.setRemainingBalls(new Counter(1));
        Ball ball = new Ball(block.getUpLeftPoint().getX() + block.getWidth() / 2,
                block.getUpLeftPoint().getY() + block.getHeight() + 3, 5, Color.RED.darker());
        ball.setHitPoints(1);
        Velocity v = Velocity.fromAngleAndSpeed(180, 4);
        v.setSpeed(4);
        ball.setVelocity(v);
        ball.setGameEnvironment(this.returnEnviroment());
        ball.addHitListener(ballRemover);
        ball.addToGame(this);
        ball.setBallOfEnemy(true);
        ball.setGameLevel(this);
        balls.add(ball);
        stopTime2 = currentTime2 + 500;
    }

    /**
     * Function Name: getLowestBlock.
     *
     * @param blocks - list of blocks
     * @return the lowest block of a list.
     */
    public Block getLowestBlock(List<Block> blocks) {
        Block minBlock;
        if (blocks.size() == 0) {
            return null;
        } else {
            minBlock = blocks.get(0);
            for (int i = 0; i < blocks.size(); i++) {
                if (minBlock.getUpLeftPoint().getY() < blocks.get(i).getUpLeftPoint().getY()) {
                    minBlock = blocks.get(i);
                }
            }
        }
        return minBlock;
    }

    /**
     * Function Name: getStartSpeed.
     *
     * @return start speed.
     */
    public int getStartSpeed() {
        return startSpeed;
    }

    /**
     * Function Name: setStartSpeed.
     *
     * @param ss - start speed.
     */
    public void setStartSpeed(int ss) {
        this.startSpeed = ss;
    }

    /**
     * Function Name: getRemainingEnemies.
     * @return remaining enemies.
     */
    public int getRemainingEnemies() {
        List<Block> allblocks = new ArrayList<Block>();
        for (int i = 0; i < sprites.getLength(); i++) {
            if (sprites.getI(i).isEnemyBlock()) {
                allblocks.add((Block) sprites.getI(i));
            }
        }
        return allblocks.size();
    }

    /**
     * 4
     * Function Name:setFirstTime.
     *
     * @param ft - if first time
     */
    public void setFirstTime(boolean ft) {
        this.firstTime = ft;
    }

    /**
     * Function Name: setLevelNo.
     *
     * @param levelNumber - level number
     */
    public void setLevelNo(int levelNumber) {
        this.levelNo = levelNumber;
    }
}

