public class Page {

  private int proc;
  private int page;
  private int frame;
  private int timeLoaded;
  private int residency;
  private int evictions;
  
  public Page(int proc, int page, int frame) {
    this.proc  = proc;
    this.page  = page;
    this.frame = frame;
    timeLoaded = 0;
    residency  = 0;
    evictions  = 0;
  }
  
  public void setTimeLoaded(int time) {
    timeLoaded = time;
  }
  
  public int getTimeLoaded() {
    return timeLoaded;
  }
  
  public void setResidency(int timeEvicted) {
    evictions++;
    residency += timeEvicted - timeLoaded;
  }
  
  public int getResidency() {
    return residency;
  }
  
  public double getAvgResidency() {
    if (evictions == 0) return 0;
    return (double)residency / evictions;
  }
  
  public int getEvictions() {
    return evictions;
  }
  
  public int getPage() {
    return page;
  }
  
  public void setProc(int proc) {
    this.proc = proc;
  }
  
  public int getProc() {
    return proc;
  }
  
  public void setFrame(int frame) {
    this.frame = frame;
  }
  
  public int getFrame() {
    return frame;
  }
  
  public String toString() {
    return "Process " + proc + 
        " Page " + page + 
        " Frame " + frame + 
        " Residency " + getAvgResidency();
  }
}
