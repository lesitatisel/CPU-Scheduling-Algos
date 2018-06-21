import java.io.*;
import java.util.*;

public class ReadFile implements Runnable {
	
    String fileName;
    private LinkedList<Process> readyQueue;
    public static ArrayList<Process> procList = null;

    ReadFile(LinkedList<Process> readyQueue,String fileName) {
        this.readyQueue = readyQueue;
        this.fileName = fileName;
    }
	
    @Override
	public void run() {
        try {
	    BufferedReader br = null;
            FileReader fr = null;
            procList = new ArrayList<Process>();
           int i = 1;
           try {

		 fr = new FileReader(fileName);
                 br = new BufferedReader(fr);
		
               String line;
               while ((line = br.readLine()) != null) {
                   String[] s = line.split("\\s");
		    String inst = s[0];             
                   if (inst.equalsIgnoreCase("proc")) {
		       int index =2;
		       String processId = "P" +i;
                       int priority = Integer.parseInt(s[1]);
                       ArrayList<Integer> cpuIoBurstTime= new ArrayList<Integer>();
                       
			  while(index < s.length){
                          cpuIoBurstTime.add(Integer.parseInt(s[index]));
                          index++;
                           }

                       long Wt=  System.currentTimeMillis();
		       long arrivalTime = System.currentTimeMillis();
                       long firstTime = 0;
                       long lastTime = 0;
                       long totalWaitingTime = 0;
                       boolean flag = false;
		       Process p = new	Process(processId,priority,cpuIoBurstTime,Wt,arrivalTime,firstTime,lastTime,totalWaitingTime,flag);		
                       readyQueue.add(p);
		       
                     procList.add(p);

                       synchronized (readyQueue) {
                       	readyQueue.notifyAll();
   					}
                       Prog.numProcess++;
			i++;
                   }
                   if (inst.equalsIgnoreCase("sleep")) {
                       Thread.sleep(Integer.parseInt(s[1]));
                   }
                   if (inst.equalsIgnoreCase("stop")) {
                   	Prog.terminate = true;
                      break;
                   }
               }
           }catch (Exception ex) {
                ex.printStackTrace();
        }

		
        } catch (Exception ex) {
		ex.printStackTrace();
	}
	}


	void setTimes(long start){
			
			for (Process proc :procList) {
                        long arrival_time = proc.arrivalTime - start;
                        Prog.Tat+= (proc.lastTime - start) - arrival_time;
                        Prog.Wt+= proc.totalWaitingTime;
                        Prog.Rt += (proc.firstTime - start) - arrival_time;
                }

		
		}



	
}
