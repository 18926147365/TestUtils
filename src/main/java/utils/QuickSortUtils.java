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

    public static void main(String[] args) throws Exception {
        int total = 2000;
        int[] list = new int[total];
        int[] list1 = new int[total];
        for (int i = 0; i < total; i++) {
            int rand = (int) (Math.random() * 900000100);
            list[i] = rand;
            list1[i] = rand;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //多次试验得出50万数据以上多线程快速排序耗时较少
        quickSort(list, 0, list.length);//快速排序实现
        stopWatch.stop();
        System.out.println("耗时:" + (new BigDecimal(stopWatch.getNanoTime()).divide(new BigDecimal(1000 * 1000), 2, RoundingMode.DOWN)) + "ms");

        StopWatch stopWatch1 = new StopWatch();
        stopWatch1.start();
        //多次试验得出50万数据以上多线程快速排序耗时较少
        threadQuickSort(list1, 0, list.length, 2);//多线程快速排序实现
        stopWatch1.stop();
        System.out.println("多线程耗时:" + (new BigDecimal(stopWatch1.getNanoTime()).divide(new BigDecimal(1000 * 1000), 2, RoundingMode.DOWN)) + "ms");


    }

    /**
     * @param depth 深度
     */
    private static void qucikSortSpaceSplit(List<int[]> depthSort, int[] list, int start, int end, int depth, int threadNum) {
        if (depth == threadNum) {
            int[] ints = new int[2];
            ints[0] = start;
            ints[1] = end;
            depthSort.add(ints);
        }
        if (depth > threadNum) {
            return;
        }
        depth++;
        int i = getQuickSortIndex(list, start, end);
        if (i == -1) {
            return;
        }
        qucikSortSpaceSplit(depthSort, list, start, i, depth, threadNum);
        qucikSortSpaceSplit(depthSort, list, i, end, depth, threadNum);
        return;
    }

    /**
     * @param threadNum 线程数：threadNum² 值建议不要大于10
     */
    public static void threadQuickSort(int[] list, int start, int end, int threadNum) throws InterruptedException {
        List<int[]> spaceSplit = new ArrayList<>();
        qucikSortSpaceSplit(spaceSplit, list, start, end, 0, threadNum);
        CountDownLatch countDownLatch = new CountDownLatch(spaceSplit.size());
        for (int[] ints : spaceSplit) {
            int instart = ints[0];
            int inend = ints[1];
            new Thread(new Runnable() {
                @Override
                public void run() {
                    quickSort(list, instart, inend);
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
    }

    private static int getQuickSortIndex(int[] list, int start, int end) {
        if (end - start <= 2) {
            if (end - start == 2) {
                if (list[start] > list[end - 1]) {
                    int temp = list[start];
                    list[start] = list[end - 1];
                    list[end - 1] = temp;
                }
            }
            return -1;
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
        return i;

    }

    public static void quickSort(int[] list, int start, int end) {
        int i = getQuickSortIndex(list, start, end);
        if (i == -1) {
            return;
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
