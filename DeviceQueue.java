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

        this.deviceQueue.put(QUEUE.RUNNING, new ArrayList<IORB>());
        this.deviceQueue.put(QUEUE.WAITING, new ArrayList<IORB>());
    }

    /**
     * Required - return length/size of running queue.
     * 
     * @see Only checks the running queue.
     */
    public final int length() {
        List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

        return runningQueue.size();
    }

    /**
     * Required - return whether running queue is empty.
     * 
     * @see Only checks the running queue.
     */
    public final boolean isEmpty() {
        List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

        return runningQueue.isEmpty();
    }

    /**
     * Required - return whether running queue contains specified iorb.
     * 
     * @see Only checks the running queue.
     */
    public final synchronized boolean contains(IORB iorb) {
        List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

        return runningQueue.contains(iorb);
    }

    /**
     * Object to add to the specified queue type
     * 
     * @see Device.do_enqueueIORB()
     */
    public void add(IORB iorb, QUEUE type) {
        if (type == QUEUE.RUNNING) {
            List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

            runningQueue.add(iorb);

            /** NOTE: might be redundant, don't remember. */
            this.deviceQueue.replace(QUEUE.RUNNING, runningQueue);
        } else if (type == QUEUE.WAITING) {
            List waitingQueue = this.deviceQueue.get(QUEUE.WAITING);

            waitingQueue.add(iorb);

            /** NOTE: might be redundant, don't remember. */
            this.deviceQueue.replace(QUEUE.WAITING, waitingQueue);
        }

        return;
    }

    /**
     * Remove the specified object from the specified thread.
     */
    public synchronized IORB remove(IORB iorb, QUEUE type) {
        if (type == QUEUE.RUNNING) {
            List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

            if (runningQueue.contains(iorb)) {
                IORB removed = runningQueue.remove(iorb);

                /** NOTE: might be redundant, don't remember. */
                this.deviceQueue.replace(QUEUE.RUNNING, runningQueue);

                return removed;
            }
        } else if (type == QUEUE.WAITING) {
            List waitingQueue = this.deviceQueue.get(QUEUE.WAITING);

            if (waitingQueue.contains(iorb)) {
                IORB removed = waitingQueue.remove(iorb);

                /** NOTE: might be redundant, don't remember. */
                this.deviceQueue.replace(QUEUE.WAITING, waitingQueue);

                return removed;
            }
        }

        return null;
    }

    /**
     * Get the first element of the running queue
     */
    public synchronized IORB remove() {
        if (this.isEmpty() == false) {
            List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);
            IORB iorb = runningQueue.remove(0);

            /** NOTE: might be redundant, don't remember. */
            this.deviceQueue.replace(QUEUE.RUNNING, runningQueue);

            return iorb;
        } else {
            return null;
        }
    }

    /**
     * Swap the running queue and waiting queue with eachother.
     * 
     * Typically this should be called when Device module detects that the running
     * queue is empty.
     */
    public synchronized void swap_queues() {
        List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);
        List waitingQueue = this.deviceQueue.get(QUEUE.WAITING);

        this.deviceQueue.replace(QUEUE.WAITING, runningQueue);
        this.deviceQueue.replace(QUEUE.RUNNING, waitingQueue);
    }

    /**
     * Get the specified queue obj
     */
    public synchronized List get_queue(QUEUE type) {
        if (type == QUEUE.RUNNING) {

            return this.deviceQueue.get(QUEUE.RUNNING);
        } else if (type == QUEUE.WAITING) {

            return this.deviceQueue.get(QUEUE.WAITING);
        }

        return null;
    }
}
