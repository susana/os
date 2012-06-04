import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class HprnScheduler {	
	// Flags.
	private boolean verbose;
	// Scanners.
	private Scanner randScanner;
	// Data strucs for storing procs.
	private int n;
	private HprnProcess[] procs; // Original list of procs.
	private ArrayList<HprnProcess> blockList; // Array list of blocked procs.
	private ArrayList<HprnProcess> readyList;
	// Scheduling data.
	private HprnProcess runningProc;
	private int cycle;
	private int burst;
	private int done; // Number of procs terminated/done.
	
	public HprnScheduler(boolean verbose, int n, HprnProcess[] procs)
	{
		this.verbose = verbose;
		if ( readRandFile() == false )
			System.exit(0);
		this.n = n;
		this.procs = procs;
		blockList = new ArrayList<HprnProcess>(n);
		readyList = new ArrayList<HprnProcess>(n);
		runningProc = null;
		cycle = 0;
		burst = 0;
		done = 0;
	}
	
	public void runHprn()
	{
		hprn();
	}
	
	private void hprn()
	{
		System.out.print("======================= HPRN =======================");
		printSortedOutput();
		
		while( done != procs.length )
		{
			if( verbose ) printCycle( );
			unblock( );
			create( );
			
			if( runningProc != null )
			{
				if( burst > 0 )
				{
					burst--;
					runningProc.decCpuTimeLeft(1);
				}
				if( runningProc.getCpuTimeLeft() == 0 )
				{
					terminate();
					runningProc = null;
				}
				if( runningProc != null && burst == 0 )
				{
					block();
					runningProc = null;
				}
			}
			
			if( runningProc == null )
			{
				run();
			}
			cycle++;
		}
		
		printProcs();
		printSummary();
	}
	
	private void terminate()
	{
		runningProc.setState("done");
		runningProc.setFinish(cycle);
		runningProc.setBurstRemaining(burst + 1);
		done++;
	}
	
	private void block()
	{
		runningProc.setState("blocked");
		int r = randomOS(runningProc.getIoBurst());
		runningProc.setBlockTimeLeft( r );
		blockList.add((HprnProcess)runningProc);
	}
	
	private void run()
	{
		if( !readyList.isEmpty() )
		{
			Collections.sort(readyList);
			runningProc = readyList.remove(0);
			runningProc.setState("running");
			int r = randomOS(runningProc.getCpuBurst());
			burst = r;
			runningProc.setTimeRun(burst);
		}
	}
	
	private void unblock()
	{
		HprnProcess blockedProc = null;
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
	

	private void create()
	{
		for( int i = 0; i < procs.length; i++ )
		{
			if( procs[i].getArrival() == cycle )
			{
				procs[i].setState("ready");
				procs[i].setReadyCycle(cycle);
				readyList.add(procs[i]);
			}
			if( procs[i].getState().equals("ready") || 
				procs[i].getState().equals("blocked") || 
				procs[i].getState().equals("running") )
			{
				procs[i].setAge(1);
			}
		}
	}	

	private int randomOS( int B )
	{
		return 1 + (randScanner.nextInt() % B);
	}
	
	private void printProcs()
	{
		for( int i = 0; i < procs.length; i++ )
		{
			System.out.println( "\nProcess " + i + ":"
					+ "\n" + procs[i].toString() );
		}
	}
	
	private void printSortedOutput()
	{
		System.out.print("\nThe (sorted) input is: " + n + " ");
		for( int i = 0; i < procs.length; i++ )
		{
			System.out.print( procs[i].procData() + " " );
		}
	}
	
	private void printCycle()
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
	
	private void printSummary()
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
	private boolean readRandFile()
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
