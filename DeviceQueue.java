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

import osp.Utilities.*;

public class DeviceQueue implements GenericQueueInterface {

    Map<QUEUE, List> deviceQueue;

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
    @Override
    public final int length() {
        List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

        return runningQueue.size();
    }

    /**
     * Required - return whether running queue is empty.
     * 
     * @see Only checks the running queue.
     */
    @Override
    public final boolean isEmpty() {
        List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

        return runningQueue.isEmpty();
    }

    /**
     * Required - return whether running queue contains specified iorb.
     * 
     * @see Only checks the running queue.
     */
    @Override
    public final synchronized boolean contains(Object obj) {
        List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

        return runningQueue.contains((IORB) obj);
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
        } else if (type == QUEUE.WAITING) {
            List waitingQueue = this.deviceQueue.get(QUEUE.WAITING);

            waitingQueue.add(iorb);
        }

        return;
    }

    /**
     * Remove the specified object from the specified thread.
     */
    public synchronized boolean remove(IORB iorb, QUEUE type) {
        if (type == QUEUE.RUNNING) {
            List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

            if (runningQueue.contains(iorb)) {
                return runningQueue.remove(iorb);
            }
        } else if (type == QUEUE.WAITING) {
            List waitingQueue = this.deviceQueue.get(QUEUE.WAITING);

            if (waitingQueue.contains(iorb)) {
                return waitingQueue.remove(iorb);
            }
        }

        return false;
    }

    /**
     * Get the first element of the running queue
     */
    public synchronized IORB remove() {
        if (this.isEmpty() == false) {
            List runningQueue = this.deviceQueue.get(QUEUE.RUNNING);

            return (IORB) runningQueue.remove(0);
        }

        return null;
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
    public synchronized List<IORB> get_queue(QUEUE type) {
        if (type == QUEUE.RUNNING) {

            return this.deviceQueue.get(QUEUE.RUNNING);
        } else if (type == QUEUE.WAITING) {

            return this.deviceQueue.get(QUEUE.WAITING);
        }

        return null;
    }
}
