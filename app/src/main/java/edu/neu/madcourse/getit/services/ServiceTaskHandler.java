package edu.neu.madcourse.getit.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServiceTaskHandler {

    public static void performTask(Runnable task) {
        ExecutorService threadpool = Executors.newCachedThreadPool();
        Future futureTask = threadpool.submit(() -> task);
        while(!futureTask.isDone());
        threadpool.shutdown();
    }
}
