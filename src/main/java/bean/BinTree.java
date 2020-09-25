package bean;

/**
 * @Description: 二叉树 示例，只实现long值对比
 * @Author: lihaoming
 * @Date: 2020/9/21 10:40 上午
 */
public class BinTree<T> {


    private BinTree leftTree;

    private BinTree rightTree;

    private Long data;

    private T object;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

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

    public BinTree(BinTree<T> leftTree, BinTree<T> rightTree, Long data) {
        this.leftTree = leftTree;
        this.rightTree = rightTree;
        this.data = data;
    }



    public BinTree(BinTree<T> leftTree, BinTree<T> rightTree, Long data, T object) {
        this.leftTree = leftTree;
        this.rightTree = rightTree;
        this.data = data;
        this.object = object;
    }


    @Override
    public String toString() {
        return "BinTree{" +
                "leftTree=" + leftTree +
                ", rightTree=" + rightTree +
                ", data=" + data +
                '}';
    }

    public String toStringObject() {
        return "BinTree{" +
                "leftTree=" + leftTree +
                ", rightTree=" + rightTree +
                ", data=" + data +
                ", object=" + object +
                '}';
    }

    public boolean updateNode( Long data, T object) {
        return updateNode(this,data,object);
    }

    private boolean updateNode(BinTree rootTree, Long data, T object) {
        BinTree leftTree = rootTree.getLeftTree();//10
        BinTree rightTree = rootTree.getRightTree();//20
        Long rootData = rootTree.getData();
        boolean isAdd = false;
        if (rootData == data) {
            rootTree.setObject(object);
            return true;
        }
        if (rootData > data) {
            if (leftTree == null) {
                return false;
            }
            isAdd = updateNode(leftTree, data, object);
        }
        if (rootData < data) {
            if (rightTree == null) {
                return false;
            }
            isAdd = updateNode(rightTree, data, object);
        }
        return isAdd;
    }


    private  boolean delNote(BinTree rootTree, Long data) {
        BinTree leftTree = rootTree.getLeftTree();//10
        BinTree rightTree = rootTree.getRightTree();//20
        Long rootData = rootTree.getData();
        boolean isAdd = false;
        if (rootData == data) {
            if(leftTree==null || rightTree==null){
                rootTree=null;
            }
            if(leftTree!=null && rightTree!=null){
                //计算左右节点深度
                int leftDep=leftTree.treeDepth();
                int rightDep=rightTree.treeDepth();
                if(leftDep<rightDep){

                }else{

                }
            }
            if(leftTree!=null){
                rootTree=leftTree;
            }else{
                rootTree=rightTree;
            }
            return true;
        }
        if (rootData > data) {
            if (leftTree == null) {
                return false;
            }
            isAdd = delNote(leftTree, data);
        }
        if (rootData < data) {
            if (rightTree == null) {
                return false;
            }
            isAdd = delNote(rightTree, data);
        }
        return isAdd;
    }


    /**
     * 不存在则新增，存在则覆盖
     * */
    public boolean setNode(Long data, T object) {
        return setNode(this,data,object);
    }

    private boolean setNode(BinTree rootTree, Long data, T object) {
        BinTree leftTree = rootTree.getLeftTree();//10
        BinTree rightTree = rootTree.getRightTree();//20
        Long rootData = rootTree.getData();
        boolean isAdd = false;
        if (rootData == data) {
            rootTree.setObject(object);
            return true;
        }
        if (rootData > data) {
            if (leftTree == null) {
                rootTree.setLeftTree(new BinTree(null, null, data,object));
                return true;
            }
            isAdd = setNode(leftTree, data, object);
        }
        if (rootData < data) {
            if (rightTree == null) {
                rootTree.setRightTree(new BinTree(null, null, data, object));
                return true;
            }
            isAdd = setNode(rightTree, data, object);
        }
        return isAdd;
    }

    private boolean addNode(BinTree rootTree, Long data, T object) {
        BinTree leftTree = rootTree.getLeftTree();//10
        BinTree rightTree = rootTree.getRightTree();//20
        Long rootData = rootTree.getData();
        boolean isAdd = false;
        if (rootData == data) {
            return false;
        }
        if (rootData > data) {
            if (leftTree == null) {
                rootTree.setLeftTree(new BinTree(null, null, data,object));
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


    public boolean addNode(Long data) {
        return addNode(this, data, null);
    }

    public boolean addNode(Long data, T object) {
        return addNode(this, data, object);
    }


    public BinTree<T> getBinTree(Long data) {
        return getBinTree(this, data);
    }


    private BinTree<T> getBinTree(BinTree<T> rootTree, Long data) {
        BinTree<T> leftTree = rootTree.getLeftTree();//10
        BinTree<T> rightTree = rootTree.getRightTree();//20
        Long rootData = rootTree.getData();
        if (rootData.longValue() == data.longValue()) {
            return rootTree;
        }
        if (leftTree != null && rootData.longValue() > data.longValue()) {
            BinTree<T> binTree = getBinTree(leftTree, data);
            if (binTree != null) {
                return binTree;
            }
        }
        if (rightTree != null && rootData.longValue() < data.longValue()) {
            BinTree<T> binTree = getBinTree(rightTree, data);
            if (binTree != null) {
                return binTree;
            }
        }
        return null;
    }

    /**
     * 前序遍历
     */
    public void perEach() {
        preEach(this);
    }

    private void preEach(BinTree rootTree) {
        if (rootTree != null) {
            System.out.print(rootTree.getData() + ">");
            preEach(rootTree.getLeftTree());
            preEach(rootTree.getRightTree());
        }
    }

    /**
     * 中序遍历（左根右）
     */
    public void inEach() {
        inEach(this);
    }

    private void inEach(BinTree rootTree) {
        if (rootTree != null) {
            inEach(rootTree.getLeftTree());
            System.out.print(rootTree.getData() + ">");
            inEach(rootTree.getRightTree());
        }
    }


    /**
     * 后序遍历(左右跟)
     */
    public void postEach() {
        postEach(this);
    }

    private void postEach(BinTree rootTree) {
        if (rootTree != null) {
            postEach(rootTree.getLeftTree());
            postEach(rootTree.getRightTree());
            System.out.print(rootTree.getData() + ">");
        }
    }



    /**
     * 递归求深度
     */
    public int treeDepth() {
        return treeDepth(this);
    }

    private int treeDepth(BinTree rootTree) {
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

}
