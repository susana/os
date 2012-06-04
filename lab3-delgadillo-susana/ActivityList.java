import java.util.ArrayList;
import java.util.List;

public class ActivityList {
  
  private int task;
  private int actPtr;
  private List<String> activities;
  
  /**
   * Constructor for a list of activities for a single task.
   * Each index in the array list is an activity (as a String).
   * @param task
   */
  public ActivityList(int task) {
    this.task = task;
    actPtr = 0;
    activities = new ArrayList<String>();
  }
  
  public void add(String activity) {
    activities.add(activity);
  }
  
  public String[] next() {
    String[] activity = activities.get(actPtr).split(" ");
    actPtr++;
    return activity;
  }
  
  public String[] peek() {
    return activities.get(actPtr).split(" ");
  }
  
  public int getTaskNum() {
    return task;
  }

}
