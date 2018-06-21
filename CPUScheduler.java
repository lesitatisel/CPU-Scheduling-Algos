import java.util.*;
import java.io.*;
class CPUScheduler implements Runnable {

	private LinkedList<Process> readyQueue;
	private LinkedList<Process> queue;
	private String schedulerType;
	private Integer quantum;

	public CPUScheduler(LinkedList<Process> readyQueue,
			LinkedList<Process> queue, String schedulerType,
			Integer quantum) {
		this.readyQueue = readyQueue;
		this.queue = queue;
		this.schedulerType = schedulerType;
		this.quantum = quantum;
	}

	@Override
	public void run() {
			try {
			switch (schedulerType) {
			case "FIFO":
				Prog.TCPU = System.currentTimeMillis();
				//System.out.println("here at CPU FIFO");
				FIFOcpu();
				Prog.TCPU = System.currentTimeMillis()- Prog.TCPU;
				break;
			case "RR":
				Prog.TCPU = System.currentTimeMillis();
				//System.out.println("here at RR");
				RRcpu();
				Prog.TCPU = System.currentTimeMillis()- Prog.TCPU;
				break;
			case "SJF":
				Prog.TCPU = System.currentTimeMillis();
				//System.out.println("here at SJF");
				SJFcpu();
				Prog.TCPU = System.currentTimeMillis()- Prog.TCPU;
				break;
			case "PR":
				Prog.TCPU = System.currentTimeMillis();
				//System.out.println("here at PR");
				PRcpu();
				Prog.TCPU = System.currentTimeMillis()- Prog.TCPU;
				break;
			default:
				break;
			}
			}catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		


	}
 void FIFOcpu()throws InterruptedException
	{
		Integer cpuBT;
		while(true){
			
			if((Prog.numProcess==Prog.numCompleted )&& Prog.terminate)
			{
				synchronized (queue) {
                                                queue.notifyAll();
                                        }

				break;

			}

			else if ((readyQueue.size())>0)
			{
				long cpu_usage_time = System.currentTimeMillis();			
				cpuBT = readyQueue.peekFirst().get_list_cpu(0);
				//System.out.println("CPU - Process ID\t : " + readyQueue.peekFirst().processId + " and Time\t : " +cpuBT);
		
				if (!readyQueue.peekFirst().flag) {
                                        readyQueue.peekFirst().firstTime = System.currentTimeMillis();
                                        readyQueue.peekFirst().flag = true;
                                }
				readyQueue.peekFirst().totalWaitingTime +=System.currentTimeMillis() -readyQueue.peekFirst().Wt;
				try {
                                        Thread.sleep((long) cpuBT);
                                } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                }
				
				 readyQueue.peekFirst().remove_cpu_burst(0);
				
				 if (readyQueue.peekFirst().cpuIoBurstTime.isEmpty())
				{
					readyQueue.peekFirst().lastTime=System.currentTimeMillis();
					readyQueue.remove();
					Prog.numCompleted++;
				}
				else
				{
					readyQueue.peekFirst().Wt =System.currentTimeMillis();
					queue.add(readyQueue.peekFirst());
					 synchronized (queue) {
                                                queue.notifyAll();
                                        }
                                        readyQueue.remove();
				}
				Prog.TCPUBusy +=System.currentTimeMillis()-cpu_usage_time;
				}
												
			
			else if (readyQueue.isEmpty()) {
                                synchronized (readyQueue) {
                                        readyQueue.wait();
                                }
			} else {
                                continue;
                        }	
		}
	}
		//End of FIFO

		void SJFcpu()throws InterruptedException{
		
		Integer burstTime;
	      	int shortestJob=0;
		int count=0;
	          while(true){
	          	if((Prog.numProcess==Prog.numCompleted) && Prog.terminate){
					synchronized (queue) {
	          					queue.notifyAll();
	  						}
				//System.out.println("I am here at cpu");
			//	throw new InterruptedException("and terimate from CPU");
	      			break;
	      		}
	          	else if((readyQueue.size())>0){
	          		long cpu_usage_time = System.currentTimeMillis();
			//	if(!readyQueue.peekFirst().cpuIoBurstTime.isEmpty())
				//try{
	          		shortestJob = getShortestJobIndex();//}catch(IndexOutOfBoundsException e) { 
						/* synchronized (readyQueue) {
                                        readyQueue.wait();
                                        }*/

						/*synchronized (queue) {
                                                        queue.notifyAll();
                                                        }*///Thread.currentThread().interrupt();throw new InterruptedException("and terimate from CPU");}
	          		for(int i=0;i<shortestJob;i++){
					count=i;
	          		}
	          		burstTime = readyQueue.get(count).get_list_cpu(0);
	          		//System.err.println("CPU - Process ID\t : "+ readyQueue.peekFirst().processId + " and Time\t : "+ burstTime);
	          		//System.out.println("1");
	          		if(!readyQueue.get(count).flag){
	        			readyQueue.get(count).firstTime = System.currentTimeMillis();
	        			readyQueue.get(count).flag = true;
	        		}
	        		readyQueue.get(count).totalWaitingTime += System.currentTimeMillis() - readyQueue.get(count).Wt; 
	          		try {
	          			Thread.sleep((long) burstTime);
	  				} catch (InterruptedException e) {
	  					e.printStackTrace();
	  					break;
	  				}
	          		//System.err.println("2");
	          		readyQueue.get(count).remove_cpu_burst(0);
	          		if(readyQueue.get(count).cpuIoBurstTime.isEmpty()){
	          			readyQueue.get(count).lastTime = System.currentTimeMillis();
	          			readyQueue.remove(count);
	          			Prog.numCompleted++;
	          		}else{
	          			readyQueue.get(count).Wt= System.currentTimeMillis();
	          				queue.add(readyQueue.get(count));
	          				synchronized (queue) {
	          					queue.notifyAll();
	  						}
	          				readyQueue.remove(count);
	          		}
	          		//System.err.println("3");
	          		//readyQueue.clear();
	          		Prog.TCPUBusy += System.currentTimeMillis() - cpu_usage_time;	
	          	}
	          	else if(readyQueue.isEmpty()){
	          		synchronized (readyQueue) {
	          			readyQueue.wait();	
	  				}
	          	}
	          	else{
	          		continue;
	          	}
	          }	
			
		}
		
		synchronized public int getShortestJobIndex(){
	        int position = 0;
	        int count = 0;
	    
		//System.out.println(readyQueue.peekFirst().cpuIoBurstTime.size());	
	        int temp = readyQueue.peekFirst().get_list_cpu(0);
		
		
	        while (count<readyQueue.size()) {
	            if (temp < readyQueue.get(count).get_list_cpu(0)) {
	                position=count;
	                temp = readyQueue.get(count).get_list_cpu(0);
	            }
	  
	            count++;
	        }
	  
	        return position;
	    }
		
		//end of SJF

		void PRcpu()throws InterruptedException{
			Integer burstTime;
                int highestPriorityJob=0;
                int count=0;
                  while(true){
                        if((Prog.numProcess==Prog.numCompleted) && Prog.terminate){
                                        synchronized (queue) {
                                                        queue.notifyAll();
                                                        }
                                //System.out.println("I am here at cpu");
                        //      throw new InterruptedException("and terimate from CPU");
                                break;
                        }
                        else if((readyQueue.size())>0){
                                long cpu_usage_time = System.currentTimeMillis();
                        //      if(!readyQueue.peekFirst().cpuIoBurstTime.isEmpty())
                                //try{
                                highestPriorityJob = getHighestPriorityIndex();//}catch(IndexOutOfBoundsException e) {
                                                /* synchronized (readyQueue) {
                                        readyQueue.wait();
                                        }*/

                                                /*synchronized (queue) {
                                                        queue.notifyAll();
                                                        }*///Thread.currentThread().interrupt();throw new InterruptedException("and terimate from CPU");}
                                for(int i=0;i<highestPriorityJob;i++){
                                        count=i;
                                }
                                burstTime = readyQueue.get(count).get_list_cpu(0);
                                //System.err.println("CPU - Process ID\t : "+ readyQueue.peekFirst().processId + " and Time\t : "+ burstTime);
                                if(!readyQueue.get(count).flag){
                                        readyQueue.get(count).firstTime = System.currentTimeMillis();
                                        readyQueue.get(count).flag = true;
                                }
                                readyQueue.get(count).totalWaitingTime += System.currentTimeMillis() - readyQueue.get(count).Wt;
                                try {
                                        Thread.sleep((long) burstTime);
                                        } catch (InterruptedException e) {
                                                e.printStackTrace();
                                                break;
                                        }
                                readyQueue.get(count).remove_cpu_burst(0);
                                if(readyQueue.get(count).cpuIoBurstTime.isEmpty()){
                                        readyQueue.get(count).lastTime = System.currentTimeMillis();
                                        readyQueue.remove(count);
                                        Prog.numCompleted++;
                                }else{
                                        readyQueue.get(count).Wt= System.currentTimeMillis();
                                                queue.add(readyQueue.get(count));
                                                synchronized (queue) {
                                                        queue.notifyAll();
                                                        }
                                                readyQueue.remove(count);
                                }

                                Prog.TCPUBusy += System.currentTimeMillis() - cpu_usage_time;
                        }
                        else if(readyQueue.isEmpty()){
                                synchronized (readyQueue) {
                                        readyQueue.wait();
                                        }
                        }
                        else{
                                continue;
                        }
                  }


				
		}

	synchronized public int getHighestPriorityIndex() {
                int position = 0;
                int count = 0;
                int temp = 0;
                while (count<readyQueue.size()) {
                        if (temp > readyQueue.get(count).priority) {
                                position = count;
                                temp = readyQueue.get(count).priority;
                        }
                        count++;
                }
                return position;
        }

		
		//end of PR

		void RRcpu()throws InterruptedException{
			
		Integer cpuBT;
                boolean flag;
				
			
	while(true)
                {
                        flag = false;
                        if ((Prog.numProcess == Prog.numCompleted)&& Prog.terminate) {
				synchronized (queue) {
                                                queue.notifyAll();
                                        }
	
                                break;
                        } else if (readyQueue.size()> 0){
                                long cpuTT = System.currentTimeMillis();
                                cpuBT = readyQueue.peekFirst().get_list_cpu(0);
                                //System.out.println("CPU - Process ID\t : "+ readyQueue.peekFirst().processId + " and Time\t : "+ cpuBT);

                                 if (cpuBT <= quantum) {
                                      //  allowedBurstTime = burstTime;
                                        flag = true;
                                }
                                if (!readyQueue.peekFirst().flag) {
                                        readyQueue.peekFirst().firstTime = System.currentTimeMillis();
                                        readyQueue.peekFirst().flag = true;
                        	        }
				readyQueue.peekFirst().totalWaitingTime += System.currentTimeMillis() - readyQueue.peekFirst().Wt;
			
			try {
                                        Thread.sleep((long) quantum);
                                } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                }
				if (flag) {
                                        readyQueue.peekFirst().remove_cpu_burst(0);
                                        if (readyQueue.peekFirst().cpuIoBurstTime.isEmpty()) {
                                                readyQueue.peekFirst().lastTime = System.currentTimeMillis();
                                                readyQueue.remove();
                                                Prog.numCompleted++;
                                        } else {
                                                readyQueue.peekFirst().Wt= System.currentTimeMillis();
                                                queue.add(readyQueue.peekFirst());
                                                synchronized (queue) {
                                                        queue.notifyAll();
                                                }
                                                readyQueue.remove();
                                        }
					} else {

                                        readyQueue.peekFirst().update_cpu_burst(0,(cpuBT - quantum));
                                        readyQueue.peekFirst().Wt= System.currentTimeMillis();
                                        readyQueue.add(readyQueue.peekFirst());
                                        readyQueue.remove();
                                }
                                Prog.TCPUBusy += System.currentTimeMillis()- cpuTT;



                        } else if (readyQueue.isEmpty()) {
                                synchronized (readyQueue) {
                                        readyQueue.wait();
                                }
                        } else {
				continue;
				}
		}
	}
		
		//end of RR
}
