package renderEngine;

import java.util.ArrayList;
import java.util.Iterator;

public class ListOfLasers {

    private static ArrayList<Laser> listofLasers;

    public ListOfLasers(){

        this.listofLasers = new ArrayList<>();
    }

    public ArrayList<Laser> getLasers() {
        return this.listofLasers;
    }

    public void add(Laser laser) {
        listofLasers.add(laser);
    }

    public void update() {
        for(Laser curr : listofLasers) {
            curr.update();
        }
        Iterator<Laser> iter = listofLasers.iterator();
        while (iter.hasNext()) {
            Laser temp = iter.next();
            if (temp.getPosition()[1] > 605)
                iter.remove();
        }
    }

}
