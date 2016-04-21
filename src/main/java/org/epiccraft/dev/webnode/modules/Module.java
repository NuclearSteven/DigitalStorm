package org.epiccraft.dev.webnode.modules;

public abstract class Module
{
	
	private boolean enabled;

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isEnabled()
	{
		return enabled;
	}
	
}
