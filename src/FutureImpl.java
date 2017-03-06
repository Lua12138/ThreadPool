public class FutureImpl implements Future {
    private Object result;
    private Runnable correlation; // <- 相关的任务对象
    private FutureStatus status;

    protected FutureImpl() {
        this.status = FutureStatus.READY;
    }

    protected FutureImpl(Runnable correlation) {
        this.correlation = correlation;
    }

    protected void setResult(Object result) {
        this.result = result;
    }

    protected FutureImpl status(FutureStatus status) {
        this.status = status;
        return this;
    }

    public FutureStatus status() {
        return this.status;
    }

    public Runnable correlation() {
        return correlation;
    }

    public Object get() {
        return get(Long.MAX_VALUE);
    }

    public Object get(long timeout) {
        long cur = System.currentTimeMillis();
        while (FutureStatus.DONE != this.status() &&
                FutureStatus.ERROR != this.status() &&
                FutureStatus.INTERRUPT != this.status()) {
            if (System.currentTimeMillis() - cur > timeout) return Future.timeoutExceptionResult;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        return this.result;
    }
}
