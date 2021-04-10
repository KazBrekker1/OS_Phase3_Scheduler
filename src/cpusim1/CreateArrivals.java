package cpusim1;

import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CreateArrivals {

    public CreateArrivals(PriorityQueue<Event> eventCalendar, int size, double rtPercentage) {
        // Add the Task to the CPU with the least tasks (Node balancing)
        Task task;
        Event event;
        Random rPrio = new Random(141); // This is set according to specification
        int rtAmount = (int) (size * rtPercentage);
//        For Testing
        Task[] testTasks = {
                new Task(1, 3, 10, 30, 0, 0, 128),
                new Task(2, 4, 20, 40, 0, 0, 128),
                new Task(3, 2, 35, 30, 0, 0, 128),
                new Task(4, 5, 45, 10, 0, 0, 80),
                new Task(5, 1, 50, 50, 0, 0, 90),
                new Task(6, 6, 20, 70, 0, 0, 130),
//                new Task(7, 7, 35, 60, 0, 0, 150),
//                new Task(8, 5, 45, 110, 0, 0, 18)
        };

        for (int i = 0; i < size; i++) {
            task = new Task(i, 0, 0, 0, 0, 0, ThreadLocalRandom.current().nextInt(1, 200 + 1));
//            For Testing
//            task = testTasks[i];

            if (i < rtAmount) {
                task.setPrio(rPrio.nextInt(100));
                task.setSchedPolicy(rPrio.nextDouble() >= 0.5 ? 1 : 2);
                task.setSt(100);
            } else {
                task.setSchedPolicy(0);
                task.setNiceValue(20 - ThreadLocalRandom.current().nextInt(-19, 20 + 1)); // -19 => 20
                task.setPrio(100 + task.getNiceValue());
                task.setSt(10); // Setting Slice Time
            }

            task.setAt((ThreadLocalRandom.current().nextDouble(10, 50 + 1)));
            task.setBt((ThreadLocalRandom.current().nextDouble(30, 150 + 1)));

            event = new Event();
            event.setTask(task);
            event.setTime((task.getAt()));
            eventCalendar.add(event);
            System.out.println(task);
        }
        System.out.println("===================================================");
    }

}
