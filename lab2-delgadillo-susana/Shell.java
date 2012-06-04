import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

public class Shell {	
	// Flags.
	private boolean verbose;
	// Scanners.
	private Scanner inScanner;
	// Array of hprn processes.
	private HprnProcess[] hprnProcs;
	
	private Process[] parseInput()
	{
		int mix = inScanner.nextInt();
		String a, io = "";
		int b, c = 0;
		int count = 0;
		Process[] procs = new Process[mix];
		hprnProcs = new HprnProcess[mix];
		
		while( mix > 0 )
		{
			a = inScanner.next().substring(1); // Can't use nextInt() because token contains paren.
			b = inScanner.nextInt();
			c = inScanner.nextInt();
			io = inScanner.next().substring(0, 1); // Same as above.
			procs[count] = new Process( Integer.parseInt(a), b, c, Integer.parseInt(io), count );
			hprnProcs[count] = new HprnProcess( Integer.parseInt(a), b, c, Integer.parseInt(io), count );
			
			count++;
			mix--;
		}

		Arrays.sort(procs);
		Arrays.sort(hprnProcs);
		return procs;
	}
	
	private boolean readInfile( String fileName )
	{
		try
		{
			BufferedReader buf = new BufferedReader( new FileReader( fileName ) );
			inScanner = new Scanner( buf );
		}
		catch(FileNotFoundException e)
		{
			System.err.println("FileNotFoundException: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean parseCmdLine( String[] args )
	{		
		if( args.length == 0 ) // No args.
		{
			System.out.println("No args provided.");
			return false;
		}
		else if( args[0].equals("--verbose") && !args[1].equals(null) ) // Verbose flag and file name.
		{
			verbose = true;
			if( readInfile(args[1]) == false ) System.exit(0);
			Process[] procs = parseInput();
			UniScheduler uni = new UniScheduler(verbose, procs.length, procs);
			uni.runUni();
			FcfsScheduler fcfs = new FcfsScheduler(verbose, procs.length, procs);
			fcfs.runFcfs();
			RrScheduler rr = new RrScheduler(verbose, procs.length, procs);
			rr.runRr();
			HprnScheduler hprn = new HprnScheduler(verbose, hprnProcs.length, hprnProcs );
			hprn.runHprn();
			return true;
		}
		else if( !args[0].equals(null) ) // A file name.
		{
			if( readInfile(args[0]) == false ) System.exit(0);
			Process[] procs = parseInput();
			UniScheduler uni = new UniScheduler(verbose, procs.length, procs);
			uni.runUni();
			FcfsScheduler fcfs = new FcfsScheduler(verbose, procs.length, procs);
			fcfs.runFcfs();
			RrScheduler rr = new RrScheduler(verbose, procs.length, procs);
			rr.runRr();
			HprnScheduler hprn = new HprnScheduler(verbose, hprnProcs.length, hprnProcs );
			hprn.runHprn();
			return true;
		}
		return false;
	}
	
	public static void main( String[] args )
	{
		Shell s = new Shell();
		s.parseCmdLine(args);
	}
}