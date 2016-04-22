package org.epiccraft.dev.webnode.runtime.network.modules;

import org.epiccraft.dev.webnode.runtime.network.NetworkManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Project WebNode
 */
public class ModuleManager {

    private NetworkManager networkManager;
    private List<Module> modules;

    public ModuleManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
        modules = new LinkedList<>();
    }

    public void attach(Module module){
        modules.add(module);
    }

    public void detach(Module module){
        module.onDisabled(networkManager);
        modules.remove(module);
    }

    public Module[] getModule(Class<? extends Module> type){
        List<Module> list = new LinkedList<>();

        for (Module m : modules){
            if (m.getClass().equals(type)){
                list.add(m);
            }
        }

        Module[] result = new Module[list.size()];
        for (int i = 0; i < list.size(); i++){
            result[i] = list.get(i);
        }
        return result;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void initModules() {
        for (Module module : modules) {
            module.onEnabled(networkManager);
        }
    }

}
