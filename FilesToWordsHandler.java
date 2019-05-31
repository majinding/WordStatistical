package cn.wang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author JingjingMa
 * @Date 2019/6/1 01:46
 */
public class FilesToWordsHandler {

    public Map<String, Integer> read(String fpath) throws Exception {
        long t10 = System.currentTimeMillis();
        //多个文本文件的根目录
        Path path = Paths.get(fpath);

        int fileCount = path.toFile().listFiles().length;
        System.out.println("文件个数：\t" + fileCount);

        //---5个线程并发计算---
        ExecutorService service = Executors.newFixedThreadPool(5);

        //初始化count数目的同步计数器，只有当同步计数器为0，主线程才会向下执行
        CountDownLatch countDownLatch = new CountDownLatch(fileCount);

        //存储子线程返回到数据
        List<Future<Map<String, Integer>>> list = new ArrayList<>(fileCount);

        //遍历文件，使用子线程读取
        Files.list(path).forEach(t -> list.add(service.submit(new FileWordHandlerThread(t, countDownLatch))));

        //停止接收新任务，原来的任务继续执行
        service.shutdown();

        long t11 = System.currentTimeMillis();
        System.out.println(String.format("===scheduling thread to read file , cost time %d ms===", (t11 - t10)));
        //等待当前线程等待计数器为0
        countDownLatch.await();

        long t12 = System.currentTimeMillis();
        System.out.println(String.format("===all thread read file over , cost time %d ms===", (t12 - t11)));

        //---全部计算后做结果合并---
        Map<String, Integer> hashMap = null;
        for (int i = 0; i < list.size(); i++) {
            hashMap = mapMerge(hashMap, list.get(i).get());
        }

        long t13 = System.currentTimeMillis();
        System.out.println(String.format("===all thread read data to merge, cost time %d ms===", (t13 - t12)));

        return hashMap;
    }


    /**
     * 文件单词处理线程
     */
    class FileWordHandlerThread implements Callable<Map<String, Integer>> {
        private Path path;
        private CountDownLatch countDownLatch;
        private Map<String, Integer> map = new ConcurrentHashMap<>();

        public FileWordHandlerThread(Path path, CountDownLatch countDownLatch) {
            this.path = path;
            this.countDownLatch = countDownLatch;
        }

        //预处理特殊字符
        private String preProcess(String str) {
            StringBuilder sb = new StringBuilder();
            char[] ch = str.trim().toCharArray();
            for (char c : ch) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    sb.append(c);
                }
            }
            return sb.toString().trim();
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            //System.out.println(Thread.currentThread().getName() + "\t" + path.toString());
            try (Scanner scanner = new Scanner(Files.newInputStream(path))) {
                while (scanner.hasNext()) {
                    Arrays.stream(scanner.nextLine().split(" ")).filter(t -> !"".equals(t))
                            .forEach(word -> {
                                word = preProcess(word);
                                if (!word.equals("")) {
                                    if (map.containsKey(word)) {
                                        map.put(word, map.get(word) + 1);
                                    } else {
                                        map.put(word, 1);
                                    }
                                }
                            });

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
            return map;
        }
    }

    /**
     * 合并map
     *
     * @param map1
     * @param map2
     * @return
     */
    private Map<String, Integer> mapMerge(Map<String, Integer> map1, Map<String, Integer> map2) {
        if (null == map1) {
            return map2;
        }
        return Stream.concat(
                map1.entrySet().stream(),
                map2.entrySet().stream()
        ).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (v1, v2) -> v1 + v2)
        );
    }
}