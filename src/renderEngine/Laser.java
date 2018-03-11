package renderEngine;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class Laser {

    //variables
    private float xPos;
    private float yPos;

    //laser constructor
    public Laser(float x, float y) {
        this.xPos = x + 20f;
        this.yPos = y + 49f;
    }

    public float[] getPosition() {
        float[] location = {this.xPos, this.yPos};
        return location;
    }

    public void display() {

        glColor3f(1.0f,0.0f,0.0f);

        // draw quad (x, y)
        GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(this.xPos, this.yPos); //lower left
            GL11.glVertex2f(this.xPos + 5f, this.yPos); //lower right
            GL11.glVertex2f(this.xPos + 5f, this.yPos + 20f); //upper right
            GL11.glVertex2f(this.xPos, this.yPos + 20f); //upper left
        GL11.glEnd();
    }

    public void update() {

        this.yPos += 15f;
    }

}
