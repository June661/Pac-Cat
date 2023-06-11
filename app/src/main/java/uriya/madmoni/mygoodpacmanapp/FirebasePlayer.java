package uriya.madmoni.mygoodpacmanapp;

import java.util.HashMap;

public class FirebasePlayer {
    public String uid;
    public String email;
    public String userName;
    public int bestScore;

    public int numberOfGame;

    private final String UID_MAP_KEY = "uid";
    private final String EMAIL_MAP_KEY = "email";
    private final String USER_NAME_MAP_KEY = "userName";
    private final String BEST_SCORE_MAP_KEY = "bestScore";
    private final String NUMBER_OF_GAMES_MAP_KEY = "numberOfGame";

    public FirebasePlayer(String uid, String email, String userName) {
        this.uid = uid;
        this.email = email;
        this.userName = userName;
        this.bestScore = 0;
        this.numberOfGame = 0;
    }
    public FirebasePlayer(HashMap<String, Object> map) {
        this.uid = String.valueOf(map.get(UID_MAP_KEY));
        this.email = String.valueOf(map.get(EMAIL_MAP_KEY));
        this.userName = String.valueOf(map.get(USER_NAME_MAP_KEY));
        this.bestScore = Integer.parseInt(String.valueOf(map.get(BEST_SCORE_MAP_KEY)));
        this.numberOfGame = Integer.parseInt(String.valueOf(map.get(NUMBER_OF_GAMES_MAP_KEY)));
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(UID_MAP_KEY,uid);
        map.put(EMAIL_MAP_KEY,email);
        map.put(USER_NAME_MAP_KEY,userName);
        map.put(BEST_SCORE_MAP_KEY,bestScore);
        map.put(NUMBER_OF_GAMES_MAP_KEY,numberOfGame);
        return map;
    }
}
