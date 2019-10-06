package org.mustream.common.sys;

public class TaskRunner {
    public static Thread execute(Runnable runnable, int priority) {
        Thread thread = new Thread(runnable);
        thread.setName(runnable.getClass().getSimpleName() + " " + thread.getId());
        thread.setPriority(priority);
        thread.start();
        return thread;
    }

    public static Thread execute(Runnable runnable) {
        return execute(runnable, 5);
    }
}
