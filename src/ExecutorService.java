/**
 * 采用Future模式的多线程异步线程池实现<br>
 */
public interface ExecutorService {
    Future execute(Runnable runnable);

    Future execute(Thread thread);

    Future execute(Callable callable);

    /**
     * 关闭线程池<br>
     * 已经进入线程池的工作线程会继续执行直到结束<br>
     * 不再接受新的工作线程加入到线程池<br>
     * 所有工作线程结束后 线程池自毁
     */
    void shutdown();

    /**
     * 立刻关闭线程池<br>
     * 已经进入线程池的工作线程会被强制结束<br>
     * 由此可能导致运行状态的不确定
     */
    void shutdownNow();
}
