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
import java.util.List;

public class OSPQueue implements GenericQueueInterface {

    List<Object> queue;

    public OSPQueue() {
        queue = new ArrayList<>();
    }

    public final int length() {
        return queue.size();
    }

    public final boolean isEmpty() {
        return queue.isEmpty();
    }

    public final synchronized boolean contains(Object obj) {
        return queue.contains(o);
    }
}
