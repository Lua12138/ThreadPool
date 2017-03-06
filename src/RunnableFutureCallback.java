/**
 * 工作线程与线程池之间回调
 */
public interface RunnableFutureCallback {
    /**
     * 工作线程正常完后被触发
     *
     * @param currentThread 相关线程
     * @param future
     */
    void threadWorkoutNotify(Runnable currentThread, Future future);

    /**
     * 工作线程发生未捕获异常造成线程被迫终止时 被调用
     *
     * @param currentThread 工作线程
     * @param e             异常
     * @return 需要线程继续尝试则返回true 否则将终止工作线程
     */
    boolean threadUncatchExceptionNotify(Runnable currentThread, Throwable e);

    /**
     * 由于超过生存周期，线程即将自毁，通知线程池
     *
     * @param currentThread 即将自毁的线程
     * @return 线程池决定是否允许自毁，允许返回true
     */
    boolean threadDestroyNotify(Runnable currentThread);
}
