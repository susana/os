import java.util.LinkedList;

public abstract class ResourceManager {

   /** 
    * Fields
    * tasks - Stores all tasks. Task number corresponds to index. 
    *   Array starts at index 1.
    * numTasks - Total number of tasks in the system initially.
    * resources - Stores all real time res quantities. Res number corresponds 
    *   to index. Starts at index 1.
    * numRes - Total number of resources in the system initially.
    * waitList - Tasks that have a request that needs to be satisfied.
    * tasksToResume - Tasks that had a request satisfied and are to resume at 
    *   the end of a cycle.
    */
  protected Task[] tasks;
  protected int numTasks;
  protected int[] resources;
  protected int numRes;
  protected LinkedList<Task> waitList;
  protected LinkedList<Task> tasksToResume;

  abstract void run();
  abstract void procWaitingReq();
  
  /**
   * Any task that has a Status of RELEASING will have the specified amount of
   * a particular resources released from its own holding and given back to
   * the system. The task is now considered RUNNING.
   */
  protected void recvRel() {
    int res, qty;
    for (int t = 1; t <= numTasks; t++) {
      if (tasks[t].getStatus().equals(Status.RELEASING)) {
        res = tasks[t].rel();
        qty = tasks[t].relQty();
        tasks[t].releaseRes(res, qty);
        resources[res] += qty;
        tasks[t].resume();
      }
    }
  }
  
  /**
   * Checks if a particular amount of a resource is available.
   * @param res - the number of the resource to be looked at
   * @param qty - how much of the resource is needed
   * @return true if the amount of the resource is available, false if not
   */
  protected boolean isAvail(int res, int qty) {
    if (resources[res] - qty >= 0) return true;
    return false;
  }
  
  /**
   * Resource manager removes the resources to be granted.
   * @param res - the resource being given
   * @param qty - the amount being given
   */
  protected void grantRes(int res, int qty) {
    if (isAvail(res, qty)) resources[res] -= qty;
  }
  
  /**
   * Resumes a waiting task.
   */
  protected void resumeWaitingTasks() {
    Task t = null;
    while (!tasksToResume.isEmpty()) {
      t = tasksToResume.remove();
      t.resume();
    }
  }
  
  /**
   * Aborts a particular task. The task releases the resources it is currently
   *  holding, gives it all back to the system and is now considered TERMINATED.
   * @param task - the task to be aborted
   */
  protected void abortTask(int task) {
    int qty = 0;
    int[] resToBeRel = tasks[task].abort();
    for (int r = 1; r <= numRes; r++) {
      qty = resToBeRel[r];
      tasks[task].releaseRes(r, qty);
      resources[r] += qty;
    }
  }
  
  /**
   * (From the lab 3 PDF) "Print[s], for each task, the time taken, the waiting 
   * time, and the percentage of time spent waiting. Also print[s] the total time 
   * for all tasks, the total waiting time, and the overall percentage of time 
   * spent waiting."
   * @param alg - which algorithm was performed
   */
  protected void printOutput(String alg) {
    int totalTimeTaken   = 0;
    int totalTimeWaiting = 0;
    int percTimeWaiting  = 0;
    
    System.out.println("\n" + alg);
    for (int t = 1; t <= numTasks; t++) {
      System.out.println(tasks[t].toString());
      totalTimeTaken += tasks[t].getTimeTaken();
      totalTimeWaiting += tasks[t].getTimeWaiting();
    }
    percTimeWaiting = Math.round(((float)totalTimeWaiting/totalTimeTaken)*100);
    
    System.out.println("Total\t" + totalTimeTaken + "\t" + totalTimeWaiting 
        + "\t" + percTimeWaiting + "%");
  }
  
}
