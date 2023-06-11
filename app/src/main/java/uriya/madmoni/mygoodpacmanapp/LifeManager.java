package uriya.madmoni.mygoodpacmanapp;

public class LifeManager {
    private final int N = 3;
    private Place[] arrPlaces;
    private int life;

    public LifeManager(Place p1, Place p2, Place p3) {
        this.arrPlaces = new Place[N];
        arrPlaces[0] = p1;
        arrPlaces[1] = p2;
        arrPlaces[2] = p3;
        life = 3;
    }

    public Place getPlace(int index) {
        return arrPlaces[index];
    }

    public int getLife() {
        return this.life;
    }

    public void removeLife() {
        this.life--;
    }
}
