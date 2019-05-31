package cn.wang;

/**
 * @Author JingjingMa
 * @Date 2019/5/31 20:24
 */

import java.util.Map;

public class WordStatistical {
    public static final int TOP = 100;

    public void execute() {
        long t1 = System.currentTimeMillis();
        try {
            String fpath = "/Users/apple/Documents/tmp/20190531/哈利波特英文版1-7全集";

            //将文件中的单词读取到map中
            Map<String, Integer> wordsMap = new FilesToWordsHandler().read(fpath);

            long t2 = System.currentTimeMillis();

            //todo 两种方式排序时间差别不大
            IMapSort mapSort
                    //= new HeapSort();//堆排序
                    = new MapSelfSort();//map自身排序

            //---选出出现频率最高的100个单词及对应次数---
            Map<String, Integer> topMap = mapSort.sort(wordsMap);
            topMap.forEach((k,v)-> System.out.println(String.format("%-30s%s",k,v)));

            long t3 = System.currentTimeMillis();
            System.out.println(String.format("===words sort cost time %d ms===", (t3 - t2)));

        } catch (Exception e) {
            e.printStackTrace();
        }

        long t100 = System.currentTimeMillis();
        System.out.println("====================================");
        System.out.println(String.format("total cost time %d ms", (t100 - t1)));
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        WordStatistical obj = new WordStatistical();
        obj.execute();
    }


}

