package utils;

import org.apache.commons.lang3.time.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description: 快速排序算法
 * @Author: lihaoming
 * @Date: 2021/1/7 上午9:53
 */
public class QuickSortUtils {
    public static void main(String[] args) throws InterruptedException {
        int total = 100000;
        int[] list = new int[total];
        for (int i = 0; i < total; i++) {
            int rand = (int) (Math.random() * 111112300);
            list[i] = rand;
        }
//        printList(list);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        quickSort(list, 0, list.length);
        stopWatch.stop();
//        printList(list);

        System.out.println("耗时:" + (new BigDecimal(stopWatch.getNanoTime()).divide(new BigDecimal(1000*1000),2, RoundingMode.DOWN)) +"ms");
    }

    private static void quickSort(int[] list, int start, int end) {
        if (end - start <= 2) {
            if (end - start == 2) {
                if (list[start] > list[end - 1]) {
                    int temp = list[start];
                    list[start] = list[end - 1];
                    list[end - 1] = temp;
                }
            }
            return;
        }
        int base = list[start];//11
        int i = start;
        int j = end;
        a:
        for (int k = start + 1; k < end; k++) {
            if (i >= j) {
                break a;
            }
            int rval = list[--j];//7
            while (rval > base) {
                if (i == j) {
                    break a;
                }
                if (i + 1 == j) {
                    break;
                }
                rval = list[--j];
            }
            if (i == j) {
                list[start] = rval;//4
                list[i] = base;//44
                break a;
            }
            int lval = list[++i];
            while (lval < base) {
                if (i == j) {
                    list[start] = lval;
                    list[j] = base;
                    break a;
                }
                lval = list[++i];
            }
            list[i] = rval;
            list[j] = lval;
        }
        quickSort(list, start, i);
        quickSort(list, i, end);
    }

    private static void printList(int[] list) {
        for (int i1 : list) {
            System.out.print(i1 + ",");
        }
        System.out.println();
    }
}
