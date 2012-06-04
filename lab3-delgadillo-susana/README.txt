Lab 3 - 11/18/2011
Susana C Delgadillo

1)Compilation command:
  javac ActivityList.java Status.java Task.java ResourceManager.java OptimisticManager.java BankersAlgorithmManager.java Shell.java

2) Running command:
   (Run from within lab3-delgadillo-susana)
   java Shell input-##.txt

4)File descriptions:
  ActivityList.java
    A class for the list of activities for a single task.
  Status.java
    Enum for tasks' status/state.
  Task.java
    Class that represents a class. A task has a number assigned to it, a current status/state, the resource
    claims it makes, its latest request and release, the resources its been granted, etc.
  ResourceManager.java
    Parent class of OptimisticManager and Bankers AlgorithmManager.
  OptimisticManager.java
    FIFO algorithm implementation.
  BankersAlgorithmManager.java
    Banker's algorithm implementation.
  Shell.java
    The driver class. Parses the input, creates an instance of OptimisticManager and BankersAlgorithmManager, 
    and runs them.