package uriya.madmoni.mygoodpacmanapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static MediaPlayer player;
    public static FirebasePlayer CURRENT_USER;

    public void showHelpActivity() {
    }

    public void showPlayActivity(View view) {
        startActivity(new Intent(this, PlayActivity.class));
    }


    public void logout() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("REMEMBER_LOGIN", false).apply();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = MediaPlayer.create(this, R.raw.pacman_song);
        player.setVolume(100, 100);
        player.setLooping(true);
        player.start();
    }

    public static MediaPlayer getPlayer() {
        return player;
    }

    @Override
    public void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onResume() {
        Log.i("info", "MainActivity onResume");
        super.onResume();
        player.start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_how_to_play:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.action_best_players:
                startActivity(new Intent(this, BestPlayerActivity.class));
                break;
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
}
