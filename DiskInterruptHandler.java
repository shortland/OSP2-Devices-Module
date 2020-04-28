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
import osp.Hardware.*;
import osp.Interrupts.*;
import osp.Threads.*;
import osp.Utilities.*;
import osp.Tasks.*;
import osp.Memory.*;
import osp.FileSys.*;

/**
 * The disk interrupt handler. When a disk I/O interrupt occurs, this class is
 * called upon the handle the interrupt.
 * 
 * @OSPProject Devices
 */
public class DiskInterruptHandler extends IflDiskInterruptHandler {

    /**
     * Handles disk interrupts.
     * 
     * This method obtains the interrupt parameters from the interrupt vector. The
     * parameters are IORB that caused the interrupt:
     * (IORB)InterruptVector.getEvent(), and thread that initiated the I/O
     * operation: InterruptVector.getThread(). The IORB object contains references
     * to the memory page and open file object that participated in the I/O.
     * 
     * The method must unlock the page, set its IORB field to null, and decrement
     * the file's IORB count.
     * 
     * The method must set the frame as dirty if it was memory write (but not, if it
     * was a swap-in, check whether the device was SwapDevice)
     * 
     * As the last thing, all threads that were waiting for this event to finish,
     * must be resumed.
     * 
     * @OSPProject Devices
     */
    public void do_handleInterrupt() {

        /**
         * Get the iorb from the interrupt vector
         * 
         * & Get other used properties of the iorb.
         */
        InterruptVector interrupt = this.InterruptVector;
        Event interruptEvent = interrupt.getEvent();
        IORB iorb = (IORB) interruptEvent;
        OpenFile iorbFile = iorb.getOpenFile();

        /** Decrement the iorb count */
        iorbFile.decrementIORBCount();

        /** Close file if iorb count = 0 and close pending set */
        if (iorbFile.closePending == true && iorbFile.getIORBCount() == 0) {
            iorbFile.close();
        }

        /** Unlock the page */
        PageTableEntry iorbPageTable = iorb.getPage();

        iorbPageTable.unlock();

        /** Check that it's alive */
        TaskCB iorbTask = iorb.getThread().getTask();

        if (iorbTask.getStatus() == TaskLive) {
            FrameTableEntry iorbFrameTable = iorbPageTable.getFrame();

            /** Check if iorb was from swap */
            if (iorb.getDeviceID() != SwapDeviceID) {

                /** Set the frame to referenced = true */
                iorbFrameTable.setReferenced(true);

                /** If was type read, then set dirty */
                if (iorb.getIOType() == FileRead) {
                    iorbFrameTable.setDirty(true);
                }
            } else {

                /** Was directed to swap device (& is live), so mark as clean; dirty = false */
                iorbFrameTable.setDirty(false);
            }
        } else {

            /**
             * Task is dead & frame was associated with iorb is reserved by task
             * (getReserved(), unreserved it: setUnreserved())
             */
            if (iorbFrameTable.getReserved() == true) {
                iorbFrameTable.setUnreserved(iorbTask);
            }
        }

        /** Wake up threads waiting on iorb */
        interruptEvent.notifyThreads();

        /** Set the device as not busy */
        Device iorbDevice = Device.get(iorb.getDeviceID);

        iorbDevice.setBusy(false);

        /** Dequeue iorb from Device.dequeueIORB() */
        IORB iorbDequeued = iorbDevice.dequeueIORB();

        /** If not null, startIO() */
        if (iorbDequeued != null) {
            iorbDevice.startIO(iorbDequeued);
        }

        /** Finally, dispatch */
        ThreadCB.dispatch();
    }
}
