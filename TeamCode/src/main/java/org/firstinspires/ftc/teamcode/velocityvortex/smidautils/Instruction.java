package org.firstinspires.ftc.teamcode.velocityvortex.smidautils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.Arrays;
import java.util.List;

/**
 * This util class was written by Nathan Smith on 9/13/16.
 * This class is provided for ease of use when writing autonomous based
 * opmodes.
 */

@Deprecated
public class Instruction {

    public final String NAME;
    private List<Object> data;

    public Instruction(String name, Object...data){
        this.NAME = name;
        this.data = Arrays.asList(data);
    }

    public void execute(OpMode robot){
        /*
        override for instruction placement
         */
    }

    public void modify(Object...data){ this.data = Arrays.asList(data); }

    public void modify(Object data, int index){ this.data.set(index,data); }

    protected List<Object> getData(){
        return this.data;
    }
}

