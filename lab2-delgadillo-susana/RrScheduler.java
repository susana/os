import java.util.ArrayList;
import java.util.Collections;

public class RrScheduler extends Scheduler {

	private int quantum;
	
	public RrScheduler(boolean verbose, int n, Process[] procs)
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
		quantum = 2;
	}
	
	public void runRr()
	{
		rr();
	}
	
	private void rr()
	{
		System.out.print("======================= ROUND ROBIN, Q=2 =======================");
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
				quantum--;
				if( burst > 0 )
				{
					burst--;
					runningProc.decCpuTimeLeft(1);
					runningProc.setBurstRemaining(burst);
				}
				if( runningProc.getCpuTimeLeft() == 0 )
				{
					quantum = 2;
					terminate();
					runningProc = null;
					runRR();
				}
				if( runningProc != null && burst == 0 )
				{
					quantum = 2;
					block();
					runningProc = null;
					runRR();
				}
				if( runningProc != null && quantum == 0 )
				{
					quantum = 2;
					preempt();
					runningProc = null;
					runRR();
				}
			}
			
			if( runningProc == null )
			{
				runRR();
			}
			cycle++;
		}
		
		printProcs();
		printSummary();
	}
		
	private void runRR()
	{
		if( !readyList.isEmpty() )
		{
			Collections.sort(readyList);
			runningProc = readyList.remove(0);
			runningProc.setState("running");
			if( runningProc.getBurstRemaining() <= 0 )
			{
				int r = randomOS(runningProc.getCpuBurst());
				if( verbose ) System.out.print( "\nCalc cpu burst when running: " + r );
				burst = r;
			}
			else
			{
				burst = runningProc.getBurstRemaining();
			}
		}
	}
	
	private void preempt()
	{			
		runningProc.setState("ready");
		runningProc.setReadyCycle(cycle);
		readyList.add(runningProc);
	}	
	
}
