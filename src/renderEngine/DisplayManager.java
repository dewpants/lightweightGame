package renderEngine;


import org.lwjgl.opengl.GL11;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;/**
 * Created by davidmarnocha on 7/24/16.
 */
public class DisplayManager {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    public static void createDisplay() {

        ContextAttribs attribs = new ContextAttribs(3.2).withForwardCompatible(true).withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), attribs);
        } catch (LWJFLExeption e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, WIDTH, HEIGHT);
    }

    public static void updateDisplay() {

    }

    public static void closeDisplay() {
        Display.destroy();
    }
}
