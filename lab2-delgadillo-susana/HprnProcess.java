public class HprnProcess extends Process {
	
	private float age;
	private float timeRun;

	public HprnProcess(int a, int b, int c, int io, int count) {
		super(a, b, c, io, count);
		age = 0;
		timeRun = 1;
	}
	
	public Float getPenalty( )
	{
		return age/timeRun;
	}
	
	public void setAge( int time )
	{
		age += time;
	}
	
	public float getAge()
	{
		return age;
	}
	
	public void setTimeRun( int burst )
	{
		timeRun += burst;
	}
	
	public int compareTo(Process p) {
		return this.compareTo(((HprnProcess) p));
	}
	
	public int compareTo(HprnProcess p)
	{
		if( this.getPenalty().compareTo(p.getPenalty()) != 0 )
			return -1 * this.getPenalty().compareTo(p.getPenalty());
		if( this.arrival.compareTo(p.arrival) != 0 )
			return this.arrival.compareTo(p.arrival);
		if( this.cpuBurst.compareTo(p.cpuBurst) != 0 )
			return this.cpuBurst.compareTo(p.cpuBurst);
		if( this.cpuTime.compareTo(p.cpuTime) != 0 )
			return this.cpuTime.compareTo(p.cpuTime);
		if( this.ioBurst.compareTo(p.ioBurst) != 0 )
			return this.ioBurst.compareTo(p.ioBurst);
		if( this.orderNum.compareTo(p.orderNum) != 0 )
			return this.orderNum.compareTo(p.orderNum);		
		
		return 0;
	}
	
}
