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

import java.util.*;

import osp.Utilities.*;

public class DeviceQueue implements GenericQueueInterface {

    Map<QUEUE, List<IORB>> deviceQueue;

    public DeviceQueue() {
        this.deviceQueue = new HashMap<>();

        this.deviceQueue.put(QUEUE.RUNNING, new ArrayList<IORB>());
        this.deviceQueue.put(QUEUE.WAITING, new ArrayList<IORB>());
    }

    /**
     * Required - return length/size of both queues.
     * 
     * @see Checks both queues
     */
    @Override
    public final int length() {
        return this.deviceQueue.get(QUEUE.RUNNING).size() + this.deviceQueue.get(QUEUE.WAITING).size();
    }

    /**
     * Required - return whether both queues are empty
     * 
     * @see Checks both queues
     */
    @Override
    public final boolean isEmpty() {
        return this.deviceQueue.get(QUEUE.RUNNING).isEmpty() && this.deviceQueue.get(QUEUE.WAITING).isEmpty();
    }

    /**
     * Required - return whether either queue contains specified iorb.
     * 
     * @see Checks both queues
     */
    @Override
    public final synchronized boolean contains(Object obj) {
        for (IORB iorb : this.deviceQueue.get(QUEUE.RUNNING)) {
            if (iorb.getID() == ((IORB) obj).getID()) {
                return true;
            }
        }

        for (IORB iorb : this.deviceQueue.get(QUEUE.WAITING)) {
            if (iorb.getID() == ((IORB) obj).getID()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Object to add to the specified queue type
     * 
     * @see Device.do_enqueueIORB()
     */
    public synchronized void add(IORB iorb, QUEUE type) {
        if (type == QUEUE.RUNNING) {
            this.deviceQueue.get(QUEUE.RUNNING).add(iorb);
        } else if (type == QUEUE.WAITING) {
            this.deviceQueue.get(QUEUE.WAITING).add(iorb);
        }

        return;
    }

    /**
     * Remove the specified object from the specified thread.
     */
    public synchronized boolean remove(IORB iorb, QUEUE type) {
        if (type == QUEUE.RUNNING) {
            if (this.deviceQueue.get(QUEUE.RUNNING).contains(iorb)) {
                return this.deviceQueue.get(QUEUE.RUNNING).remove(iorb);
            }

        } else if (type == QUEUE.WAITING) {
            if (this.deviceQueue.get(QUEUE.WAITING).contains(iorb)) {
                return this.deviceQueue.get(QUEUE.WAITING).remove(iorb);
            }
        }

        return false;
    }

    /**
     * Removes an element
     */
    public synchronized IORB remove() {
        /**
         * Initially, checks the running queue whether there are any elemts to remove
         */
        if (this.deviceQueue.get(QUEUE.RUNNING).size() > 1) {
            return (IORB) this.deviceQueue.get(QUEUE.RUNNING).remove(0);
        } else if (this.deviceQueue.get(QUEUE.RUNNING).size() == 1) {
            IORB last = (IORB) this.deviceQueue.get(QUEUE.RUNNING).remove(0);

            /**
             * Just removed the last element in a queue (its size was 1). Thus swap the
             * queues out s.t. the waiting queue is now the running queue.
             */
            this.swap_queues();

            return last;
        }

        /**
         * No elements found are removable from the running queue - so if the waiting
         * queue has any elements we can remove.
         */
        if (this.deviceQueue.get(QUEUE.WAITING).size() > 1) {
            return (IORB) this.deviceQueue.get(QUEUE.WAITING).remove(0);
        } else if (this.deviceQueue.get(QUEUE.WAITING).size() == 1) {
            IORB last = (IORB) this.deviceQueue.get(QUEUE.WAITING).remove(0);

            return last;
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
        List runningQueue = new ArrayList<IORB>(); // this.deviceQueue.get(QUEUE.RUNNING);
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

    /**
     * Remove all objects from either queue
     */
    public synchronized void remove_all(List<IORB> itemsToRemove) {
        this.deviceQueue.get(QUEUE.RUNNING).removeAll(itemsToRemove);
        this.deviceQueue.get(QUEUE.WAITING).removeAll(itemsToRemove);
    }
}
