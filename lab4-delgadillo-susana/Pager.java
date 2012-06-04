import java.util.LinkedList;
import java.util.Scanner;

public class Pager {
  
  private String           alg; // algorithm
  private int              p; // pg size
  private Page[][]         pt; //pg table
  private int[]            frames;
  private LinkedList<Page> residentPgs;
  private boolean          hasFreeFrames;
  private Integer          lastFreeFrame;
  private int              time;
  private int              DEBUG;
  private Proc[]           procs;
  private Scanner          rs;
  
  public Pager(String algorithm, Proc[] procs, int machSize, int pgSize, 
      int procSize, Scanner rs, int DEBUG) {
    pt = new Page[procs.length][procSize/pgSize];
    for (int p = 1; p < procs.length; p++) 
      for (int pg = 0; pg < procSize/pgSize; pg++) 
        pt[p][pg] = new Page(p, pg, -1);
    frames = new int[machSize/pgSize];
    for (int f = 0; f < frames.length; f++) frames[f] = 0;
    alg           = algorithm;
    this.procs    = procs;
    p             = pgSize;
    residentPgs   = new LinkedList<Page>();
    hasFreeFrames = true;
    lastFreeFrame = frames.length - 1;
    time          = 1;
    this.rs       = rs;
    this.DEBUG = DEBUG;
  }

  public void faultCheck(int proc, int word) {
    int page = word / p;
    
    if (pt[proc][page].getFrame() != -1 && 
        frames[pt[proc][page].getFrame()] == proc) {
      Page pg = pt[proc][page];
      
      if (alg.equals("lru")) {
        residentPgs.remove(pg);
        residentPgs.add(pg);
      }
      
      if (DEBUG == 1 || DEBUG == 11) {
        System.out.println("Process " + proc + " references word " + word + 
            " (page " + (page) + ") at time " + time + ": Hit in frame " + 
            pt[proc][page].getFrame() + ".");
      }
    }
    else {
      if (hasFreeFrames) {
        placePage(proc, word);
      }
      else {
        if (!alg.equals("random")) replacePage(proc, word);
        else replaceRandom(proc, word);
      }
      procs[proc].fault();
    }
    time++;
  }
  
  private void replaceRandom(int proc, int word) {
    int r = rs.nextInt();
    if (DEBUG == 11) System.out.println(proc + " uses random number " + r + ".");
    int page = word / p;
    int frameToEmpty = (r + frames.length) % frames.length;
    int evictedProc = frames[frameToEmpty];
    Page evictedPg = null;
    int evictedPgFrame = 0;
    int evictedPgNum = 0;
    
    for (int pg = 0; pg < pt[evictedProc].length; pg++) {
      if (pt[evictedProc][pg].getFrame() == frameToEmpty) {
        evictedPg = pt[evictedProc][pg];
      }
    }
    evictedPgFrame = evictedPg.getFrame();
    evictedPgNum = evictedPg.getPage();
    
    frames[evictedPgFrame] = proc;
    pt[proc][page].setFrame(evictedPgFrame);
    
    evictedPg.setResidency(time);
    pt[proc][page].setTimeLoaded(time);
    
    if (DEBUG == 1 || DEBUG == 11) {
      System.out.println("Process " + proc + " references word " + word + 
          " (page " + (page) + ") at time " + time + ": Fault, evicting page "
          + evictedPgNum + " of process " + evictedProc + " from frame " 
          + evictedPgFrame);
    }
  }
  
  private void replacePage(int proc, int word) {
    int  page           = word / p;
    Page evictedPg      = residentPgs.removeFirst();
    int  evictedPgFrame = evictedPg.getFrame();
    int  evictedPgNum   = evictedPg.getPage();
    int  evictedProc    = evictedPg.getProc();
    
    frames[evictedPgFrame] = proc;
    pt[proc][page].setFrame(evictedPgFrame);
    
    evictedPg.setResidency(time);
    pt[proc][page].setTimeLoaded(time);
    
    residentPgs.add(pt[proc][page]);
    
    if (DEBUG == 1 || DEBUG == 11) {
      System.out.println("Process " + proc + " references word " + word + 
          " (page " + (page) + ") at time " + time + ": Fault, evicting page "
          + evictedPgNum + " of process " + evictedProc + " from frame " 
          + evictedPgFrame);
    }
    pt[evictedProc][evictedPgNum].setFrame(-1);
  }
  
  private void placePage(int proc, int word) {
    int page = word / p;    
    frames[lastFreeFrame] = proc;
    pt[proc][page].setFrame(lastFreeFrame);    
    pt[proc][page].setTimeLoaded(time);    
    if (!alg.equals("random")) residentPgs.add(pt[proc][page]);
    
    if (DEBUG == 1 || DEBUG == 11) {
      System.out.println("Process " + proc + " references word " + word + 
          " (page " + (page) + ") at time " + time 
          + ": Fault, using free frame " + lastFreeFrame + ".");
    }
    
    if(lastFreeFrame >= 0) lastFreeFrame--;
    if(lastFreeFrame < 0) hasFreeFrames = false;
  }
  
  public Proc[] getProcs() {
    // Calculate and set the avg residency for each proc.
    int totalProcRes   = 0;
    int totalProcEvics = 0;
    
    for (int proc = 1; proc < procs.length; proc++) {
      for (int pg = 0; pg < pt[proc].length; pg++) {
        totalProcRes += pt[proc][pg].getResidency();
        totalProcEvics += pt[proc][pg].getEvictions();
      }
      procs[proc].setResidency(totalProcRes);
      procs[proc].setEvictions(totalProcEvics);
      
      totalProcRes = 0;
      totalProcEvics = 0;
    }
    return procs;
  }
}
