package cn.wang;

import java.util.*;

/**
 * @Author JingjingMa
 * @Date 2019/6/1 02:11
 */
public class MapSelfSort implements IMapSort{

    /**
     * map排序
     *
     * @param map Map<String, Integer> 按照值进行排序
     * @return：返回排序后的Map
     */
    @Override
    public Map<String, Integer> sort(Map<String, Integer> map) {

        List<Map.Entry<String, Integer>> list = new ArrayList<>();
        list.addAll(map.entrySet());
        Collections.sort(list, (o1, o2) -> {
            if (o2.getValue().compareTo(o1.getValue()) > 0) {
                return 1;
            } else if (o2.getValue().compareTo(o1.getValue()) < 0) {
                return -1;
            } else {
                return 0;
            }
        });

        Map<String, Integer> map0 = new LinkedHashMap<>(WordStatistical.TOP);

        for (int i = 0; i < WordStatistical.TOP; i++) {
            Map.Entry<String, Integer> entry = list.get(i);
            map0.put(entry.getKey(), entry.getValue());
        }

        return map0;

    }

}
