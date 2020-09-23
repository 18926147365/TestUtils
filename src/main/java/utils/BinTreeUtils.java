package utils;

import bean.BinTree;

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
        addNode(root, 10l);
        addNode(root, 20l);
        addNode(root, 24l);
        addNode(root, 34l);
    }

    public static void addNode(BinTree rootTree, Long data) {
        BinTree leftTree = rootTree.getLeftTree();
        BinTree rightTree = rootTree.getRightTree();
        BinTree thisTree = new BinTree(null, null, data);
        //root节点
        if (leftTree == null && rightTree == null) {
            if (data == thisTree.getData()) {
                return;
            }
            if (data > thisTree.getData()) {
                rootTree.setRightTree(thisTree);
            } else {
                rootTree.setLeftTree(thisTree);
            }
            return;
        }


    }
}
