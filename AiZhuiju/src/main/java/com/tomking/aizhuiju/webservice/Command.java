package com.tomking.aizhuiju.webservice;

/**
 * Created by xtang on 14-4-2.
 */
public abstract class Command {
    private int commandType;

    public Command(int commandType){
        this.commandType = commandType;
    }

    public abstract Object execute();
    public int getCommandType() {
        return commandType;
    };

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }
}


