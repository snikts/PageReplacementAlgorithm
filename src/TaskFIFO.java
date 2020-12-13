import java.util.LinkedList;
import java.util.Queue;

public class TaskFIFO implements Runnable {

    int[] m_sequence;
    int maxMem;
    int maxPageRef;
    int[] faults;
    int numFaults = 0;
    Queue currMem = new LinkedList<Integer>();

    public TaskFIFO(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        m_sequence = sequence;
        maxMem= maxMemoryFrames;
        maxPageRef=maxPageReference;
        faults=pageFaults;
    }
    public void run() {
        for (int i = 0; i < m_sequence.length; i++) {
            if(currMem.size() < maxMem) {
                if(!currMem.contains(m_sequence[i])) {
                    currMem.add(m_sequence[i]);
                    numFaults = numFaults+1;
                }
            }
            else {
                if(!currMem.contains(m_sequence[i])) {
                    currMem.remove();
                    currMem.add(m_sequence[i]);
                    numFaults = numFaults+1;
                }
            }
        }
        faults[maxMem-1] = numFaults;
    }

}
