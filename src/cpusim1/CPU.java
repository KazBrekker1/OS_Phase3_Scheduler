package cpusim1;

import java.util.PriorityQueue;

public class CPU {
    private boolean idel = true;
    private int id;
    private Task currentServed;
    private PriorityQueue<Task> queueA = new PriorityQueue<Task>(10, new
            TaskComparator());
    private PriorityQueue<Task> queueE = new PriorityQueue<Task>(10, new
            TaskComparator());
    private static int cpuCount = 0;


    private double totalIDEL = 0;
    private double endIDEL = 0;
    private double startIDEL = 0;
    private double totalBusy = 0;
    private double endBusy = 0;
    private double startBusy = 0;

    public CPU() {
        setId(cpuCount++);
    }

    public boolean isIdel() {
        return idel;
    }

    public void setIdel(boolean idel) {
        this.idel = idel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task getCurrentServed() {
        return currentServed;
    }

    public void setCurrentServed(Task currentServed) {
        this.currentServed = currentServed;
    }

    public PriorityQueue<Task> getQueueA() {
        return queueA;
    }

    public void setQueueA(PriorityQueue<Task> queueA) {
        this.queueA = queueA;
    }

    public PriorityQueue<Task> getQueueE() {
        return queueE;
    }

    public void setQueueE(PriorityQueue<Task> queueE) {
        this.queueE = queueE;
    }

    public double getTotalIDEL() {
        return totalIDEL;
    }

    public void setTotalIDEL(double totalIDEL) {
        this.totalIDEL = totalIDEL;
    }

    public double getStartIDEL() {
        return startIDEL;
    }

    public void setStartIDEL(double startIDEL) {
        this.startIDEL = startIDEL;
    }

    public double getEndIDEL() {
        return endIDEL;
    }

    public void setEndIDEL(double endIDEL) {
        this.endIDEL = endIDEL;
    }

    public double getTotalBusy() {
        return totalBusy;
    }

    public void setTotalBusy(double totalBusy) {
        this.totalBusy = totalBusy;
    }

    public double getEndBusy() {
        return endBusy;
    }

    public void setEndBusy(double endBusy) {
        this.endBusy = endBusy;
    }

    public double getStartBusy() {
        return startBusy;
    }

    public void setStartBusy(double startBusy) {
        this.startBusy = startBusy;
    }

    public void swapQueues() {
        PriorityQueue<Task> tempQueue = this.queueA;
        this.queueA = this.queueE;
        this.queueE = tempQueue;
    }
}
