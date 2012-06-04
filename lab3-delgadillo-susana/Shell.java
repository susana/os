import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Shell {
  
  private Scanner s;
  
  /**
   * Constructor.
   */
  public Shell() {
    // Do nothing.
  }
  
  /**
   * Parses input file and returns array of ResourceManagers. Each 
   * ResourceManager contains the tasks and resources to be managed.
   * @return array of ResourceManagers
   */
  private ResourceManager[] parseInput() {
    int numTasks      = s.nextInt();
    Task[] tasks = new Task[numTasks + 1]; // Starts at index 1.
    Task[] tasksCopy = new Task[numTasks + 1]; // Starts at index 1.
    int numRes        = s.nextInt();
    int[] resources   = new int[numRes + 1]; // Starts at index 1.
    int[] resourcesCopy   = new int[numRes + 1];
    
    String act = "";
    int task = 0;
    String line = "";
    
    for (int t = 1; t <= numTasks; t++) {
      tasks[t] = new Task(t, numRes, new ActivityList(t));
      tasksCopy[t] = new Task(t, numRes, new ActivityList(t));
    }
    
    for (int r = 1; r <= numRes; r++) {
      resources[r] = s.nextInt();
      resourcesCopy[r] = resources[r];
    }
    
    while (s.hasNext()) {
      act = s.next();
      task = s.nextInt();
      line = act + " " + task + " ";
      while (s.hasNextInt()) line += s.nextInt() + " ";
      tasks[task].addActivity(line);
      tasksCopy[task].addActivity(line);
    }
    
    // Create res managers.
    ResourceManager[] resMgrs = new ResourceManager[2];
    resMgrs[0] = new OptimisticManager(tasks, numTasks, resources, numRes);
    resMgrs[1] = new BankersAlgorithmManager(tasksCopy, numTasks, resourcesCopy, numRes);
    
    return resMgrs;
  }
  
  /**
   * Reads in file and creates scanner for the file. Returns true if the file
   * exists and false if it doesn't.
   * @param fname - name of the file to be read
   * @return true if file exists and false if file doesn't exist
   */
  private boolean readInfile(String fname) {
    try {
      BufferedReader buf = new BufferedReader(new FileReader(fname));
      s = new Scanner(buf);
    }
    catch(FileNotFoundException e) {
      System.err.println("FileNotFoundException: " + e.getMessage());
      return false;
    }
    return true;
  }
  
  /**
   * Parses cmd line args for a valid file name, parses the input if file 
   * exists and runs each resource manager. If file 
   * doesn't exist, program exits.
   * @param args - command line input
   */
  public void parseCmdLine(String[] args) {
    if( !args[0].equals(null) ) {
      if( readInfile(args[0]) == false ) System.exit(0);
      System.out.println("File name " + args[0]);
      ResourceManager resMgrs[] = parseInput();
      for (ResourceManager rm : resMgrs) rm.run();
    }
    else {
      System.out.println("No args provided.");
    }
  }
  
  public static void main(String[] args) {
    Shell s = new Shell();
    s.parseCmdLine(args);
  }
}
