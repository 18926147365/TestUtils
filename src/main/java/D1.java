import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/11/13 2:08 下午
 */
public class D1 {

    static int[][] sudoku = new int[9][9];
    static int[][] initSudoku = new int[9][9];

    public static void main(String[] args) {
        copy(sudoku, initSudoku);
        printSudoKu(sudoku);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            calc();
        } catch (Exception e) {
            System.out.println("失败");
        }
        stopWatch.stop();
        System.out.println("暴力破解结果耗时：" + stopWatch.getTime() + "ms");
        printSudoKu(sudoku);

    }

    private static void calc() {
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

    //初始化随机9宫格数独
    private static void initRandSudoKu() {

    }

    //根据x,y 获取x整行，y整列，归属9格中剩余可填值
    private static int[] getxyou(int x, int y) {
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

    private static boolean repet(int[] ds, int val) {
        for (int d : ds) {
            if (val == d) {
                return true;
            }
        }
        return false;
    }

    //获取根据y获取一整行的值
    private static int[] getX(int y) {
        int[] result = new int[9];
        for (int i = 0; i < 9; i++) {
            result[i] = sudoku[i][y];
        }
//        for (int i : result) {
//            System.out.print(i);
//        }
//        System.out.println();
        return result;
    }

    //获取根据获取一整列的值
    private static int[] getY(int x) {
        return sudoku[x];
    }

    //根据x,y获取大9格中的数据
    private static int[] getOu(int x, int y) {
        int[] result = new int[9];
        int num = getOuNum(x, y);
        int yd = (int) (Math.ceil((double) num / 3));
        int xd = ((int) (Math.ceil((x + 0.1d) / 3d) * 3));
        int k = 0;
        for (int i = (yd - 1) * 3; i < yd * 3; i++) {
            for (int j = xd - 3; j < xd; j++) {
                result[k++] = sudoku[j][i];
            }
        }
        return result;
    }


    private static int getOuNum(int x, int y) {
        int xd = ((int) (Math.ceil((x + 0.1d) / 3d) * 3));
        int yd = ((int) (Math.ceil((y + 0.1d) / 3d) * 3));
        return (xd / 3) + ((yd / 3) - 1) * 3;
    }

    private static void copy(int[][] source, int[][] target) {
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[i].length; j++) {
                target[j][i] = source[j][i];
            }
        }
    }

    //----------

    private static void printSudoKu(int[][] sudoku) {
        printSudoKu(sudoku, -1, -1);
    }

    private static void printInts(int[] datas) {
        for (int i = 0; i < datas.length; i++) {
            print(datas[i] + ",");
        }
    }

    private static void printSudoKu(int[][] sudoku, int x, int y) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int rand = sudoku[j][i];
                if (x == j) {
                    //获取y行数据
                    printRed(rand + " ");
                } else if (y == i) {
                    //获取x行数据
                    printRed(rand + " ");
                } else if (getOuNum(x, y) == getOuNum(j, i)) {
                    //获取当前大9格数据
                    printRed(rand + " ");
                } else {
                    print(rand + " ");
                }
                if ((j - 2) % 3 == 0 && j != 8) {
                    print("| ");
                }
            }
            if ((i + 1) % 3 == 0) {
                System.out.println();
                println("---------------------");
            } else {
                System.out.println();
            }
        }
    }

    private static void print(Object str) {
        System.out.print("\033[37m" + str);
    }

    private static void println(Object str) {
        System.out.println("\033[37m" + str);
    }

    private static void printRed(Object str) {
        System.out.print("\033[31m" + str);
    }

}
