package utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/10/12 10:36 上午
 */
public class PhoneBrandUtils {


    public static void main(String[] args) {
        System.out.println(getBrand("Linux; Android 10; TAS-AN00 Build/HUAWEITAS-AN00; wv"));
    }
    private static String getBrand(String m) {
        if(StringUtils.isEmpty(m))return null;
        if (isOf(m, "iPhone")) {
            return "iPhone";
        } else if (isOf(m, "HUAWEI", "Honor", "CHM-CL00", "; H60", "; Che")) {
            return "华为";
        } else if (isOf(m, "OPPO", "; R7", "; A31", "; N5", "; R8", "; R6", "; A51","; PACM00","; PAAM00","; PBCM30","; PAHM00","; PBBM00","; PACT00","; PCKM00",";  PBEM00","; RMX1901",";  PCGM00","; PADM00","; PAFM00","; PBDM00","; PBEM00","; PCAM00","; PCAM00","; PCAM10","; PCAT00","; PCCM00","; PCDM10","; PCEM00","; PAAT00","; PADT00","; PBAM00","; PBAT00","; PBCM10","; PBCT10","; PBDT00","; PBET00","; PBFM00","; CPH1907","; PCGM00","; CPH1823")) {
            return "OPPO";
        } else if (isOf(m, "vivo","; V1813BT","; V1916A","; V1914A","; V1913A","; V1912A","; V1911A","; V1901A ","; V1829T","; V1829A","; V1824BA","; V1824A","; V1821A","; V1816T","; V1816A","; V1814A","; V1813T","; V1813A","; V1809A","; V1730EA","; V1731CA","; V1732A","; V1801A0","; V1809T","; V1813BA","; V1818A","; V1818CA","; V1818CT","; V1818T","; V1814T")) {
            return "vivo";
        } else if (isOf(m, "Redmi", "Mi-4c", "MI 5", "HM 2A", "; MIX", "; MI", "; HM","; 2014","; AWM-A0","; SKW-A0","; SKR-A0","; DLT-A0")) {
            return "小米";
        } else if (isOf(m, "Coolpad", "-M0", "C105-8","; POL-T0","; VCR-A0")) {
            return "酷派";
        } else if (isOf(m, "TA-1000","; TA-1041","; Nokia")) {
            return "诺基亚";
        } else if (isOf(m, "; GN", "; F10", "; GIONEE","; V183")) {
            return "金立";
        } else if (isOf(m, "GT-", "; SM","; SAMSUNG","; Note9")) {
            return "三星";
        } else if (isOf(m, "ZTE")) {
            return "中兴";
        } else if (isOf(m, "; Le", "; X90", "; X60", "C106")) {
            return "乐视";
        } else if (isOf(m, "; MX", "; U10", "; PRO", "; m1", "; m2", "; m3", "; m5", "; m6", "; U20","; MEIZU","; Meizu","; M721C","; 16th","; 16X","; 16s","; 16 X")) {
            return "魅族";
        } else if (isOf(m, "; DOOV")) {
            return "朵唯";
        } else if (isOf(m, "; ONE","; A0001")) {
            return "一加";
        } else if (isOf(m, "Meitu", "; MP")) {
            return "美图";
        } else if (isOf(m, "; 150", "; 160", "-A01")) {
            return "360";
        } else if (isOf(m, "; HTC")) {
            return "HTC";
        } else if (isOf(m, "; NX")) {
            return "努比亚";
        } else if (isOf(m, "; YQ", "; OD", "; OS","; DE106","; OC105","; OE106")) {
            return "锤子";
        } else if (isOf(m, "; HLJ", "; GM", "; LA")) {
            return "小辣椒";
        } else if (isOf(m, "; LG")) {
            return "LG";
        } else if (isOf(m, "; FS")) {
            return "夏普";
        } else if (isOf(m, "; koobee")) {
            return "酷比";
        } else if (isOf(m, "; M8","; CMCC-")) {
            return "中国移动";
        } else if (isOf(m, "; XT")) {
            return "摩托罗拉";
        } else if (isOf(m, "G0215D")) {
            return "格力";
        }else if (isOf(m, "Lenovo","; ZUK")) {
            return "联想";
        }else if (isOf(m, "; G8441","; E6653","; G8232")) {
            return "索尼";
        }else if (isOf(m, "; 8848")) {
            return "8848";
        }else if (isOf(m, "; Hisense A2T")) {
            return "海信";
        }else if (isOf(m, "; Pixel")) {
            return "谷歌";
        }

        return "unknow";
    }


    private static boolean isOf(String m, String... str) {
        for (int i = 0; i < str.length; i++) {
            if (m.toUpperCase().indexOf(str[i].toUpperCase()) != -1) {
                return true;
            }
        }
        return false;
    }
}
