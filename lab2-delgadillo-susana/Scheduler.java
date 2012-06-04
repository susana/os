import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class Scheduler {
		
		// Flags.
		protected boolean verbose;
		// Scanners.
		protected Scanner randScanner;
		// Data strucs for storing procs.
		protected int n;
		protected Process[] procs; // Original list of procs.
		protected ArrayList<Process> blockList;
		protected ArrayList<Process> readyList;
		// Scheduling data.
		protected Process runningProc;
		protected int cycle;
		protected int burst;
		protected int done; // Number of procs terminated/done.
		
		protected void reset()
		{
			readyList = new ArrayList<Process>(procs.length);
			blockList =  new ArrayList<Process>(procs.length);;
			runningProc = null;
			cycle = 0;
			burst = 0;
			done = 0;
			
			for( int i = 0; i < procs.length; i++ )
			{
				procs[i].reset();
			}
		}
		
		protected void run()
		{
			if( !readyList.isEmpty() )
			{
				Collections.sort(readyList);
				runningProc = readyList.remove(0);
				runningProc.setState("running");
				int r = randomOS(runningProc.getCpuBurst());
				burst = r;
			}	
		}

		protected void create()
		{
			for( int i = 0; i < procs.length; i++ )
			{
				if( procs[i].getArrival() == cycle )
				{
					procs[i].setState("ready");
					procs[i].setReadyCycle(cycle);
					readyList.add(procs[i]);
				}
			}
		}
		
		protected void terminate()
		{
			runningProc.setState("done");
			runningProc.setFinish(cycle);
			runningProc.setBurstRemaining(burst + 1);
			done++;
		}
		
		protected void block()
		{
			runningProc.setState("blocked");
			int r = randomOS(runningProc.getIoBurst());
			runningProc.setBlockTimeLeft( r );
			blockList.add((Process)runningProc);
		}		
		
		protected void unblock()
		{
			Process blockedProc = null;
			int itr = 0;
			int timeRem = 0;
			
			while( !blockList.isEmpty() && (itr < blockList.size()) )
			{
				blockedProc = blockList.get(itr);
				if( blockedProc.getState().equals("blocked") && blockedProc.getBlockTimeLeft() > 0 )
				{
					blockedProc.decBlockTime(1);
					timeRem = itr;
					itr++;
				}
				if( blockedProc.getBlockTimeLeft() == 0 )
				{
					blockedProc.setState("ready");
					blockedProc.setReadyCycle(cycle);
					blockList.remove(timeRem);
					readyList.add(blockedProc);
					itr--;
				}
			}
		}
		
		protected void printSortedOutput()
		{
			System.out.print("\nThe (sorted) input is: " + n + " ");
			for( int i = 0; i < procs.length; i++ )
			{
				System.out.print( procs[i].procData() + " " );
			}
		}
		
		protected void printSummary()
		{
			float cpuUse = 0;
			float cpuUtil = 0;
			float ioUse = 0;
			float ioUtil = 0;
			int turnaround = 0;
			float avgTA = 0;
			int wait = 0;
			float avgWait = 0;
			float throughput = 0;
			
			for( int i = 0; i < procs.length; i++)
			{
				cpuUse += procs[i].getCpuTime();
				ioUse += procs[i].getIoTime();
				turnaround += procs[i].getFinish() - procs[i].getArrival();
				wait += procs[i].waitTime();
			}
						
			cpuUtil = cpuUse / (cycle - 1);
			ioUtil = ioUse / (cycle - 1);
			avgTA = (float)turnaround / n;
			avgWait = (float)wait / n;
			throughput = ((float)n * 100)/(cycle - 1);
			
			System.out.print("\nSummary Data:"
					+ "\nFinishing time: " + (cycle - 1)
					+ "\nCPU Utilization: " + cpuUtil
					+ "\nI/O Utilization: " + ioUtil
					+ "\nThroughput: " + throughput + " number of processes per hundred cycles"
					+ "\nAverage turnaround time: " + avgTA
					+ "\nAverage waiting time: " + avgWait
					+ "\n\n"
					);
		}
		

		protected int randomOS( int B )
		{
			return 1 + (randScanner.nextInt() % B);
		}
		
		protected void printProcs()
		{
			for( int i = 0; i < procs.length; i++ )
			{
				System.out.println( "\nProcess " + i + ":"
						+ "\n" + procs[i].toString() );
			}
		}
		
		protected void printCycle()
		{
			System.out.print("\nCycle\t" + cycle + ":" );
			for( int i = 0; i < procs.length; i++ )
			{
				if( procs[i].getState().equals("blocked") )
				{
					System.out.print("\tblocked\t" + procs[i].getBlockTimeLeft() );
				}
				else if( procs[i].getState().equals("ready") )
				{
					System.out.print("\tready\t" + procs[i].getBurstRemaining() );
				}
				else if( procs[i].getState().equals("running") )
				{
					System.out.print("\trunning\t" + burst );
				}
				else if( procs[i].getState().equals("done") )
				{
					System.out.print("\tdone\t" + procs[i].getBurstRemaining() );
				}
				else if( procs[i].getState().equals("unborn") )
				{
					System.out.print("\tunborn\t0" );
				}
				else
				{
					System.out.print("ERROR in printCycle(): Proc was not assigned a state.");
				}
			}
		}
		
		protected boolean readRandFile()
		{
			try
			{
				BufferedReader buf = new BufferedReader( new FileReader( "random-numbers.txt" ) );
				randScanner = new Scanner( buf );
			}
			catch(FileNotFoundException e)
			{
				System.err.println("FileNotFoundException: " + e.getMessage()
						+ "\nMake sure random-numbers.txt is in the current directory.");
				return false;
			}
			return true;
		}
}


