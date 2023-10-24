package codelets.behaviors;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author bruno
 */
public class Grid {

    private int[][] map;
    private int gx;
    private int gy;
    
    public int minx = 0, miny = 0;

    public Grid(int[][] m, int x, int y) {
        map = m;
        gx = x;
        gy = y;
    }

    public void route() {
        double[][] cost = new double[map.length][map[0].length];
        cost[gx][gy] = 1.0;
        for (int i = 0; i < cost.length; i++) {
            for (int j = 0; j < cost[i].length; j++) {
                if (map[i][j] != -1) {
                    double maxNear = getMaxNearCost(cost, i, j);
                    if(maxNear > cost[i][j])
                        cost[i][j] = 0.9 * maxNear;
                }
            }
        }
        for (int i = cost.length - 1; i >= 0; i--) {
            for (int j = cost[i].length - 1; j >= 0; j--) {
                if (map[i][j] != -1) {
                    double maxNear = getMaxNearCost(cost, i, j);
                    if(maxNear > cost[i][j])
                        cost[i][j] = 0.9 * maxNear;
                }
            }
        }

        int myX = 0, myY = 0;
        for (int i = 0; i < cost.length; i++) {
            for (int j = 0; j < cost[i].length; j++) {
                if (map[i][j] == 1) {
                    myX = i;
                    myY = j;
                }
            }
        }

        double max = 0;

        if (myX > 0) {
            if (cost[myX - 1][myY] > max) {
                max = cost[myX - 1][myY];
                minx = myX - 1;
                miny = myY;
            }
        }
        if (myY > 0) {
            if (cost[myX][myY - 1] > max) {
                max = cost[myX][myY - 1];
                minx = myX;
                miny = myY - 1;
            }
        }
        if (myX < cost.length - 1) {
            if (cost[myX + 1][myY] > max) {
                max = cost[myX + 1][myY];
                minx = myX + 1;
                miny = myY;
            }
        }
        if (myY < cost[myX].length - 1) {
            if (cost[myX][myY + 1] > max) {
                max = cost[myX][myY + 1];
                minx = myX;
                miny = myY + 1;
            }
        }
        
//        String line = "map    ";
//        for (int l = 0; l < cost.length; l++) {
//            for (int m = 0; m < cost[l].length; m++) {
//                line += String.format("%.3f",cost[l][m]) + " ";
//            }
//            line +="\nmap    ";
//        }
//        System.out.println(line);
    }

    private double getMaxNearCost(double[][] cost, int i, int j) {
        double max = 0;

        if (i > 0) {
            if (cost[i - 1][j] > max) {
                max = cost[i - 1][j];
            }
        }
        if (j > 0) {
            if (cost[i][j - 1] > max) {
                max = cost[i][j - 1];
            }
        }
        if (i < cost.length - 1) {
            if (cost[i + 1][j] > max) {
                max = cost[i + 1][j];
            }
        }
        if (j < cost[i].length - 1) {
            if (cost[i][j + 1] > max) {
                max = cost[i][j + 1];
            }
        }

        return max;
    }

}
