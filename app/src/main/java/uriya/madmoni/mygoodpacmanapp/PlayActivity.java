package uriya.madmoni.mygoodpacmanapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class PlayActivity extends Activity {
    static PlayActivity activity;
    private LandScapeDrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawingView = new LandScapeDrawingView(this,this);
        setContentView(drawingView);
        activity = this;
    }

    @Override
    protected void onPause() {
        Log.i("info", "onPause");
        super.onPause();
        drawingView.pause();
        MainActivity.getPlayer().pause();
    }

    @Override
    protected void onResume() {
        Log.i("info", "onResume");
        super.onResume();
        drawingView.resume();
        MainActivity.getPlayer().start();

    }

    public static PlayActivity getInstance() {
        return activity;
    }

}
