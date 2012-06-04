import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class BankersAlgorithmManager extends ResourceManager {
  
  /**
   * Constructor
   * @param tasks - array of tasks in the system
   * @param numTasks - total number of tasks initially
   * @param resources - array of the resource quantities
   * @param numRes - total number of resources initially
   */
  public BankersAlgorithmManager(
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
   * In this algorithm, tasks are initiated and claims do matter so each claim
   * is checked to ensure no claim is made that exceeds the quantity of a
   * resource.
   * At the end of the cycle, tasks that have had their requests granted (and
   * are waiting) are resumed, and any resources that were released from a task 
   * are added back to the system.
   */
  public void run() {
    int done     = 0;
    String act   = "";
    int cycle    = 0;
    int task     = 0;
    int res      = 0;
    int qty      = 0;
    
    System.out.println("\n---------- Banker's Algorithm ----------");
    
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
            if (resources[res] < qty) {
              tasks[t].abort();
              done++;
              System.out.println("\tTask " + t + " " + act + "s " + qty 
                  + " units of resource " + res + ". ABORTED");
            }
            else {
              tasks[t].initiate(res, qty);
              System.out.println("\tTask " + t + " claims " + qty 
                  + " units of resource " + res + ".");
            }
          }
          else if (act.equals("request")) {
            if (!isValidReq(t, res, qty)) {
              abortTask(t);
              done++;
              System.out.println("\tTask " + task + " requests " + qty 
                  + " units of resource " + res + ". ABORTED");
            }
            else {
              if (isSafe(t, res, qty) && isAvail(res, qty)) {
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
      recvRel(); // Sys regains resources here.      
      for (int r = 1; r <= numRes; r++)
        System.out.println("\tResource " + r + ": " + resources[r] 
            + " units available at " + (cycle + 1));      
      cycle++;
    } // end while
    
    printOutput("BANKER'S");
  }
  
  /**
   * Iterates through the tasks in the waitList and grants requests if possible,
   * first checking if its safe to grant the request.
   * Also removes tasks that had been terminated due to deadlock.
   */
  void procWaitingReq() {
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
      
      if (status.equals(Status.TERMINATED)) {
        itr.remove();
      }
      else {
        // resume
        if (isSafe(taskNum, req, qty) && status.equals(Status.WAITING) 
            && isAvail(req, qty)) {
          System.out.println("\t\tTask " + taskNum 
              + " is granted " + qty 
              + " of resource " + req + ".");
          grantRes(req, qty);
          currentTask.receiveRes(req, qty);
          itr.remove();
          tasksToResume.offer(currentTask);
        }
        // waiting
        else if (status.equals(Status.WAITING) && !isAvail(req, qty)) {
          System.out.println("\t\tTask " + taskNum 
              + " pending for " + qty 
              + " of resource " + req + ".");
        }
        // unsafe
        else if (!isSafe(taskNum, req, qty)) {
          System.out.println("\t\t(Not safe) Task " + taskNum 
              + " pending for " + qty 
              + " of resource " + req + ".");
        }
        currentTask.incWaitCounter();
      }
    }
  }
  
  /**
   * Checks whether a request is valid. A request is considered valid if the
   * request does NOT exceed the available quantity of a particular resource.
   * @param task - number of the task making the request
   * @param res - the resource the task is requesting
   * @param qty - the amount the task is requesting
   * @return - true is the request is valid, false if not
   */
  private boolean isValidReq(int task, int res, int qty) {
    if (tasks[task].getClaim(res) >= qty
        && (tasks[task].getResQty(res) + qty) <= tasks[task].getClaim(res)) return true;
    return false;
  }

  /**
   * Checks to see if a request being made by a task would be safe, and not
   * cause a deadlock. A copy of the current amount of resources is made
   * and is used to "pretend" to grant the task's request. Then for each task,
   * its checked if all of its resource claims can be completed at this point.
   * If at least one task can be terminated, then the system is considered safe.
   * @param task - task that is making request
   * @param res - resource the task is requesting
   * @param qty - amount the task is requesting
   * @return
   */
  private boolean isSafe(int task, int res, int qty) {
    //int[] resCopy = Arrays.copyOf(resources, resources.length);
    int[] resCopy = new int[resources.length];
    for (int i = 0; i < resources.length; i++) resCopy[i] = resources[i];
    int grantableReq = 0;
    int terminateTasks = 0;
    
    resCopy[res] -= qty;
    
    for (int t = 1; t <= numTasks; t++) {
      if (!tasks[t].getStatus().equals(Status.TERMINATED)) {
        for (int r = 1; r <= numRes; r++) {
          if (t == task && r == res) {
            if ((resCopy[res]  + tasks[task].getResQty(res) + qty) 
                - tasks[task].getClaim(res) > -1)
              grantableReq++;
          }
          else {
            if ((resCopy[r]  + tasks[t].getResQty(r)) 
                - tasks[t].getClaim(r) > -1) {
              grantableReq++;
            }
          }
        }
        if (grantableReq == numRes) terminateTasks++;
        grantableReq = 0;
      }
    }

    if (terminateTasks > 0) return true;
    return false;    
  }
}
