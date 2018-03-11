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
    private static boolean singleCannon = false;
    private static boolean twinCannon = true;

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
        if (this.lowerLeftX + 50 > 800) this.lowerLeftX = 750;
        if (this.lowerLeftY < 0) this.lowerLeftY = 0;
        if (this.lowerLeftY + 50 > 605) this.lowerLeftY = 555;
    }

    //displays the falcon and lasers on the screen
    public void display() {

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
        if (singleCannon == true) {
            Laser newLaser = new Laser(this.lowerLeftX, this.lowerLeftY);
            lasersList.add(newLaser);
        }
        if (twinCannon == true) {
            System.out.println("shooting double");
            Laser newLaser = new Laser(this.lowerLeftX - 10f, this.lowerLeftY);
            Laser newLaser2 = new Laser(this.lowerLeftX + 10f, this.lowerLeftY);
            lasersList.add(newLaser);
            lasersList.add(newLaser2);
        }
    }

    public void setCapabilities() {

    }

}
