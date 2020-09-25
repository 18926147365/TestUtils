package utils;

import bean.BinTree;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:二叉树工具类
 * @Author: lihaoming
 * @Date: 2020/9/21 10:46 上午
 */
public class BinTreeUtils {


    public static void main(String[] args) {

        BinTree<Long> root = new BinTree(null, null, 13l);
        addNode(root, 10l,10l);
        addNode(root, 20l,12l);
        addNode(root, 8l,31l);
        addNode(root, 12l,32l);
        System.out.println(addNode(root, 24l,23l));
        System.out.println(addNode(root, 34l,23l));
        System.out.println(addNode(root, 34l,33l));

        System.out.println(getBinTree(root, 34l).getObject());
    }


    /**
     * 二叉树的范围查询
     */
    public static void betweenBitTree(BinTree rootTree, IntersectionValidUtils.Rule rule) {
        BinTree leftTree = rootTree.getLeftTree();//10
        BinTree rightTree = rootTree.getRightTree();//20
        Long rootData = rootTree.getData();
        if (leftTree != null && rootData.doubleValue() > rule.getMin()) {
            betweenBitTree(leftTree, rule);
        }

        if (rightTree != null && rootData.doubleValue() < rule.getMax()) {
            betweenBitTree(rightTree, rule);
        }

        if (IntersectionValidUtils.existIntersection(rule, new IntersectionValidUtils.Rule("=", rootData.toString()))) {
            System.out.println(rootData);
        }

    }


    /**
     * 测试二叉树范围查询
     */
    public static void betweenTest() {
        BinTree root = new BinTree(null, null, 13l);
        addNode(root, 10l);
        addNode(root, 20l);
        addNode(root, 8l);
        addNode(root, 12l);
        addNode(root, 24l);
        addNode(root, 34l);

        betweenBitTree(root, new IntersectionValidUtils.Rule("[8,10)"));
    }

    /**
     * 测试o(n) 和 o(Log n)性能查询
     */
    private static void test() {
        BinTree root = new BinTree(null, null, 13l);

        List<Long> list = new ArrayList<>();
        for (int j = 0; j < 10000000; j++) {
            long rand = (long) (Math.random() * 1000000);
            list.add(rand);
            addNode(root, rand);
        }
        list.add(1112223331l);
        addNode(root, 1112223331l);

        long key = 1l;
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        System.out.println(list.contains(key));
        stopWatch.stop();
        System.out.println("o(n)耗时:" + stopWatch.getTime() + "ms");


        stopWatch = new StopWatch();
        stopWatch.start();
        BinTree binTree = (getBinTree(root, key));
        System.out.println(binTree.getData());
        stopWatch.stop();
        System.out.println("二叉树查询耗时:" + stopWatch.getTime() + "ms");
    }

    /**
     * 递归求深度
     *
     * @param rootTree
     * @return
     */
    public static int treeDepth(BinTree rootTree) {
        if (rootTree == null) {
            return 0;
        }
        // 计算左子树的深度
        int left = treeDepth(rootTree.getLeftTree());
        // 计算右子树的深度
        int right = treeDepth(rootTree.getRightTree());
        // 树root的深度=路径最长的子树深度 + 1
        return left >= right ? (left + 1) : (right + 1);
    }


    public static boolean addNode(BinTree rootTree, Long data, Object object) {
        BinTree leftTree = rootTree.getLeftTree();//10
        BinTree rightTree = rootTree.getRightTree();//20
        Long rootData = rootTree.getData();
        boolean isAdd = false;
        if (rootData == data) {
            return false;
        }
        if (rootData > data) {
            if (leftTree == null) {
                rootTree.setLeftTree(new BinTree(null, null, data));
                return true;
            }
            isAdd = addNode(leftTree, data, object);
        }
        if (rootData < data) {
            if (rightTree == null) {
                rootTree.setRightTree(new BinTree(null, null, data, object));
                return true;
            }
            isAdd = addNode(rightTree, data, object);
        }
        return isAdd;
    }


    public static boolean addNode(BinTree rootTree, Long data) {
        return addNode(rootTree, data, null);
    }


    /**
     * 前序遍历
     */
    public static void preEach(BinTree rootTree) {
        if (rootTree != null) {
            System.out.print(rootTree.getData() + ">");
            preEach(rootTree.getLeftTree());
            preEach(rootTree.getRightTree());
        }
    }

    /**
     * 中序遍历（左根右）
     */
    public static void inEach(BinTree rootTree) {
        if (rootTree != null) {
            inEach(rootTree.getLeftTree());
            System.out.print(rootTree.getData() + ">");
            inEach(rootTree.getRightTree());
        }
    }

    public static BinTree getBinTree(BinTree rootTree, Long id) {
        BinTree leftTree = rootTree.getLeftTree();//10
        BinTree rightTree = rootTree.getRightTree();//20
        Long rootData = rootTree.getData();
        if (rootData.longValue() == id.longValue()) {
            return rootTree;
        }
        if (leftTree != null && rootData.longValue() > id.longValue()) {
            BinTree binTree = getBinTree(leftTree, id);
            if (binTree != null) {
                return binTree;
            }
        }
        if (rightTree != null && rootData.longValue() < id.longValue()) {
            BinTree binTree = getBinTree(rightTree, id);
            if (binTree != null) {
                return binTree;
            }
        }
        return null;
    }

    /**
     * 后序遍历(左右跟)
     */
    public static void postEach(BinTree rootTree) {
        if (rootTree != null) {
            postEach(rootTree.getLeftTree());
            postEach(rootTree.getRightTree());
            System.out.print(rootTree.getData() + ">");
        }
    }


}
