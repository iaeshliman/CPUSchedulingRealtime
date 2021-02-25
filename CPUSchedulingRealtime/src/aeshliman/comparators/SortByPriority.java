package aeshliman.comparators;

import java.util.Comparator;

import aeshliman.structure.Burst;
import aeshliman.structure.CustomProcess;

public class SortByPriority implements Comparator<CustomProcess>
{
	public int compare(CustomProcess p1, CustomProcess p2)
	{
		return p1.getPriority() - p2.getPriority();
	}
}
