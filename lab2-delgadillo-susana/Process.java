public class Process implements Comparable<Process>{

	// Process data.
	protected Integer orderNum;
	protected Integer arrival;
	protected Integer cpuBurst;
	protected Integer cpuTime;
	protected Integer ioBurst;
	// Scheduling data.
	protected String state; // unborn, ready, blocked, running
	protected int cpuTimeLeft;
	protected int blockTimeLeft;
	protected Integer readyCycle;
	protected int burstRemaining;
	// Process analysis data.
	protected int finish;
	// turnaround = (finish - arrival)
	protected int ioTime;
	// wait = (finish - ioTime - cpuTime - arrival)
	
	public Process( int a, int b, int c, int io, int count )
	{
		orderNum = count;
		arrival = a;
		cpuBurst = b;
		cpuTime = c;
		ioBurst = io;
		
		state = "unborn";
		cpuTimeLeft = cpuTime;
		blockTimeLeft = 0;
		readyCycle = 0;
		burstRemaining = 0;
		
		finish = 0;
		ioTime = 0;
	}
	
	public int getArrival()
	{
		return arrival;
	}
	
	public int getCpuBurst()
	{
		return cpuBurst;
	}
	
	public int getCpuTime()
	{
		return cpuTime;
	}
	
	public int getIoBurst()
	{
		return ioBurst;
	}	
	
	public int getBurstRemaining()
	{
		return burstRemaining;
	}
	
	public void setBurstRemaining( int burstRemaining )
	{
		this.burstRemaining = burstRemaining;
	}
	
	public void setState( String state )
	{
		this.state = state;
	}
	
	public String getState()
	{
		return state;
	}
	
	public void decCpuTimeLeft(int decrement)
	{
		this.cpuTimeLeft -= decrement;
	}
	
	public int getCpuTimeLeft()
	{
		return cpuTimeLeft;
	}
	
	public void setBlockTimeLeft(int blockTimeLeft)
	{
		this.blockTimeLeft = blockTimeLeft;
		ioTime += blockTimeLeft;
	}
	
	public void decBlockTime(int decrement)
	{
		this.blockTimeLeft -= decrement;
	}
	
	public int getBlockTimeLeft()
	{
		return blockTimeLeft;
	}
	
	public void setReadyCycle(int readyCycle)
	{
		this.readyCycle = readyCycle;
	}
	
	public int getReadyCycle()
	{
		return readyCycle;
	}
	
	public void setFinish(int finish)
	{
		this.finish = finish;
	}
	
	public int getFinish()
	{
		return finish;
	}
	
	public int getIoTime()
	{
		return ioTime;
	}
	
	public int waitTime()
	{
		return (finish - ioTime - cpuTime - arrival);
	}
	
	public void reset()
	{
		state = "unborn";
		cpuTimeLeft = cpuTime;
		blockTimeLeft = 0;
		readyCycle = 0;
		burstRemaining = 0;
		
		finish = 0;
		ioTime = 0;
	}
	
	public String procData()
	{
		return "(" 
			+ arrival + " " 
			+ cpuBurst + " " 
			+ cpuTime + " " 
			+ ioBurst + ")";
	}
	
	public String toString()
	{
		return "(A,B,C,IO) = (" 
			+ arrival + "," 
			+ cpuBurst + "," 
			+ cpuTime + "," 
			+ ioBurst + ")"
			+ "\nFinishing time: " + finish
			+ "\nTurnaround time: " + (finish - arrival)
			+ "\nI/O time: " + ioTime
			+ "\nWaiting time: " + (finish - ioTime - cpuTime - arrival);
	}
	
	public int compareTo(Process p)
	{
		if( this.readyCycle.compareTo(p.readyCycle) != 0 ) 
			return this.readyCycle.compareTo(p.readyCycle);
		if( this.arrival.compareTo(p.arrival) != 0 ) // Compare by arrival first.
			return this.arrival.compareTo(p.arrival);
		if( this.cpuBurst.compareTo(p.cpuBurst) != 0 ) // Compare by cpuBurst if arrivals are equal.
			return this.cpuBurst.compareTo(p.cpuBurst);
		if( this.cpuTime.compareTo(p.cpuTime) != 0 ) // Compare by cpuTime if cpuBursts are equal.
			return this.cpuTime.compareTo(p.cpuTime);
		if( this.ioBurst.compareTo(p.ioBurst) != 0 ) // Compare by ioBurst if cpuTimes are equal.
			return this.ioBurst.compareTo(p.ioBurst);
		if( this.orderNum.compareTo(p.orderNum) != 0 ) // Compare by orderNum if ioBursts are equal.
			return this.orderNum.compareTo(p.orderNum);
		
		return 0;
	}
}