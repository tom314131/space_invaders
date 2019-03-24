package sprites;


import biuoop.DrawSurface;

import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import gamepackage.GameLevel;
import geomitryprimitives.Line;
import geomitryprimitives.Velocity;
import geomitryprimitives.Point;
import geomitryprimitives.Rectangle;
import interfaces.Sprite;
import interfaces.Collidable;
import interfaces.HitNotifier;
import interfaces.HitListener;
import interfaces.Drawer;



/**
 * Class Name: Block.
 */
public class Block implements Collidable, Sprite, HitNotifier {
    private Rectangle rectangle;
    private int line;
    private int hitNum;
    private List<HitListener> hitListeners = new ArrayList<HitListener>();
    private int level;
    private int part;
    private Drawer defaultStrokeDrawer;
    private Map<Integer, Drawer> strokeDrawers;
    private Drawer defaultFillDrawer;
    private Map<Integer, Drawer> fillDrawers;
    private boolean isShield;
    private Velocity velocity;
    private int limit0;
    private int limitX;
    private int colNumber;
    private boolean hitPaddle;
    private int enemyNumber;


    /**
     * Function Name: Block.
     * constructor
     *
     * @param rect - the rectangle of the block
     */
    public Block(Rectangle rect) {
        this.rectangle = rect;
    }

    /**
     * Function Name: Block.
     * constructor
     * @param xpos  - the x pos ition of up left point
     * @param ypos  - the y position of up left point
     */
    public Block(double xpos, double ypos) {
        this.rectangle = new Rectangle(new Point(xpos, ypos), 0, 0);
        this.strokeDrawers = new HashMap<Integer, Drawer>();
        this.fillDrawers = new HashMap<Integer, Drawer>();
        this.hitListeners = new ArrayList<HitListener>();
        this.defaultFillDrawer = new Block.NullDrawer();
        this.defaultStrokeDrawer = new Block.NullDrawer();
    }

    /**
     * Function Name: getCollisionRectangle.
     *
     * @return the rectangle of theblock
     */
    public Rectangle getCollisionRectangle() {
        return this.rectangle;
    }

    /**
     * Function Name: hit.
     *
     * @param hitter          - the ball
     * @param collisionPoint  - the collision point of the ball with the block
     * @param currentVelocity - the current velocity of the ball
     * @return new velocity of the ball after it hits the block
     */
    // Notice that we changed the hit method to include a "Ball hitter" parameter -- update the
    // Collidable interface accordingly.
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
        if (!(this.isEnemyBlock() && hitter.isBallOfEnemy())) {

            double dx = currentVelocity.getDX();
            double dy = currentVelocity.getDY();
            if (this.hitNum > 0) {
                this.hitNum--;
            } else if (this.hitNum != -1) {
                this.hitNum = 0;
            }
            if (collisionPoint.getX() == this.rectangle.getUpperLeft().getX()
                    || collisionPoint.getX() == this.rectangle.getUpperRight().getX()) {
                dx = dx * (-1);
            }
            if (collisionPoint.getY() == this.rectangle.getUpperLeft().getY()
                    || collisionPoint.getY() == this.rectangle.getBottomRight().getY()) {
                dy = dy * (-1);
            }
            this.notifyHit(hitter);
            return new Velocity(dx, dy);
        }
        hitter.removeFromGame(hitter.getGameLevel());
        return null;
    }

    /**
     * Function Name: isEnemyBlock.
     *
     * @return true if it is block and false if it is not.
     */
    @Override
    public Boolean isEnemyBlock() {
        if (this.getVelocity().getDX() != 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean isPaddle() {
        return false;
    }

    @Override
    public Boolean isBlock() {
        return true;
    }

    @Override
    public Boolean isBall() {
        return false;
    }

    /**
     * Function Name: drawOn.
     * Function Operation: draw the blocks
     *
     * @param surface - the surface that the block are drawn
     */
    public void drawOn(DrawSurface surface) {
        if (!this.isShield) {
            if (this.hitNum != -1) {
                if (this.fillDrawers.containsKey(this.hitNum)) {
                    (this.fillDrawers.get(this.hitNum)).drawAt(surface, this.rectangle);
                } else {
                    this.defaultFillDrawer.drawAt(surface, this.rectangle);
                }

                if (this.strokeDrawers.containsKey(this.hitNum)) {
                    (this.strokeDrawers.get(this.hitNum)).drawAt(surface, this.rectangle);
                } else {
                    this.defaultStrokeDrawer.drawAt(surface, this.rectangle);
                }
            }
        } else {
            surface.setColor(Color.decode("#00BCD4"));
            surface.fillRectangle((int) this.rectangle.getUpperLeft().getX(),
                    (int) this.rectangle.getUpperLeft().getY(),
                    (int) this.rectangle.getBottomRight().getX()
                            - (int) this.rectangle.getUpperLeft().getX(),
                    (int) this.rectangle.getBottomRight().getY()
                            - (int) this.rectangle.getUpperLeft().getY());
        }

    }


    /**
     * Function Name: timePassed.
     * Function Operation: nothing (meanwhile)
     * @param dt  - 1/fps
     */
    public void timePassed(double dt) {
        moveOneStep();
    }

    /**
     * Function Name: addToGame.
     * Function Operation: add the block to the gameLevel.
     *
     * @param gameLevel - the gameLevel.
     */
    public void addToGame(GameLevel gameLevel) {
        gameLevel.addSprite(this);
        gameLevel.addCollidable(this);
    }

    /**
     * Function Name: removeFromGame.
     * Function Operation: remove the block to the gameLevel.
     *
     * @param gameLevel - the gameLevel.
     */
    public void removeFromGame(GameLevel gameLevel) {
        gameLevel.removeSprite(this);
        gameLevel.removeCollidable(this);
    }

    /**
     * Function name: setLineNumber.
     * Function Operation: set the line of the block to be found (1 to 6)
     *
     * @param lineNum - the line that the block is found.
     */
    public void setLineNumber(int lineNum) {
        this.line = lineNum;
    }

    /**
     * Function name: setHitNum.
     * Function Operation: set the hit number that the block has left.
     *
     * @param hitNumber - the hit number of the block.
     */
    public void setHitNum(int hitNumber) {
        this.hitNum = hitNumber;
    }

    /**
     * Function Name: addHitListener.
     * Function Operation: add the listener of the block.
     *
     * @param hl - listener
     */
    @Override
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    /**
     * Function Name: removeHitListener.
     * Function Operation: remove the listener of the block.
     *
     * @param hl - listener
     */
    @Override
    public void removeHitListener(HitListener hl) {
        this.hitListeners.remove(hl);
    }

    /**
     * Function Name: notifyHit.
     * Function Operation: to notify if an hit was made and to to
     * see if the block should be removed from the game.
     *
     * @param hitter - the ball
     */
    private void notifyHit(Ball hitter) {
        // Make a copy of the hitListeners before iterating over the    m.
        List<HitListener> listeners = new ArrayList<HitListener>(this.hitListeners);
        // Notify all listeners about a hit event:
        for (HitListener hl : listeners) {
            hl.hitEvent(this, hitter);
        }
    }

    /**
     * Function Name: getHitPoints.
     *
     * @return - the number of the hits that the block has before it is removed
     */
    public int getHitPoints() {
        return this.hitNum;
    }

    /**
     * Function Name: setLevel.
     *
     * @param lev of the game that the block is added to.
     */
    public void setLevel(int lev) {
        this.level = lev;
    }

    /**
     * Function Name: setPart.
     *
     * @param p - the part of the block between
     *          all the blocks of the game level
     */
    public void setPart(int p) {
        this.part = p;
    }

    /**
     * Function Name: setWidth
     * Function Operation: set the width of the block.
     * @param value - the width of the block
     */
    public void setWidth(int value) {
        this.rectangle = new Rectangle(new Point(this.rectangle.getUpperLeft().getX(),
                this.rectangle.getUpperLeft().getY()), (double) value, this.rectangle.getHeight());
    }
    /**
     * Function Name: setHeight
     * Function Operation: set the height of the block.
     * @param value - the height of the block
     */
    public void setHeight(int value) {
        this.rectangle = new Rectangle(new Point(this.rectangle.getUpperLeft().getX(),
                    this.rectangle.getUpperLeft().getY()), this.rectangle.getWidth(), (double) value);
    }

    /**
     * Function Name: addStrokeDrawer.
     * Function Operation: set hit points and fit stroke
     * @param hitPointsValue - hit point of block
     * @param d - the drawer
     */
    public void addStrokeDrawer(int hitPointsValue, Drawer d) {
        this.strokeDrawers.put(hitPointsValue, d);
    }
    /**
     * Function Name: addFillDrawer.
     * Function Operation: set hit points and fit filling
     * @param hitPointsValue - hit point of block
     * @param d - the drawer
     */
    public void addFillDrawer(int hitPointsValue, Drawer d) {
        this.fillDrawers.put(hitPointsValue, d);
    }
    /**
     * Function Name: setDefaultStrokeDrawer.
     * Function Operation: set default drawer of the block
     * @param d - the drawer
     */
    public void setDefaultStrokeDrawer(Drawer d) {
        this.defaultStrokeDrawer = d;
    }
    /**
     * Function Name: setDefaultFillDrawer.
     * Function Operation: set default stroke of the block
     * @param d - the drawer
     */
    public void setDefaultFillDrawer(Drawer d) {
        this.defaultFillDrawer = d;
    }

    /**
     * Class Name: NullDrawer.
     */
    final class NullDrawer implements Drawer {
        /**
         * Function Name: NullDrawer.
         */
        private NullDrawer() {
        }

        /**
         * Function Name: drawAt.
         * @param ds - surface
         * @param r -block
         */
        public void drawAt(DrawSurface ds, Rectangle r) {
        }
    }

    /**
     * Function Name: setShield.
     * @param flag - true if shield
     */
    public void setShield(boolean flag) {
        this.isShield = true;
    }

    /**
     * Function Name: isShield.
     * @return true if shield.
     */
    public boolean isShield() {
        return this.isShield;
    }

    /**
     * Function Name: getVelocity.
     * @return velocity.
     */
    public Velocity getVelocity() {
        return this.velocity;
    }

    /**
     * Function Name:setVelocity.
     * @param v velocity
     */
    public void setVelocity(Velocity v) {
        this.velocity = v;
    }

    /**
     * Function Name: hitLeftEdge.
     * @return true if hit left edge.
     */
    public boolean hitLeftEdge() {
        Point currentPoint = new Point(this.rectangle.getUpperLeft().getX(), this.rectangle.getUpperLeft().getY());
        Point endPoint = new Point(this.rectangle.getUpperLeft().getX() + this.velocity.getDX(),
                this.rectangle.getUpperLeft().getY() + this.velocity.getDY());
        Line trajectory = new Line(currentPoint, endPoint);
        Line leftLine = new Line(new Point(0, 0), new Point(0, this.limitX));
        if (leftLine.isIntersecting(trajectory)) {
            return true;
        }
        return false;
    }

    /**
     * Function Name: hitrightEdge.
     * @return true if hit right edge.
     */
    public boolean hitrightEdge() {
        Point currentPoint = new Point(this.rectangle.getUpperLeft().getX() + this.rectangle.getWidth(),
                this.rectangle.getUpperLeft().getY());
        Point endPoint = new Point(this.rectangle.getUpperLeft().getX() + this.rectangle.getWidth()
                + this.velocity.getDX(),
                this.rectangle.getUpperLeft().getY() + this.velocity.getDY());
        Line trajectory = new Line(currentPoint, endPoint);
        Line rightLine = new Line(new Point(this.limitX, 0), new Point(this.limitX, this.limitX));
        if (rightLine.isIntersecting(trajectory)) {
            return true;
        }
        return false;
    }
    /**
     * Function Name: moveOneStep.
     * Function Operation: move the ball by the velocity
     */
    public void moveOneStep() {
        if (this.getVelocity() == null) {
            return;
        }

        this.rectangle.setUpLeftPoint(new Point(this.rectangle.getUpperLeft().getX() + this.velocity.getDX(),
                this.rectangle.getUpperLeft().getY()));
    }

    /**
     * Function Name:setLimit.
     * @param limit - limit
     */
    public void setLimit(int limit) {
        this.limit0 = 0;
        this.limitX = limit;
    }

    /**
     * Function Name: getUpLeftPoint.
     * @return the up left point of a block.
     */
    public Point getUpLeftPoint() {
        return this.rectangle.getUpperLeft();
    }

    /**
     * Function Name: getWidth.
     * @return width
     */
    public int getWidth() {
        return (int) this.rectangle.getWidth();
    }
    /**
     * Function Name: setUpLefPoint.
     * @param point - point
     */
    public void setUpLefPoint(Point point) {
        this.rectangle.setUpLeftPoint(point);
    }
    /**
     * Function Name: getHeight.
     * @return hieght
     */
    public int getHeight() {
        return (int) this.rectangle.getHeight();
    }

    /**
     * Function Name: setColNumber.
     * @param cn - col number
     */
    public void setColNumber(int cn) {
        this.colNumber = cn;
    }

    /**
     * Function Name: getColNumber.
     * @return col number
     */
    public int getColNumber() {
        return colNumber;
    }

    /**
     * Function Name: isHitPaddle.
     * @return true if hit paddle.
     */
    public boolean isHitPaddle() {
        return hitPaddle;
    }

    /**
     * Function Name: setUpperLeftPoint.
     * @param upperLeftPoint of the block.
     */
    public void setUpperLeftPoint(Point upperLeftPoint) {
        this.rectangle.setUpLeftPoint(upperLeftPoint);
    }

    /**
     * Function Name: getEnemyNumber.
     * @return enemy number.
     */
    public int getEnemyNumber() {
        return enemyNumber;
    }
    /**
     * Function Name: setEnemyNumber.
     * @param en  - enemy number
     */
    public void setEnemyNumber(int en) {
        this.enemyNumber = en;
    }
}

