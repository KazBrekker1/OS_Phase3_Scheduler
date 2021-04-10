package cpusim1;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task t1, Task t2) {
        if (t1.getPrio() < t2.getPrio()) {
            return 1;
        } else if (t1.getPrio() > t2.getPrio()) {
            return -1;
        }
        return 0;
    }

}
