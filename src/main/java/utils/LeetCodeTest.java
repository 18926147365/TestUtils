package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * @Description:leetcode 算法测试
 * @Author: lihaoming
 * @Date: 2021/1/19 上午9:05
 */
public class LeetCodeTest {

    public static void main(String[] args) {
        LeetCodeTest leetCodeTest = new LeetCodeTest();
        int[] nums = new int[]{4, 5, 6, 7, 8,   9, 11, 0, 1, 2};
        int target =3;
        leetCodeTest.search(nums, target);
    }

    public int search(int[] nums, int target) {
        int base = nums.length / 2;
        if (nums[0] <= target && nums[base] >= target) {//4
            System.out.println("0," + base);
        } else {
//            System.out.println((base) + "," + (nums.length - 1));
            search(nums, base, nums.length - 1, target);
        }
        return 0;
    }

    public int search(int[] nums, int start, int end, int target) {
        int base = (end + 1 - start) / 2 + start;
        int baseVal = nums[base];
        if (baseVal < nums[end]) {

            if( target >= baseVal && target <= nums[end]){
                System.out.println(base + "," + end);
                return 0;
            }

        }
        if (nums[start] < baseVal ) {
            if(baseVal<target){
                return -1;
            }
            if(target >= nums[start] && target <= baseVal){
                System.out.println(start + "," + base);
                return 0;
            }

        }

        if(nums[start] > target){
            return search(nums, start, base, target);
        }else{
            return search(nums, base, end, target);
        }
    }


}
