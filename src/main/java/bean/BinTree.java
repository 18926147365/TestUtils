package bean;

/**
 * @Description: 二叉树 示例，只实现long值对比
 * @Author: lihaoming
 * @Date: 2020/9/21 10:40 上午
 */
public class BinTree {


    private BinTree leftTree;

    private BinTree rightTree;

    private Long data;


    public BinTree getLeftTree() {
        return leftTree;
    }

    public void setLeftTree(BinTree leftTree) {
        this.leftTree = leftTree;
    }

    public BinTree getRightTree() {
        return rightTree;
    }

    public void setRightTree(BinTree rightTree) {
        this.rightTree = rightTree;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public BinTree(BinTree leftTree, BinTree rightTree, Long data) {
        this.leftTree = leftTree;
        this.rightTree = rightTree;
        this.data = data;
    }



}
