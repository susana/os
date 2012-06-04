import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

public class Linker
{	
	private String fileName; // file name containing modules.
	private int[] baseAddr; // Array to hold base addresses of modules.
	private HashMap<String, Integer> symbolsUsed; // Used symbols have appeared at least once in use list.
	private HashMap<String, Integer> symbolsNotUsed; // Unused symbols never appear in use list.
	// If a symbol appears in either HashMap, it has been declared. To see if it has been defined, check its value.
	// If value < -1, then the symbol has never been defined. Otherwise it has a definition.
	
	/*
	 * 1 param constructor takes a String with the fileName.
	 * Checks if the file exists.
	 */
	public Linker( String fileName )
	{
		this.fileName = fileName;
		this.baseAddr = new int[10];
		this.symbolsUsed = new HashMap<String, Integer>();
		this.symbolsNotUsed = new HashMap<String, Integer>();
	}
	
	
	/*
	 * passOne takes String fileName containing the name of the input file.
	 * "...Determines the base address for each module and the absolute
	 * address for each external symbol, storing the latter in the symbol table..." 
	 */
	public void passOne( )
	{
		try
		{
			FileReader fReader = new FileReader( fileName );
			Scanner scanner = new Scanner ( fReader );
			
			int count, moduleCount = 0;
			String symbol, addrType = "";
			int definition, absDef, base, addr = 0;
			
			System.out.print( "Symbol Table" );
			
			while( scanner.hasNext() )
			{
				// Definition list.
				count = scanner.nextInt();
				while( count > 0 )
				{
					symbol = scanner.next();
					definition = scanner.nextInt();
					
					absDef = definition + baseAddr[moduleCount]; // Compute absolute addr of symbol def.
					if( (symbolsUsed.get(symbol) != null && symbolsUsed.get(symbol) != -1 )
							|| symbolsNotUsed.get(symbol) != null && symbolsNotUsed.get(symbol) != -1 )// Check HashMaps to see if symbol was declared and defined already.
					{
						System.out.print( "Error: This variable is multiply defined. The first value has been used." );
					}
					else
					{
						symbolsNotUsed.put( symbol, absDef ); // Add new symbol to HashMap with its abs def.
						System.out.print( "\n" + symbol +  " = " + absDef + " " );
					}
					count--;
				}
				
				// Use list.
				count = scanner.nextInt();
				while( count > 0 )
				{
					symbol = scanner.next();
					count--;
				}
				
				// Program text.
				count = scanner.nextInt();
				baseAddr[moduleCount+1] += baseAddr[moduleCount] + count; // Compute base addr of next module.
				while( count > 0 )
				{
					addrType = scanner.next();
					addr = scanner.nextInt();
					count--;
				}
				
				moduleCount++;
			} // end while
		}
		catch( FileNotFoundException fnfe )
		{
			System.err.println("FileNotFoundException: " + fnfe.getMessage());
		}
	}
	
	
	/*
	 * passTwo "...uses the base addresses and the symbol table computed in pass one
	 * to generate the actual output by relocating relative addresses and resolving
	 * external references..." 
	 */
	public void passTwo( )
	{
		try
		{
			FileReader fReader = new FileReader( fileName );
			Scanner scanner = new Scanner ( fReader );
			
			int count, moduleCount = 0;
			int line = 0;
			int definition = 0;
			int useListPos = 0;
			int addr, resolvedAddr = 0;
			String symbol, addrType = "";
			int[] symbolUseList = new int[10];
			String[] unusedSymbErr = new String[10];
			
			System.out.println( "\n\nMemory Map" );
			
			while( scanner.hasNext() )
			{
				// Definition list.
				count = scanner.nextInt();
				while( count > 0 )
				{
					symbol = scanner.next();
					definition = scanner.nextInt();
					count--;
				}
				
				// Use list.
				count = scanner.nextInt();
				while( count > 0 )
				{
					symbol = scanner.next(); // Symbol that appears in use list.
					if( symbolsNotUsed.get(symbol) != null ) // If symbol was unused, remove it and put it in SymbolsUsed.
					{
						int def = symbolsNotUsed.get(symbol); // Get the symbol's value.
						symbolsNotUsed.remove(symbol); // Remove symbol from unused list.
						symbolsUsed.put(symbol, def); // Add symbol to used list.
						symbolUseList[useListPos] = def; // Add value to array.
					}
					if( symbolsUsed.get(symbol) == null ) // Check if symbol is defined.
						//Remember, a symbol can't be defined if it hasn't been declared.
					{
						unusedSymbErr[useListPos] = " Error: " + symbol + " is not defined; zero used.";
						symbolsUsed.put(symbol, 0); // Add new symbol to the list of used symbols with val 0.
					}
					else
					{
						symbolUseList[useListPos] = symbolsUsed.get(symbol);
						unusedSymbErr[useListPos] = ""; // Note that there is no error.
					}
					useListPos++;
					count--;
				}
				
				// Program text.
				count = scanner.nextInt();
				while( count > 0 )
				{
					addrType = scanner.next();
					addr = scanner.nextInt();
					//System.out.println( addrType + " " + addr );
					
					if( addrType.equals("A") || addrType.equals("I"))
					{
						if( addrType.equals("A") && (addr % 1000) > 600 )
						{
							System.out.print( line + ": " + (addr - (addr % 1000)) );
							System.out.println( " Error: Absolute address exceeds machine size; zero used." );
						}
						else
						{
							System.out.println( line + ": " + addr );
						}
					}
					if( addrType.equals("E") )
					{
						if( addr % 1000 < 10 )
							resolvedAddr = symbolUseList[addr % 1000] + addr; // External addr = Symbol def +  addr.
						if( (addr % 1000) >= useListPos )
						{
							System.out.print( line + ": " + addr );
							System.out.println( " Error: External address exceeds length of use list; treated as immediate." );
						}
						else
						{
							System.out.print( line + ": " + resolvedAddr );
							if( unusedSymbErr[addr % 1000] == null ) 
							{
								// Do nothing.
							}
							else
							{
								System.out.println( unusedSymbErr[addr % 1000] );
							}
						}
					}
					if( addrType.equals("R"))
					{
						if( ((addr % 1000) > baseAddr[moduleCount+1] - baseAddr[moduleCount]) )
						{
							System.out.print( line + ": " + (addr - (addr % 1000)) );
							System.out.println( " Error: Relative address exceeds module size; zero used." );
						}
						else
						{
							resolvedAddr = baseAddr[moduleCount] + addr; // Relative addr = baseAddr[moduleCount] + addr.
							System.out.println( line + ": " + resolvedAddr );
						}
					}
					
					line++;
					count--;
				}
				
				useListPos = 0;
				moduleCount++;
			} // end while
			
			checkUnusedSymbols();
			
		}
		catch( FileNotFoundException fnfe )
		{
			System.err.println("FileNotFoundException: " + fnfe.getMessage());
		}
	}
	
	
	/*
	 * checkUnusedSymbols iterates through the symbolsNotUsed hashMap for symbols 
	 * that were never used in the program, and prints a warning for these symbols.
	 * */
	private void checkUnusedSymbols( )
	{
		for( String s : symbolsNotUsed.keySet() )
		{
			System.out.println("Warning: " + s + " defined but never used." );
		}
	}
	
	
	public static void main( String [ ] args )
	{
		Linker linker = new Linker( args[0] );
		linker.passOne();
		linker.passTwo();
	}
}

