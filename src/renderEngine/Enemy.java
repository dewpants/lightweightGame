package renderEngine;

public interface Enemy  {

    void display();
    void update();
    float[] getPosition();
    public int getHealth();
    public void decHealth();

}
