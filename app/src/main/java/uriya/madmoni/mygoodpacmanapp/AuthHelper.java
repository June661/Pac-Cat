package uriya.madmoni.mygoodpacmanapp;

import static uriya.madmoni.mygoodpacmanapp.MainActivity.CURRENT_USER;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AuthHelper {
    Activity activity;

    public AuthHelper(Activity activity) {
        this.activity = activity;
    }

    public boolean isThereEmptyData(String email, String password) {
        return email.trim().isEmpty() || password.trim().isEmpty();
    }

    public boolean isThereEmptyData(String email, String password, String userName) {
        return email.trim().isEmpty() || password.trim().isEmpty() || userName.trim().isEmpty();
    }


    public void login(String email, String password, boolean rememberLogin) {
        if (!isThereEmptyData(email, password)) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        getCurrentUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("REMEMBER_LOGIN", rememberLogin).apply();
                    } else
                        Toast.makeText(activity, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else Toast.makeText(activity, "Enter all Data!", Toast.LENGTH_SHORT).show();
    }

    public void getCurrentUser(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.PLAYERS_REF);
        ref.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    CURRENT_USER = new FirebasePlayer((HashMap<String, Object>) task.getResult().getValue());
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.finish();
                } else
                    Toast.makeText(activity, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateInRealTimeDatabase(FirebasePlayer firebasePlayer) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.PLAYERS_REF);
        ref.child(firebasePlayer.uid).setValue(firebasePlayer.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    CURRENT_USER = firebasePlayer;
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.finish();
                } else
                    Toast.makeText(activity, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void register(String email, String password, String userName, boolean rememberRegister) {
        if (!isThereEmptyData(email, password, userName)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebasePlayer firebasePlayer = new FirebasePlayer(FirebaseAuth.getInstance().getCurrentUser().getUid(), email.trim(), userName.trim());
                        updateInRealTimeDatabase(firebasePlayer);
                        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("REMEMBER_LOGIN", rememberRegister).apply();
                    } else
                        Toast.makeText(activity, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } else Toast.makeText(activity, "Enter all Data!", Toast.LENGTH_SHORT).show();
    }

    public boolean isRememberAndLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null && (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("REMEMBER_LOGIN", false));
    }
}
