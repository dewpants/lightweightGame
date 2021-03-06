package renderEngine;

import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;
import java.util.*;

public class TieFighter implements Enemy {

    private float xPos;
    private float yPos;
    private int health = 1;

    public TieFighter() {

        Random rand = new Random();
        int num = rand.nextInt(780) + 1;

        this.xPos = (float) num;
        this.yPos = 620f;
    }

    public void display() {

        //System.out.println("Displaying Enemy!");

        // set the color of the quad (R,G,B,A)
        glColor3f(1.0f,0.5f,1.0f);

        glBegin(GL_TRIANGLES);
            GL11.glVertex2f( this.xPos, this.yPos );
            GL11.glVertex2f( this.xPos + 30f, this.yPos );
            GL11.glVertex2f( this.xPos + 15f, this.yPos - 30f );
        glEnd();
    }

    public void update() {
        this.yPos -= 10f;

        // keep quad on the screen
        if (this.xPos < 0) this.xPos = 0;
        if (this.xPos + 50 > 820) this.xPos = 770;
        if (this.yPos < 0) this.yPos = 800f;
    }

    public float[] getPosition() {
        float[] location = {this.xPos, this.yPos};
        return location;
    }

    public int getHealth() {
        return health;
    }

    public void decHealth() {
        health--;
    }

}
