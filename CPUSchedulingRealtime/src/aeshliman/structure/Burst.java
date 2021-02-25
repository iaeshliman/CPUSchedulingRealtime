package aeshliman.structure;

import aeshliman.enumerators.DeviceType;

public class Burst
{
	// Instance Variables
	private int duration;
	private DeviceType type;
	
	// Constructors
	public Burst(int duration, DeviceType type)
	{
		this.duration = duration;
		this.type = type;
	}
	
	// Getters and Setters
	public int getDuration() { return this.duration; }
	public DeviceType getType() { return this.type; }
	
	// Operations
	public int decrement()
	{
		return duration--;
	}
	
	// toString
	public String toString()
	{
		return type + " Burst - Duration: " + duration; 
	}
}
