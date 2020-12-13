import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Assn5 {

    public static void main(String[] args) {

        final int simulations = 1000;
        final int sequenceLength = 1000;
        final int maxMemoryFrames = 100;

        int numberCores = Runtime.getRuntime().availableProcessors();

        ExecutorService threadPool = Executors.newFixedThreadPool(numberCores);
        long start = System.currentTimeMillis();

        int[][] fifoFaults = new int[simulations][maxMemoryFrames];
        int[][] lruFaults = new int[simulations][maxMemoryFrames];
        int[][] mruFaults = new int[simulations][maxMemoryFrames];

        int[] sequence = new int[sequenceLength];
        Random rand = new Random();
        for (int j = 0; j < sequenceLength; j++) {
            sequence[j] = rand.nextInt(250)+1;
        }

        for (int i = 0; i < simulations; i++) {
            for (int j = 1; j < maxMemoryFrames+1; j++) {
                Runnable fifo = new TaskFIFO(sequence, j, 250, fifoFaults[i]);
                Runnable lru = new TaskLRU(sequence, j, 250, lruFaults[i]);
                Runnable mru = new TaskMRU(sequence, j, 250, mruFaults[i]);
                threadPool.execute(fifo);
                threadPool.execute(lru);
                threadPool.execute(mru);

            }

        }
        threadPool.shutdown();
        while(!threadPool.isTerminated()) {

        }
        long end = System.currentTimeMillis();
        long time = end-start;
        System.out.println("Simulation took " + time + " ms");
        System.out.println(" ");
        int minFifo = -1;

        for (int i = 0; i < fifoFaults.length; i++) {
            for (int j = 0; j < fifoFaults[i].length; j++) {
                if((fifoFaults[i][j] < minFifo) || minFifo == -1) {
                    minFifo = fifoFaults[i][j];
                }
            }
        }

        System.out.println("FIFO min PF: " + minFifo);

        int minLRU = -1;
        for (int i = 0; i < lruFaults.length; i++) {
            for (int j = 0; j < lruFaults[i].length; j++) {
                if((lruFaults[i][j] < minLRU) || minLRU == -1) {
                    minLRU = lruFaults[i][j];
                }
            }
        }
        System.out.println("LRU min PF: " + minLRU);
        int minMRU = -1;
        for (int i = 0; i < mruFaults.length; i++) {
            for (int j = 0; j < mruFaults[i].length; j++) {
                if((mruFaults[i][j] < minMRU) || minMRU == -1) {
                    minMRU = mruFaults[i][j];
                }
            }
        }
        System.out.println("MRU min PF: " + minMRU);

        System.out.println(" ");

        int fifoAnomolies = 0;
        int fifoDeltaTotal = 0;

        System.out.println("Belady's Anomaly Report for FIFO");
        for (int i = 0; i < fifoFaults.length; i++) {
            int prevFifo = -1;
            int currFifo = -1;
            for (int j = 0; j < fifoFaults[i].length; j++) {
                if(prevFifo == -1 && currFifo == -1) {
                    currFifo = fifoFaults[i][j];
                }
                else {
                    prevFifo = currFifo;
                    currFifo = fifoFaults[i][j];
                    if(prevFifo < currFifo) {
                        System.out.println("Anomaly detected in simulation #" + i + " - " + prevFifo + "PF's @ " + j + "frames vs. " + currFifo + "PF's @ " + (j-1) + "frames (Δ " + (currFifo-prevFifo) + ")");
                        fifoAnomolies++;
                        fifoDeltaTotal = fifoDeltaTotal + (currFifo-prevFifo);
                    }
                }
            }
        }

        int fifoDeltaAverage = 0;
        if(fifoAnomolies != 0) {
            fifoDeltaAverage = fifoDeltaTotal / fifoAnomolies;
        }
        System.out.println("Anomaly detected " + fifoAnomolies + " times in " + simulations + " simulations with a max delta of " + fifoDeltaAverage);

        int lruAnomolies = 0;
        int lruDeltaTotal = 0;

        System.out.println(" ");

        System.out.println("Belady's Anomaly Report for LRU");
        for (int i = 0; i < lruFaults.length; i++) {
            int prevlru = -1;
            int currlru = -1;
            for (int j = 0; j < lruFaults[i].length; j++) {
                if(prevlru == -1 && currlru == -1) {
                    currlru = lruFaults[i][j];
                }
                else {
                    prevlru = currlru;
                    currlru = lruFaults[i][j];
                    if(prevlru < currlru) {
                        System.out.println("Anomaly detected in simulation #" + i + " - " + prevlru + "PF's @ " + "frames vs. " + currlru + "PF's @ " + "frames (Δ " + (currlru-prevlru) + ")");
                        lruAnomolies++;
                        lruDeltaTotal = lruDeltaTotal + (currlru-prevlru);
                    }
                }
            }
        }

        int lruDeltaAverage = 0;
        if(lruAnomolies != 0) {
            lruDeltaAverage = lruDeltaTotal / lruAnomolies;
        }
        System.out.println("Anomaly detected " + lruAnomolies + " times in " + simulations + " simulations with a max delta of " + lruDeltaAverage);

        int mruAnomolies = 0;
        int mruDeltaTotal = 0;

        System.out.println(" ");

        System.out.println("Belady's Anomaly Report for MRU");
        for (int i = 0; i < mruFaults.length; i++) {
            int prevmru = -1;
            int currmru = -1;
            for (int j = 0; j < mruFaults[i].length; j++) {
                if(prevmru == -1 && currmru == -1) {
                    currmru = mruFaults[i][j];
                }
                else {
                    prevmru = currmru;
                    currmru = mruFaults[i][j];
                    if(prevmru < currmru) {
                        System.out.println("Anomaly detected in simulation #" + i + " - " + prevmru + "PF's @ " + j + " frames vs. " + currmru + "PF's @ " + (j-1) + " frames (Δ " + (currmru-prevmru) + ")");
                        mruAnomolies++;
                        mruDeltaTotal = mruDeltaTotal + (currmru-prevmru);
                    }
                }
            }
        }
        int mruDeltaAverage = 0;
        if(mruAnomolies != 0) {
            mruDeltaAverage = mruDeltaTotal / mruAnomolies;
        }
        System.out.println("Anomaly detected " + mruAnomolies + " times in " + simulations + " simulations with a max delta of " + mruDeltaAverage);


    }
}
