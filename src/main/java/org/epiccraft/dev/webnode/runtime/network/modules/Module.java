package org.epiccraft.dev.webnode.runtime.network.modules;

import org.epiccraft.dev.webnode.runtime.network.NetworkManager;

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

	public abstract void onEnabled(NetworkManager networkManager);

	public abstract void onDisabled(NetworkManager networkManager);
	
}
