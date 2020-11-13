package com.gl.elasticsearch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {
    public static ExecutorService executorService= Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("ok");
            int i = 1 / 0;
            return "re";
        }, executorService);

    }
}
