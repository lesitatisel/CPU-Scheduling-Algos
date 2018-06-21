import java.util.*;
import java.io.*;
class IOScheduler implements Runnable {
	
    private LinkedList<Process> readyQueue;
    private LinkedList<Process> queue;
    private String schedulerType;
    private Integer quantum;

    public IOScheduler(LinkedList<Process> readyQueue, LinkedList<Process> queue, String schedulerType, Integer quantum) {
        this.queue = queue;
        this.readyQueue = readyQueue;
        this.schedulerType = schedulerType;
        this.quantum = quantum;
    }

    @Override
    public void run() {
	
try{
            switch (schedulerType) {
			case "FIFO":
				//System.out.println("I am here IO FIFOs");
				FIFOio();
				/*FIFO f = new FIFO(readyQueue,queue);
				f.FIFOio();*/
				break;
			case "RR":
				//System.out.println("I am here IO RR");
				RRio();
				break;
			case "SJF":
				//System.out.println("I am here IO SJF");
				SJFio();
				break;
			case "PR":
				//System.out.println("I am here IO PR");
				PRio();
				break;	
			default:
				break;
			}
		}catch (InterruptedException ex) {
                                ex.printStackTrace();
				//System.err.println("exception in IO thread");
				//Thread.currentThread().interrupt();
                        }

	}
		
		 synchronized void FIFOio() throws InterruptedException{
			Integer ioBT;
		                
			while(true){
			 //System.out.println("@ IO thread" + Prog.terminate + Prog.numCompleted);
			//System.out.println(Prog.terminate);
			//System.out.println(Prog.numCompleted);
			
                        if((Prog.numProcess==Prog.numCompleted )&& Prog.terminate)
                        {
			//	System.out.println("returning from IO Thread");
				//throw new InterruptedException("IO thread ededed");
                                break;

                        }

                        else if ((queue.size())>0)
                        {
                                ioBT = queue.peekFirst().get_list_cpu(0);
                              // System.out.println("IO - Process ID\t : " + queue.peekFirst().processId + " and Time\t : " +ioBT);

                                if (!queue.peekFirst().flag) {
                                        queue.peekFirst().firstTime = System.currentTimeMillis();
                                        queue.peekFirst().flag = true;
                                }
                                queue.peekFirst().totalWaitingTime +=System.currentTimeMillis() -readyQueue.peekFirst().Wt;
                                try {
                                        Thread.sleep((long) ioBT);
                                } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                }

                                 queue.peekFirst().remove_cpu_burst(0);

                                 if (queue.peekFirst().cpuIoBurstTime.isEmpty())
                                {
                                        queue.peekFirst().lastTime=System.currentTimeMillis();
                                        queue.remove();
                                        Prog.numCompleted++;
                                }
                                else
                                {
                                        queue.peekFirst().Wt =System.currentTimeMillis();
                                        readyQueue.add(queue.peekFirst());
                                         synchronized (readyQueue) {
                                                readyQueue.notifyAll();
                                        }
                                        queue.remove();
                                }
                                }


                        else if (queue.isEmpty()) {
                                synchronized (queue) {
                                        queue.wait();                            
				    }
                        } else {
                                continue;
                        }
                }
        }

		//end of FIFO

			synchronized void RRio() throws InterruptedException{	
				Integer ioBT;
				 boolean flag;


        while(true)
                {
                        flag = false;
                        if ((Prog.numProcess == Prog.numCompleted)
                                        && Prog.terminate) {
                                break;
                        } else if (queue.size()> 0){
                                ioBT = queue.peekFirst().get_list_cpu(0);
                                //System.out.println("CPU - Process ID\t : "+ queue.peekFirst().processId + " and Time\t : "+ ioBT);

                                 if (ioBT <= quantum) {
                                      //  allowedBurstTime = burstTime;
                                        flag = true;
                                }
                                if (!queue.peekFirst().flag) {
                                        queue.peekFirst().firstTime = System.currentTimeMillis();
                                        queue.peekFirst().flag = true;
                                        }
                                queue.peekFirst().totalWaitingTime += System.currentTimeMillis() - queue.peekFirst().Wt;

                        try {
                                        Thread.sleep((long) quantum);
                                } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                }
				if (flag) {
                                        queue.peekFirst().remove_cpu_burst(0);
                                        if (queue.peekFirst().cpuIoBurstTime.isEmpty()) {
                                                queue.peekFirst().lastTime = System.currentTimeMillis();
                                                queue.remove();
                                                Prog.numCompleted++;
                                        } else {
                                                queue.peekFirst().Wt= System.currentTimeMillis();
                                                readyQueue.add(queue.peekFirst());
                                                synchronized (readyQueue) {
                                                        readyQueue.notifyAll();
                                                }
                                                queue.remove();
                                        }
					} else {
                                        queue.peekFirst().update_cpu_burst(0,(ioBT - quantum));
                                        queue.peekFirst().Wt= System.currentTimeMillis();
                                        queue.add(queue.peekFirst());
                                        queue.remove();
                                
                                }



                        } else if (queue.isEmpty()) {
                                synchronized (queue) {
                                        queue.wait();
                                }
                        } else {
                                continue;
                                }
                }
	}

	
			
    
		//end of RR

	synchronized void PRio() throws InterruptedException{
				Integer ioBT;
                                int priorityValue=0;
                                int count=0;
                while(true){
                //System.out.println(Prog.numCompleted);
                if((Prog.numProcess==Prog.numCompleted) && Prog.terminate){
                      //  System.out.println("returning from IO ");
                        break;
                        //throw new InterruptedException("thrown from IO");
                }
                else if((queue.size())>0){
                        priorityValue = getHighPriorityIndex();
                        //System.out.println(priorityValue);
                        for(int i=0;i<priorityValue;i++){
                        count= i;
                        }
                        ioBT = queue.get(count).get_list_cpu(0);
                        //System.out.println("IO  - Process ID\t : "+ queue.peekFirst().processId + " and Time\t : "+ ioBT);
                        if(!queue.get(count).flag){
                                queue.get(count).firstTime = System.currentTimeMillis();
                                queue.get(count).flag = true;
                        }
                        queue.get(count).totalWaitingTime+= System.currentTimeMillis() - queue.get(count).Wt;
                        try {
                                Thread.sleep((long) ioBT);
                                } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                }
                        queue.get(count).remove_cpu_burst(0);
                        if(queue.get(count).cpuIoBurstTime.isEmpty()){
                                queue.get(count).lastTime = System.currentTimeMillis();
                                queue.remove(priorityValue);
                                Prog.numCompleted++;
                        }
                        else{
                                        queue.get(count).Wt= System.currentTimeMillis();
                                        readyQueue.add(queue.get(count));
                                        synchronized (readyQueue) {
                                                readyQueue.notifyAll();
                                                }
                                        queue.remove(count);
                        }

                }
                else if(queue.isEmpty()){
                        synchronized (queue) {
                                queue.wait();
                                }
                }
                else{
                        continue;
                }
        }

			}

	synchronized public int getHighPriorityIndex() {
        	int position = 0;
        	int count = 0;
        	int temp = 0;
        while (count<queue.size()) {
            if (temp > queue.get(count).priority) {
                position = count;
                temp = queue.get(count).priority;
            }
            count++;
        	}
	
       	 	return position;
	    }

		//end of PR
			
	synchronized void SJFio() throws InterruptedException{
			    	Integer ioBT;
    				int priorityValue=0;
	    			int count=0;
        	while(true){
		//System.out.println(Prog.numCompleted);
        	if((Prog.numProcess==Prog.numCompleted) && Prog.terminate){
		//	System.out.println("returning from IO ");
    			break;
			//throw new InterruptedException("thrown from IO");
    		}
        	else if((queue.size())>0){
         		priorityValue = getShortestJobIndex(); 
			//System.out.println(priorityValue);
			for(int i=0;i<priorityValue;i++){
      			count= i;
			}
         		ioBT = queue.get(count).get_list_cpu(0);
         		//System.out.println("IO  - Process ID\t : "+ queue.peekFirst().processId + " and Time\t : "+ ioBT);
        		if(!queue.get(count).flag){
        			queue.get(count).firstTime = System.currentTimeMillis();
        			queue.get(count).flag = true;
        		}
        		queue.get(count).totalWaitingTime+= System.currentTimeMillis() - queue.get(count).Wt; 
        		try {
        			Thread.sleep((long) ioBT);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
        		queue.get(count).remove_cpu_burst(0);
        		if(queue.get(count).cpuIoBurstTime.isEmpty()){
        			queue.get(count).lastTime = System.currentTimeMillis();
        			queue.remove(priorityValue);
        			Prog.numCompleted++;
        		}
        		else{
        				queue.get(count).Wt= System.currentTimeMillis();
        				readyQueue.add(queue.get(count));
        				synchronized (readyQueue) {
        					readyQueue.notifyAll();
						}
        				queue.remove(count);
        		}

        	}
        	else if(queue.isEmpty()){
        		synchronized (queue) {
        			queue.wait();	
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
        		int temp = queue.peekFirst().get_list_cpu(0);

        		while (count<queue.size()) {
            		if (temp < queue.get(count).get_list_cpu(0)) {
				//System.out.println(count);
                		position = count;
                		temp = queue.get(count).get_list_cpu(0);
            		}
            
            		count++;
        		}

        		return position;
    			}
}
