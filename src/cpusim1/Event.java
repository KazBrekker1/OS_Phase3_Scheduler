package cpusim1;

public class Event {
    private double time;
    private Task task;
    private int type = 0; // 0 => arrival, 1 => departure

    public Event() {
    }

    public Event(double time, Task task, int type) {
        this.time = time;
        this.task = task;
        this.type = type;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double d) {
        this.time = d;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
