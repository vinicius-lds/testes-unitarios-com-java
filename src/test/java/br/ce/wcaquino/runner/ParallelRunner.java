package br.ce.wcaquino.runner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

public class ParallelRunner extends BlockJUnit4ClassRunner {

    public ParallelRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        super.setScheduler(new ThreadPool());
    }

    private static class ThreadPool implements RunnerScheduler {

        private ExecutorService executorService;

        public ThreadPool() {
            this.executorService = Executors.newFixedThreadPool(5);
        }

        @Override
        public void schedule(Runnable runnable) {
            executorService.submit(runnable);
        }

        @Override
        public void finished() {
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
