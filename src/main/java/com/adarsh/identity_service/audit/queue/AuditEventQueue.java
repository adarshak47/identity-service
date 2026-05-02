package com.adarsh.identity_service.audit.queue;

import com.adarsh.identity_service.audit.event.AuditEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class AuditEventQueue {

    private final BlockingQueue<AuditEvent> queue = new LinkedBlockingQueue<>();

    public void publish(AuditEvent event) {
        queue.offer(event);
    }

    public AuditEvent consume() throws InterruptedException {
        return queue.take();
    }
}
