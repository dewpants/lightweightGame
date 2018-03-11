package renderEngine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import java.util.*;
import java.io.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import static org.lwjgl.stb.STBEasyFont.*;

public class MainLoop {

    // The window handle
    private long window;

    //initialize game variables
    private long lastUpdate = 0;
    private int numOfUpdates = 0;
    private int shipsDestroyed = 0;
    private int lives = 3;

    private boolean roundover = true;
    private boolean gamerunning = false;
    private boolean gamelost = false;
    private boolean showscores = false;
    private boolean completed = true;

    private boolean moveRight = false;
    private boolean moveLeft = false;
    private Random r = new Random();
    private File scorefile = new File("highscores.txt");

    //initialize game objects
    private Milleniumfalcon mainShip = new Milleniumfalcon(350, 50);
    private ListOfLasers lasersList = new ListOfLasers();
    private ListOfEnemies enemiesList = new ListOfEnemies();

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

        System.out.println("initializing");
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
        System.out.println("loop");
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

        //set background color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // main game loop
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        int updatenum = 100;
        while ( !glfwWindowShouldClose(window)) {

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            //listen for key input (always running)
            glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
                this.getInput(key, action);
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                    glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
                }
            });

            if(gamerunning && !gamelost && !roundover) {
                if (System.nanoTime() - lastUpdate > 10000000) {
                    this.update();
                    lastUpdate = System.nanoTime();
                    numOfUpdates++;
                    if (numOfUpdates == updatenum) {
                        if (updatenum > 10){
                            updatenum -= 3;
                        }
                        if (updatenum == 50) {
                            //powerups.add();
                        }
                        System.out.println(updatenum);
                        Enemy newenemy;

                        if(r.nextInt(3) == 1) {
                            newenemy = new TieBomber();
                        } else {
                            newenemy = new TieFighter();
                        }
                        enemiesList.add(newenemy);
                        numOfUpdates = 0;
                    }
                }
                this.render();
            } else {
                /*
                if(showscores) {
                    this.displayHighscores();
                } else {
                */
                    this.displayMainText();
               // }
            }

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

        glDisableClientState(GL_VERTEX_ARRAY);
    }

    //looks for relevant keypresses and calls each components update
    private void update() {
        if (lasersList.getLasers().size() > 0) {
            lasersList.update();
        }
        if (enemiesList.getEnemies().size() > 0) {
            enemiesList.update();
        }
        if (enemiesList.getEnemies().size() > 0 && lasersList.getLasers().size() > 0) {
            this.checkForLaserImpact();
        }
        if (enemiesList.getEnemies().size() > 0) {
            this.checkForEnemyImpact();
        }
        if (moveRight == true){
            mainShip.update(10f, 0f);
        }
        if (moveLeft == true) {
            mainShip.update(-10f, 0f);
        }
    }

    //renders and displays the various components of the game
    private void render() {

        this.displayStats();
        this.displayLives();
        mainShip.display();
        for (Laser currLaser : lasersList.getLasers()) {
            currLaser.display();
        }
        for (Enemy currEnemy : enemiesList.getEnemies()) {
            currEnemy.display();
        }
    }

    private void checkForLaserImpact() {
        //check for laser to hit enemy
        for(int i=0; i < enemiesList.getEnemies().size(); i++){
            for (int j=0; j < lasersList.getLasers().size(); j++) {
                if (enemiesList.getEnemies().get(i).getPosition()[0] <= lasersList.getLasers().get(j).getPosition()[0] + 5f &&
                        enemiesList.getEnemies().get(i).getPosition()[0] +30f >= lasersList.getLasers().get(j).getPosition()[0] &&
                        enemiesList.getEnemies().get(i).getPosition()[1] - 30f < lasersList.getLasers().get(j).getPosition()[1] + 10f) {
                    System.out.println("impact (laser on enemy)");
                    enemiesList.getEnemies().get(i).decHealth();
                    if(enemiesList.getEnemies().get(i).getHealth() == 0) {
                        enemiesList.getEnemies().remove(i);
                    }
                    lasersList.getLasers().remove(j);
                    shipsDestroyed++;
                    break;
                }
            }
        }
    }

    private void checkForEnemyImpact() {
        //check for enemy to hit ship
        for(int i=0; i < enemiesList.getEnemies().size(); i++){
            if (enemiesList.getEnemies().get(i).getPosition()[0] + 30f >= mainShip.getLocation()[0] &&
                    enemiesList.getEnemies().get(i).getPosition()[0] < mainShip.getLocation()[0] + 50 &&
                    enemiesList.getEnemies().get(i).getPosition()[1] - 30f < mainShip.getLocation()[1] + 40f
                    && enemiesList.getEnemies().get(i).getPosition()[1] > mainShip.getLocation()[1] - 10f) {
                System.out.println("impact (enemy on ship");
                lives--;
                roundover = true;
                enemiesList.clearEnemies();
                if (lives <= 0) {
                    gamelost = true;
                }
                break;
            }
        }
    }

    private void getInput(int key, int action) {
        if ( key == GLFW_KEY_RIGHT && action == GLFW_PRESS){
            moveRight = true;
        }
        if ( key == GLFW_KEY_RIGHT && action == GLFW_RELEASE){
            moveRight = false;
        }
        if ( key == GLFW_KEY_LEFT && action == GLFW_PRESS){
            moveLeft = true;
        }
        if ( key == GLFW_KEY_LEFT && action == GLFW_RELEASE){
            moveLeft = false;
        }
        if ( key == GLFW_KEY_SPACE && action == GLFW_PRESS ) {
            mainShip.shoot(lasersList);
            System.out.println("shooting!");
        }
        if ( key == GLFW_KEY_ENTER && action == GLFW_RELEASE ) {
            if(gamelost) {
                showscores = true;
            }
            gamerunning = true;
            roundover = false;
        }
    }

    private void displayMainText(){
        String text;
        if(!gamelost && lives == 3) {
            text = "DESTROY THE\nREBEL SCUM";
        }
        else if(roundover && lives > 0) {
            text = Integer.toString(lives) + " Lives\nRemaining";
        }
        else {
            text = "YOU LOST\n Score: " + Integer.toString(shipsDestroyed);
        }
        ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 270);
        int maintext = stb_easy_font_print(0, 0, text, null, charBuffer);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);
        glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT);
        float scaleFactor = 1.0f + 30f * 0.25f;
        glPushMatrix();
        //translate/size/rotate
        glColor3f(1.0f,0.0f,0.0f);
        glTranslatef(100.0f, 500.0f, 0f);
        glScalef(scaleFactor, -scaleFactor, 1f);
        glDrawArrays(GL_QUADS, 0, maintext * 4);
        glPopMatrix();
    }

    private void displayStats() {
        //ships destroyed
        String text = "Rebel Ships Destroyed: " + Integer.toString(shipsDestroyed);
        ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 270);
        int stattext = stb_easy_font_print(0, 0, text, null, charBuffer);
        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);

        //glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT);
        float scaleFactor = 1.0f + 5f * 0.25f;
        glPushMatrix();
        //translate/size/rotate
        glColor3f(1.0f,0.0f,0.0f);
        glTranslatef(15.0f, 585.0f, 0f);
        glScalef(scaleFactor, -scaleFactor, 1f);
        glDrawArrays(GL_QUADS, 0, stattext * 4);
        glPopMatrix();

    }

    private void displayLives() {

        float livescoordy = 540f;
        float livescoordx = 215f;

        //ships destroyed
        String text = "Lives Remaining: ";
        ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 270);
        int stattext = stb_easy_font_print(0, 0, text, null, charBuffer);
        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);

        glfwPollEvents();
        float scaleFactor = 1.0f + 5f * 0.25f;
        glPushMatrix();
        //translate/size/rotate
        glColor3f(1.0f,0.0f,0.0f);
        glTranslatef(15.0f, 560.0f, 0f);
        glScalef(scaleFactor, -scaleFactor, 1f);
        glDrawArrays(GL_QUADS, 0, stattext * 4);
        glPopMatrix();

        for(int i=0; i < lives; i++) {
            glColor3f(0.5f,0.5f,1.0f);
            // draw quad (x, y)
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(livescoordx, livescoordy); //lower left
            GL11.glVertex2f(livescoordx + 20f, livescoordy); //lower right
            GL11.glVertex2f(livescoordx + 20f, livescoordy + 20f); //upper right
            GL11.glVertex2f(livescoordx, livescoordy + 20f); //upper left
            GL11.glEnd();
            livescoordx += 30f;
        }
    }

    private void displayHighscores() {
        /*
        String text;
        String text2;
        if (!completed) {
            Scanner scnr = null;
            try {
                scnr = new Scanner(scorefile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            text = "HIGHSCORES";
            text2 = "";
            int[] highscores = new int[3];
            String[] names = new String[3];
            for (int i = 0; i < 3; i++) {
                String temp1 = scnr.nextLine();
                String[] temp = temp1.split(" ");
                names[i] = temp[0];
                System.out.println(i);
                highscores[i] = Integer.valueOf(temp[1]);
                text2 += temp[0] + " " + temp[1] + "\n";
            }
        }
        completed = false;

        ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 270);
        ByteBuffer charBuffer2 = BufferUtils.createByteBuffer(text2.length() * 270);

        int scoretext = stb_easy_font_print(0, 0, text, null, charBuffer);
        int scoretext2 = stb_easy_font_print(0, 0, text2, null, charBuffer);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);
        glClear(GL_COLOR_BUFFER_BIT);
        float scaleFactor = 1.0f + 5f * 0.25f;
        glPushMatrix();
        //translate/size/rotate
        glColor3f(1.0f,0.0f,0.0f);
        glTranslatef(15.0f, 585.0f, 0f);
        glScalef(scaleFactor, -scaleFactor, 1f);
        glDrawArrays(GL_QUADS, 0, scoretext * 4);
        glPopMatrix();


        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer2);
        float scaleFactor2 = 1.0f + 5f * 0.25f;
        glPushMatrix();
        //translate/size/rotate
        glColor3f(1.0f,0.0f,0.0f);
        glTranslatef(15.0f, 585.0f, 0f);
        glScalef(scaleFactor2, -scaleFactor2, 1f);
        glDrawArrays(GL_QUADS, 0, scoretext2 * 4);
        glPopMatrix();
        */

    }

    //main loop that starts running the program
    public static void main(String[] args) {
        new MainLoop().run();
    }

}