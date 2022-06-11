package kr.kj.javautils.pystring;

import java.util.ArrayList;

public class PyString {
    private final String str;
    private final int strlen;

    public PyString(String s) {
        this.str = s;
        this.strlen = s.length();
    }

    private String slice(int startIdx, int endIdx) {
        int sIdx = (startIdx < 0)? strlen + startIdx : startIdx;
        int eIdx = (endIdx < 0)? strlen + endIdx : endIdx;

        return str.substring(sIdx, eIdx);
    }

    public String slice(String idxExpr) {
        if(idxExpr.contains(":")) {
            int startIdx, endIdx;

            PyString ps = new PyString(idxExpr);
            ArrayList<String> splitList = ps.split(":");

            startIdx = (splitList.get(0).isEmpty())? 0 : Integer.parseInt(splitList.get(0));
            endIdx = (splitList.get(1).isEmpty())? strlen : Integer.parseInt(splitList.get(1));

            return slice(startIdx, endIdx);
        }

        else {
            int idx = Integer.parseInt(idxExpr);
            if(idx < 0) idx = strlen + idx;

            return Character.toString(str.charAt(idx));
        }
    }


    public ArrayList<String> split(String token){
        ArrayList<String> list = new ArrayList<String>();

        if(this.str != null && !this.str.isEmpty()) {
            int start = 0, curIdx;

            while((curIdx = str.indexOf(token, start)) >= 0) {
                list.add(str.substring(start, curIdx));
                start = curIdx + 1;
            }

            list.add(str.substring(start));
        }

        return list;
    }


    public String join(ArrayList<String> itemList) {
        StringBuilder sb = new StringBuilder();
        int len = itemList.size();

        if(len > 0) {
            for(int i = 0; i < len - 1; i++) {
                sb.append(itemList.get(i));
                sb.append(this.str);
            }
            sb.append(itemList.get(len - 1));
        }

        return sb.toString();
    }
}
