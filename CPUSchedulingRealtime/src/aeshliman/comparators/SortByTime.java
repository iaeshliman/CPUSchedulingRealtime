package aeshliman.comparators;

import java.util.Comparator;

import aeshliman.simulation.Burst;
import aeshliman.simulation.CustomProcess;

public class SortByTime implements Comparator<CustomProcess>
{
	public int compare(CustomProcess p1, CustomProcess p2)
	{
		//System.err.println(p1 + "\n" + p2);
		//System.err.println("\t" + p1.peekTime() + " - " + p2.peekTime());
		
		return p1.peekTime() - p2.peekTime();
	}
}
