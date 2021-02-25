package aeshliman.comparators;

import java.util.Comparator;

import aeshliman.structure.Burst;
import aeshliman.structure.CustomProcess;

public class SortByTime implements Comparator<CustomProcess>
{
	public int compare(CustomProcess p1, CustomProcess p2)
	{
		return p1.peekTime() - p2.peekTime();
	}
}
