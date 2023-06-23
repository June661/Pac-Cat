package uriya.madmoni.mygoodpacmanapp;

import static uriya.madmoni.mygoodpacmanapp.MainActivity.CURRENT_USER;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Queue;

public class LandScapeDrawingView extends SurfaceView implements Runnable, SurfaceHolder.Callback, SensorEventListener {

    private Activity activity;
    private Thread thread;
    private SurfaceHolder holder;
    private boolean canDraw = true;

    private Paint paint;
    private Bitmap[] pacmanRight, pacmanDown, pacmanLeft, pacmanUp;
    private Bitmap[] ghostBitmap;
    private int totalFrame = 4;             // Total amount of frames fo each direction
    private int currentPacmanFrame = 0;     // Current Pacman frame to draw
    private int currentArrowFrame = 0;      // Current arrow frame to draw
    private long frameTicker;               // Current time since last frame has been drawn
    public static int xPosPacman;                 // x-axis position of pacman
    public static int yPosPacman;                 // y-axis position of pacman
    public static int[] xPosGhost = new int[4];                  // x-axis position of ghost
    public static int[] yPosGhost = new int[4];                  // y-axis position of ghost
    int[] xDistance = new int[4];
    int[] yDistance = new int[4];
    private float x1, x2, y1, y2;           // Initial/Final positions of swipe
    public static int direction = 4;              // Direction of the swipe, initial direction is right
    private int nextDirection = 4;          // Buffer for the next direction you choose
    private int viewDirection = 2;          // Direction that pacman is facing
    public static int[] ghostDirection;
    private int screenHeight, screenWidth;                // Width of the phone screen
    public static int blockSize;                  // Size of a block on the map
    private int currentScore = 0;           //Current game score

    final int DIVIDE_HEIGHT = 21;

    LifeManager lifeManager;

    int MAX_SCORE = 0;
    int MAX_CIRCLE = 0;
    final int NUMBER_OF_GHOST = 1; //4

    long startTime;

    private SensorManager sensorManager;
    private Sensor sensor;

    boolean tiltEnabled=false;


    enum DirectionKey {
        // 0 means going up
        // 1 means going right
        // 2 means going down
        // 3 means going left
        // 4 means stop moving, look at move function
        UP,
        RIGHT,
        DOWN,
        LEFT,
        STOP
    }

    public LandScapeDrawingView(Activity activity, Context context) {
        super(context);
        this.activity = activity;
        holder = getHolder();
        holder.addCallback(this);
        frameTicker = 1000 / totalFrame;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        startTime = System.currentTimeMillis();

        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        blockSize = screenHeight / DIVIDE_HEIGHT;
        blockSize = (blockSize / 5) * 5;

        xPosGhost = new int[4];
        xPosGhost[0] = 3 * blockSize;
        xPosGhost[1] = 3 * blockSize;
        xPosGhost[2] = 13 * blockSize;
        xPosGhost[3] = 13 * blockSize;

        ghostDirection = new int[4];
        ghostDirection[0] = 4;

        yPosGhost = new int[4];
        yPosGhost[0] = 4 * blockSize;
        yPosGhost[1] = 12 * blockSize;
        yPosGhost[2] = 4 * blockSize;
        yPosGhost[3] = 12 * blockSize;

        xPosPacman = 8 * blockSize;
        yPosPacman = 13 * blockSize;

        lifeManager = new LifeManager(new Place(100, 150),
                new Place(200, 150),
                new Place(300, 150));



        loadBitmapImages();
        Log.i("info", "Constructor");

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void run() {
        Log.i("info", "Run");
        while (canDraw) {
            if (!holder.getSurface().isValid()) {
                continue;
            }
            Canvas canvas = holder.lockCanvas();
            // Set background color to Transparent
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);
                drawMap(canvas);

                updateFrame(canvas, System.currentTimeMillis());

                moveGhost(canvas);

                // Moves the pacman based on his direction
                movePacman(canvas);

                // Draw the pellets
                drawPellets(canvas);

                //Update current and high scores
                updateScores(canvas);

                for (int i = 0; i < lifeManager.getLife(); i++)
                    canvas.drawBitmap(pacmanRight[currentPacmanFrame],
                            lifeManager.getPlace(i).getX(),
                            lifeManager.getPlace(i).getY(),
                            paint);

                //todo: add ppause and control buttons here

                holder.unlockCanvasAndPost(canvas);

                //Log.e("level.length", "" + leveldata1.length);
                //Log.e("level[0].length", "" + leveldata1[0].length);


                //add AI


                //close AI

            }
        }
    }

    public void updateScores(Canvas canvas) {
        paint.setTextSize(blockSize);

        int textMargin = 30;
        String formattedHighScore = String.format("%05d", CURRENT_USER.bestScore);
        String hScore = "High Score : " + formattedHighScore;
        canvas.drawText(hScore, 0 + textMargin, 2 * blockSize - 10, paint);

        String formattedScore = String.format("%05d", currentScore);
        String score = "Score : " + formattedScore;

        Rect bounds = new Rect();
        paint.getTextBounds(score, 0, score.length(), bounds);
        int textWidth = bounds.width();

        canvas.drawText(score, screenWidth - textWidth - textMargin, 2 * blockSize - 10, paint);

    }

    public void moveGhost(Canvas canvas) {
        short ch = 0;


        for (int i = 0; i < NUMBER_OF_GHOST; i++) {
            xDistance[i] = xPosPacman - xPosGhost[i];
            yDistance[i] = yPosPacman - yPosGhost[i];

            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;

            if (((Math.sqrt(Math.pow(xDistance[i], 2) + Math.pow(yDistance[i], 2))) <= 8 * blockSize)&&((elapsedTime >= 8000))) {
                BFS bfsInstance = new BFS(i);
                moveByQueue(bfsInstance.bfs(),canvas, i, ch);
                startTime = currentTime;
            } else {
                regularMoveGhost(canvas, i, ch);
            }


        }
    }

    public void moveByQueue (Queue<String> path,Canvas canvas,int i,short ch){
        if(path == null) {
            regularMoveGhost(canvas, i, ch);
        }
        else{
            for(int j=0; j<path.size();j++ ){
                switch (path.poll()){
                    case "U":
                        int currY0=yPosGhost[i];
                        while((currY0-yPosGhost[i])>blockSize){
                            ghostDirection[i]=0;
                        }
                        break;
                    case "D":
                        int currY1=yPosGhost[i];
                        while((yPosGhost[i]-currY1)>blockSize){
                            ghostDirection[i]=2;
                        }
                        break;
                    case "L":
                        int currX0=xPosGhost[i];
                        while((currX0-xPosGhost[i])>blockSize){
                            ghostDirection[i]=3;
                        }
                        break;
                    case "R":
                        int currX1=xPosGhost[i];
                        while((xPosGhost[i]-currX1)>blockSize){
                            ghostDirection[i]=1;
                        }
                        break;
                }
            }
        }
    }


        public void regularMoveGhost (Canvas canvas,int i, short ch){
            if (((xPosGhost[i]) % blockSize == 0) && (yPosGhost[i] % blockSize == 0)) {
                try {
                    ch = leveldata1[yPosGhost[i] / blockSize][xPosGhost[i] / blockSize];
                } catch (Exception e) {
                    Log.e("move ghost", e.getLocalizedMessage());
                    return;
                }
                if (xPosGhost[i] >= blockSize * leveldata1[0].length) {
                    xPosGhost[i] = 0;
                }
                if (xPosGhost[i] < 0) {
                    xPosGhost[i] = blockSize * leveldata1[0].length;
                }
            

                if (xDistance[i] >= 0 && yDistance[i] >= 0) { // Move right and down
                    if ((ch & 4) == 0 && (ch & 8) == 0) {
                        if (Math.abs(xDistance[i]) > Math.abs(yDistance[i])) {
                            ghostDirection[i] = LandScapeDrawingView.DirectionKey.RIGHT.ordinal();
                        } else {
                            ghostDirection[i] = LandScapeDrawingView.DirectionKey.DOWN.ordinal();
                        }
                    } else if ((ch & 4) == 0) {
                        ghostDirection[i] = LandScapeDrawingView.DirectionKey.RIGHT.ordinal();
                    } else if ((ch & 8) == 0) {
                        ghostDirection[i] = LandScapeDrawingView.DirectionKey.DOWN.ordinal();
                    } else
                        ghostDirection[i] = LandScapeDrawingView.DirectionKey.LEFT.ordinal();
                }
                if (xDistance[i] >= 0 && yDistance[i] <= 0) { // Move right and up
                    if ((ch & 4) == 0 && (ch & 2) == 0) {
                        if (Math.abs(xDistance[i]) > Math.abs(yDistance[i])) {
                            ghostDirection[i] = LandScapeDrawingView.DirectionKey.RIGHT.ordinal();
                        } else {
                            ghostDirection[i] = LandScapeDrawingView.DirectionKey.UP.ordinal();
                        }
                    } else if ((ch & 4) == 0) {
                        ghostDirection[i] = LandScapeDrawingView.DirectionKey.RIGHT.ordinal();
                    } else if ((ch & 2) == 0) {
                        ghostDirection[i] = LandScapeDrawingView.DirectionKey.UP.ordinal();
                    } else ghostDirection[i] = LandScapeDrawingView.DirectionKey.DOWN.ordinal();
                }
                if (xDistance[i] <= 0 && yDistance[i] >= 0) { // Move left and down
                    if ((ch & 1) == 0 && (ch & 8) == 0) {
                        if (Math.abs(xDistance[i]) > Math.abs(yDistance[i])) {
                            ghostDirection[i] = LandScapeDrawingView.DirectionKey.LEFT.ordinal();
                        } else {
                            ghostDirection[i] = LandScapeDrawingView.DirectionKey.DOWN.ordinal();
                        }
                    } else if ((ch & 1) == 0) {
                        ghostDirection[i] = LandScapeDrawingView.DirectionKey.LEFT.ordinal();
                    } else if ((ch & 8) == 0) {
                        ghostDirection[i] = LandScapeDrawingView.DirectionKey.DOWN.ordinal();
                    } else ghostDirection[i] = LandScapeDrawingView.DirectionKey.RIGHT.ordinal();
                }
                if (xDistance[i] <= 0 && yDistance[i] <= 0) { // Move left and up
                    if ((ch & 1) == 0 && (ch & 2) == 0) {
                        if (Math.abs(xDistance[i]) > Math.abs(yDistance[i])) {
                            ghostDirection[i] = LandScapeDrawingView.DirectionKey.LEFT.ordinal();
                        } else {
                            ghostDirection[i] = LandScapeDrawingView.DirectionKey.UP.ordinal();
                        }
                    } else if ((ch & 1) == 0) {
                        ghostDirection[i] = LandScapeDrawingView.DirectionKey.LEFT.ordinal();
                    } else if ((ch & 2) == 0) {
                        ghostDirection[i] = LandScapeDrawingView.DirectionKey.UP.ordinal();
                    } else ghostDirection[i] = LandScapeDrawingView.DirectionKey.DOWN.ordinal();
                }
                // Handles wall collisions
                if ((ghostDirection[i] == 3 && (ch & 1) != 0) ||
                        (ghostDirection[i] == 1 && (ch & 4) != 0) ||
                        (ghostDirection[i] == 0 && (ch & 2) != 0) ||
                        (ghostDirection[i] == 2 && (ch & 8) != 0)) {
                    ghostDirection[i] = LandScapeDrawingView.DirectionKey.STOP.ordinal();
                }
            }

            if (ghostDirection[i] == 0) {
                yPosGhost[i] += -blockSize / 20;
            } else if (ghostDirection[i] == 1) {
                xPosGhost[i] += blockSize / 20;
            } else if (ghostDirection[i] == 2) {
                yPosGhost[i] += blockSize / 20;
            } else if (ghostDirection[i] == 3) {
                xPosGhost[i] += -blockSize / 20;
            }

            canvas.drawBitmap(ghostBitmap[i], xPosGhost[i] + (screenWidth / 3), yPosGhost[i], paint);
        }


    //Updates the character sprite and handles collisions
    public void movePacman(Canvas canvas) {
        short ch;

        // Check if xPos and yPos of pacman is both a multiple of block size
        if ((xPosPacman % blockSize == 0) && (yPosPacman % blockSize == 0)) {

            // When pacman goes through tunnel on
            // the right reappear at left tunnel
            if (xPosPacman >= blockSize * leveldata1[0].length) {
                Log.e("pacman transform", "transform");
                xPosPacman = 0;
            }

            // Is used to find the number in the level array in order to
            // check wall placement, pellet placement, and candy placement
            ch = leveldata1[yPosPacman / blockSize][xPosPacman / blockSize];

            // If there is a pellet, eat it
            if ((ch & 16) != 0) {
                // Toggle pellet so it won't be drawn anymore
                leveldata1[yPosPacman / blockSize][xPosPacman / blockSize] = (short) (ch ^ 16);
                currentScore += 10;
                if (currentScore >= MAX_SCORE) endGame(true);
            }

            // Checks for direction buffering
            if (!((nextDirection == DirectionKey.LEFT.ordinal() && (ch & 1) != 0) ||
                    (nextDirection == DirectionKey.RIGHT.ordinal() && (ch & 4) != 0) ||
                    (nextDirection == DirectionKey.UP.ordinal() && (ch & 2) != 0) ||
                    (nextDirection == DirectionKey.DOWN.ordinal() && (ch & 8) != 0))) {
                viewDirection = direction = nextDirection;
            }

            // Checks for wall collisions
            if ((direction == DirectionKey.LEFT.ordinal() && (ch & 1) != 0) ||
                    (direction == DirectionKey.RIGHT.ordinal() && (ch & 4) != 0) ||
                    (direction == DirectionKey.UP.ordinal() && (ch & 2) != 0) ||
                    (direction == DirectionKey.DOWN.ordinal() && (ch & 8) != 0)) {
                direction = 4;
            }
        }

        // When pacman goes through tunnel on
        // the left reappear at right tunnel
        if (xPosPacman < 0) {
            xPosPacman = blockSize * leveldata1[0].length;
            Log.e("pacman transform", "transform");
        }

        drawPacman(canvas);

        // Depending on the direction move the position of pacman
        if (direction == 0) {
            yPosPacman += -blockSize / DIVIDE_HEIGHT; //OG: BS/15
        } else if (direction == 1) {
            xPosPacman += blockSize / DIVIDE_HEIGHT;
        } else if (direction == 2) {
            yPosPacman += blockSize / DIVIDE_HEIGHT;
        } else if (direction == 3) {
            xPosPacman += -blockSize / DIVIDE_HEIGHT;
        }
    }

    // Method that draws pacman based on his viewDirection
    public void drawPacman(Canvas canvas) {
        switch (viewDirection) {
            case (0):
                canvas.drawBitmap(pacmanUp[currentPacmanFrame], xPosPacman + (screenWidth / 3), yPosPacman, paint);
                break;
            case (1):
                canvas.drawBitmap(pacmanRight[currentPacmanFrame], xPosPacman + (screenWidth / 3), yPosPacman, paint);
                break;
            case (3):
                canvas.drawBitmap(pacmanLeft[currentPacmanFrame], xPosPacman + (screenWidth / 3), yPosPacman, paint);
                break;
            default:
                canvas.drawBitmap(pacmanDown[currentPacmanFrame], xPosPacman + (screenWidth / 3), yPosPacman, paint);
                break;
        }
    }

    // Method that draws pellets and updates them when eaten
    public void drawPellets(Canvas canvas) {
        float x;
        float y;
        for (int i = 0; i < leveldata1.length; i++) {
            for (int j = 0; j < leveldata1[0].length; j++) {
                x = j * blockSize + (screenWidth / 3);
                y = i * blockSize;
                // Draws pellet in the middle of a block
                if ((leveldata1[i][j] & 16) != 0) {
                    canvas.drawCircle(x + blockSize / 2, y + blockSize / 2, blockSize / 10, paint);
                    MAX_CIRCLE++;
                    MAX_SCORE = Math.max(10 * MAX_CIRCLE, MAX_SCORE);
                }
            }
        }

    }

    // Method to draw map layout
    public void drawMap(Canvas canvas) {
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2.5f);
        int x;
        int y;
        for (int i = 0; i < leveldata1.length; i++) {
            for (int j = 0; j < leveldata1[i].length; j++) {
                x = j * blockSize + (screenWidth / 3);
                y = i * blockSize;
                if ((leveldata1[i][j] & 1) != 0) // draws left
                    canvas.drawLine(x, y, x, y + blockSize - 1, paint);

                if ((leveldata1[i][j] & 2) != 0) // draws top
                    canvas.drawLine(x, y, x + blockSize - 1, y, paint);

                if ((leveldata1[i][j] & 4) != 0) // draws right
                    canvas.drawLine(
                            x + blockSize, y, x + blockSize, y + blockSize - 1, paint);
                if ((leveldata1[i][j] & 8) != 0) // draws bottom
                    canvas.drawLine(
                            x, y + blockSize, x + blockSize - 1, y + blockSize, paint);
            }
        }
        paint.setColor(Color.WHITE);
    }

    Runnable longPressed = new Runnable() {
        public void run() {
            Log.i("info", "LongPress");
            Intent pauseIntent = new Intent(getContext(), PauseActivity.class);
            getContext().startActivity(pauseIntent);
        }
    };

    // Method to get touch events
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN): {
                x1 = event.getX();
                y1 = event.getY();
                // todo: pause handler.postDelayed(longPressed, LONG_PRESS_TIME);
                break;
            }
            case (MotionEvent.ACTION_UP): {
                x2 = event.getX();
                y2 = event.getY();
                calculateSwipeDirection();
                // todo: pause handler.removeCallbacks(longPressed);
                break;
            }
        }
        return true;
    }

    // Calculates which direction the user swipes
    // based on calculating the differences in
    // initial position vs final position of the swipe
    private void calculateSwipeDirection() {
        float xDiff = (x2 - x1);
        float yDiff = (y2 - y1);

        // Directions
        // 0 means going up
        // 1 means going right
        // 2 means going down
        // 3 means going left
        // 4 means stop moving, look at move function

        // Checks which axis has the greater distance
        // in order to see which direction the swipe is
        // going to be (buffering of direction)
        if (Math.abs(yDiff) > Math.abs(xDiff)) {
            if (yDiff < 0) {
                nextDirection = DirectionKey.UP.ordinal();
            } else if (yDiff > 0) {
                nextDirection = DirectionKey.DOWN.ordinal();
            }
        } else {
            if (xDiff < 0) {
                nextDirection = DirectionKey.LEFT.ordinal();
            } else if (xDiff > 0) {
                nextDirection = DirectionKey.RIGHT.ordinal();
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(tiltEnabled){
        float x = event.values[0];
        float y = event.values[1];
        if (Math.abs(x) > Math.abs(y)) {
            if (x < 0) {
                nextDirection = DirectionKey.UP.ordinal();
            }
            if (x > 0) {
                nextDirection = DirectionKey.DOWN.ordinal();
            }
        } else {
            if (y < 0) {
                nextDirection = DirectionKey.LEFT.ordinal();
            }
            if (y > 0) {
                nextDirection = DirectionKey.RIGHT.ordinal();
            }
        }
        if (x > (-2) && x < (2) && y > (-2) && y < (2)) {
            direction=4;
        }
    }}

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //todo: ???
    }

    // Check to see if we should update the current frame
    // based on time passed so the animation won't be too
    // quick and look bad
    private void updateFrame(Canvas canvas, long gameTime) {

        // If enough time has passed go to next frame
        if (gameTime > frameTicker + (totalFrame * DIVIDE_HEIGHT)) {
            frameTicker = gameTime;

            // Increment the frame
            currentPacmanFrame++;
            // Loop back the frame when you have gone through all the frames
            if (currentPacmanFrame >= totalFrame) {
                currentPacmanFrame = 0;
            }
        }
        if (gameTime > frameTicker + (50)) {
            currentArrowFrame++;
            if (currentArrowFrame >= 7) {
                currentArrowFrame = 0;
            }
        }

        if (isPacmanGetTouched()) {
            //todo: add death animation
            lifeManager.removeLife();
            xPosPacman = 8 * blockSize;
            yPosPacman = 13 * blockSize;
            if (lifeManager.getLife() <= 0) endGame(false);
        }
    }

    public int getCurrentScore(){
        return this.currentScore;
    }

    public void endGame(boolean isWin) {
       /* Intent intent = new Intent(activity, EndGameActivity.class);
        intent.putExtra(Constants.INTENT_SCORE_PLAY_ACTIVITY_TO_LOSE_ACTIVITY, currentScore);
        intent.putExtra(Constants.INTENT_IS_WIN_ACTIVITY_TO_LOSE_ACTIVITY, isWin);
        activity.startActivity(intent);
        activity.finish(); */
        canDraw=false;
       updateBestScore(getCurrentScore());
        //showAlert(isWin);

        activity.runOnUiThread(() -> showAlert(isWin));
    }


   public void updateBestScore(int currentScore) {
        //todo: add broadcast receiver for internet connection
        if (CURRENT_USER.bestScore < currentScore)
            CURRENT_USER.bestScore = currentScore;
        CURRENT_USER.numberOfGame++;
        FirebaseDatabase.getInstance().getReference(Constants.PLAYERS_REF).child(CURRENT_USER.uid).setValue(CURRENT_USER.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(getContext(), "The Score updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "The Score didn't updated because " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlert(boolean isWin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); //get context prob???
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
                // make playable again
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

    public boolean isPacmanGetTouched() {
        for (int i = 0; i < NUMBER_OF_GHOST; i++) {
            if (Math.max(xPosPacman, xPosGhost[i]) - Math.min(xPosPacman, xPosGhost[i]) < 15
                    && Math.max(yPosPacman, yPosGhost[i]) - Math.min(yPosPacman, yPosGhost[i]) < 15)
                return true;
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("info", "Surface Created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("info", "Surface Changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("info", "Surface Destroyed");
    }

    public void pause() {
        sensorManager.unregisterListener(this);
        Log.i("info", "pause");
        canDraw = false;
        thread = null;
    }

    public void resume() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("info", "resume");
        if (thread != null) {
            thread.start();
        }
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
            Log.i("info", "resume thread");
        }
        canDraw = true;
    }

    private void loadBitmapImages() {
        // Scales the sprites based on screen
        int spriteSize = screenHeight / DIVIDE_HEIGHT;        // Size of Pacman & Ghost
        spriteSize = (spriteSize / 5) * 5;      // Keep it a multiple of 5

        pacmanRight = new Bitmap[totalFrame];
        pacmanRight[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_right1), spriteSize, spriteSize, false);
        pacmanRight[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_right2), spriteSize, spriteSize, false);
        pacmanRight[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_right3), spriteSize, spriteSize, false);
        pacmanRight[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_right), spriteSize, spriteSize, false);
        // Add bitmap images of pacman facing down
        pacmanDown = new Bitmap[totalFrame];
        pacmanDown[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_down1), spriteSize, spriteSize, false);
        pacmanDown[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_down2), spriteSize, spriteSize, false);
        pacmanDown[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_down3), spriteSize, spriteSize, false);
        pacmanDown[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_down), spriteSize, spriteSize, false);
        // Add bitmap images of pacman facing left
        pacmanLeft = new Bitmap[totalFrame];
        pacmanLeft[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_left1), spriteSize, spriteSize, false);
        pacmanLeft[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_left2), spriteSize, spriteSize, false);
        pacmanLeft[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_left3), spriteSize, spriteSize, false);
        pacmanLeft[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_left), spriteSize, spriteSize, false);
        // Add bitmap images of pacman facing up
        pacmanUp = new Bitmap[totalFrame];
        pacmanUp[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_up1), spriteSize, spriteSize, false);
        pacmanUp[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_up2), spriteSize, spriteSize, false);
        pacmanUp[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_up3), spriteSize, spriteSize, false);
        pacmanUp[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman_up), spriteSize, spriteSize, false);

        ghostBitmap = new Bitmap[4];
        ghostBitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ghost0), spriteSize, spriteSize, false);
        ghostBitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ghost1), spriteSize, spriteSize, false);
        ghostBitmap[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ghost2), spriteSize, spriteSize, false);
        ghostBitmap[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ghost3), spriteSize, spriteSize, false);
    }

    // 0 = null
    // 1 = left boarder
    // 2 = right boarder
    // 4 = right boarder

    // 1 = left
    // 2 = top
    // 3 = left && top
    // 4 = right
    // 5 = left && right
    // 6 = right && top
    // 7 = left && top && right
    // 8 = bottom
    // 9 = left && bottom
    // 10 = top && bottom
    // 11 = left && top && bottom
    // 12 = right && bottom
    // 13 = left && right && bottom
    // 14 = top && right && bottom
    // 15 = top && left && right && bottom

    // 16 = white dot
    // 17 = white dot && left
    // 18 = white dot && top
    // 19 = white dot && left && top
    // 20 = white dot && right
    // 21 = white dot && left && right
    // 22 = white dot && top && right
    // 23 = white dot && top && left && right
    // 24 = white dot && bottom
    // 25 = white dot && left && bottom
    // 26 = white dot && top && bottom
    // 27 = white dot && top && left && bottom
    // 28 = white dot && right && bottom

    // [18][17]
    static final short[][] leveldata1 = new short[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {19, 26, 26, 18, 26, 26, 26, 22, 0, 19, 26, 26, 26, 18, 26, 26, 22},
            {21, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 21},
            {17, 26, 26, 16, 26, 18, 26, 24, 26, 24, 26, 18, 26, 16, 26, 26, 20},
            {25, 26, 26, 20, 0, 25, 26, 22, 0, 19, 26, 28, 0, 17, 26, 26, 28},
            {0, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 0},
            {0, 0, 0, 21, 0, 19, 26, 24, 26, 24, 26, 22, 0, 21, 0, 0, 0},
            {26, 26, 26, 16, 26, 20, 0, 0, 0, 0, 0, 17, 26, 16, 26, 26, 26},
            {0, 0, 0, 21, 0, 17, 26, 26, 26, 26, 26, 20, 0, 21, 0, 0, 0},
            {0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0},
            {19, 26, 26, 16, 26, 24, 26, 22, 0, 19, 26, 24, 26, 16, 26, 26, 22},
            {21, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 21},
            {25, 22, 0, 21, 0, 0, 0, 17, 2, 20, 0, 0, 0, 21, 0, 19, 28}, // "2" in this line is for
            {0, 21, 0, 17, 26, 26, 18, 24, 24, 24, 18, 26, 26, 20, 0, 21, 0}, // pacman's spawn
            {19, 24, 26, 28, 0, 0, 25, 18, 26, 18, 28, 0, 0, 25, 26, 24, 22},
            {21, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 21},
            {25, 26, 26, 26, 26, 26, 26, 24, 26, 24, 26, 26, 26, 26, 26, 26, 28},
    };
}
