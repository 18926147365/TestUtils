package utils;

import bean.Shop;
import mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 动态规划DP算法基础
 * @Author: lihaoming
 * @Date: 2020/12/30 上午11:02
 */
public class DPUtils {

    public static void main(String[] args) throws Exception {
        UserMapper userMapper ;

        Shop yx = new Shop();
        yx.setName("音响");//4kg
        yx.setWeight(4);
        yx.setMoney(3000);
        Shop bjb = new Shop();

        bjb.setName("笔记本");//3kg
        bjb.setWeight(3);
        bjb.setMoney(2000);
        Shop jt = new Shop();

        jt.setName("吉他");//1kg
        jt.setWeight(1);
        jt.setMoney(1500);

        Shop ip = new Shop();
        ip.setName("iphone");//1kg
        ip.setWeight(1);
        ip.setMoney(2000);

        Shop mp3 = new Shop();
        mp3.setName("mp3");//1kg
        mp3.setWeight(1);
        mp3.setMoney(200);

        List<Shop> list = new ArrayList<>();
        list.add(ip);
        list.add(yx);
        list.add(bjb);
        list.add(jt);
        list.add(mp3);

        int maxWeight = 5;
        Shop[][] shops = new Shop[list.size()][maxWeight];
        for (int j = 0; j < list.size(); j++) {
            Shop indexShop = list.get(j);
            for (int i = 0; i < maxWeight; i++) {
                if(j==0){
                    if(indexShop.getWeight()<=(i+1)){
                        shops[j][i]=toShop(indexShop);
                    }
                }else{
                    Shop lastShop = shops[j-1][i];
                    if(indexShop.getWeight()<=(i+1)){
                        if(lastShop == null){
                            shops[j][i]=toShop(indexShop);
                        }else{
                            Shop tempShop = null;
                            if(i>=indexShop.getWeight()){
                                tempShop=shops[j-1][i-indexShop.getWeight()];
                            }
                            if(tempShop == null ){
                                if(indexShop.getMoney() > lastShop.getMoney()){
                                    shops[j][i]=toShop(indexShop);
                                }else{
                                    shops[j][i]=toShop(lastShop);
                                }
                            }else{
                                if(indexShop.getMoney() + tempShop.getMoney()> lastShop.getMoney()){
                                    shops[j][i] = mergeShop(indexShop,tempShop);
                                }else{
                                    shops[j][i]=toShop(lastShop);
                                }
                            }
                        }
                    }else{
                        shops[j][i] = toShop(lastShop);
                    }
                }
            }
        }

        for (Shop[] shopd : shops) {
            for (Shop shop : shopd) {
                if (shop == null) {
                    System.out.print("null ");
                } else {
                    System.out.print(shop.getName() + " ");
                }
            }
            System.out.println();
        }
        System.out.println(total);
    }

    private static void printTrue() {
        System.out.println(" 1    2     3   4");
        System.out.println("null null null 音响");
        System.out.println("null null 笔记本 音响");
        System.out.println("吉他  吉他 笔记本 笔记本,吉他");
    }

    static int total =0;
    private static Shop mergeShop(Shop shop1, Shop shop2) {
        total++;
        Shop newShop = new Shop();
        newShop.setMoney(shop1.getMoney() + shop2.getMoney());
        newShop.setWeight(shop1.getWeight() + shop2.getWeight());
        newShop.setName(shop1.getName() + "," + shop2.getName());
        return newShop;
    }

    private static Shop toShop(Shop shop) {
        total++;

        if(shop==null){
//            System.out.print("null ");
            return null;
        }
        Shop newShop = new Shop();
//        System.out.print(shop.getName()+" ");
        newShop.setMoney(shop.getMoney());
        newShop.setWeight(shop.getWeight());
        newShop.setName(shop.getName());
        return newShop;
    }

}
