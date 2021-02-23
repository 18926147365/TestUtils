package system;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/2/7 上午11:33
 */
public abstract class People {
    
    private Integer age;

    public People(Integer age) {
        this.age = age;
    }

    public void test() {

    }

    public abstract void runs();

}
