import java.util.ArrayList;
import java.util.Collections;

public class TaskMRU implements Runnable {

    int[] m_sequence;
    int maxMem;
    int maxPageRef;
    int[] faults;
    int numFaults = 0;
    ArrayList currMem = new ArrayList<Integer>();
    ArrayList used = new ArrayList<Integer>();

    public TaskMRU(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
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
                    used.add(1);
                    numFaults=numFaults+1;
                }
                else {
                    used.set(currMem.indexOf(m_sequence[i]), (Integer)used.get(currMem.indexOf(m_sequence[i]))+1);
                }
            }
            else {
                if(currMem.contains(m_sequence[i])) {
                    used.set(currMem.indexOf(m_sequence[i]), (Integer)used.get(currMem.indexOf(m_sequence[i]))+1);
                }
                else {
                    int maxIndex = used.indexOf(Collections.max(used));
                    currMem.set(maxIndex, m_sequence[i]);
                    used.set(maxIndex, 1);
                    numFaults = numFaults+1;
                }
            }
        }
        faults[maxMem-1] = numFaults;
    }

}
