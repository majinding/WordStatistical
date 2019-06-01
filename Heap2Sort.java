package cn.wang;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author JingjingMa
 * @Date 2019/6/1 20:38
 */
public class Heap2Sort implements IMapSort {
    @Override
    public Map<String, Integer> sort(Map<String, Integer> map) {
        Handler handler = new Handler();
        Words[] words = new Words[WordStatistical.TOP + 1];
        int i = 1;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (i > WordStatistical.TOP) {
                //将新元素与堆顶元素相比，大于对顶元素则替换掉堆顶元素
                if (entry.getValue() > words[1].counts) {
                    words[1].word = entry.getKey();
                    words[1].counts = entry.getValue();
                    //维护最小堆的性质
                    handler.heapFy(words, 1, WordStatistical.TOP);
                }
            } else {
                words[i++] = new Words(entry.getKey(), entry.getValue());
                if (i == WordStatistical.TOP + 1) {
                    handler.buildMinHeap(words);//构建最小堆
                }
            }
        }

        //堆排序，将第一个元素与“最后一个元素”交换，然后维护最小堆的性质
        for (int k = 1; k < words.length - 1; k++) {
            handler.exchange(words, 1, words.length - k);
            handler.heapFy(words, 1, words.length - 1 - k);
        }

        Map<String, Integer> map0 = new LinkedHashMap<>(WordStatistical.TOP);
        for (int j = 0; j < WordStatistical.TOP; j++) {
            map0.put(words[j + 1].word, words[j + 1].counts);
        }

        return map0;
    }

    class Words implements Comparable<Words> {
        String word;
        int counts;

        public Words(String word, int counts) {
            this.word = word;
            this.counts = counts;
        }

        //重写compareTo方法，用于比较两个对象大小
        @Override
        public int compareTo(Words w) {
            if (this.counts > w.counts) {
                return 1;
            }
            if (this.counts < w.counts) {
                return -1;
            }
            return 0;
        }

        //重写clone方法
        @Override
        protected Words clone() throws CloneNotSupportedException {
            Words words = new Words(word, counts);
            return words;
        }
    }


    class Handler {

        //构建最小堆
        private void buildMinHeap(Words[] words) {
            int length = words.length - 1;
            for (int i = length / 2; i >= 1; i--) {
                heapFy(words, i, length);
            }
        }

        //维护最小堆
        private void heapFy(Words[] words, int i, int length) {
            if (i > length)
                return;
            int left = left(i);
            int right = right(i);
            int minIndex = i;
            //找到左右节点中最小的节点
            if (left >= 1 && left <= length) {
                if (words[left].compareTo(words[minIndex]) < 0) {
                    minIndex = left;
                }
            }
            if (right >= 1 && right <= length) {
                if (words[right].compareTo(words[minIndex]) < 0) {
                    minIndex = right;
                }
            }
            if (minIndex == i) {
                return;
            } else {
                exchange(words, i, minIndex);
                heapFy(words, minIndex, length);
            }
        }

        //交换两个元素
        private void exchange(Words[] words, int i, int maxindex) {
            try {
                Words temp = words[i].clone();
                words[i] = words[maxindex].clone();
                words[maxindex] = temp.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

        }

        //返回左节点索引
        private int left(int i) {
            return 2 * i;
        }

        //返回右节点索引
        private int right(int i) {
            return 2 * i + 1;
        }
    }
}

