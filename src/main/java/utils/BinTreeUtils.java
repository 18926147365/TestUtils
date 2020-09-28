package utils;

import bean.BinTree;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:二叉树工具类
 * @Author: lihaoming
 * @Date: 2020/9/21 10:46 上午
 */
public class BinTreeUtils {

    private static final Map<String, BinTree<?>> TREE_MAP=new ConcurrentHashMap<>();

    public static <T> BinTree<T> getRootBinTree(String key,Class<T> classz){
        if(!TREE_MAP.containsKey(key)){
            synchronized (TREE_MAP){
                if(!TREE_MAP.containsKey(key)){
                    BinTree<T> binTree=new BinTree<>();
                    TREE_MAP.put(key,binTree);
                    return binTree;
                }
            }
        }
        return (BinTree<T>) TREE_MAP.get(key);
    }




    public static void main(String[] args) {
       BinTree<Long> rootTree=getRootBinTree("test",Long.class);
       rootTree.addNode(22l);
       rootTree.addNode(10l);
       rootTree.addNode(40l);
       rootTree.inEach();
    }

    /**
     * 测试删除二叉树节点
     * */

    private static void  testDelNote(){
        BinTree<Long> root = new BinTree(null, null, 13l,13l);
//        root.addNode( 18l,18l);
//        root.addNode( 34l,34l);
//        root.addNode( 8l,8l);
//        root.addNode( 10l,10l);
//        root.addNode( 12l,12l);
//        root.addNode( 11l,11l);
//        root.addNode( 20l,20l);
//        root.addNode( 24l,24l);
//        root.addNode( 23l,23l);




        root.inEach();
        System.out.println();
        System.out.println("是否删除成功"+root.delNote(13l));

        root.inEach();
        System.out.println();
        System.out.println(root.treeDepth());
    }



    /**
     * 二叉树的范围查询
     */
    private static void betweenBitTree(BinTree rootTree, IntersectionValidUtils.Rule rule) {
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
    private static void betweenTest() {
        BinTree root = new BinTree(null, null, 13l);
        root.addNode( 10l);
        root.addNode( 20l);
        root.addNode( 8l);
        root.addNode( 12l);
        root.addNode( 24l);
        root.addNode(34l);

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
            root.addNode( rand);
        }
        list.add(1112223331l);
        root.addNode( 1112223331l);

        long key = 1l;
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        System.out.println(list.contains(key));
        stopWatch.stop();
        System.out.println("o(n)耗时:" + stopWatch.getTime() + "ms");


        stopWatch = new StopWatch();
        stopWatch.start();
        BinTree binTree = (root.getBinTree( key));
        System.out.println(binTree.getData());
        stopWatch.stop();
        System.out.println("二叉树查询耗时:" + stopWatch.getTime() + "ms");
    }



}
