import java.util.Iterator;
import java.util.LinkedList;

public class OptimisticManager extends ResourceManager {  
  
  /**
   * Constructor.
   * @param tasks - array of tasks in the system
   * @param numTasks - total number of tasks initially
   * @param resources - array of the resource quantities
   * @param numRes - total number of resources initially
   */
  public OptimisticManager(
      Task[] tasks, int numTasks, int[] resources, int numRes) {
    waitList = new LinkedList<Task>();
    this.resources = resources;
    this.numRes = numRes;
    this.tasks = tasks;
    this.numTasks = numTasks;
    tasksToResume = new LinkedList<Task>();
  }
  
  /**
   * Performs the activities for each task and prints the output once all tasks
   * have been terminated or aborted.
   * Waiting/blocked tasks are processed first to see if any of their requests
   * can be granted. Then the next activity for each task is performed.
   * Tasks are initiated with a claim but are not checked as this algorithm
   * makes no use of them.
   * At the end of the cycle, tasks that have had their requests granted (and
   * are waiting) are resumed, deadlocks are checked for and dealt with if
   * present, and any resources that were released from a task are added back 
   * to the system.
   */
  public void run() {
    int done     = 0;
    String act   = "";
    int cycle    = 0;
    int task     = 0;
    int res      = 0;
    int qty      = 0;
    
    System.out.println("---------- FIFO Algorithm ----------");
    
    while (done != numTasks) { // Cycle
      System.out.println("\nCycle " + cycle + "-" + (cycle + 1));
      procWaitingReq();
      for (int t = 1; t <= numTasks; t++) {
        if (tasks[t].getStatus().equals(Status.RUNNING)
            || tasks[t].getStatus().equals(Status.RELEASING)
            && tasks[t].peekNextActivity() != null) {
          String[] activity = tasks[t].getNextActivity();
          
          act = activity[0];
          task = Integer.parseInt(activity[1]); // init, req, rel, comp, term
          if (activity.length > 2 && activity[2] != null) 
            res = Integer.parseInt(activity[2]); // init, req, rel, comp
          if (activity.length > 3 && activity[3] != null) 
            qty = Integer.parseInt(activity[3]); // init, req, rel
          
          if (act.equals("initiate")) {
            tasks[t].initiate(res, qty);
            System.out.println("\tTask " + t + " claims " + qty 
                + " units of resource " + res + ".");
          }
          else if (act.equals("request")) {
            if (isAvail(res, qty)) {
              grantRes(res, qty);
              tasks[t].receiveRes(res, qty);
              System.out.println("\tTask " + t + " requests " + qty 
                  + " units of resource " + res + ". Request GRANTED.");
            }
            else {
              tasks[t].makeWait();
              tasks[t].setResReq(res, qty);
              waitList.offer(tasks[t]);
              System.out.println("\tTask " + t + " requests " + qty 
                  + " units of resource " + res 
                  + ". Request DENIED. Placed on waiting list.");
            }
          }
          else if (act.equals("release")) {
            tasks[t].releasing(res, qty);
            System.out.println("\tTask " + t + " to release " + tasks[t].relQty() 
                + " units of resource " + res + ", available at cycle " 
                + (cycle + 1) + ".");
          }
          else if (act.equals("compute")) {
            tasks[t].computing(res);
          }          

          if (act.equals("terminate")) {
            tasks[t].terminate(cycle);
            System.out.println("\tTask " + t + " terminated at " + (cycle));
            done++;
          }
        } // end if
        
        if (tasks[t].getStatus().equals(Status.COMPUTING)) {
          tasks[t].decCompT();
          System.out.println("\tTask " + t + " is computing. " 
              + tasks[t].getCompT() + " cycles left.");
        }
      } // end for
      
      resumeWaitingTasks();
      while (done != numTasks && isDeadlocked()) {
        done++;
        resolveDeadlock(); // Sys regains resources here.
      }
      recvRel(); // Sys regains resources here as well.      
      for (int r = 1; r <= numRes; r++)
        System.out.println("\tResource " + r + ": " + resources[r] 
            + " units available at " + (cycle + 1));      
      cycle++;
    } // end while
    
    printOutput("FIFO");
  }
  
  /**
   * Iterates through the tasks in the waitList and grants requests if possible.
   * Also removes tasks that had been terminated due to deadlock.
   */
  public void procWaitingReq() {
    if (waitList.size() == 0) return;
    
    System.out.println("\tCheck blocked tasks: ");
    Iterator<Task> itr = waitList.iterator();
    Task currentTask = null;
    int taskNum = 0;
    int req = 0;
    int qty = 0;
    Status status = null;
    
    while (itr.hasNext()) {
      currentTask = itr.next();
      taskNum = currentTask.getNum();
      req = currentTask.req();
      qty = currentTask.qty();
      status = currentTask.getStatus();
      
      // Remove tasks that had been terminated due to deadlock.
      if (status.equals(Status.TERMINATED)) {
        itr.remove();
      }
      else {
        // Request granted, waiting -> running.
        if (status.equals(Status.WAITING) && isAvail(req, qty)) {
          System.out.println("\t\tTask " + taskNum 
              + " is granted " + qty 
              + " of resource " + req + ".");
          grantRes(req, qty);
          currentTask.receiveRes(req, qty);
          itr.remove();
          tasksToResume.offer(currentTask);
        }
        // Request can't be granted, task remains waiting.
        else if (status.equals(Status.WAITING) && !isAvail(req, qty))
          System.out.println("\t\tTask " + taskNum 
              + " pending for " + qty
              + " of resource " + req + ".");
        currentTask.incWaitCounter();
      }
    }
  }
  
  /**
   * Checks if a deadlock has occurred. Counts the number of non-terminated tasks
   * and number of waiting tasks. If these numbers are equal, that means all
   * non-terminated tasks are blocked.
   * @return - true if all non-terminated tasks are blocked, false if not
   */
  private boolean isDeadlocked() {
    int blockedTasks = 0;
    int nonTermTasks = 0;
    for (int t = 1; t <= numTasks; t++) {
      if (!tasks[t].getStatus().equals(Status.TERMINATED)) {
        nonTermTasks++;
      }
      if (tasks[t].getStatus().equals(Status.WAITING)) {
        int res = tasks[t].req();
        int qty = tasks[t].qty();
        if (!isAvail(res, qty)) blockedTasks++;
      }
    }
    if (blockedTasks == nonTermTasks) return true;
    return false;
  }
  
  /**
   * Gets lowest number task that is involved in the deadlock and aborts it.
   * First iterates through the waiting tasks to get the lowest numbered one
   * and then calls abortTask to abort it.
   */
  private void resolveDeadlock() {
    Task minNumTask = null;
    Task currentTask = null;
    
    for (int t = 1; t <= numTasks; t++) { 
      if (tasks[t].getStatus().equals(Status.WAITING)) {
        if (minNumTask == null) {
          minNumTask = tasks[t];
          currentTask = tasks[t];
        }
        if (minNumTask.getNum() > currentTask.getNum()) 
          minNumTask = currentTask;
      }
    }
    
    System.out.println("\tReleasing deadlocked task " + minNumTask.getNum() 
        + "'s resources. Task " + minNumTask.getNum() + " ABORTED.");
    abortTask(minNumTask.getNum());
  }
}
