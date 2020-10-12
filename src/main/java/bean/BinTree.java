package bean;

/**
 * @Description: 二叉树 示例，只实现long值对比
 * @Author: lihaoming
 * @Date: 2020/9/21 10:40 上午
 */
public class BinTree<T> {


    private BinTree<T> leftTree;

    private BinTree<T> rightTree;

    private Long data;

    private T object;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public BinTree<T> getLeftTree() {
        return leftTree;
    }

    public void setLeftTree(BinTree<T> leftTree) {
        this.leftTree = leftTree;
    }

    public BinTree<T> getRightTree() {
        return rightTree;
    }

    public void setRightTree(BinTree<T> rightTree) {
        this.rightTree = rightTree;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public BinTree(){}
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
        if(getData()==null){
            return false;
        }
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



    public boolean delNote(Long data){
        if(getData()==null){
            return false;
        }
        BinTree<T> parentTree= getBinParentTree(data);
        if(parentTree==null){
            return false;
        }
        BinTree<T> leftTree=parentTree.getLeftTree();
        BinTree<T> rightTree=parentTree.getRightTree();
        BinTree<T> thisTree=null;

        if(data.longValue()==parentTree.getData().longValue()){
            delNote(parentTree,parentTree,"root");
        }
        if(leftTree!=null && leftTree.getData().longValue()==data.longValue()){
            thisTree=leftTree;
            delNote(parentTree,thisTree,"left");
        }else if (rightTree!=null && rightTree.getData().longValue()==data.longValue()){
            thisTree=rightTree;
            delNote(parentTree,thisTree,"right");
        }

        return true;

    }
    private void delNote(BinTree<T> parentTrree,BinTree<T> binTree,String action){
        BinTree<T> r=binTree.getRightTree();
        BinTree<T> coverTree=null;
        if(r==null){
            //删除本身
            if(binTree.getLeftTree()!=null){
                coverTree=binTree.getLeftTree();
            }
        }else{
            BinTree<T> rParent=null;
            while (r!=null){
                if(r.getLeftTree()==null){
                    if (rParent == null) {
                        r.setLeftTree(binTree.getLeftTree());
                        coverTree=r;
                        break;
                    }
                    if(r.getRightTree()!=null){
                        rParent.setLeftTree(r.getRightTree());
                    }else{
                        rParent.setLeftTree(null);
                    }
                    binTree.setData(r.getData());
                    binTree.setObject(r.getObject());
                    return;
                }
                rParent=r;
                r=r.getLeftTree();
            }
        }
        if("left".equals(action)){
            parentTrree.setLeftTree(coverTree);
        }else if("right".equals(action)){
            parentTrree.setRightTree(coverTree);
        }else if("root".equals(action)){
            if(coverTree==null){


                return;
            }
            parentTrree.setData(coverTree.getData());
            parentTrree.setObject(coverTree.getObject());
            if(parentTrree.getRightTree()==null){
                parentTrree.setLeftTree(coverTree.getLeftTree());
                parentTrree.setRightTree(coverTree.getRightTree());
            }else{
                parentTrree.setRightTree(coverTree.getRightTree());
            }
        }
    }




    /**
     * 不存在则新增，存在则覆盖
     * */
    public boolean setNode(Long data, T object) {
        if(getData()==null){
            setData(data);
            setObject(object);
            return true;
        }
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
        if(getData()==null){
            setData(data);
            return true;
        }
        return addNode(this, data, null);
    }

    public boolean addNode(Long data, T object) {
        if(getData()==null){
            setObject(object);
            setData(data);
            return true;
        }
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


    public BinTree<T> getBinParentTree(Long data) {
        return getBinParentTree(this, data);
    }

    private BinTree<T> getBinParentTree(BinTree<T> rootTree, Long data) {
        BinTree<T> leftTree = rootTree.getLeftTree();//10
        BinTree<T> rightTree = rootTree.getRightTree();//20
        Long rootData = rootTree.getData();
        if (rootData.longValue() == data.longValue()) {
            return rootTree;
        }else if(leftTree!=null && leftTree.getData().longValue()==data.longValue()){
            return rootTree;
        }else if(rightTree!=null && rightTree.getData().longValue()==data.longValue()){
            return rootTree;
        }

        if (leftTree != null && rootData.longValue() > data.longValue()) {
            BinTree<T> binTree = getBinParentTree(leftTree, data);
            if (binTree != null) {
                return binTree;
            }
        }
        if (rightTree != null && rootData.longValue() < data.longValue()) {
            BinTree<T> binTree = getBinParentTree(rightTree, data);
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



    public void addAVLNode(Long data){
        if(getData()==null){
            setData(data);
            return;
        }
        addAVLNode(this,data);
    }

    private void addAVLNode(BinTree binTree,Long data){

    }

    public void getTTT(long tt){
        BinTree leftTree=getLeftTree();
        BinTree rightTree=getRightTree();
        if(rightTree==null && leftTree==null){
            System.out.println("可以直接插入");
            return;
        }
    }
}
