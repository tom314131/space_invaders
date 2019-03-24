package interfaces;

import biuoop.DrawSurface;

/**
 * Class Name: Sprite.
 */
public interface Sprite {
    /**
     * Function Name: drawOn.
     * Function Operation: draw the sprite on screen.
     * @param d - the surface
     */
    void drawOn(DrawSurface d);

    /**
     * Function Name: timePassed.
     * Function Operation: notify the sprite that time has passed.
     * @param dt - 1/fps
     */
    void timePassed(double dt);

    /**
     * Function Name: isEnemyBlock.
     * @return true if it is enemy block and false if it is not.
     */
    Boolean isEnemyBlock();

    /**
     * Function Name: isPaddle.
     * @return true if it is paddle and false if it is not.
     */
    Boolean isPaddle();
    /**
     * Function Name: isBlock.
     * @return true if it is block and false if it is not.
     */
    Boolean isBlock();
    /**
     * Function Name: isBall.
     * @return true if it is ball and false if it is not.
     */
    Boolean isBall();
}

