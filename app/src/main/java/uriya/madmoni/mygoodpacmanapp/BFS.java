package uriya.madmoni.mygoodpacmanapp;

import java.util.LinkedList;
import java.util.Queue;

public class BFS {
    short[][] tempMaze=LandScapeDrawingView.leveldata1.clone();

    int[][] maze=new int[tempMaze.length][tempMaze[0].length];
   for(int i =0; i<maze.length; i++){

    }


    //BFS AI FUNCTION MAIN FUNCTION

    public int [][] bfs(int[][] maze, int depth, gamePiece currentGhost, int ghostNum) {
        //receives the initial array (int[][] maze), the location of the current ghost (gamePiece currentGhost), and the number that the ghost is represented by in the array (int ghostNum)
        //returns an updated array after the bfs algorithm was applied (or the random movement functions) and the ghost has moved
        Queue<String> ghostPath=new LinkedList<String>();
        ghostPath.add("");
        String nextMoveCheck;
        String currentPath = "";

        while(!foundPacMan(pathToLocation(lastInQueue(ghostPath), currentGhost))) {
            nextMoveCheck=ghostPath.remove();
            for(int i=0; i<4; i++) {
                switch(i) {
                    case 0:
                        currentPath=nextMoveCheck+"U";   // U for up
                        break;
                    case 1:
                        currentPath=nextMoveCheck+"D";   // D for down
                        break;
                    case 2:
                        currentPath=nextMoveCheck+"L";   // L for left
                        break;
                    case 3:
                        currentPath=nextMoveCheck+"R";   // R for right
                        break;
                }
                if (validMove(maze, pathToLocation(currentPath, currentGhost)))
                    ghostPath.add(currentPath);
                else if(lastInQueue(ghostPath).length()>=depth)
                    return moveRandom(maze, currentGhost, ghostNum);
            }
        }
        nextMoveCheck=lastInQueue(ghostPath);
        switch(nextMoveCheck.charAt(0)) {
            case 'U':
                if(currentGhost.getRow()-1>currentGhost.getNorthBorder() && currentGhost.getRow()-1<currentGhost.getSouthBorder() && currentGhost.getCol()>currentGhost.getWestBorder() && currentGhost.getCol()<currentGhost.getEastBorder()) {
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
        }
        maze[currentGhost.getRow()][currentGhost.getCol()]=1;
        currentLocation(maze);
        return maze;
    }



    //BFS AI FUNCTION SUB-FUNCTIONS

    public gamePiece pathToLocation(String currentPath, gamePiece currentGhost) {
        //receives path represnted by a string (String currentPath), and the location of the current ghost (gamePiece currentGhost)
        //returns an updated ghost location after it moved by the received path
        gamePiece ghostLocation= new gamePiece(currentGhost.getRow(), currentGhost.getCol(), 0, 22, 0, 16);
        for(int i=0; i<currentPath.length(); i++) {
            switch(currentPath.charAt(i)) {
                case 'U':
                    ghostLocation.setRow(ghostLocation.getRow()-1);
                    break;
                case 'D':
                    ghostLocation.setRow(ghostLocation.getRow()+1);
                    break;
                case 'L':
                    ghostLocation.setCol(ghostLocation.getCol()-1);
                    break;
                case 'R':
                    ghostLocation.setCol(ghostLocation.getCol()+1);
                    break;
            }
        }
        return ghostLocation;
    }

    public boolean validMove(int [][] maze, gamePiece ghostLocation) {
        //receives the initial array (int[][] maze), the location of the current ghost (gamePiece ghostLocation)
        //returns true if the location is valid (inside the ghost borders, and in playable area), otherwise return false
        if(!(ghostLocation.getRow()>ghostLocation.getNorthBorder() && ghostLocation.getRow()<ghostLocation.getSouthBorder()))
            return false;
        if(!(ghostLocation.getCol()>ghostLocation.getWestBorder() && ghostLocation.getCol()<ghostLocation.getEastBorder()))
            return false;
        for (int col = 0; col < 17; col++) {
            for (int row = 0; row < 23; row++) {
                switch(maze[ghostLocation.getRow()][ghostLocation.getCol()]) {
                    case 0:         // can't go through walls
                        return false;
                    case 2:         // can't exit game field
                        return false;
                    case 4:         // can't go through teleport
                        return false;
                    case 5:         // can't go through teleport
                        return false;
                    case 6:         // can't go through teleport
                        return false;
                    case 7:         // can't go through teleport
                        return false;
                }
            }
        }
        return true;
    }

    public int[][] moveRandom(int[][] maze, gamePiece ghostLocation, int ghostNum){
        //receives the initial array (int[][] maze), the location of the current ghost (gamePiece ghostLocation), and the number that the ghost is represented by in the array (int ghostNum)
        //return an updated array after the current ghost made a random (and valid) move
        Random random=new Random();
        boolean hasMoved=true;
        while(hasMoved) {
            switch(random.nextInt(4)) {
                case 0:     // move up
                    if(validMove(maze, ghostLocation.up())) {
                        maze[ghostLocation.getRow()][ghostLocation.getCol()]=1;
                        maze[ghostLocation.getRow()-1][ghostLocation.getCol()]=ghostNum;
                        hasMoved=false;
                        break;
                    }
                    break;
                case 1:     // move down
                    if(validMove(maze, ghostLocation.down())) {
                        maze[ghostLocation.getRow()][ghostLocation.getCol()]=1;
                        maze[ghostLocation.getRow()+1][ghostLocation.getCol()]=ghostNum;
                        hasMoved=false;
                        break;
                    }
                    break;
                case 2:     // move right
                    if(validMove(maze, ghostLocation.right())) {
                        maze[ghostLocation.getRow()][ghostLocation.getCol()]=1;
                        maze[ghostLocation.getRow()][ghostLocation.getCol()+1]=ghostNum;
                        hasMoved=false;
                        break;
                    }
                    break;
                case 3:     // move left
                    if(validMove(maze, ghostLocation.left())) {
                        maze[ghostLocation.getRow()][ghostLocation.getCol()]=1;
                        maze[ghostLocation.getRow()][ghostLocation.getCol()-1]=ghostNum;
                        hasMoved=false;
                        break;
                    }
                    break;
            }
        }
        //System.out.println("random move");
        currentLocation(maze);
        return maze;
    }

    public boolean foundPacMan(gamePiece ghostLocation) {
        //receives the location of the current ghost
        //return true if the location of the ghost is identical to the location of the pacman, otherwise returns false
        return (ghostLocation.getRow()==pacmanPos.getRow() && ghostLocation.getCol()==pacmanPos.getCol());
    }

    public void currentLocation(int[][] maze) {
        //receives the initial array (int [][] maze) and their absolute locations
        //the function sets the ghosts and pacmans location as the closest in-direction cell in the clone maze


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
