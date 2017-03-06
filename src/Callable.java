/**
 * 可以返回结果的线程对象
 */
public interface Callable extends Runnable {
    /**
     * 返回给线程池调用者的运行结果
     *
     * @return
     */
    Object result();
}
