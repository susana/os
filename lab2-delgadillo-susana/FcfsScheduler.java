import java.util.ArrayList;

public class FcfsScheduler extends Scheduler {
		
	public FcfsScheduler(boolean verbose, int n, Process[] procs)
	{
		this.verbose = verbose;
		if ( readRandFile() == false )
			System.exit(0);
		this.n = n;
		this.procs = procs;
		blockList = new ArrayList<Process>(n);
		readyList = new ArrayList<Process>(n);
		runningProc = null;
		cycle = 0;
		burst = 0;
		done = 0;
	}
	
	public void runFcfs()
	{
		fcfs();
	}
	
	private void fcfs() // no initializations (readRandFile, new readyList, etc) need to be done for this.
	{
		System.out.print("======================= FCFS =======================");
		printSortedOutput();
		readRandFile();
		reset();
		
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
					run();
				}
				if( runningProc != null && burst == 0 )
				{
					block();
					runningProc = null;
					run();
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
}
