import java.util.ArrayList;

public class UniScheduler extends Scheduler {
	
	public UniScheduler(boolean verbose, int n, Process[] procs)
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
	
	public void runUni()
	{
		uniprgrm();
	}
	
	private void uniprgrm()
	{
		System.out.print("======================= UNIPROGRAMMING =======================");
		printSortedOutput();
		readRandFile();
		reset();
		
		while( done != procs.length )
		{
			if( verbose ) printCycle( );
			create( );			

			if( runningProc != null )
			{		
				if(burst > 0)
				{
					burst--;
					if( runningProc.getState().equals("running") )
					{
						runningProc.decCpuTimeLeft(1);
						runningProc.setBurstRemaining(burst);
					}
					else
					{
						runningProc.decBlockTime(1);
					}
				}
				if( runningProc.getCpuTimeLeft() == 0 )
				{
					terminate();
					runningProc = null;
					run();
				}
				if( burst == 0 && runningProc.getState().equals("blocked") )
				{
					unblock();
					runCrnt();
				}
				if( burst == 0 && runningProc.getState().equals("running") )
				{
					block();
					burst = runningProc.getBlockTimeLeft();
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
	
	void runCrnt()
	{
		runningProc.setState("running");
		int r = randomOS(runningProc.getCpuBurst());
		if( verbose ) System.out.print( "\nCalc cpu burst when running: " + r );
		burst = r;
	}

}
