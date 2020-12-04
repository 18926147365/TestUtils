package utils;

import bean.BinTree;

import java.util.*;

/**
 * @Description:回溯算法计算
 * @Author: lihaoming
 * @Date: 2020/11/18 4:07 下午
 */
public class SudokuUtils2 {

    private int[][] sudoku = new int[9][9];
    private int[][] initSudoku = new int[9][9];

    public static void main(String[] args) {
        SudokuUtils2 sudokuUtils = new SudokuUtils2();

        sudokuUtils.calcSudoKu();


        for (int i = 0; i < 70; i++) {
            int x=(int)(Math.random()*9);
            int y=(int)(Math.random()*9);
            sudokuUtils.sudoku[x][y]=0;
        }
        int[][] backUp=new int[9][9];
        sudokuUtils.copy(sudokuUtils.getSudoku(),backUp);

        List<String> bigTempList = new ArrayList<>();
        a:for (int i = 0; i < 9; i++) {
            List<String> tempList = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                int val=sudokuUtils.sudoku[j][i];
                if(val!=0)continue;
                tempList=getList(tempList,sudokuUtils.getxyou(j,i));
            }
            bigTempList=getList2(bigTempList,tempList);
            System.out.println(tempList);
        }
        sudokuUtils.printSudoKu(sudokuUtils.getSudoku());



        System.out.println("-----");
        int index=0;
        c:for (String str : bigTempList) {
            String[] strs = str.split("");
            int k=0;
            a:for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    int val=sudokuUtils.sudoku[j][i];
                    if(val!=0)continue;

                    if (sudokuUtils.getxyou(j,i).length==0) {
                        sudokuUtils.copy(backUp,sudokuUtils.getSudoku());
                       continue c;
                    }else{
                        sudokuUtils.sudoku[j][i]=Integer.valueOf(strs[k++]);
                    }
                }
            }
        }
        sudokuUtils.printSudoKu(sudokuUtils.getSudoku());
    }

    private static boolean isTest(){


        return true;
    }

    private static List<String> getList2(List<String> list, List<String> iList) {
        if (list == null || list.size() == 0) {
            list = new ArrayList<>();
            for (String i : iList) {
                list.add(i );
            }
            return list;
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < iList.size(); j++) {
                String val = list.get(i);
                String data = iList.get(j);
                result.add(val + data);
            }
        }
        return result;
    }

    private static List<String> getList(List<String> list, int[] iList) {
        if (list == null || list.size() == 0) {
            list = new ArrayList<>();
            for (Integer i : iList) {
                list.add(i + "");
            }
            return list;
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < iList.length; j++) {
                String val = list.get(i);
                String data = iList[j] + "";
                if (val.contains(data)) {
                    continue;
                }
                result.add(val + data);
            }
        }
        return result;
    }

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
            int tempIndex=0;
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

    //获取上一个左边x,y
    private int[] getLastxy(int x,int y){
        int[] xy={0,0};
        if(x==0 && y==0){
            return xy;
        }
        if(x==0){
            y--;
            x=8;
        }else{
            x--;
        }
        xy[0]=x;
        xy[1]=y;
        return xy;
    }
    private void printx(int[] str){
        for (int i : str) {
            System.out.print(i);
        }
        System.out.println();
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
