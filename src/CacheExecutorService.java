/**
 * 可缓存的线程池对象
 */
public class CacheExecutorService implements ExecutorService, RunnableFutureCallback {
    private final int minThread;
    private final int maxThread;
    private Hashtable pool;
    private int workThreadAlive; // 当前可用的线程计数
    private Queue tasks;
    private boolean stopped; // 线程池终止标记

    public CacheExecutorService() {
        this(2, 8);
    }

    /**
     * @param minThread 线程池保留的最小线程数
     * @param maxThread 线程池保留的最大线程数
     */
    public CacheExecutorService(int minThread, int maxThread) {
        this.minThread = minThread;
        this.maxThread = maxThread;
        //this.pool = new Runnable[this.maxThread];
        this.pool = new Hashtable();
        this.tasks = new AsyncQueue();
        this.stopped = false;
    }

    public synchronized Future execute(Runnable runnable) {
        if (this.stopped) return new FutureImpl().status(FutureStatus.INTERRUPT);
        if (this.workThreadAlive > 0) { // 当前是否存在空闲线程
            WorkerThread thread = null;
            for (Enumeration enumeration = this.pool.keys(); enumeration.hasMoreElements(); ) {
                thread = (WorkerThread) enumeration.nextElement();
                if (Boolean.FALSE.equals(this.pool.get(thread))) break;
            }
            if (thread != null) {
                this.workThreadAlive--;
                this.pool.put(thread, Boolean.TRUE);
                return thread.setWorks(runnable, new FutureImpl());
            }
        }

        // 是否到达设置的线程上限
        if (this.maxThread - this.pool.size() > 0) {
            WorkerThread thread = new WorkerThread(this, 1000 * 30); // 30s 生存周期
            this.pool.put(thread, Boolean.TRUE); // 放到池子
            new Thread(thread).start(); // 启动线程
            return thread.setWorks(runnable, new FutureImpl()); // 设置工作内容
        }

        // 已到达上限 将请求放置队列
        Future future = new FutureImpl(runnable).status(FutureStatus.QUEUING);
        this.tasks.push(future);
        return future;
    }

    public Future execute(Thread thread) {
        return this.execute((Runnable) thread);
    }

    public Future execute(Callable callable) {
        return this.execute((Runnable) callable);
    }

    public void shutdown() {
        this.stopped = true;
    }

    public synchronized void shutdownNow() {
        this.stopped = true;
        for (Enumeration enumeration = this.pool.keys(); enumeration.hasMoreElements(); ) {
            WorkerThread thread = (WorkerThread) enumeration.nextElement();
            thread.shutdown();
        }
        // 若队列中存在任务则统一标记
        while (!this.tasks.empty())
            ((FutureImpl) this.tasks.pop()).status(FutureStatus.INTERRUPT);
    }

    /**
     * 从任务队列中拉取一个任务
     *
     * @return 成功true
     */
    private boolean pollFromQueue(Runnable thread) {
        if (!this.tasks.empty()) {
            FutureImpl future = (FutureImpl) this.tasks.pop();
            ((WorkerThread) thread).setWorks(null, future);
            return true;
        }
        return false;
    }

    public synchronized void threadWorkoutNotify(Runnable currentThread, Future future) {
        if (!this.pollFromQueue(currentThread)) {
            this.pool.put(currentThread, Boolean.FALSE);
            this.workThreadAlive++; // 可用活动线程 + 1
        }
    }

    public synchronized boolean threadUncatchExceptionNotify(Runnable currentThread, Throwable e) {
        //this.workThreadAlive++; // 故障恢复后终止原工作内容
        return false;
    }

    public synchronized boolean threadDestroyNotify(Runnable currentThread) {
        if (this.pool.size() > this.minThread) {
            if (this.stopped) this.shutdownNow(); // 队列为空且已经停止线程池则彻底终止
            this.pool.remove(currentThread);
            if (this.workThreadAlive > 0) this.workThreadAlive--;
            return true;
        }
        return false;
    }
}
