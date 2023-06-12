package uriya.madmoni.mygoodpacmanapp;

import static uriya.madmoni.mygoodpacmanapp.LandScapeDrawingView.ghostDirection;
import static uriya.madmoni.mygoodpacmanapp.LandScapeDrawingView.xPosGhost;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

public class BFS {
    int[][] maze= convertShortToInt( LandScapeDrawingView.leveldata1.clone());
    int Xghost;
    int Yghost;
    int Xpacman;
    int Ypacman;
    int currentGhost;


    public BFS(int CurrentGhost) {
        Xghost = currentLocationGhost(maze, "X", CurrentGhost);
        Yghost = currentLocationGhost(maze, "Y", CurrentGhost);
        Xpacman = currentLocationPacman(maze, "X", LandScapeDrawingView.direction);
        Ypacman = currentLocationPacman(maze, "Y", LandScapeDrawingView.direction);
        currentGhost=CurrentGhost;
    }

    public static int[][] convertShortToInt(short[][] shortArray) {
        int numRows = shortArray.length;
        int numCols = shortArray[0].length;

        int[][] intArray = new int[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                intArray[i][j] = shortArray[i][j];
            }
        }

        return intArray;
    }





    //BFS AI FUNCTION MAIN FUNCTION

    public Queue<String> bfs(int[][] maze, int depth) {
        //receives the initial array (int[][] maze), the location of the current ghost (gamePiece currentGhost), and the number that the ghost is represented by in the array (int ghostNum)
        //returns an updated array after the bfs algorithm was applied (or the random movement functions) and the ghost has moved
        Queue<String> ghostPath=new LinkedList<String>();
        ghostPath.add("");
        String nextMoveCheck;
        String currentPath = "";

        while(!foundPacMan()) {
            pathToLocation(lastInQueue(ghostPath));
            nextMoveCheck=ghostPath.remove();
            boolean foundMove = false;
            for(int i=0; i<4; i++) {
                switch(i) {
                    case 0:
                        currentPath=nextMoveCheck+"U";   // U for up
                        if (validMove(maze, i)) {
                            ghostPath.add(currentPath);
                            foundMove=true;
                        }
                        break;
                    case 1:
                        currentPath=nextMoveCheck+"D";   // D for down
                        if (validMove(maze, i)) {
                            ghostPath.add(currentPath);
                            foundMove=true;
                        }
                        break;
                    case 2:
                        currentPath=nextMoveCheck+"L";   // L for left
                        if (validMove(maze, i)) {
                            ghostPath.add(currentPath);
                            foundMove=true;
                        }
                        break;
                    case 3:
                        currentPath=nextMoveCheck+"R";   // R for right
                        if (validMove(maze, i)) {
                            ghostPath.add(currentPath);
                            foundMove=true;
                        }
                        break;
                }

                if(!foundMove)
                    return RegularMove(currentGhost);
                //todo: RegularMove callback
            }
        }
        nextMoveCheck=lastInQueue(ghostPath);

      /*  switch(nextMoveCheck.charAt(0)) {
            case 'U':
                if(Yghost-1>currentGhost.getNorthBorder() && currentGhost.getRow()-1<currentGhost.getSouthBorder() && currentGhost.getCol()>currentGhost.getWestBorder() && currentGhost.getCol()<currentGhost.getEastBorder()) {
                    if(maze[currentGhost.getRow()-1][currentGhost.getCol()]>=21 && maze[currentGhost.getRow()-1][currentGhost.getCol()]<=24) {
                        maze[currentGhost.getRow()][currentGhost.getCol()]=1;
                        maze[currentGhost.getRow()-1][currentGhost.getCol()]=ghostNum;
                        drawMaze(firstTimeDraw, mazeDrawing, maze);
                        faceGhost();
                        currentLocation(maze);
                        return maze;
                    }
                }
                maze[currentGhost.getRow()-1][currentGhost.getCol()]=ghostNum;
                break;
            case 'D':
                if(currentGhost.getRow()+1>currentGhost.getNorthBorder() && currentGhost.getRow()+1<currentGhost.getSouthBorder() && currentGhost.getCol()>currentGhost.getWestBorder() && currentGhost.getCol()<currentGhost.getEastBorder()) {
                    if(maze[currentGhost.getRow()+1][currentGhost.getCol()]>=21 && maze[currentGhost.getRow()+1][currentGhost.getCol()]<=24) {
                        maze[currentGhost.getRow()][currentGhost.getCol()]=1;
                        maze[currentGhost.getRow()+1][currentGhost.getCol()]=ghostNum;
                        drawMaze(firstTimeDraw, mazeDrawing, maze);
                        faceGhost();
                        currentLocation(maze);
                        return maze;
                    }
                }
                maze[currentGhost.getRow()+1][currentGhost.getCol()]=ghostNum;
                break;
            case 'L':
                if(currentGhost.getRow()>currentGhost.getNorthBorder() && currentGhost.getRow()<currentGhost.getSouthBorder() && currentGhost.getCol()-1>currentGhost.getWestBorder() && currentGhost.getCol()-1<currentGhost.getEastBorder()) {
                    if(maze[currentGhost.getRow()][currentGhost.getCol()-1]>=21 && maze[currentGhost.getRow()][currentGhost.getCol()-1]<=24) {
                        maze[currentGhost.getRow()][currentGhost.getCol()]=1;
                        maze[currentGhost.getRow()][currentGhost.getCol()-1]=ghostNum;
                        drawMaze(firstTimeDraw, mazeDrawing, maze);
                        faceGhost();
                        currentLocation(maze);
                        return maze;
                    }
                }
                maze[currentGhost.getRow()][currentGhost.getCol()-1]=ghostNum;
                break;
            case 'R':
                if(currentGhost.getRow()>currentGhost.getNorthBorder() && currentGhost.getRow()<currentGhost.getSouthBorder() && currentGhost.getCol()+1>currentGhost.getWestBorder() && currentGhost.getCol()+1<currentGhost.getEastBorder()) {
                    if(maze[currentGhost.getRow()][currentGhost.getCol()+1]>=21 && maze[currentGhost.getRow()][currentGhost.getCol()+1]<=24) {
                        maze[currentGhost.getRow()][currentGhost.getCol()]=1;
                        maze[currentGhost.getRow()][currentGhost.getCol()+1]=ghostNum;
                        drawMaze(firstTimeDraw, mazeDrawing, maze);
                        faceGhost();
                        currentLocation(maze);
                        return maze;
                    }
                    maze[currentGhost.getRow()][currentGhost.getCol()+1]=ghostNum;
                    break;
                }
        } */
        //maze[Yghost][Xghost]=1; //after movement- redraws point
        //currentLocation(maze);
        return ghostPath;
    }



    //BFS AI FUNCTION SUB-FUNCTIONS

    public void pathToLocation(String currentPath) {
        //receives path represnted by a string (String currentPath), and the location of the current ghost (gamePiece currentGhost)
        //returns an updated ghost location after it moved by the received path
        for(int i=0; i<currentPath.length(); i++) {
            switch(currentPath.charAt(i)) {
                case 'U':
                    Yghost--;
                    break;
                case 'D':
                    Yghost++;
                    break;
                case 'L':
                    Xghost--;
                    break;
                case 'R':
                    Xghost++;
                    break;
            }
        }
    }

    public void moveToLocation(String currentPath) {
        //receives path represnted by a string (String currentPath), and the location of the current ghost (gamePiece currentGhost)
        //returns an updated ghost location after it moved by the received path
        for(int i=0; i<currentPath.length(); i++) {
            switch(currentPath.charAt(i)) {
                case 'U':
                    Yghost--;
                    break;
                case 'D':
                    Yghost++;
                    break;
                case 'L':
                    Xghost--;
                    break;
                case 'R':
                    Xghost++;
                    break;
            }
        }
    }

    public boolean validMove(int [][] maze, int moveDirection) {
        //receives the initial array (int[][] maze), the location of the current ghost (gamePiece ghostLocation)
        //returns true if the location is valid (inside the ghost borders, and in playable area), otherwise return false
                switch(moveDirection) {
                    case 2:         // left
                        if ((maze[Xghost][Yghost] & 1) != 0){
                        return false;}
                    case 0:         // up
                        if ((maze[Xghost][Yghost] & 2) != 0){
                        return false;}
                    case 3:         // right
                        if ((maze[Xghost][Yghost] & 4) != 0){
                        return false;}
                    case 1:         // down
                        if ((maze[Xghost][Yghost] & 8) != 0){
                        return false;}
                }

        return true;
    }


    public boolean foundPacMan() {
        //return true if the location of the ghost is identical to the location of the pacman, otherwise returns false
        return (Xpacman==Xghost)&&(Yghost==Ypacman);
    }

    public int currentLocationPacman(int[][] maze, String s, int direction) {
        //receives the initial array (int [][] maze) and their absolute locations
        //the function sets pacmans location as the closest in-direction cell in the clone maze
    if (s.equals("Y")){
    switch (direction){
        case 2:
            return (LandScapeDrawingView.yPosPacman-(LandScapeDrawingView.yPosPacman%LandScapeDrawingView.blockSize))/LandScapeDrawingView.blockSize;
        case 1:
        case 3:
            return LandScapeDrawingView.yPosPacman/LandScapeDrawingView.blockSize;
        case 4:
            return ((LandScapeDrawingView.yPosPacman-(LandScapeDrawingView.yPosPacman%LandScapeDrawingView.blockSize))/LandScapeDrawingView.blockSize)+1;
    }
        }
    else if (s.equals("X")){
        switch (direction){
        case 1:
            return (LandScapeDrawingView.xPosPacman-(LandScapeDrawingView.xPosPacman%LandScapeDrawingView.blockSize))/LandScapeDrawingView.blockSize;
        case 2:
            case 4:
                return LandScapeDrawingView.xPosPacman/LandScapeDrawingView.blockSize;
        case 3:
            return ((LandScapeDrawingView.xPosPacman-(LandScapeDrawingView.xPosPacman%LandScapeDrawingView.blockSize))/LandScapeDrawingView.blockSize)+1;
        } }
    else
    Log.i("BFS", "currentLocationP: :(((((");
return 0;
    }


    public int currentLocationGhost(int[][] maze, String s, int currentGhost) {
        //receives the initial array (int [][] maze) and their absolute locations
        //the function sets the ghosts location as the closest in-direction cell in the clone maze
        if (s.equals("Y")){
            switch (ghostDirection[currentGhost]){
                case 2:
                    return (LandScapeDrawingView.yPosGhost[currentGhost]-(LandScapeDrawingView.yPosGhost[currentGhost]%LandScapeDrawingView.blockSize))/LandScapeDrawingView.blockSize;
                case 1:
                case 3:
                    return LandScapeDrawingView.yPosGhost[currentGhost]/LandScapeDrawingView.blockSize;
                case 4:
                    return ((LandScapeDrawingView.yPosGhost[currentGhost]-(LandScapeDrawingView.yPosGhost[currentGhost]%LandScapeDrawingView.blockSize))/LandScapeDrawingView.blockSize)+1;
            }
        }
        else if (s.equals("X")){
            switch (ghostDirection[currentGhost]){
                case 1:
                    return (xPosGhost[currentGhost]-(xPosGhost[currentGhost]%LandScapeDrawingView.blockSize))/LandScapeDrawingView.blockSize;
                case 2:
                case 4:
                    return xPosGhost[currentGhost]/LandScapeDrawingView.blockSize;
                case 3:
                    return ((xPosGhost[currentGhost]-(xPosGhost[currentGhost]%LandScapeDrawingView.blockSize))/LandScapeDrawingView.blockSize)+1;
            } }
        else
            Log.i("BFS", "currentLocationG: :((((((((((((((((");
        return 0;
    }


    public String lastInQueue(Queue<String> ghostPath) {
        //receives String-type Queue
        //returns last String in Queue
        String last="";
        Queue<String> temp1=new LinkedList<String>();
        Queue<String> temp2=new LinkedList<String>();

        while(!ghostPath.isEmpty()) {
            temp1.add(ghostPath.peek());
            temp2.add(ghostPath.remove());
        }
        while(!temp2.isEmpty()) {
            ghostPath.add(temp2.remove());
        }

        while(!temp1.isEmpty())
            last=temp1.remove();
        return last;
    }
}
