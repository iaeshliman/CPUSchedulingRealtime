package aeshliman.comparators;

import java.util.Comparator;

import aeshliman.simulation.Burst;
import aeshliman.simulation.CustomProcess;

public class SortByPriority implements Comparator<CustomProcess>
{
	public int compare(CustomProcess p1, CustomProcess p2)
	{
		return p1.getPriority() - p2.getPriority();
	}
}
