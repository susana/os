import java.util.Arrays;

public class Task {

  /**
   * Fields
   * num - Task number.
   * status - Task's current status (RUNNING, TERMINATED, WAITING, RELEASING, or
   *  COMPUTING)
   * activities - Task's activities to be performed.
   * claims - Task's claims for each resource.
   * resReq - [0] is the resource being requested, [1] is the amount.
   * relReq - [0] is the resource being released, [1] is the amount.
   * compTime - Amount of cycles left for the task to be computed.
   * timeTaken - Number of cycles it's taken for this task to terminate.
   * timeWaiting - Total amount of cycles this task has spent waiting.
   */
  private int num;
  private Status status;
  private ActivityList activities;
  private int[] resources;
  private int[] claims;
  private int[] resReq;
  private int[] resRel;
  private int compTime;
  private int timeTaken;
  private int timeWaiting;
  
  /**
   * Constructor for a task.
   * @param num - the task number
   * @param resources - an array containing the initial amount of each resource
   * @param activities - the activities to be performed for this task
   */
  public Task(int num, int resources, ActivityList activities) {
    this.num        = num;
    status          = Status.RUNNING;
    this.activities = activities;
    this.resources  = new int[resources + 1];
    claims          = new int[resources + 1];
    resReq          = new int[2];
    resRel          = new int[2];
    compTime        = 0;
    timeTaken       = 0;
    timeWaiting     = 0;
  }
  
  public String[] peekNextActivity() {
    return activities.peek();
  }  
  public String[] getNextActivity() {
    return activities.next();
  }
  
  public void addActivity(String activity) {
    activities.add(activity);
  }
  
  public void setResReq(int res, int qty) {
    resReq[0] = res;
    resReq[1] = qty;
  }
  
  public void initiate(int res, int claim) {
    claims[res] = claim;
  }
  
  public void receiveRes(int res, int qty) {
    resources[res] += qty;
  }
  
  public void releaseRes(int res, int qty) {
    resources[res] -= qty;
    if (resources[res] < -1) resources[res] = 0;
  }
  
  public void terminate(int cycle) {
    timeTaken = cycle;
    status = Status.TERMINATED;
  }
  
  public void computing(int time) {
    this.compTime = time;
    status = Status.COMPUTING;
  }
  
  public void decCompT() {
    if (compTime > 0) compTime -= 1;
    if (compTime == 0) status = Status.RUNNING;
  }
  
  public void resume() {
    status = Status.RUNNING;
  }
  
  public void releasing(int rel, int relQty) {
    resRel[0] = rel;
    if (relQty > resources[rel]) resRel[1] = resources[rel];
    else resRel[1] = relQty;
    status = Status.RELEASING;
  }
  
  public void makeWait() {
    this.status = Status.WAITING;
  }
  
  public void incWaitCounter() {
    timeWaiting++;
  }
  
  public int[] abort() {
    //int[] resReleased = Arrays.copyOf(resources, resources.length);
    int[] resReleased = new int[resources.length];
    for (int i = 0; i < resources.length; i++) resReleased[i] = resources[i];
    for (int r = 1; r < resources.length; r++) resources[r] = 0;
    timeTaken = 0;
    timeWaiting = 0;
    status = Status.TERMINATED;
    return resReleased;
  }
  
  public Status getStatus() {
    return status;
  }
  
  public int req() {
    return resReq[0];
  }
  
  public int qty() {
    return resReq[1];
  }
  
  public int rel() {
    return resRel[0];
  }
  
  public int relQty() {
    return resRel[1];
  }
  
  public int getResQty(int res) {
    return resources[res];
  }
  
  public int getTimeTaken() {
    return timeTaken;
  }
  
  public int getTimeWaiting() {
    return timeWaiting;
  }
  
  public int getClaim(int res) {
    return claims[res];
  }
  
  public int getNum() {
    return num;
  }
  
  public int getCompT() {
    return compTime;
  }
  
  public String toString() {
    if (getTimeTaken()  == 0) return "Task " + num + "\tABORTED";
    int percTimeWaiting = Math.round(((float)getTimeWaiting()/getTimeTaken()) * 100);
    String s = "Task " + getNum() 
        + "\t" + getTimeTaken() 
        + "\t" + getTimeWaiting() 
        + "\t" + percTimeWaiting + "%";
    return s;
  }
  
}
