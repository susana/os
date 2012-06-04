public class Proc {

  private int     num;
  private double  avgResidency;
  private int     faults;
  private int     currRef;
  private int     evictions;
  private int     residency;
  
  public Proc(int procSize, int num) {
    currRef      = 111 * num % procSize;
    this.num     = num;
    avgResidency = 0;
    faults       = 0;
  }
  
  public void setEvictions(int count) {
    evictions = count;
  }
  
  public int getEvictions() {
    return evictions;
  }
  
  public void setResidency(int count) {
    residency = count;
  }
  
  public int getResidency() {
    return residency;
  }
  
  public double getAvgRes() {
    avgResidency = (double)residency / evictions;
    return avgResidency;
  }
  
  public int getNum() {
    return num;
  }
  
  public void setCurrRef(int lastRef) {
    this.currRef = lastRef;
  }
  
  public int getCurrRef()  {
    return currRef;
  }
  
  public void fault() {
    faults++;
  }
  
  public int getFaults() {
    return faults;
  }
  
  public String toString() {
    String output = "Process " + num + " had " + faults + " faults";
    if (evictions <= 0) {
      output += ".\n\tWith no evictions, the average residence is undefined.";
    }
    else {
      output += " and " + getAvgRes() + " average residency.";
    }
    return output;
  }
  
}
