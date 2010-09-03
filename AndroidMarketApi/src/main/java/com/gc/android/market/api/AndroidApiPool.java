package com.gc.android.market.api;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Amir Raminfar
 */
public class AndroidApiPool {
    private BlockingQueue<AndroidMarketApi> queue = new LinkedBlockingQueue<AndroidMarketApi>();

    public AndroidApiPool(Collection<AndroidMarketApi> queue) {
        this.queue.addAll(queue);
    }

    public AndroidMarketApi acquire() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void release(AndroidMarketApi e) {
        if (e != null) {
            queue.offer(e);
        }
    }
}
