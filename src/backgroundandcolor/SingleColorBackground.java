package backgroundandcolor;

import biuoop.DrawSurface;
import interfaces.Sprite;

import java.awt.Color;

/**
 * Class Name: SingleColorBackground.
 */
public class SingleColorBackground implements Sprite {
    //members
    private Color color;

    /**
     * Constructor.
     * @param col - the color
     */
    public SingleColorBackground(Color col) {
        this.color = col;
    }

    /**
     * Function Name: drawOn.
     * Function Operation: draw the background.
     * @param surface - the surface
     */
    public void drawOn(DrawSurface surface) {
        surface.setColor(this.color);
        surface.fillRectangle(0, 0, surface.getWidth(), surface.getHeight());
    }
    /**
     * Function Name:timePassed.
     * @param dt - 1/fps
     */
    public void timePassed(double dt) {
    }

    /**
     * Function Name: isEnemyBlock.
     *
     * @return true if it is block and false if it is not.
     */
    @Override
    public Boolean isEnemyBlock() {
        return false;
    }

    @Override
    public Boolean isPaddle() {
        return false;
    }

    @Override
    public Boolean isBlock() {
        return false;
    }

    @Override
    public Boolean isBall() {
        return false;
    }
}
