package cpusim1;

public class Page {
    private int ParentTask, remSize;
    private double timeInserted;
    public static int size = 32;

    public Page(int remSize, int parentTask, double timeInserted) {
        this.remSize = remSize;
        this.ParentTask = parentTask;
        this.timeInserted = timeInserted;
    }

    public int getParentTask() {
        return ParentTask;
    }

    public double getTimeInserted() {
        return timeInserted;
    }

    public void setTimeInserted(double timeInserted) {
        this.timeInserted = timeInserted;
    }

    @Override
    public String toString() {
        return "Page {" + " Owner Task ID: " + ParentTask + ", Remaining Size: " + remSize + ", Time inserted: " + timeInserted + '}' + "\n";
    }
}
