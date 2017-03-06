/**
 * 运行状态
 */
public interface FutureStatus {
    /**
     * 准备好 尚未执行
     */
    FutureStatus READY = new FutureStatus() {
        public String toString() {
            return "FutureStatus - Ready";
        }
    };
    /**
     * 已完成
     */
    FutureStatus DONE = new FutureStatus() {
        public String toString() {
            return "FutureStatus - Done";
        }
    };
    /**
     * 正在队列等待执行
     */
    FutureStatus QUEUING = new FutureStatus() {
        public String toString() {
            return "FutureStatus - Queuing";
        }
    };
    /**
     * 正在执行
     */
    FutureStatus RUNNING = new FutureStatus() {
        public String toString() {
            return "FutureStatus - Running";
        }
    };
    /**
     * 运行过程中发生异常
     */
    FutureStatus ERROR = new FutureStatus() {
        public String toString() {
            return "FutureStatus - Error";
        }
    };
    /**
     * 由于线程池终止造成工作线程终止
     */
    FutureStatus INTERRUPT = new FutureStatus() {
        public String toString() {
            return "FutureStatus - Interrupt";
        }
    };
}
