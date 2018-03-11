package renderEngine;

import java.util.*;

public class ListOfEnemies {

    private static ArrayList<Enemy> listofEnemies;

    public ListOfEnemies() {
        listofEnemies = new ArrayList<Enemy>();
    }

    public void add(Enemy en) {
        listofEnemies.add(en);
    }

    public ArrayList<Enemy> getEnemies() {
        return listofEnemies;
    }

    public void update() {
        for(Enemy curr : listofEnemies) {
            curr.update();
        }
    }

    public void clearEnemies() {
        listofEnemies.clear();
    }

}
