package uriya.madmoni.mygoodpacmanapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BestPlayerAdapter extends RecyclerView.Adapter<BestPlayerAdapter.MiniBestPlayerHolder> implements RecyclerView.OnItemTouchListener {

    public ArrayList<FirebasePlayer> mList;
    private final Context context;
    private boolean isManager;
    private Activity activity;

    public BestPlayerAdapter(Context context, ArrayList<FirebasePlayer> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MiniBestPlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.best_player_tile, parent, false);
        return new MiniBestPlayerHolder(v);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MiniBestPlayerHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvName.setText(String.valueOf(mList.get(position).userName));
        holder.tvPlace.setText(String.valueOf(position+1));
        holder.tvNumberOfGames.setText("Number of Games: " +mList.get(position).numberOfGame);
        holder.tvScore.setText("Best Score: " + mList.get(position).bestScore);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MiniBestPlayerHolder extends RecyclerView.ViewHolder {

        TextView tvPlace, tvNumberOfGames, tvName, tvScore;

        public MiniBestPlayerHolder(@NonNull View itemView) {
            super(itemView);
            tvPlace = itemView.findViewById(R.id.tvPlace);
            tvNumberOfGames = itemView.findViewById(R.id.tvNumberOfGames);
            tvName = itemView.findViewById(R.id.tvName);
            tvScore = itemView.findViewById(R.id.tvScore);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}