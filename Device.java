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

import osp.IFLModules.*;
import osp.Threads.*;
import osp.Utilities.*;
import osp.Hardware.*;
import osp.Memory.*;
import osp.FileSys.*;
import osp.Tasks.*;

/**
 * This class stores all pertinent information about a device in the device
 * table. This class should be sub-classed by all device classes, such as the
 * Disk class.
 * 
 * @OSPProject Devices
 */
public class Device extends IflDevice {

    /**
     * This constructor initializes a device with the provided parameters. As a
     * first statement it must have the following:
     * 
     * super(id, numberOfBlocks);
     * 
     * @param numberOfBlocks -- number of blocks on device
     * 
     * @OSPProject Devices
     */
    public Device(int id, int numberOfBlocks) {
        super(id, numberOfBlocks);

        /** Set DeviceQueue to my queue class */
        this.iorbQueue = new DeviceQueue();
    }

    /**
     * This method is called once at the beginning of the simulation. Can be used to
     * initialize static variables.
     * 
     * @OSPProject Devices
     */
    public static void init() {
        return;
    }

    /**
     * Enqueues the IORB to the IORB queue for this device according to some kind of
     * scheduling algorithm.
     * 
     * This method must lock the page (which may trigger a page fault), check the
     * device's state and call startIO() if the device is idle, otherwise append the
     * IORB to the IORB queue.
     * 
     * @return SUCCESS or FAILURE. FAILURE is returned if the IORB wasn't enqueued
     *         (for instance, locking the page fails or thread is killed). SUCCESS
     *         is returned if the IORB is fine and either the page was valid and
     *         device started on the IORB immediately or the IORB was successfully
     *         enqueued (possibly after causing pagefault pagefault)
     * 
     * @OSPProject Devices
     */
    public int do_enqueueIORB(IORB iorb) {
        /**
         * Initially lock page
         */
        PageTableEntry iorbPage = iorb.getPage();
        iorbPage.lock(iorb);

        /**
         * Second increment open page count
         */
        OpenFile iorbFile = iorb.getOpenFile();
        iorbFile.incrementIORBCount();

        /**
         * Third, set iorb cylinder
         */
        int cylinderPos = this.calculate_cylinder_pos(iorb);
        iorb.setCylinder(cylinderPos);

        /**
         * Check status of thread that requested iorb
         */
        ThreadCB requesterThread = iorb.getThread();

        /**
         * If thread is not ThreadKill then enqueue, if is, return failure
         */
        if (requesterThread.getStatus() != ThreadCB.ThreadKill) {

            /**
             * Check that the device is idle
             */
            if (this.isBusy() == false) {

                /** Not busy, so start io on current device */
                this.startIO(iorb);
            } else {

                /**
                 * Is busy, so put iorb on the queue. Must cast to my implementation of queue.
                 */
                ((DeviceQueue) this.iorbQueue).add(iorb, QUEUE.WAITING);
            }

            return SUCCESS;
        }

        /** Return false mainly b/c thread has status of ThreadKill */
        return FAILURE;
    }

    /**
     * Selects an IORB (according to some scheduling strategy) and dequeues it from
     * the IORB queue.
     * 
     * @OSPProject Devices
     */
    public IORB do_dequeueIORB() {
        if (((DeviceQueue) this.iorbQueue).isEmpty() == false) {
            return ((DeviceQueue) this.iorbQueue).remove();
        }

        /**
         * Device running queue is empty, so return null.
         * 
         * But before doing so, swap the running queue with the waiting queue.
         */
        ((DeviceQueue) this.iorbQueue).swap_queues();

        if (((DeviceQueue) this.iorbQueue).isEmpty() == false) {
            return ((DeviceQueue) this.iorbQueue).remove();
        }

        return null;
    }

    /**
     * Remove all IORBs that belong to the given ThreadCB from this device's IORB
     * queue
     * 
     * The method is called when the thread dies and the I/O operations it requested
     * are no longer necessary. The memory page used by the IORB must be unlocked
     * and the IORB count for the IORB's file must be decremented.
     * 
     * @param thread thread whose I/O is being canceled
     * 
     * @OSPProject Devices
     */
    public void do_cancelPendingIO(ThreadCB thread) {

        /**
         * Iterate through the objects in the queue and remove those that match the
         * argument thread.
         */
        // Arrays.stream(QUEUE.values()).forEach(queueType -> {
        for (IORB iorb : ((DeviceQueue) this.iorbQueue).get_queue(QUEUE.WAITING)) {
            ThreadCB iorbThread = iorb.getThread();

            if (iorbThread.equals(thread)) {
                PageTableEntry iorbPage = iorb.getPage();
                OpenFile iorbFile = iorb.getOpenFile();

                /** Unlock page */
                iorbPage.unlock();

                /** Decrement open io */
                iorbFile.decrementIORBCount();

                /** Check and close the file */
                if (iorbFile.closePending == true && iorbFile.getIORBCount() == 0) {
                    iorbFile.close();
                }

                /**
                 * Finally, remove the iorb from the queue.
                 * 
                 * removedIorb should be the same as iorb
                 */
                ((DeviceQueue) this.iorbQueue).remove(iorb, QUEUE.WAITING);
            }
        }
        // });

        return;
    }

    /**
     * Calculate the current cylinder position using the number of blocks in a
     * block.
     * 
     * @return
     */
    public int calculate_cylinder_pos(IORB iorb) {
        int blocksInTrack = this.calculate_blocks_in_track();

        return iorb.getBlockNumber() / (blocksInTrack * ((Disk) this).getPlatters());
    }

    /**
     * Calculates the number of blocks in a track.
     * 
     * "first need to compute the number of blocks in a track." -> "number of
     * sectors"
     * 
     * @return
     */
    private int calculate_blocks_in_track() {
        int numInBlock = (int) Math.floor(Math.pow(2, MMU.getVirtualAddressBits() - MMU.getPageAddressBits()));
        int numSectorsInBlock = (int) Math.floor(numInBlock / ((Disk) this).getBytesPerSector());

        return (int) Math.floor(((Disk) this).getSectorsPerTrack() / numSectorsInBlock);
    }

    /**
     * Called by OSP after printing an error message. The student can insert code
     * here to print various tables and data structures in their state just after
     * the error happened. The body can be left empty, if this feature is not used.
     * 
     * @OSPProject Devices
     */
    public static void atError() {
        return;
    }

    /**
     * Called by OSP after printing a warning message. The student can insert code
     * here to print various tables and data structures in their state just after
     * the warning happened. The body can be left empty, if this feature is not
     * used.
     * 
     * @OSPProject Devices
     */
    public static void atWarning() {
        return;
    }
}
