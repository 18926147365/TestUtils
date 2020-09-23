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
        BinTree root = new BinTree(null, null, 13l);

        List<Long> list=new ArrayList<>();
        for (int j = 0; j < 10000000; j++) {
            long rand=(long)(Math.random()*1000000);
            list.add(rand);
            addNode(root,rand);
        }
        list.add(1112223331l);
        addNode(root,1112223331l);

        long key=1l;
        StopWatch stopWatch=new StopWatch();



        stopWatch.start();
        System.out.println(list.contains(key));
        stopWatch.stop();
        System.out.println("o(n)耗时:"+stopWatch.getTime()+"ms");


        stopWatch=new StopWatch();
        stopWatch.start();
        BinTree binTree=(getBinTree(root, key));
        System.out.println(binTree.getData());
        stopWatch.stop();
        System.out.println("二叉树查询耗时:"+stopWatch.getTime()+"ms");





    }


    public static void addNode(BinTree rootTree, Long data) {
        BinTree leftTree = rootTree.getLeftTree();//10
        BinTree rightTree = rootTree.getRightTree();//20
        Long rootData=rootTree.getData();
        if(rootData==data){
            return;
        }
        if(rootData>data){
            if(leftTree==null){
                rootTree.setLeftTree(new BinTree(null, null, data));
                return;
            }
            addNode(leftTree,data);
        }
        if(rootData<data){
            if(rightTree==null){
                rootTree.setRightTree(new BinTree(null,null,data));
                return;
            }
            addNode(rightTree, data);
        }
    }


    /**
     * 前序遍历
     * */
    public static void preEach(BinTree rootTree){
        if(rootTree!=null){
            System.out.print(rootTree.getData()+">");
            preEach(rootTree.getLeftTree());
            preEach(rootTree.getRightTree());
        }
    }

    static int i=1;
    /**
     * 中序遍历（左根右）
     * */
    public static void inEach(BinTree rootTree){
        if(rootTree!=null){
            inEach(rootTree.getLeftTree());
            System.out.print(rootTree.getData()+">");
            inEach(rootTree.getRightTree());
        }
    }

    public static BinTree getBinTree(BinTree rootTree,Long id){
        BinTree leftTree = rootTree.getLeftTree();//10
        BinTree rightTree = rootTree.getRightTree();//20
        Long rootData=rootTree.getData();
        if(rootData.longValue()==id.longValue()){
            return rootTree;
        }
        if(leftTree!=null && rootData.longValue()>id.longValue()){
            BinTree binTree=getBinTree(leftTree,id);
            if(binTree!=null){
                return binTree;
            }
        }
        if(rightTree!=null && rootData.longValue()<id.longValue()){
            BinTree binTree=getBinTree(rightTree,id);
            if(binTree!=null){
                return binTree;
            }
        }
        return null;
    }

    /**
     * 后序遍历(左右跟)
     * */
    public static void postEach(BinTree rootTree){
        if(rootTree!=null){
            postEach(rootTree.getLeftTree());
            postEach(rootTree.getRightTree());
            System.out.print(rootTree.getData()+">");
        }
    }


}
