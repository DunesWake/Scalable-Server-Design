package cs455.scaling.server;

import java.util.LinkedList;

public class WorkerThread extends Thread {
    private int batchSize;
    private int batchTime;

    WorkerThread(int batchSize, int batchTime) {
        this.batchTime = batchTime;
        this.batchSize = batchSize;
    }

    public void run() {
        Task task;
        LinkedList<byte[]> batch;
        while (true) {
            synchronized (ThreadPoolManager.queue) {
                while (ThreadPoolManager.queue.isEmpty()) {
                    try {
                        ThreadPoolManager.queue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("An error occurred while queue is waiting: " + e.getMessage());
                    }
                }
                long activatedAt = System.currentTimeMillis() / 1000;
                task = ThreadPoolManager.queue.poll();

                //wait for batchSize or batchTime
                long activeFor = (System.currentTimeMillis() / 1000) - activatedAt;
                while ((task.get().size() < batchSize) && activeFor != batchTime) {
                    activeFor = (System.currentTimeMillis() / 1000) - activatedAt;
                }
            }
            task.run();

            //TODO OR WAIT TILL TIME
            //start building a packet
            //flush it when reach time or size
        }
    }
}
