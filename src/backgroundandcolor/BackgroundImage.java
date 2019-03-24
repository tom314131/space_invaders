package backgroundandcolor;

import biuoop.DrawSurface;
import interfaces.Sprite;

import java.awt.Image;

/**
 * Class Name: BackgroundImage.
 */
public class BackgroundImage implements Sprite {
    //member
    private Image image;

    /**
     * Function Name: BackgroundImage.
     * Function Operation: Constructor.
     * @param image - the background image
     */
    public BackgroundImage(Image image) {
        this.image = image;
    }

    /**
     * Function Name: drawOn.
     * Function Operation: draw image.
     * @param surface - the surface
     */
    public void drawOn(DrawSurface surface) {
        surface.drawImage(0, 0, this.image);
    }
    /**
     * Function Name: timePassed.
     * Function Operation: nothing
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
