package cn.wang;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author JingjingMa
 * @Date 2019/6/1 02:12
 */
public class HeapSort implements IMapSort {
    @Override
    public Map<String, Integer> sort(Map<String, Integer> map) {
        Words[] words = new Words[map.size()];
        int i = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            words[i++] = new Words(entry.getKey(), entry.getValue());
        }

        new Handler().process(words);

        Map<String, Integer> map0 = new LinkedHashMap<>(WordStatistical.TOP);
        for (int j = 0; j < WordStatistical.TOP; j++) {
            map0.put(words[j].word, words[j].counts);
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

        public void process(Words[] array) {
            if (array == null || array.length == 1)
                return;

            buildMaxHeap(array); // 第一次排序，构建最大堆，只保证了堆顶元素是数组里最大的

            for (int i = array.length - 1; i >= 1; i--) {
                // 经过上面的一些列操作，目前array[0]是当前数组里最大的元素，需要和末尾的元素交换
                // 然后，拿出最大的元素
                swap(array, 0, i);

                // 交换完后，下次遍历的时候，就应该跳过最后一个元素，也就是最大的那个值，然后开始重新构建最大堆
                // 堆的大小就减去1，然后从0的位置开始最大堆
                minHeap(array, i, 0);
            }
        }

        // 构建堆
        public void buildMaxHeap(Words[] array) {
            if (array == null || array.length == 1)
                return;

            // 堆的公式就是 int root = 2*i, int left = 2*i+1, int right = 2*i+2;
            int cursor = array.length / 2;
            for (int i = cursor; i >= 0; i--) { // 这样for循环下，就可以第一次排序完成
                minHeap(array, array.length, i);
            }
        }

        // 最小堆
        public void minHeap(Words[] array, int heapSieze, int index) {
            int left = left(index); // 左子节点
            int right = right(index); // 右子节点
            int maxValue = index; // 暂时定在Index的位置就是最小值

            // 如果左子节点的值，比当前最小的值小，就把最小值的位置换成左子节点的位置
            if (left < heapSieze && array[left].compareTo(array[maxValue]) < 0) {
                maxValue = left;
            }

            //  如果右子节点的值，比当前最小的值小，就把最小值的位置换成左子节点的位置
            if (right < heapSieze && array[right].compareTo(array[maxValue]) < 0) {
                maxValue = right;
            }

            // 如果不相等，说明啊，这个子节点的值有比自己小的，位置发生了交换了位置
            if (maxValue != index) {
                swap(array, index, maxValue); // 就要交换位置元素

                // 交换完位置后还需要判断子节点是否打破了最小堆的性质。最小性质：两个子节点都比父节点大。
                minHeap(array, heapSieze, maxValue);
            }
        }

        // 数组元素交换
        public void swap(Words[] array, int index1, int index2) {
            Words temp = array[index1];
            array[index1] = array[index2];
            array[index2] = temp;
        }

        //返回左节点索引
        private int left(int i) {
            return 2 * i + 1;
        }

        //返回右节点索引
        private int right(int i) {
            return 2 * i + 2;
        }
    }
}
