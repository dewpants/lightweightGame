package renderEngine;

import java.util.ArrayList;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Milleniumfalcon {

    //variables
    private static float lowerLeftX;
    private static float lowerLeftY;

    //constructor
    public Milleniumfalcon(float x, float y) {

        //determine initial coordinates
        this.lowerLeftX = x;
        this.lowerLeftY = y;
    }

    //getter method for falcon's location
    public float[] getLocation() {
        float[] location = {this.lowerLeftX, this.lowerLeftY};
        return location;
    }

    //updates the falcon's location
    public void update(float x, float y) {

        this.lowerLeftX += x;
        this.lowerLeftY += y;

        // keep quad on the screen
        if (this.lowerLeftX < 0) this.lowerLeftX = 0;
        if (this.lowerLeftX + 50 > 820) this.lowerLeftX = 770;
        if (this.lowerLeftY < 0) this.lowerLeftY = 0;
        if (this.lowerLeftY + 50 > 480) this.lowerLeftY = 430;
    }

    //displays the falcon and lasers on the screen
    public void display() {

        // clear the screen and depth buffer
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);   NEEDED??

        // set the color of the quad (R,G,B,A)
        glColor3f(0.5f,0.5f,1.0f);

        // draw quad (x, y)
        GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(this.lowerLeftX, this.lowerLeftY); //lower left
            GL11.glVertex2f(this.lowerLeftX + 50f, this.lowerLeftY); //lower right
            GL11.glVertex2f(this.lowerLeftX + 50f, this.lowerLeftY + 50f); //upper right
            GL11.glVertex2f(this.lowerLeftX, this.lowerLeftY + 50f); //upper left
        GL11.glEnd();
    }

    public void shoot(ListOfLasers lasersList) {
        Laser newLaser = new Laser(this.lowerLeftX, this.lowerLeftY);
        lasersList.add(newLaser);
    }

}
