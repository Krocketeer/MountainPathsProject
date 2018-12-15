import java.util.*;
import java.io.*;
import java.awt.*;

public class MountainPaths {
    public static void main(String[] args) throws Exception{
        //construct DrawingPanel, and get its Graphics contex
        DrawingPanel panel = new DrawingPanel(844, 480);
        Graphics g = panel.getGraphics();

        //Test Step 1 - construct mountain map data
        Scanner S = new Scanner(new File("Colorado_844x480.dat"));
        int[][] grid = read(S, 480, 844);

        //Test Step 2 - min, max
        int min = findMinValue(grid);
        System.out.println("Min value in map: "+min);

        int max = findMaxValue(grid);
        System.out.println("Max value in map: "+max);


        //Test Step 3 - draw the map
        drawMap(g, grid);

        //Test Step 4 - draw a greedy path

        // 4.1 implement indexOfMinInCol
        int minRow = indexOfMinInCol(grid, 0); // find the smallest value in col 0
        System.out.println("Row with lowest val in col 0: "+minRow);

        // 4.2 use minRow as starting point to draw path
        g.setColor(Color.RED); //can set the color of the 'brush' before drawing, then method doesn't need to worry about it
        int totalChange = drawLowestElevPath(g, grid, minRow); //
        System.out.println("Lowest-Elevation-Change Path starting at row "+minRow+" gives total change of: "+totalChange);

        //Test Step 5 - draw the best path
        g.setColor(Color.RED);
        int bestRow = indexOfLowestElevPath(g, grid);

        //map.drawMap(g); //use this to get rid of all red lines
        g.setColor(Color.GREEN); //set brush to green for drawing best path
        totalChange = drawLowestElevPath(g, grid, bestRow);
        System.out.println("The Lowest-Elevation-Change Path starts at row: "+bestRow+" and gives a total change of: "+totalChange);
    }
  
    /**
     * @param s a Scanner instantiated and pointing at a file
     * @param numRows the number of rows represented in the file
     * @param numCols the number of cols represented in the file
     * @return a 2D array (rows x cols) of the data from the file read
     */
    public static int[][] read(Scanner s, int numRows, int numCols) {
        int [][] mountainData = new int [numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numCols; column++) {
                mountainData[row][column] = s.nextInt();
            }
        }
        //complete me
        /* Hint: To get the next integer from the file, use s.nextInt(). */
        return mountainData;
    }
  
    /**
     * @param grid a 2D array from which you want to find the smallest value
     * @return the smallest value in the given 2D array
     */
    public static int findMinValue(int[][] grid){
        int min = Integer.MAX_VALUE;
        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                if (grid[row][column] < min) {
                    min = grid[row][column];
                }
            }
        }
        return min;
    }
  
    /**
     * @param grid a 2D array from which you want to find the largest value
     * @return the largest value in the given 2D array
     */
    public static int findMaxValue(int[][] grid){
        int max = Integer.MIN_VALUE;
        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                if (grid[row][column] > max) {
                    max = grid[row][column];
                }
            }
        }
        return max;
    }
  
    /**
     * Given a 2D array of elevation data create a image of size rows x cols,
     * drawing a 1x1 rectangle for each value in the array whose color is set
     * to a a scaled gray value (0-255).  Note: to scale the values in the array
     * to 0-255 you must find the min and max values in the original data first.
     * @param g a Graphics context to use
     * @param grid a 2D array of the data
     */
    public static void drawMap(Graphics g, int[][] grid){
        double slope = 255 / (double) (findMaxValue(grid) - findMinValue(grid));
        double b = -1 * (slope * findMinValue(grid));

        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                double color = (grid[row][column] * slope) + b;
                g.setColor(new Color((int) color, (int) color, (int) color));
                g.fillRect(column, row, 1, 1);
            }
        }
    }
  
    /**
     * Scan a single column of a 2D array and return the index of the
     * row that contains the smallest value
     * @param grid a 2D array
     * @col the column in the 2D array to process
     * @return the index of smallest value from grid at the given col
     */
    public static int indexOfMinInCol(int[][] grid, int col){
        int min = Integer.MAX_VALUE;
        int rowIndex = 0;
        for (int row = 0; row < grid.length; row++) {
            if (grid[row][col] < min) {
                min = grid[row][col];
                rowIndex = row;
            }
        }
        return rowIndex;
    }
  


    /**
     * Find the minimum elevation-change route from West-to-East in the given grid, from the
     * given starting row, and draw it using the given graphics context
     * @param g - the graphics context to use
     * @param grid - the 2D array of elevation values
     * @param row - the starting row for traversing to find the min path
     * @return total elevation of the route
     */
    public static int drawLowestElevPath(Graphics g, int[][] grid, int row){
        g.fillRect(0, row,1,1);
        int currentRow = row;
        int elevationChange = 0;
        int upfwdChange;
        int downfwdChange;
        for (int column = 0; column < grid[currentRow].length - 1; column++) {
            int fwdChange = Math.abs(grid[currentRow][column] - grid[currentRow][column + 1]);

//          Special Edge Case
            if (currentRow == 0) {
                upfwdChange = Integer.MAX_VALUE;
            } else {
                upfwdChange = Math.abs(grid[currentRow][column] - grid[currentRow - 1][column + 1]);
            }
            if (currentRow == grid.length - 1) {
                downfwdChange = Integer.MAX_VALUE;
            } else {
                downfwdChange = Math.abs(grid[currentRow][column] - grid[currentRow + 1][column + 1]);

            }

//          Case 1: always going forward
            if (upfwdChange < fwdChange && upfwdChange < downfwdChange) {
                elevationChange+=upfwdChange;
                currentRow--;
            } else if (fwdChange <= upfwdChange && fwdChange <= downfwdChange) {
                elevationChange+=fwdChange;
            } else if (downfwdChange < upfwdChange &&  downfwdChange < fwdChange){
               elevationChange+=downfwdChange;
               currentRow++;
            }

//          Case 2: tie w/ then go forward
            if (upfwdChange == fwdChange || downfwdChange == fwdChange) {
                elevationChange+=fwdChange;
            }

//          Case 3: bottom & top tied but less than forward, randomly flip
            if (upfwdChange < fwdChange && downfwdChange < fwdChange && upfwdChange == downfwdChange) {
                int coin = (int) (2 * Math.random());
                if (coin % 2 == 0) {
//                    choose upfoward
                    elevationChange+=upfwdChange;
                    currentRow--;
                } else {
                    elevationChange+=downfwdChange;
                    currentRow++;
                }
            }
            g.fillRect(column + 1, currentRow,1, 1);
        }
        return elevationChange;
    }
  
    /**
     * Generate all west-to-east paths, find the one with the lowest total elevation change,
     * and return the index of the row that path starts on.
     * @param g - the graphics context to use
     * @param grid - the 2D array of elevation values
     * @return the index of the row where the lowest elevation-change path starts.
     */
    public static int indexOfLowestElevPath(Graphics g, int[][] grid){
        int lowestElevation = Integer.MAX_VALUE;
        int rowIndex = 0;
        for (int row = 0; row < grid.length; row++) {
           int currentElevPath = drawLowestElevPath(g, grid, row);
           if(currentElevPath < lowestElevation) {
               lowestElevation = currentElevPath;
               rowIndex = row;
           }
        }
        return rowIndex;
    }
}
