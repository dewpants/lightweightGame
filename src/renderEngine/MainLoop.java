package renderEngine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.util.*;
import java.util.Iterator;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class MainLoop {

    // The window handle
    private long window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        try {
            init();
            loop();

            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        } finally {
            // Terminate GLFW and free the error callback
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    //initialization
    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        int WIDTH = 720;
        int HEIGHT = 480;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello Lightweight!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (vidmode.width() - WIDTH) / 2,
                (vidmode.height() - HEIGHT) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    //loop that the game runs on
    private void loop() {

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // init OpenGL
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 800, 0, 600, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        //create new Millenium Falcon
        Milleniumfalcon mainShip = new Milleniumfalcon(350, 50);
        ListOfLasers lasersList = new ListOfLasers();
        ListOfEnemies enemiesList = new ListOfEnemies();


        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        long lastUpdate = 0;
        int numOfUpdates = 0;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            if (System.nanoTime() - lastUpdate > 10000000){
                this.update(enemiesList, lasersList);
                lastUpdate = System.nanoTime();
                numOfUpdates++;
                if (numOfUpdates == 84) {
                    System.out.println("new enemy created!");
                    TieFighter newTie = new TieFighter();
                    enemiesList.add(newTie);
                    numOfUpdates = 0;
                }
            }
            this.render(mainShip, lasersList, enemiesList);

            glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
                if ( key == GLFW_KEY_RIGHT && action == GLFW_PRESS){
                    mainShip.update(30f, 0f);
                }
                if ( key == GLFW_KEY_LEFT && action == GLFW_PRESS) {
                    mainShip.update(-30f, 0f);
                }
                if ( key == GLFW_KEY_SPACE && action == GLFW_PRESS ) {
                    mainShip.shoot(lasersList);
                }
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                    glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
                }
            });

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    //looks for relevant keypresses and calls each components update
    private void update(ListOfEnemies enemiesList, ListOfLasers lasersList) {
        if (lasersList.getLasers().size() > 0) {
            lasersList.update();
        }
        if (enemiesList.getEnemies().size() > 0) {
            enemiesList.update();
        }
        if (enemiesList.getEnemies().size() > 0 && lasersList.getLasers().size() > 0) {
            this.checkForImpact(enemiesList, lasersList);
        }
    }

    //renders and displays the various components of the game
    private void render(Milleniumfalcon mainShip, ListOfLasers lasersList, ListOfEnemies enemiesList) {
        mainShip.display();
        for (Laser currLaser : lasersList.getLasers()) {
            currLaser.display();
        }
        for (Enemy currEnemy : enemiesList.getEnemies()) {
            currEnemy.display();
        }
    }

    //USE ITERATOR INSTEAD
    private void checkForImpact(ListOfEnemies enemiesList, ListOfLasers lasersList) {

        Iterator<Laser> laserIterator = lasersList.getLasers().iterator();
        Iterator<Enemy> enemyIterator = enemiesList.getEnemies().iterator();

        while (enemyIterator.hasNext()) {
            Enemy tempEnemy = enemyIterator.next();
            while (laserIterator.hasNext()) {
                Laser tempLaser = laserIterator.next();
                if ((tempLaser.getPosition()[0] >= tempEnemy.getPosition()[0] &&
                        tempLaser.getPosition()[0] < tempEnemy.getPosition()[0] + 20) &&
                        (tempLaser.getPosition()[1] + 20 >= tempEnemy.getPosition()[1] - 20 &&
                                tempLaser.getPosition()[1] < tempEnemy.getPosition()[1] + 20)) {
                    enemyIterator.remove();
                }
            }
        }
    }

    //main loop that starts running the program
    public static void main(String[] args) {

        new MainLoop().run();
    }
}