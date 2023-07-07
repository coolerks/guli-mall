package top.integer.gulimall.search.thread;

import java.util.concurrent.*;

public class ThreadTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
//        Thread001 thread001 = new Thread001();
//        Thread001 thread002 = new Thread001();
//        Thread001 thread003 = new Thread001();
//        Thread001 thread004 = new Thread001();
//
//        thread001.start();
//        thread002.start();
//        thread003.start();
//        thread004.start();

//        Thread001 target = new Thread001();
//        Thread thread1 = new Thread(target);
//        Thread thread2 = new Thread(target);
//        Thread thread3 = new Thread(target);
//        Thread thread4 = new Thread(target);
//        thread1.start();
//        thread2.start();
//        thread3.start();
//        thread4.start();
//        MyCallable myCallable = new MyCallable();
//        FutureTask<String> futureTask = new FutureTask<>(myCallable);
//        FutureTask<String> futureTask2 = new FutureTask<>(myCallable);
//        new Thread(futureTask).start();
//        new Thread(futureTask2).start();
//        String s = futureTask.get();
//        String s1 = futureTask2.get();
//        System.out.println("s1 = " + s1);
//        System.out.println("s = " + s);

//        ExecutorService executorService = Executors.newFixedThreadPool(10);
//        MyRunnable command = new MyRunnable();
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.execute(command);
//        executorService.shutdown();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        MyCallable task = new MyCallable();
        for (int i = 0; i < 30; i++) {
        }

        executorService.shutdown();
    }

    public static class Thread001 extends Thread {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getId() + "启动了...");
        }
    }

    public static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getId() + " runnable 启动了，当前线程的名字为：" + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getId() + " runnable 结束了...");
        }
    }

    public static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("my-callable调用了");
            Thread.sleep(10000);
            return "这是：" + Thread.currentThread().getId() + "号线程";
        }
    }
}


class Pool {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始进行第一个非常耗时的计算，所在线程为：" + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 10 / 2;
        }, executor);
        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始进行第二个非常耗时的计算，所在线程为：" + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 10 / 5;
        }, executor);
        CompletableFuture<Integer> task3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始进行第三个个非常耗时的计算，所在线程为：" + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 10 / 3;
        }, executor);

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(task1, task2, task3);
        System.out.println("有一个执行完了，其中的返回值为：" + anyOf.get());
        executor.shutdown();
    }
}
