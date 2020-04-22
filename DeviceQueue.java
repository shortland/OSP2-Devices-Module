/**
 * Ilan Kleiman
 * 110942711
 * 
 * I pledge my honor that all parts of this project were done by me individually, 
 * without collaboration with anyone, and without consulting any external sources 
 * that provide full or partial solutions to a similar project.
 * I understand that breaking this pledge will result in an "F" for the entire course.
 */

package osp.Devices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum QUEUE {
    WAITING, RUNNING
}

public class DeviceQueue implements GenericQueueInterface {

    Map<QUEUE_TYPE, List> deviceQueue;

    public DeviceQueue() {
        this.deviceQueue = new HashMap<>();

        this.deviceQueue.put(QUEUE.RUNNING, new ArrayList<Object>());
        this.deviceQueue.put(QUEUE.WAITING, new ArrayList<Object>());
    }

    /** Required */
    public final int length() {
        return queue.size();
    }

    /** Required */
    public final boolean isEmpty() {
        return queue.isEmpty();
    }

    /** Required */
    public final synchronized boolean contains(Object obj) {
        return queue.contains(o);
    }

    /**
     * TODO: Object to add to queue
     * 
     * @see Device.do_enqueueIORB()
     */
    public void add(Object obj, QUEUE type) {
        if (type == QUEUE.RUNNING) {
            List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

            runningQueue.add(obj);
            this.deviceQueue.replace(QUEUE.RUNNING, runningQueue);
        } else if (type == QUEUE.WAITING) {
            List waitingQueue = this.deviceQueue.get(QUEUE.WAITING);

            waitingQueue.add(obj);
            this.deviceQueue.replace(QUEUE.WAITING, waitingQueue);
        }

        return;
    }
}
