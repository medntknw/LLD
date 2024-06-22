import java.util.*;
import java.io.*;
import java.lang.*;

enum TileType{
    BOMB(0),
    SAFE(1);

    private int type;
    TileType(int type){
        this.type = type;
    }

    public int getType(){
        return this.type;
    }
}
class Tile {
    int value;
    TileType type;
    boolean isOpened;

    Tile(TileType type){
        this.type = type;
        this.isOpened = false;
        if(type.getType() == 0){
            this.value = -1;
        }
        else if(type.getType() == 1){
            this.value = 0;
        }
        else{
            throw new IllegalArgumentException("Type not defined!");
        }
    }
    int getValue(){
        return this.value;
    }
    boolean isBomb(){
        if(this.type.getType() == 0) return true;
        return false;
    }
    void open(){
        this.isOpened = true;
    }
    boolean canSelect(){
        if(!isOpened) return true;
        return false;
    }
    void incrementValue(){
        if(this.type.getType() == 1){
            this.value+=1;
        }
    }
    @Override
    public String toString() {
        if(!this.isOpened) return "?";
        if(this.type.getType() == 0) return "*";
        else if(this.type.getType() == 1) return Integer.toString(value);
        else return ".";
    }

}
class Board {
    Tile[][] board;
    int n;
    int bombs;
    int required;

    int generateRandom(){
        Random random = new Random();
        return random.nextInt(n*n - 1);
    }
    void fill(int row, int col){
        if(row<0 || row>=n || col<0 || col>=n) return;
        if(this.board[row][col] == null){
            this.board[row][col] = new Tile(TileType.SAFE);
        }
        this.board[row][col].incrementValue();
    }
    void populateAdjacent(int row, int col){
        fill(row-1, col);
        fill(row+1, col);
        fill(row, col-1);
        fill(row, col+1);
        fill(row-1, col-1);
        fill(row-1, col+1);
        fill(row+1, col-1);
        fill(row+1, col+1);
    }
    void generateBoard(){
        int bombs_left = this.bombs;
        while(bombs_left>0){
            int num = generateRandom();
            int row = num/n;
            int col = num%n;
            if(board[row][col] == null){
                board[row][col] = new Tile(TileType.BOMB);
                populateAdjacent(row, col);
                bombs_left-=1;
            }
        }
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(board[i][j] == null){
                    board[i][j] = new Tile(TileType.SAFE);
                }
            }
        }
    }
    void expand(int row, int col, int rowCounter, int colCounter){
        while(row>=0 && row<n && col>=0 && col<n){
            if(board[row][col].getValue() !=0) break;
            if(board[row][col].canSelect()){
                board[row][col].open();
                required--;
            }
            row+=rowCounter;
            col+=colCounter;
        }
        if(row>=0 && row<n && col>=0 && col<n && !board[row][col].isBomb()){
            if(board[row][col].canSelect()){
                board[row][col].open();
                required--;
            }
        }
    }

    boolean select(int row, int col){
        Tile tile = board[row][col];
        if(!tile.canSelect()) return true;
        tile.open();
        required--;
        if(tile.getValue() == 0){
            expand(row, col, 0, 1);
            expand(row, col, 0, -1);
            expand(row, col, 1, 0);
            expand(row, col, -1, 0);
            expand(row, col, -1, 1);
            expand(row, col, 1, -1);
            expand(row, col, 1, 1);
            expand(row, col, -1, -1);
        }
        return true;
    }
    void printBoard(){
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    boolean isGameOver(int row, int col){
        if(board[row][col].isBomb()) return true;
        return false;
    }
    boolean play(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter any number in range");
        int cell = scanner.nextInt();
        if(cell >= n*n){
            System.out.println("Out of range [0, " + n*n + ")");
            return true;
        }
        int row = cell/n;
        int col = cell%n;
        if(isGameOver(row, col)){
            System.out.println("GAME OVER");
            for(int i=0;i<n;i++){
                for(int j=0;j<n;j++){
                    board[i][j].open();
                }
            }
            return false;
        }
        boolean ret = select(row, col);
        if(required == 0){
            System.out.println("Congratulations! You have completed the challenge!");
            return false;
        }
        return ret;
    }
    public Board(int n, int bombs){
        this.n = n;
        this.board = new Tile[n][n];
        this.bombs = bombs;
        this.required = n*n - bombs;
        generateBoard();
    }
}
public class MineSweeper {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter board size:");
        int n = scanner.nextInt();
        Random random = new Random();
        int bombs = random.nextInt(n);
        Board board = new Board(n, bombs);
        board.printBoard();
        while(board.play()){
            board.printBoard();
        };
        board.printBoard();
        
    }
}
