/**
 * 线程池延迟获得运行结果的方法
 */
public interface Future {
    /**
     * 等待超时的结果对象
     */
    Object timeoutExceptionResult = new Object() {
        public String toString() {
            return "TimeoutExceptionResult";
        }
    };

    /**
     * @return 当前的运行状态
     */
    FutureStatus status();

    /**
     * @return 同步获得线程返回结果
     */
    Object get();

    /**
     * @param timeout 最长等待时间 ms
     * @return 指定时间内同步获得线程返回结果
     */
    Object get(long timeout);
}
