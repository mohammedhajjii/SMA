package com.example.sma.core.containers;

import com.example.sma.core.enums.AgentName;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import lombok.SneakyThrows;

public class Containers {
    @SneakyThrows(ControllerException.class)
    public static void startMainContainer(){
        Profile mainContainerProfile = new ProfileImpl();
        mainContainerProfile.setParameter(Profile.GUI, "true");

        Runtime.instance()
                .createMainContainer(mainContainerProfile)
                .start();
    }

    @SneakyThrows(StaleProxyException.class)
    public static void containerize(Agent agent, AgentName name){
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");

        Runtime.instance()
                .createAgentContainer(profile)
                .acceptNewAgent(name.getName(), agent)
                .start();
    }
}
