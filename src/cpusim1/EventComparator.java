package cpusim1;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {
    public EventComparator() {

    }

    @Override
    public int compare(Event e1, Event e2) {
        if (e1.getTime() > e2.getTime()) {
            return 1;
        } else if (e1.getTime() < e2.getTime()) {
            return -1;
        }
        return 0;
    }

}
