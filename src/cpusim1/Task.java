package cpusim1;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Task {
    private int id;
    private int TaskSize;
    private int prio;
    private double at;
    private double bt;
    private double serviceTime = 0;
    private double st;
    private int cpuId;
    private int schedPolicy;  //0 Other,1 RR,2 FIFO
    private int niceValue = 0;
    private double responseTime = 0;
    private static DecimalFormat numberFormat = new DecimalFormat("#.00");
    private ArrayList<Page> pagesList = new ArrayList<Page>();
    private static ArrayList<Integer> mapList = new ArrayList<Integer>();
    // can be static


    public Task() {
    }

    public Task(int id, int prio, double at, double bt, double st, int cpuId, double TaskSize) {
        this.id = id;
        this.prio = prio;
        this.at = at;
        this.bt = bt;
        // this is set dynamically according to an equation
        this.st = st;
        this.cpuId = cpuId;
        int fullPages = (int) (TaskSize / Page.size);
        this.TaskSize = (int) TaskSize;
        for (int i = 0; i < Math.ceil(TaskSize / Page.size); i++) {
            if (i < fullPages) {
                this.pagesList.add(new Page(0, id, -1));
            } else {
                this.pagesList.add(new Page(Page.size - (int) TaskSize % Page.size, id, -1));
            }
            mapList.add(i, null);
        }
    }

    public int getNiceValue() {
        return niceValue;
    }

    public void setNiceValue(int niceValue) {
        this.niceValue = niceValue;
    }

    public int getSchedPolicy() {
        return schedPolicy;
    }

    public void setSchedPolicy(int schedPolicy) {
        this.schedPolicy = schedPolicy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrio() {
        return prio;
    }

    public void setPrio(int prio) {
        this.prio = prio;
    }

    ArrayList<Integer> getMapList() {
        return mapList;
    }

    public double getAt() {
        return at;
    }

    public void setAt(double at) {
        this.at = Double.parseDouble(numberFormat.format(at));
    }

    public double getBt() {
        return bt;
    }

    public void setBt(double bt) {
        this.bt = Double.parseDouble(numberFormat.format(bt));
    }

    public double getSt() {
        return st;
    }

    public void setSt(double st) {
        this.st = Double.parseDouble(numberFormat.format(st));
    }

    public int getCpuId() {
        return cpuId;
    }

    public void setCpuId(int cpuId) {
        this.cpuId = cpuId;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = Double.parseDouble(numberFormat.format(serviceTime));
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public ArrayList<Page> getPagesList() {
        return this.pagesList;
    }

    @Override
    public String toString() {
        return "Task {" + " ID: " + id + ", Task Size: " + TaskSize + ", Priority: " + prio + ", Arrival Time: " + at + ", Burst Time: " + bt + ", Slice Time: " + st +
                ", Parent CPU ID: " + cpuId + ", Scheduling Policy: " + schedPolicy + ", Nice: " + niceValue + ",\nPages:\n" + pagesList + '}';
    }
}
