package aeshliman.simulation;

import aeshliman.enumerators.DeviceType;

public class Burst
{
	// Instance Variables
	private int totalDuration;
	private int remainingDuration;
	private DeviceType type;
	
	// Constructors
	public Burst(int totalDuration, DeviceType type)
	{
		this.totalDuration = totalDuration;
		this.remainingDuration = totalDuration;
		this.type = type;
	}
	
	// Getters and Setters.
	public int getTotalDuration() { return this.totalDuration; }
	public int getRemainingDuration() { return this.remainingDuration; }
	public DeviceType getType() { return this.type; }
	
	// Operations
	public int decrement() { return --remainingDuration; }
	
	// toString
	public String toString()
	{
		return type + " Burst - Duration: " + remainingDuration + "/" + totalDuration;
	}
}
