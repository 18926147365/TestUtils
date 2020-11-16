package utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/11/16 3:09 下午
 */
public class SudokuUtils {
//     0 1 2   3 4 5   6 7 8  ---x轴
//     --------------------
//  0  9 2 1 | 8 7 5 | 3 4 6
//  1  6 4 3 | 9 1 2 | 5 7 8
//  2  7 5 8 | 4 6 3 | 9 2 1
//    ---------------------
//  3  4 3 5 | 2 9 1 | 8 6 7
//  4  8 7 9 | 6 5 4 | 1 3 2
//  5  1 6 2 | 7 3 8 | 4 9 5
//    ---------------------
//  6  5 8 4 | 3 2 7 | 6 1 9
//  7  2 1 6 | 5 4 9 | 7 8 3
//  8  3 9 7 | 1 8 6 | 2 5 4
//  y轴
// 如x=1,y=3 值=3  x=5,y=6 值=7

    private int[][] sudoku = new int[9][9];
    private int[][] initSudoku = new int[9][9];

    public int[][] getSudoku() {
        return sudoku;
    }

    public int[][] calcSudoKu() {
        copy(this.sudoku, this.initSudoku);
        try {
            calc();
        } catch (Exception e) {
            throw new RuntimeException("无计算结果");
        }
        return this.sudoku;
    }

    public void setVal(int x, int y, int val) {
        if (x < 0 && x > 8) {
            throw new RuntimeException("x值范围[0-8]");
        }
        if (y < 0 && y > 8) {
            throw new RuntimeException("y值范围[0-8]");
        }
        if (val <= 0 && val >= 10) {
            throw new RuntimeException("val值范围[1-9]");
        }
        int[] ex = getxyou(x, y);
        for (int data : ex) {
            if (data == val) {
                sudoku[x][y] = val;
                return;
            }
        }
        throw new RuntimeException(String.format("val值冲突 坐标值:%s,%s  值:%s", x, y, val));
    }

    public Integer getVal(int x, int y) {
        if (x < 0 && x > 8) {
            throw new RuntimeException("x值范围[0-8]");
        }
        if (y < 0 && y > 8) {
            throw new RuntimeException("y值范围[0-8]");
        }
        int val = sudoku[x][y];
        if (val == 0) {
            return null;
        }
        return val;
    }

    /**
     * 随机生成9*9的数独
     */
    public int[][] randSudoKu() {
        calc();
        return this.sudoku;
    }

    private void calc() {
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[i].length; j++) {
                int val = sudoku[j][i];
                if (val == 0) {
                    int[] datas = getxyou(j, i);
                    if (datas.length == 0) {
                        copy(initSudoku, sudoku);
                        calc();
                        return;
                    }
                    int rand = (int) (Math.random() * datas.length);
                    sudoku[j][i] = datas[rand];
                }
            }
        }
    }

    //根据x,y 获取x整行，y整列，归属9格中剩余可填值
    private int[] getxyou(int x, int y) {
        int num = getOuNum(x, y);
        int xd = ((int) (Math.ceil((x + 0.1d) / 3d)));//1
        int yd = (int) (Math.ceil((double) num / 3));//1
        int xxd = (xd - 1) * 3;
        int yyd = (yd - 1) * 3;

        Set<Integer> set = new HashSet<>();
        for (int i = 1; i <= 9; i++) {
            set.add(i);
        }
        for (int i = 0; i < 9; i++) {
            int xval = sudoku[i][y];
            int yval = 0;
            int ouval = 0;
            if (i != y) {
                yval = sudoku[x][i];
            }
            if (i % 3 == 0) {
                (xxd)++;
                yyd = (yd - 1) * 3;
            } else {
                yyd++;
            }
            if (!(xxd - 1 == x || yyd == y)) {
                ouval = sudoku[xxd - 1][yyd];
            }
            if (xval != 0) {
                if (set.contains(xval)) {
                    set.remove(xval);
                }
            }
            if (yval != 0) {
                if (set.contains(yval)) {
                    set.remove(yval);
                }
            }
            if (ouval != 0) {
                if (set.contains(ouval)) {
                    set.remove(ouval);
                }

            }

        }
        int[] result = new int[set.size()];
        Iterator<Integer> iterator = set.iterator();
        for (int i = 0; i < set.size(); i++) {
            result[i] = iterator.next();
        }

        return result;
    }

    private int getOuNum(int x, int y) {
        int xd = ((int) (Math.ceil((x + 0.1d) / 3d) * 3));
        int yd = ((int) (Math.ceil((y + 0.1d) / 3d) * 3));
        return (xd / 3) + ((yd / 3) - 1) * 3;
    }

    private void copy(int[][] source, int[][] target) {
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[i].length; j++) {
                target[j][i] = source[j][i];
            }
        }
    }

    public void printSudoKu(int[][] sudoku) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int rand = sudoku[j][i];
                System.out.print(rand + " ");
                if ((j - 2) % 3 == 0 && j != 8) {
                    System.out.print("| ");
                }
            }
            if ((i + 1) % 3 == 0) {
                System.out.println();
                System.out.println("---------------------");
            } else {
                System.out.println();
            }
        }
    }
}
