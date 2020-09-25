package utils;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 李浩铭
 * @date 2020/09/15 15:02
 * @descroption
 */
public class IntersectionValidUtils {


    public static class Rule {
        private String op;//> >=

        private String value;//1-2

        private double min;

        private double max;

        private boolean leftClose;//左边是否闭合(开[ 闭(  )

        private boolean rightClose;//右边是否闭合(开] 闭)  )


        public void setMin(double min) {
            this.min = min;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public void setLeftClose(boolean leftClose) {
            this.leftClose = leftClose;
        }

        public void setRightClose(boolean rightClose) {
            this.rightClose = rightClose;
        }

        public Rule(){}

        /**
         * @param  section 数学区间格式 如 (1,3] 该构造方法不支持正负无穷大
         * */
        public Rule(String section){
            if(StringUtils.isBlank(section)){
                throw new RuntimeException("不是数学区间格式 例如[1,2]");
            }
            section=section.replaceAll(" ","");
            if(section.length()<5){
                throw new RuntimeException("不是数学区间格式 例如[1,2]");
            }
            if(!section.contains(",")){
                throw new RuntimeException("不是数学区间格式 例如[1,2]");
            }

            String leftStr=section.substring(0,1);
            String rightStr=section.substring(section.length()-1,section.length());

            if(!leftStr.equals("[") && !leftStr.equals("(")){
                throw new RuntimeException("不是数学区间格式 例如[1,2]");
            }
            if(!rightStr.equals("]") && !rightStr.equals(")")){
                throw new RuntimeException("不是数学区间格式 例如[1,2]");
            }
            String[] vals=section.substring(1,section.length()-1).split(",");
            double min=Double.valueOf(vals[0]);
            double max=Double.valueOf(vals[1]);
            if(max<min){
                throw new RuntimeException("数学区间值异常");
            }
            this.max=max;
            this.min=min;
            if("[".equals(leftStr)){
                this.setLeftClose(false);
            }else{
                this.setLeftClose(true);
            }
            if("]".equals(rightStr)){
                this.setRightClose(false);
            }else {
                this.setRightClose(true);
            }

        }

        public Rule(String op, String value) {
            this.op = op;
            this.value = value;
            if ("=".equals(op) || "==".equals(op)) {
                this.min = Double.valueOf(value);
                this.max = Double.valueOf(value);
                this.leftClose = false;
                this.rightClose = false;

            } else if ("<".equals(op)) {//[-∞,3)
                this.min = -Double.MAX_VALUE;
                this.max = Double.valueOf(value);
                this.leftClose = false;
                this.rightClose = true;

            } else if (">".equals(op)) {//(10,+∞]
                this.min = Double.valueOf(value);
                this.max = Double.MAX_VALUE;
                this.leftClose = true;
                this.rightClose = false;

            } else if ("<=".equals(op)) {//[-∞,3]
                this.min = -Double.MAX_VALUE;
                this.max = Double.valueOf(value);
                this.leftClose = false;
                this.rightClose = false;

            } else if (">=".equals(op)) {//[10,+∞]
                this.min = Double.valueOf(value);
                this.max = Double.MAX_VALUE;
                this.leftClose = false;
                this.rightClose = false;

            } else if ("between".equals(op)) {//[3,10)
                if (value.contains("-")) {
                    String[] sp = value.split("-");
                    try {
                        Double.parseDouble(sp[0]);
                        Double.parseDouble(sp[1]);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("范围区间值格式错误");
                    }
                    this.min = Double.valueOf(sp[0]);
                    this.max = Double.valueOf(sp[1]);
                } else if (value.contains(",")) {
                    String[] sp = value.split(",");
                    try {
                        Double.parseDouble(sp[0]);
                        Double.parseDouble(sp[1]);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("范围区间值格式错误");
                    }
                    this.min = Double.valueOf(sp[0]);
                    this.max = Double.valueOf(sp[1]);
                } else {
                    throw new RuntimeException("未找到between切割符号");
                }
                this.leftClose = false;
                this.rightClose = true;

            } else if ("betweens".equals(op)) {//[3,10]
                if (value.contains("-")) {
                    String[] sp = value.split("-");
                    try {
                        Double.parseDouble(sp[0]);
                        Double.parseDouble(sp[1]);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("范围区间值格式错误");
                    }
                    this.min = Double.valueOf(sp[0]);
                    this.max = Double.valueOf(sp[1]);
                } else if (value.contains(",")) {
                    String[] sp = value.split(",");
                    try {
                        Double.parseDouble(sp[0]);
                        Double.parseDouble(sp[1]);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("范围区间值格式错误");
                    }
                    this.min = Double.valueOf(sp[0]);
                    this.max = Double.valueOf(sp[1]);
                } else {
                    throw new RuntimeException("未找到betweens切割符号");
                }
                this.leftClose = false;
                this.rightClose = false;

            } else if ("!=".equals(op) || "<>".equals(op)) {//不等于10
                //特殊预算符号
                this.max = Double.valueOf(value);
                this.min = Double.valueOf(value);
                this.leftClose = true;
                this.rightClose = true;

            } else {
                throw new RuntimeException("未找到对比交集的符号 op=" + op);
            }


        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }


        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public boolean isLeftClose() {
            return leftClose;
        }

        public boolean isRightClose() {
            return rightClose;
        }


        @Override
        public String toString() {
            StringBuffer str = new StringBuffer();
            if ("!=".equals(op)) {
                str.append("[-∞,");
                str.append(min);
                str.append(")U(");
                str.append(max);
                str.append(",+∞]");
                return str.toString();

            }
            if (!isLeftClose()) {
                str.append("[");
            } else {
                str.append("(");
            }
            if (min == -Double.MAX_VALUE) {
                str.append("-∞");
            } else {
                str.append(min);
            }

            str.append(",");

            if (max == Double.MAX_VALUE) {
                str.append("+∞");
            } else {
                str.append(max);
            }
            if (!isRightClose()) {
                str.append("]");
            } else {
                str.append(")");
            }
            return str.toString();
        }
    }


    /**
     * 返回多个规则中的交集
     * [10,10] (8,30]
     * @return 返回null则说明没有交集
     */
    public static Rule getIntersection(Rule... rules) {
        Rule thisRule=null;
        for (Rule rule : rules) {
            if(thisRule==null){
                thisRule=rule;
                continue;
            }
            double min = Double.max(rule.min, thisRule.min);//10
            double max = Double.min(rule.max, thisRule.max);//10
            if (multiExistIntersection(rule, thisRule)) {
                Rule cRule=new Rule();
                cRule.setMax(max);
                cRule.setMin(min);
                if(rule.getMin()==min && rule.isLeftClose()){
                    cRule.setLeftClose(true);
                }
                if(rule.getMax()==max && rule.isRightClose()){
                    cRule.setRightClose(true);
                }
                if(thisRule.getMin()==min && thisRule.isLeftClose()){
                    cRule.setLeftClose(true);
                }
                if(thisRule.getMax()==max && thisRule.isRightClose()){
                    cRule.setRightClose(true);
                }
                thisRule=null;
                thisRule=cRule;
            }else{
               return null;
            }
        }
        return thisRule;
    }

    public static void main(String[] args) {
        Rule rule3=  new Rule("[9,30]");//(11,20]
        Rule rule1 = new Rule("(3,10]");
        Rule rule2 =  new Rule("(1,2]");//(11,20]
        System.out.println(getIntersection(rule1, rule2,rule3));

    }


    /**
     * 是否存在交集
     *
     * @param startOp  开始OP (填=、>、<、<=、>=、between)
     * @param startVal Double的值 ，若op为between则为"1.2-3"或 "1.2,3"
     * @param endOp    结束OP (填=、>、<、<=、>=、between)
     * @param endVal   Double的值 ，若op为between则为"1.2-3"或 "1.2,3"
     */
    public static boolean existIntersection(String startOp, String startVal, String endOp, String endVal) {
        Rule start = new Rule(startOp, startVal);
        Rule end = new Rule(endOp, endVal);

        return existIntersection(start, end);
    }

    /**
     * 是否存在交集
     */
    public static boolean existIntersection(Rule... rules) {
        List<Rule> ruleList = new ArrayList<>();

        for (Rule rule : rules) {
            //判断是否存在!=运算符
            if ("!=".equals(rule.getOp()) || "<>".equals(rule.getOp())) {
                //不等于运算符需要转换成两个rule 如 !=10 转换成 [-∞,10.0)U(10.0,+∞]
                ruleList.add(new Rule("<", rule.getValue()));
                ruleList.add(new Rule(">", rule.getValue()));
            } else {
                ruleList.add(rule);
            }
        }
        return multiExistIntersection(ruleList);
    }


    private static boolean multiExistIntersection(List<Rule> ruleList) {
        List<Rule> list = new ArrayList<>();
        a:
        for (Rule start : ruleList) {
            for (Rule end : list) {
                boolean isexists = intersection(start, end);
                if (isexists) {
                    return true;
                }
            }
            list.add(start);
        }
        return false;
    }


    /**
     * 验证多个集合是否存在交集
     */
    private static boolean multiExistIntersection(Rule... rules) {
        List<Rule> ruleList = new ArrayList<>();
        for (Rule rule : rules) {
            ruleList.add(rule);
        }
        return multiExistIntersection(ruleList);
    }


    private static boolean intersection(Rule start, Rule end) {
//        List<String> ops = Arrays.asList("<", "<=", "=", ">", ">=", "==", "between", "betweens");
//        if (!ops.contains(start.getOp()) || !ops.contains(end.getOp())) {
//            throw new RuntimeException("未找到验证交集的符号");
//        }

        double min = Double.max(start.min, end.min);//10
        double max = Double.min(start.max, end.max);//10
        if (min == max) {
            //[12,12]
            //[10,12)
            if ((start.getMin() == max && start.isLeftClose()) ||
                    (start.getMax() == max && start.isRightClose()) ||
                    (end.getMin() == max && end.isLeftClose()) ||
                    (end.getMax() == max && end.isRightClose())) {
                return false;
            } else {
                return true;
            }


        }
        if ((min == start.max && max == end.min && !start.isRightClose() && !end.isLeftClose()) ||
                (min == start.min && max == end.max && !start.isLeftClose() && !end.isRightClose())) {
            if (min <= max) {
                return true;
            } else {
                return false;
            }
        } else {
            if (min < max) {
                return true;
            } else {
                return false;
            }
        }
    }


}
