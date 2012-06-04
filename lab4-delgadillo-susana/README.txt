Lab 4
12/10/2011
Susana C Delgadillo

1) Compilation command.
    javac Page.java Proc.java Pager.java Driver.java

2) Running command.
    java Driver <machine size> <page size> <process size> <job mix> <number of references> <algorithm> <debug>

    Debug can take on a value of 0, 1 or 11.
	0: normal output
	1: debugging
	11: shows the random values used
	
4) File descriptions.
    Driver.java contains main. It generates the memory references and prints the final output.
	Pager.java contains the pager algorithm logic.
	Proc.java is a class that represents individual processes.
	Page.java is a class that represents individual pages.