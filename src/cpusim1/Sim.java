package cpusim1;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Sim {
    RAM ram;
    public static FileWriter fileWriter;

    static {
        try {
            fileWriter = new FileWriter("Reports.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final PriorityQueue<Event> eventCalendar = new PriorityQueue<Event>(10, new EventComparator());
    private double clock = 0;
    private double timeStart;
    private double timeEnd = 0;
    private final int taskAmount;
    private final CPU[] cpuArray;
    private double responseTimes = 0;

    public Sim(int cpuNu, int taskAmount, int RamSize) throws IOException {
        new CreateArrivals(eventCalendar, taskAmount, 0.5);
        timeStart = eventCalendar.peek().getTask().getAt();
        this.ram = new RAM(RamSize, Page.size);
        this.cpuArray = new CPU[cpuNu];
        this.taskAmount = taskAmount;
        fileWriter.write("\n==================== Specs ====================\n");
        fileWriter.write(String.format("\nNumber of CPUS: %d, Ram Size: %d, Pages Size: %d, Amount of Tasks: %d\n", cpuNu, RamSize, Page.size, taskAmount));
        for (int i = 0; i < cpuNu; i++) {
            cpuArray[i] = new CPU();
        }
        startSim();
    }

    private void startSim() throws IOException {
        Event event;
        Task task;
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        fileWriter.write("\n==================== Trace ====================\n");
        while (!eventCalendar.isEmpty()) {
            event = eventCalendar.poll();
            clock = Double.parseDouble(numberFormat.format(event.getTime()));
            if (event.getType() == 1) {
                int taskCPU = event.getTask().getCpuId();

                Task currentTask = event.getTask();
                ram.allocatePage(currentTask, ThreadLocalRandom.current().nextInt(0, currentTask.getPagesList().size()), clock);

                currentTask.setServiceTime(clock - currentTask.getServiceTime());
                currentTask.setBt(currentTask.getBt() - currentTask.getServiceTime());
                if (currentTask.getBt() > 0 && currentTask.getSchedPolicy() != 2) {
                    currentTask.setSt(currentTask.getSt() - currentTask.getServiceTime());
                    if (currentTask.getSchedPolicy() == 0) {
                        if (currentTask.getSt() > 0 && currentTask.getSt() < 10) {
                            cpuArray[taskCPU].getQueueA().add(currentTask);
                        } else {
                            currentTask.setSt(10);
                            cpuArray[taskCPU].getQueueE().add(currentTask);
                        }
                    } else if (currentTask.getSchedPolicy() == 1) {
                        if (currentTask.getSt() == 0) {
                            currentTask.setSt(100);
                        }
                        cpuArray[taskCPU].getQueueA().add(currentTask);
                    }
                } else if (currentTask.getBt() == 0) {
                    timeEnd = clock;
                }
                cpuArray[taskCPU].setIdel(true);
                cpuArray[taskCPU].setEndBusy(clock);
                cpuArray[taskCPU].setStartIDEL(clock);
                cpuArray[taskCPU].setCurrentServed(null);
                cpuArray[taskCPU].setTotalBusy(cpuArray[taskCPU].getTotalBusy() +
                        (cpuArray[taskCPU].getEndBusy() - cpuArray[taskCPU].getStartBusy()));
                fileWriter.write("\n * Clock: " + clock + ": Departure " + currentTask.toString());
                System.out.println("\n * Clock: " + clock + ": Departure " + currentTask.toString());
            } else if (event.getType() == 0) {

                // This is Dispatching => We can use other types of load balancing,
                // (Whenever a Queue is too full we can do different load balancing)
                CPU minCPU = cpuArray[0];
                for (CPU cpu : cpuArray) {
                    if (cpu.getQueueA().size() < minCPU.getQueueA().size()) {
                        minCPU = cpu;
                    }
                }
                minCPU.getQueueA().add(event.getTask());
                event.getTask().setCpuId(minCPU.getId());
                fileWriter.write("\n * Clock: " + clock + ": Arrival " + event.getTask().toString());
                System.out.println("\n * Clock: " + clock + ": Arrival " + event.getTask().toString());
            }
            if (eventCalendar.peek() != null && eventCalendar.peek().getTime() == clock) {
                continue;
            }
            for (CPU cpu : cpuArray) {
                if (cpu.getQueueA().isEmpty() && !cpu.getQueueE().isEmpty() && cpu.getCurrentServed() == null) {

                    fileWriter.write("\n * Clock: " + clock + ": Queues Swapped in CPU: " + cpu.getId());

                    System.out.println("\n * Clock: " + clock + ": Queues Swapped in CPU: " + cpu.getId());
                    cpu.swapQueues();
                }
                if (cpu.isIdel() && !cpu.getQueueA().isEmpty()) {
                    cpu.setIdel(false);
                    cpu.setStartBusy(clock);
                    cpu.setEndIDEL(clock);
                    cpu.setTotalIDEL(cpu.getTotalIDEL() + (cpu.getEndIDEL() - cpu.getStartIDEL()));
                    task = cpu.getQueueA().poll();
                    if (task != null) {
                        switch (task.getSchedPolicy()) {
                            case 0:
                            case 1:
                                With_ST(task, event);
                                break;
                            case 2:
                                FIFO_Sched(task, event);
                                break;
                            default:
                                System.out.println("default");
                        }
                        cpu.setCurrentServed(task);
                        if (task.getServiceTime() == 0) {
                            task.setResponseTime(clock - task.getAt());
                            this.responseTimes += task.getResponseTime();
                        }
                        task.setServiceTime(clock);
                        fileWriter.write("\n * Clock: " + clock + ": Service " + task.toString());
                        System.out.println("\n * Clock: " + clock + ": Service " + task.toString());
                    }
                } else if (!cpu.getQueueA().isEmpty() && cpu.getQueueA().peek() != null &&
                        cpu.getQueueA().peek().getPrio() > cpu.getCurrentServed().getPrio()) {

                    Task newTask = cpu.getQueueA().poll();
                    Task oldTask = cpu.getCurrentServed();

                    oldTask.setServiceTime(clock - oldTask.getServiceTime());
                    oldTask.setBt(oldTask.getBt() - oldTask.getServiceTime());
                    if (oldTask.getSchedPolicy() != 2)
                        oldTask.setSt(oldTask.getSt() - oldTask.getServiceTime());

                    // Remove Old Event
                    eventCalendar.removeIf(e -> e.getTask() == oldTask);
                    cpu.getQueueA().add(oldTask);

                    switch (newTask.getSchedPolicy()) {
                        case 0:
                        case 1:
                            With_ST(newTask, event);
                            break;
                        case 2:
                            FIFO_Sched(newTask, event);
                            break;
                        default:
                            System.out.println("default");
                    }
                    cpu.setCurrentServed(newTask);
                    if (newTask.getServiceTime() == 0) {
                        newTask.setResponseTime(clock - newTask.getAt());
                        this.responseTimes += newTask.getResponseTime();
                    }
                    newTask.setServiceTime(clock);
                    fileWriter.write("\n * Clock: " + clock + ": Service Swap " + newTask.toString());
                    System.out.println("\n * Clock: " + clock + ": Service Swap " + newTask.toString());
                }
            }
        }
        fileWriter.write("\n==================== End of Trace ====================\n");
        fileWriter.write(String.format("\n==================== Measurements ====================\n" + "AVG response Time: %.2f\n", this.responseTimes / this.taskAmount));
        System.out.printf("==================== Measurements ====================\n" +
                "AVG response Time: %.2f\n", this.responseTimes / this.taskAmount);
        fileWriter.write(String.format("Page Fault Counter: %d\n", RAM.getPageFaultCounter()));
        fileWriter.write(String.format("Page Fault Counter For NRT: %d\n", RAM.getPageFaultCounterNRT()));
        fileWriter.write(String.format("Page Fault Counter For RT: %d\n", RAM.getPageFaultCounterRT()));
        System.out.printf("Page Fault Counter: %d\n", RAM.getPageFaultCounter());
        System.out.printf("Page Fault Counter For NRT: %d\n", RAM.getPageFaultCounterNRT());
        System.out.printf("Page Fault Counter For RT: %d\n", RAM.getPageFaultCounterRT());
        for (CPU cpu : cpuArray) {
            fileWriter.write(String.format("CPU: %d, IDEL Time: %.2f, Busy Time: %.2f, CPU Utilization: %.2f%s \n", cpu.getId(),
                    cpu.getTotalIDEL(), cpu.getTotalBusy(),
                    cpu.getTotalBusy() / Double.sum(cpu.getTotalIDEL(), cpu.getTotalBusy()) * 100, "%"));
            System.out.printf("CPU: %d, IDEL Time: %.2f, Busy Time: %.2f, CPU Utilization: %.2f%s \n", cpu.getId(),
                    cpu.getTotalIDEL(), cpu.getTotalBusy(),
                    cpu.getTotalBusy() / Double.sum(cpu.getTotalIDEL(), cpu.getTotalBusy()) * 100, "%");
        }
        fileWriter.write(String.format("The Through Put: %.2f\n", taskAmount / (timeEnd - timeStart)));
        System.out.printf("The Through Put: %.2f\n", taskAmount / (timeEnd - timeStart));
        fileWriter.close();
    }

    void With_ST(Task task, Event event) {
        event.setType(1);
        event.setTask(task);
        event.setTime(Math.min(task.getBt(), task.getSt()) + clock);
        eventCalendar.add(event);
    }

    void FIFO_Sched(Task task, Event event) {
        event.setType(1);
        event.setTask(task);
        event.setTime(task.getBt() + clock);
        eventCalendar.add(event);
    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter the Number of CPUs <= 32 & a Power of 2: ");
        int cpuNu = in.nextInt();
        System.out.print("Enter the Number of Tasks : ");
        int taskAmount = in.nextInt();
        System.out.print("Enter the Size of The RAM >= 32 & a Power of 2: ");
        int RamSize = in.nextInt();
        in.close();
        new Sim(cpuNu, taskAmount, RamSize);
    }

}
