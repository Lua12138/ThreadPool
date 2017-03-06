**
 * 工作线程
 */
public class WorkerThread implements Runnable {
    private RunnableFutureCallback callback;
    private Runnable runnable;
    private FutureImpl future;
    private boolean continued;
    private final long maxTTL;
    private long ttl; // <- ttl 计数器

    public WorkerThread(RunnableFutureCallback callback, int maxTTL) {
        this.callback = callback;
        this.continued = true;
        this.maxTTL = maxTTL;
        this.ttl = -1;
    }

    /**
     * 设置线程工作内容 不检查当前工作状态
     *
     * @param runnable
     * @return
     */
    public Future setWorks(Runnable runnable, FutureImpl future) {
        synchronized (this) {
            if (runnable == null)
                this.runnable = future.correlation();
            else
                this.runnable = runnable;
            //this.runnable = runnable == null ? future.correlation() : runnable;
            this.future = future; // <- 防止与之前任务串线
        }
        return this.future;
    }

    public void shutdown() {
        this.continued = false;
        if (this.future != null) this.future.status(FutureStatus.INTERRUPT);
    }

    public void run() {
        while (this.continued) {
            try {
                if (this.runnable != null) {
                    synchronized (this) {
                        // 标记运行状态
                        this.future.status(FutureStatus.RUNNING);
                        this.runnable.run();
                        if (this.runnable instanceof Callable)
                            this.future.setResult(((Callable) this.runnable).result());
                        this.future.status(FutureStatus.DONE);
                        this.runnable = null; // 删除原任务
                        this.callback.threadWorkoutNotify(this, this.future);
                        //this.future = null;
                    }
                    continue;
                }

                // 判断无任务时 TTL超时
                if (this.ttl < 0)
                    this.ttl = System.currentTimeMillis();
                else if (System.currentTimeMillis() - this.ttl > this.maxTTL) {
                    // 超过生命周期则通知线程池
                    if (this.callback.threadDestroyNotify(this))
                        this.shutdown(); // 随后自毁
                    else
                        this.ttl = -1; // 若线程池不允许自毁则重新计算生命周期
                }
                Thread.sleep(1);
            } catch (Throwable e) {
                //TrapOutput.errln("Thread Pool Work Thread Error");
                if (this.future != null)
                    this.future.status(FutureStatus.ERROR);
                if (!this.callback.threadUncatchExceptionNotify(this, e)) {
                    this.runnable = null; // 清除工作线程
                    this.callback.threadWorkoutNotify(this, this.future); // 终止的话 通知线程池
                    this.future = null;
                }
            }
        }
    }
}
