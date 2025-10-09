import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrencyTest {
    public static void main(String[] args) {
        // Test 1: launch thread by new Thread
//        Thread thread = launchThread();
//        thread.start();
//        try {
//            thread.join();
//        } catch (InterruptedException e) { e.printStackTrace(); }
//        // Test 2: ExecutorService with Runnable
//        // observe interleave of the four threads
//        try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
//            // launch four copies of runnable, observe interleave
//            executorService.execute(new Enumerator("threadpool_1" , 1, 10));
//            executorService.execute(new Enumerator("threadpool_2" , 1, 10));
//            executorService.execute(new Enumerator("threadpool_3" , 1, 10));
//            executorService.execute(new Enumerator("threadpool_4" , 1, 10));
//        }
//
//        // Test 3: ExecutorService with Callable
//        try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
//            Future<Integer> f1 = executorService.submit(new Compute(10));
//            Future<Integer> f2 = executorService.submit(new Compute(20));
//            Future<Integer> f3 = executorService.submit(new Compute(30));
//            Future<Integer> f4 = executorService.submit(new Compute(40));
//            System.out.println("future_f4=" + f4.get());
//            System.out.println("future_f3=" + f3.get());
//            System.out.println("future_f2=" + f2.get());
//            System.out.println("future_f1=" + f1.get());
//            executorService.shutdown();
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Test 4: ExecutorCompletionService, it is executorservice + blockingquque
//        try (ExecutorService executorService = Executors.newFixedThreadPool(4);) {
//            ExecutorCompletionService<Integer> ecs = new ExecutorCompletionService<>(executorService);
//            ecs.submit(new SleepComputer(2, 100));
//            ecs.submit(new SleepComputer(3, 200));
//            ecs.submit(new SleepComputer(4, 300));
//            ecs.submit(new SleepComputer(1, 400));
//            // expect 400, 100, 200, 300
//            for (int i = 0; i < 4; i++) {
//                int res = ecs.take().get();
//                System.out.println("ecs future get: " + res);
//            }
//            executorService.shutdown();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        // Test 5: producer consumer queue
        EventWriter writer = new EventWriter();
        final int numThreads = 7;
        try (ExecutorService executorService = Executors.newFixedThreadPool(numThreads)) {
            for (int k = 0; k < numThreads; k++) {
                final int start = k;
                executorService.execute(() -> { for (int i = start; i < 100; i += numThreads) { writer.addEvent("event_" + i);}});
            }
        }
        writer.shutdown();
    }

    // Test 5 implementations
    static class EventWriter {
        public EventWriter() {
            workerThread = new Thread(new Worker(this.queue));
            workerThread.start();
        }
        public void shutdown() {
            // enqueue a poison to terminate worker thread
            queue.offer(new Record(new Event("EOF"), null));
            // workerThread.interrupt();
        }
        public void addEvent(String event) {
            System.out.println("addEvent begin:" + event);
            Event eventObj = new Event(event);
            Future<Boolean> future = submit(eventObj);
            // blocking until future available
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("addEvent end:" + event);
        }
        private Future<Boolean> submit(Event event) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            try {
                boolean success = queue.offer(new Record(event, future), 100, TimeUnit.MILLISECONDS);
                if (!success) {
                    throw new RuntimeException("failed to add event");
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            return future;
        }

        private final Thread workerThread;
        private BlockingQueue<Record> queue = new LinkedBlockingQueue<>();
        // no need to use mutex as the following are only accessed from workerThread

        static class Worker implements Runnable {
            @Override
            public void run() {
                while (true) {
                    Record record = null;
                    try {
                        record = queue.poll(100, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        // poll on empty queue can throw this InterruptedException so ignore and retry
                        e.printStackTrace();
                    }
                    boolean flushed = false;
                    if (record != null) {
                        if (record.event.getEvent().equals("EOF")) {
                            System.out.println("Worker received EOF, terminating");
                            flush();
                            return;
                        }
                        batch.add(record);
                        if (batch.size() == batchSize) {
                            System.out.println("batch size " + batchSize);
                            // size based flush
                            flush();
                            flushed = true;
                        }
                    }
                    if (!flushed) {
                        long now = System.currentTimeMillis();
                        if (now >= lastFlushTimeStamp + flushIntervalSeconds * 1000) {
                            System.out.println("flush interval " + flushIntervalSeconds + " seconds");
                            // time based flush
                            flush();
                        }
                    }
                }
            }

            private void flush() {
                for (Record rec : batch) {
                    // simulate write to disk and flush/fsync
                    System.out.println("flush_event:" + rec.event);
                }
                for (Record rec : batch) {
                    // notify client event added
                    rec.getFuture().complete(true);
                }
                batch.clear();
                lastFlushTimeStamp = System.currentTimeMillis();
            }

            public Worker(BlockingQueue<Record> queue) {
                this.queue = queue;
            }

            private final BlockingQueue<Record> queue;
            private final List<Record> batch = new ArrayList<>(batchSize);
            private static final int batchSize = 5;
            private static final int flushIntervalSeconds = 10;
            private long lastFlushTimeStamp = -1L;
        }

        static class Record {
            private final Event event;
            private final CompletableFuture<Boolean> future;
            public Record(Event event, CompletableFuture<Boolean> future) { this.event = event; this.future = future; }
            public CompletableFuture<Boolean> getFuture() { return future; }
        }
        static class Event {
            private final String event;
            public String getEvent() { return this.event;}
            public String toString() { return getEvent(); }
            public Event(String event) { this.event = event; }
        }
    }


    // Test 4 implementations
    static class SleepComputer implements Callable<Integer> {
        public SleepComputer(int sleepSeconds, int seed) {
            this.sleepSeconds = sleepSeconds;
            this.seed = seed;
        }
        public Integer call() throws Exception {
            Thread.sleep(sleepSeconds * 1000L);
            return seed * seed;
        }

        private final int sleepSeconds;
        private final int seed;
    }

    // Test 3 implementations
    // callable and future
    static class Compute implements Callable<Integer> {
        public Compute(int seed) { this.seed = seed; }
        @Override
        public Integer call() throws Exception {
            Thread.sleep(1000);
            return seed * seed;
        }
        private int seed;
    }

    // Test 2 implementations
    // fixed threadpool with runnable

    // Test 1 implementations
    public static Thread launchThread() {
        return new Thread(new Enumerator("newThread", 1, 10));
    }
    static class Enumerator implements Runnable {
        Enumerator(String prefix, int start, int end) { this.prefix = prefix; this.start = start; this.end = end; }

        @Override
        public void run() {
            for (int i = start; i <= end; i++) {
                System.out.println("enumerator:" + prefix + " " + i);
            }
        }

        private String prefix;
        private int start;
        private int end;
    }
}
