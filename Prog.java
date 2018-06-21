import java.io.*;
import java.util.*;
public class Prog {

	public static int numProcess;
	public static int numCompleted;
	public static boolean terminate = false;
	public static long TCPU;
	public static long TCPUBusy;
	public static long Tat= 0;
	public static long Wt= 0;
	public static long Rt = 0;
	

	 public static void  printPerformance(String file_name,String alg,int quantum,long start){
                
		System.out.println("Input File Name\t\t\t: " + file_name);
                if (alg.equalsIgnoreCase("RR")) {
                        System.out.println("CPU Scheduling Alg\t\t: " + alg + " ("+ quantum + ")");
                } else {
                        System.out.println("CPU Scheduling Alg\t\t: " + alg);
                }
                System.out.println("CPU utilization\t\t\t: "+ (((double) TCPUBusy / (double) TCPU) * 100));
                System.out.println("Throughput\t\t\t: "+ ((double) numProcess / (double) TCPU));
                System.out.println("Turnaround time\t\t\t: "+ ((double) Tat/ (double) numProcess));
                System.out.println("Waiting time\t\t\t: "+ ((double) Wt/ (double) numProcess));
                System.out.println("Response time\t\t\t: "+ ((double) Rt / (double) numProcess));
                long end = System.currentTimeMillis();
                System.out.println("\nTotal Execution time is "+ ((end - start) / 1000d) + " seconds\n");
        }


	public static void main(String[] args) throws IOException,InterruptedException {
		int i = 0;
		String arg;
		String alg = "SJF";
		int quantum = 8;
		String file_name = "input.txt";

		while(args.length>i){
                        switch(args[i]){
                        case "-alg":
                                alg = args[++i];
                               // System.out.println("print"+alg);
                                break;
                        case "-quantum":
                                quantum = Integer.parseInt(args[++i]);
                               // System.out.println("print"+quantam);
                                break;
                        case "-input":
                                file_name = args[++i];
                               // System.out.println("print"+file_name);
                                break;
                        }
                        ++i;
                }

		LinkedList<Process> readyQueue = new LinkedList<Process>();
		LinkedList<Process> queue = new LinkedList<Process>();
		ReadFile r = new ReadFile(readyQueue,file_name);
		Thread fr = new Thread(r);
		Thread cpuT = new Thread(new CPUScheduler(readyQueue,queue,alg, quantum));
		Thread ioT = new Thread(new IOScheduler(readyQueue,queue,alg, quantum));
		long start = System.currentTimeMillis();
		fr.start();
		cpuT.start();
		ioT.start();
	//	showThreadStatus(cpuT);
	//	showThreadStatus(ioT);
		try{
		fr.join();
		cpuT.join();
		ioT.join();
		}
		 catch (InterruptedException e) {
                        e.printStackTrace();
                }

	//	 showThreadStatus(cpuT);
          //      showThreadStatus(ioT);

		r.setTimes(start);
		printPerformance(file_name,alg,quantum,start);	
		


}
	/*static void showThreadStatus(Thread thrd) {
      System.out.println(thrd.getName()+" Alive:"+thrd.isAlive()+" State:" + thrd.getState() );
   }*/
}
