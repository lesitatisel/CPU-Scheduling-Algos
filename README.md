# CPU-Scheduling-Algos
CPU Scheduling Algorithms written in JAVA using concept of multi threading provided the process requests in the text file.
Please check the ProblemDescription file for complete description of the problem solved.
Soution included three threads implementing Runnable Interface
1.ReadFile – this thread reads the input file and places the processes into the Ready Queue and terminates upon reading the stop.
2.CPUScheduler – this thread reads the process from the ready queue, executes for the respective burst time, places the process into IO queue and notifies IO Scheduler thread. If the there is no more CPU or Io bursts it removes the process from the ready queue. There is a checkpoint to interrupt the thread when the ready queue gets empty and notifies IO thread.
3.IOScheduler – this thread reads the process from IO queue, executes for the respective burst time, places back the process to the end of the ready queue and notifies the CPU scheduler thread. There is a checkpoint to interrupt the thread when IO queue is empty.
When Ready queue and Io queue both gets empty the performance measures of the CPU are then calculated and printed.
I have implemented Process.java to store each process data and a LinkedList<Process> for the list of processes to handle the Ready Queue and IO queue.
Declared the methods for each algorithm in CPU and IO threads. It executes based on the arguments 
