package com.orangeelephant.sobriety.logging;

import androidx.annotation.Nullable;

import com.orangeelephant.sobriety.database.LogDatabase;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

import java.util.LinkedList;

/**
 * A class which stores logs to the {@link com.orangeelephant.sobriety.database.LogDatabase}
 * this should be done on a background thread to not impact app performance
 *
 * singleton as there should only be one logging thread
 */
public class Logger {
    private static final String TAG = Logger.class.getSimpleName();

    private static Logger INSTANCE = null;

    private final String THREAD_NAME = "logger";
    private final Queue queue = new Queue();
    private final DbWriteThread dbWriteThread = new DbWriteThread();

    private Logger() {}

    public static Logger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Logger();
        }
        return INSTANCE;
    }

    /**
     * method to be called only once the sqlcipherkey has been initialised and not before
     * otherwise the logger wont be able to write to the database.
     */
    public void startLoggerThread() {
        dbWriteThread.start();
        LogEvent.i(TAG, "Started logger thread");
    }

    public void logToDb(String tag, String message, @Nullable String stack_trace) {
        queue.addToQueue(new LogRecord(tag, message, stack_trace));
    }

    private class LogRecord {
        private final String TAG;
        private final String MESSAGE;
        private final String STACK_TRACE;

        private LogRecord(String tag, String message, @Nullable String stack_trace) {
            TAG = tag;
            MESSAGE = message;
            STACK_TRACE = stack_trace;
        }

        public String getTag() {
            return TAG;
        }

        public String getMessage() {
            return MESSAGE;
        }

        public String getStackTrace() {
            return STACK_TRACE;
        }
    }

    /**
     * This class is the queue of things that need to be logged, since it can be accessed
     * by more than one threads, a lock is needed
     *
     * https://stackoverflow.com/questions/26590542/java-lang-illegalmonitorstateexception-object-not-locked-by-thread-before-wait
     * information about locking was found at the above link
     */
    private class Queue {
        private final LinkedList<LogRecord> queue = new LinkedList<>();
        private final Object QUEUE_LOCK = new Object();
        private Queue() {}

        private void addToQueue(LogRecord log) {
            synchronized (QUEUE_LOCK) {
                queue.add(log);
                QUEUE_LOCK.notify();
            }
        }

        private LogRecord getNext() {
            synchronized (QUEUE_LOCK) {
                try {
                    while (queue.peek() == null) {
                        QUEUE_LOCK.wait();
                    }
                    return queue.remove();
                } catch (InterruptedException e) {
                    LogEvent.e(TAG, "Write thread was interrupted while waiting", e);
                    throw new AssertionError();
                }
            }
        }
    }

    /**
     * This thread should have super low priority because it is less important than
     * key app functionality
     */
    private class DbWriteThread extends Thread {

        private DbWriteThread() {
            super(THREAD_NAME);
            this.setPriority(Thread.MIN_PRIORITY);
        }

        @Override
        public void run() {
            LogDatabase logDatabase = new LogDatabase(ApplicationDependencies.getApplicationContext());
            while (true) {
                LogRecord record = queue.getNext();
                logDatabase.write(record.getTag(), record.getMessage(), record.getStackTrace(), 10);
                System.out.println("Written - " + record.getTag());
            }
        }
    }
}
