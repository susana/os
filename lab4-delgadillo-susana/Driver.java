import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Driver {
  
  // Input values.
  private int        m;
  private int        p;
  private int        s;
  private int        j;
  private int        n;
  private String     r;
  private int        DEBUG;  
  private Scanner    rs;
  private int        numProcs;
  private int        q;
  private Pager      pager;  
  private int[]      refs;
  private double[][] probVals;
  
  public Driver(String[] args) {
    Proc[] procs = null;
    
    // Input values.
    q = 3;    
    m = Integer.parseInt(args[0]);
    p = Integer.parseInt(args[1]);
    s = Integer.parseInt(args[2]);
    j = Integer.parseInt(args[3]);
    n = Integer.parseInt(args[4]);
    r = args[5];
    DEBUG = Integer.parseInt(args[6]);
    
    /*
     *  Create processes according to job mix. Also store probability values for 
     *  processes' references.
     */
    switch (j) {
    case 1:
      procs = new Proc[2];
      procs[1] =  new Proc(s, 1);      
      probVals = new double[2][3];
      probVals[1][0] = 1;
      probVals[1][1] = 0;
      probVals[1][2] = 0;
      break;
    case 2:
      procs = new Proc[5];
      probVals = new double[5][3];
      for (int i = 1; i < procs.length; i++) {
        procs[i] = new Proc(s, i);
        probVals[i][0] = 1;
        probVals[i][1] = 0;
        probVals[i][2] = 0;
      }
      break;
    case 3:
      procs = new Proc[5];
      probVals = new double[5][3];
      for (int i = 1; i < procs.length; i++) {
        procs[i] = new Proc(s, i);
        probVals[i][0] = 0;
        probVals[i][1] = 0;
        probVals[i][2] = 0;
      }
      break;
    case 4:
      procs = new Proc[5];
      for (int i = 1; i < procs.length; i++) procs[i] = new Proc(s, i);
      
      probVals = new double[5][3];
      probVals[1][0] = 0.75;
      probVals[1][1] = 0.25;
      probVals[1][2] = 0;

      probVals[2][0] = 0.75;
      probVals[2][1] = 0;
      probVals[2][2] = 0.25;
      
      probVals[3][0] = 0.75;
      probVals[3][1] = 0.125;
      probVals[3][2] = 0.125;
      
      probVals[4][0] = 0.5;
      probVals[4][1] = 0.125;
      probVals[4][2] = 0.125;
      break;
    }
    
    // Number of processes + 1.
    numProcs = procs.length;
    // Initialize processes' first references.
    refs = new int[numProcs];
    for (int p = 1; p < numProcs; p++) refs[p] = (111 * p) % s;
    
    // Read in random-numbers.txt.
    try {
      BufferedReader buf = new BufferedReader(new FileReader("random-numbers.txt"));
      rs = new Scanner(buf);
    }
    catch(FileNotFoundException e) {
      System.err.println("FileNotFoundException: " + e.getMessage()
          + "\nMake sure random-numbers.txt is in the current directory.");
      System.exit(0);
    }
    
    // Create pager.
    pager = new Pager(r, procs, m, p, s, rs, DEBUG);
  }
  
  private void run() {
    int[]  refCounters = new int[numProcs];
    Proc[] procs       = null;
    
    while (refCounters[refCounters.length - 1] != n) {
      for (int p = 1; p < numProcs; p++) {
        for (int ref = 0; ref < q; ref++) {
          if (refCounters[p] != n) {
            pager.faultCheck(p, refs[p]);
            refCounters[p]++;
            refs[p] = nextRef(p, refs[p]);
          }
        } // end for
      } // end for
    } // end while
    
    procs = pager.getProcs();
    printOutput(procs);
  }
  
  private int nextRef(int proc, int w) {
    int    r = rs.nextInt();
    double y = r/(Integer.MAX_VALUE + 1d);
    
    if (DEBUG == 11) System.out.println(proc + " uses random number " + r + ".");
    
    if (y < probVals[proc][0]) {
      return (w + 1 + s) % s;
    }
    else if (y < probVals[proc][0] + probVals[proc][1]) {
      return (w - 5 + s) % s;
    }
    else if (y < probVals[proc][0] + probVals[proc][1] + probVals[proc][2]) {
      return (w + 4 + s) % s;
    }
    else { // y >= A + B + C
      r = rs.nextInt();
      if (DEBUG == 11) System.out.println(proc + " uses random number " + r + ".");
      return (r + s) % s;
    }
  }
  
  private void printOutput(Proc[] procs) {
    int    totalFaults    = 0;
    int    totalResidency = 0;
    int    totalEvictions = 0;
    double totalAvgRes    = 0;
    
    System.out.println();
    
    for (int p = 1; p < procs.length; p++) {
      System.out.println(procs[p].toString());
      
      totalFaults    += procs[p].getFaults();
      totalResidency += procs[p].getResidency();
      totalEvictions += procs[p].getEvictions();
    }    
    
    totalAvgRes = (double)totalResidency / totalEvictions;
    
    if (totalEvictions != 0) {
      System.out.println(
          "\nThe total number of faults is " + totalFaults +
          " and the overall average residency is " + totalAvgRes + ".");
    }
    else {
      System.out.println("\nThe total number of faults is " + totalFaults +
          ".\n\tWith no evictions, the overall average residence is undefined.");
    }
  }
  
  public String toString() {
    return 
      "Machine size: " + m + "\n" +
      "Page size: " + p + "\n" + 
      "Process size: " + s + "\n" +
      "Job mix: " + j + "\n" +
      "Number of references: " + n + "\n" +
      "Replacement algorithm: " + r + "\n" + 
      "Debug flag: " + DEBUG +"\n";
  }
  
  public static void main(String[] args) {    
    if(args.length < 7) {
      System.out.println("Counted " + args.length + ". Missing args.");
    }
    else if(args.length > 7) {
      System.out.println("Counted " + args.length + ". Excess args.");
    }
    else {
      Driver d = new Driver(args);
      System.out.println(d.toString());
      d.run();
    }
  }
}
