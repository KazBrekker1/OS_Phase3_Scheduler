package cpusim1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RAM {

    private int initSize;
    private static final ArrayList<Page> framesList = new ArrayList<Page>();
    private static int pageFaultCounter = 0;
    private static int pageFaultCounterRT = 0;
    private static int pageFaultCounterNRT = 0;

    public RAM(int initSize, int pageSize) {
        // QueueE Complete the Task's Previously Inserted Pages first
        this.initSize = initSize;
        for (int i = 0; i < initSize / pageSize; i++) {
            framesList.add(new Page(pageSize, -1, -1));
        }
    }

    public static int getPageFaultCounter() {
        return pageFaultCounter;
    }

    public static int getPageFaultCounterRT() {
        return pageFaultCounterRT;
    }

    public static int getPageFaultCounterNRT() {
        return pageFaultCounterNRT;
    }

    public int getInitSize() {
        return initSize;
    }

    public void setInitSize(int initSize) {
        this.initSize = initSize;
    }

    public void allocatePage(Task task, int pageNo, double timeOfInsertion) throws IOException {
        if (task.getMapList().get(pageNo) == null) {
            if (framesList.stream().anyMatch(page -> page.getParentTask() == -1)) {
                for (int i = 0; i < framesList.size(); i++) {
                    if (framesList.get(i).getParentTask() == -1) {
                        task.getMapList().set(pageNo, i);
                        task.getPagesList().get(pageNo).setTimeInserted(timeOfInsertion);
                        framesList.set(i, task.getPagesList().get(pageNo));
                        Sim.fileWriter.write(task.getPagesList().get(pageNo).toString() + " PageNo : "
                                + pageNo + " Got Allocated in " + i);
                        System.out.println(task.getPagesList().get(pageNo).toString() + " PageNo : "
                                + pageNo + " Got Allocated in " + i);
                        break;
                    }
                }
            } else {
                Page lruPage = Collections.min(framesList, Comparator.comparingDouble(Page::getTimeInserted));
                int indexOfLRU = framesList.indexOf(lruPage);
                task.getMapList().set(pageNo, indexOfLRU);
                task.getPagesList().get(pageNo).setTimeInserted(timeOfInsertion);
                framesList.set(indexOfLRU, task.getPagesList().get(pageNo));
                RAM.pageFaultCounter++;
                if (task.getSchedPolicy() == 0) {
                    RAM.pageFaultCounterNRT++;
                } else {
                    RAM.pageFaultCounterRT++;
                }
                Sim.fileWriter.write(task.getPagesList().get(pageNo).toString() + " PageNo : "
                        + pageNo + " Swapped in " + indexOfLRU);
                System.out.println(task.getPagesList().get(pageNo).toString() + " PageNo : "
                        + pageNo + " Swapped in " + indexOfLRU);
            }
        } else {
            task.getPagesList().get(pageNo).setTimeInserted(timeOfInsertion);
            Sim.fileWriter.write(task.getPagesList().get(pageNo).toString() + " In Index: " + pageNo +
                    " Is Already Allocated In " + task.getMapList().get(pageNo));
            System.out.println(task.getPagesList().get(pageNo).toString() + " In Index: " + pageNo +
                    " Is Already Allocated In " + task.getMapList().get(pageNo));
        }
    }
}
