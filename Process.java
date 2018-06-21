import java.util.*;

public class Process {

	String processId;
	int priority;
	int quantumTime;
	int burstTotal;
	ArrayList<Integer> cpuIoBurstTime = new ArrayList<Integer>();
	long arrivalTime;
	long firstTime;
	long lastTime;
	long Wt;
	long totalWaitingTime;
	boolean flag;


	void Process(int quantum) {
		this.quantumTime = quantum;
	}
	 Process(String processId,int priority, ArrayList<Integer> cpuIoBurstTime,long Wt,long arrivalTime,long firstTime, long lastTime,long totalWaitingTime,boolean flag)
	{

		this.processId=processId;
		this.priority =priority;
		this.cpuIoBurstTime=cpuIoBurstTime;
		this.Wt=Wt;
		this.arrivalTime=arrivalTime;
		this.firstTime =firstTime;
		this.lastTime=lastTime;
		this.totalWaitingTime=totalWaitingTime;
		this.flag=flag;
	}


/*	int get_total_burst() {
		return this.burstTotal;
	}

	void set_priority(int x) {
		this.priority = x;
	}

	int get_priority() {
		return this.priority;
	}*/


	synchronized void update_cpu_burst(int index, int burst) {
		this.cpuIoBurstTime.set(index, burst);
	}

	synchronized int get_list_cpu(int index_cpu) {
		return this.cpuIoBurstTime.get(index_cpu);
	}

	synchronized int remove_cpu_burst(int index_cpu) {
		return this.cpuIoBurstTime.remove(index_cpu);
	}

}
