package tech.xigam.basicqueuer;

import java.time.OffsetDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public final class BasicQueue {
    private final BlockingQueue<Consumer<OffsetDateTime>> queue =
            new LinkedBlockingQueue<>();
    private final long millisUntilAdvance;
    
    public BasicQueue(long msUntilNext) {
        this.millisUntilAdvance = msUntilNext;
    }
    
    public BasicQueue queueAction(Consumer<OffsetDateTime> action) {
        this.queue.add(action); return this;
    }
    
    public void next() {
        var next = this.queue.poll();
        if(next != null) next.accept(OffsetDateTime.now());
    }

    /**
     * This starts the queue.
     * The queue can still be added to, or advanced manually.
     */
    public void start() {
        start(0);
    }

    public void start(long initialDelay) {
        new Timer().scheduleAtFixedRate(new Next(this), initialDelay, this.millisUntilAdvance);
    }
    
    static class Next extends TimerTask {
        private final BasicQueue queue;
        
        public Next(BasicQueue queue) {
            this.queue = queue;
        }
        
        @Override public void run() {
            this.queue.next();
        }
    }
}