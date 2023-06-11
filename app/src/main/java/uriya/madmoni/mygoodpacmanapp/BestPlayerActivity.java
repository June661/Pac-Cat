package uriya.madmoni.mygoodpacmanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class BestPlayerActivity extends AppCompatActivity {

    RecyclerView rvBestPlayer;
    ArrayList<FirebasePlayer> playersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_player);

        initWidgets();
        retrieveData();
    }

    public void initWidgets() {
        rvBestPlayer = findViewById(R.id.rvBestPlayer);
        rvBestPlayer.setHasFixedSize(false);
        rvBestPlayer.setLayoutManager(new LinearLayoutManager(this));
    }

    public void retrieveData()
    {
        // מציג את 10 השחקנים הכי טובים במשחק
        FirebaseDatabase.getInstance().getReference(Constants.PLAYERS_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                playersList = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    playersList.add(new FirebasePlayer((HashMap<String, Object>) data.getValue()));
                }
                sortArrayListFromBiggestToSmallest(playersList);
                BestPlayerAdapter bestPlayerAdapter = new BestPlayerAdapter(BestPlayerActivity.this, playersList);
                rvBestPlayer.setAdapter(bestPlayerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sortArrayListFromBiggestToSmallest(ArrayList<FirebasePlayer> arrayList)
    {
        // מסדר את המערך רשימה מהגדול לקטן

        int i=0,j;
        FirebasePlayer temp;

        boolean sorted = false; // מסמן אם המערך רשימה סודר או לא

        while(i<arrayList.size()-1 && !sorted)
        {
            sorted=true;
            for(j=0; j<arrayList.size()-1-i; j++)
            {
                if(arrayList.get(j).bestScore<arrayList.get(j+1).bestScore)
                {
                    temp=arrayList.get(j);
                    arrayList.set(j,arrayList.get(j+1));
                    arrayList.set(j+1,temp);
                    sorted=false;
                }
            }
            i++;
        }

        // משאיר רק 10 מהרשימה:
        if(arrayList.size()>10)
            while (arrayList.size()>10)
                arrayList.remove(10);
    }


}