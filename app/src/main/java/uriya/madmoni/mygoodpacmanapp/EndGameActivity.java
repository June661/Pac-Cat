package uriya.madmoni.mygoodpacmanapp;

import static uriya.madmoni.mygoodpacmanapp.MainActivity.CURRENT_USER;
import static uriya.madmoni.mygoodpacmanapp.PlayActivity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class EndGameActivity extends AppCompatActivity {

    TextView tvState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_end_score);

        //tvState = findViewById(R.id.tvState);
        Intent intent = getIntent();
        updateBestScore(intent.getExtras().getInt(Constants.INTENT_SCORE_PLAY_ACTIVITY_TO_LOSE_ACTIVITY,0));
        if(intent.getExtras().getBoolean(Constants.INTENT_IS_WIN_ACTIVITY_TO_LOSE_ACTIVITY,false))
            showAlert(true);
        else
            showAlert(false);
    }

    public void updateBestScore(int currentScore) {
        //todo: add broadcast reciever for internet connection
        if (CURRENT_USER.bestScore < currentScore)
            CURRENT_USER.bestScore = currentScore;
        CURRENT_USER.numberOfGame++;
        FirebaseDatabase.getInstance().getReference(Constants.PLAYERS_REF).child(CURRENT_USER.uid).setValue(CURRENT_USER.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(EndGameActivity.this, "The Score updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(EndGameActivity.this, "The Score didn't updated because " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void tryAgain(View view) {
        startActivity(new Intent(this, PlayActivity.class));
        finish();
    }

    public void goToMain(View view) {
        finish();
    }



    private void showAlert(boolean isWin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //get context prob???
        if(isWin){
            builder.setTitle("YOU WON !");
            builder.setMessage("peek at the updated scoreboard or return to menu");
            builder.setPositiveButton("YAY", (dialog, which) -> {
                // go to scoreboard
                Intent intent = new Intent(activity, BestPlayerActivity.class);
                activity.startActivity(intent);
                activity.finish();
            });
            builder.setNegativeButton("Go To Menu", (dialog, which) -> {
                // go to menu
                activity.finish();                     });

            //.show();
            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();
        }
        else {
            builder.setTitle("YOU LOST ! ;(");
            builder.setMessage("re-try or return to menu");
            builder.setPositiveButton("again!", (dialog, which) -> {
                // go to scoreboard
                Intent intent = new Intent(activity, PlayActivity.class);
                activity.startActivity(intent);
                activity.finish();
            });
            builder.setNegativeButton("Go To Menu", (dialog, which) -> {
                // go to menu
                activity.finish();
            });

            //.show();
            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();

        }
    }


}