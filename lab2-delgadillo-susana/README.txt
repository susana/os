Lab 2 - Scheduling Algorithms
Susana C Delgadillo

1) Compilation command:
   javac Process.java HprnProcess.java Scheduler.java Shell.java UniScheduler.java FcfsScheduler.java RrScheduler.java HprnScheduler.java

2) Running command:
   (Run from within lab2-delgadillo-susana)
   java Shell --verbose input-#.txt
   OR
   java Shell input-#.txt
   
4) File descriptions:
   Process.java 
      Contains the Process class which stores information about processes.
      This information is used in the schedulers.
   HprnProcess.java
      Is a subclass of Process and is the type of process used in HprnScheduler.
   Scheduler.java
      Is an abstract class that contains a large chunk of the implementation for the uni, fcfs and rr schedulers.
   Shell.java
      This is where the schedulers are run from.
      Parsing of the cmd line args and input occurs here.
   UniScheduler.java
      Uni programming scheduler.
      Extends Scheduler.
      Uses Process class.
   FcfsScheduler.java
      Fcfs scheduler.
      Extends Scheduler.
      Uses Process class.
   RrScheduler.java
      RR scheduler.
      Extends Scheduler.
      Uses Process class.
   HprnScheduler.java
      Hprn scheduler.
      Does NOT extend Scheduler.
      Uses HprnProcess class.