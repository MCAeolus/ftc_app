package org.firstinspires.ftc.teamcode.velocityvortex.smidautils;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;
import java.util.List;

/**
 * This util class was written by Nathan Smith on 9/13/16.
 * This class is provided for ease of use when writing autonomous based
 * opmodes.
 */
@Deprecated()
public class InstructionSet {

    private ArrayList<Instruction> instructionMap = new ArrayList<Instruction>();
    
    public InstructionSet(InstructionSet override) {
        //instructionMap.addAll(override.getInstructions());
        throw new UnsupportedOperationException("This class is not working.");
    }

    public InstructionSet(Instruction ... instructions){
        //instructionMap.addAll(Arrays.asList(instructions));
        throw new UnsupportedOperationException("This class is not working.");
    }

    public List<Instruction> getInstructions(){ return instructionMap; }

    public Instruction getInstruction(int loc){ return instructionMap.get(loc); }

    public Instruction getInstruction(String instruction, int place){
        int found = 0;
        for(Instruction x : instructionMap)
            if(x.NAME.equalsIgnoreCase(instruction))
                if(++found >= place) return x;
        return null;
    }

    public Instruction getInstruction(String instruction){ return getInstruction(instruction, 1); }

    public void removeInstruction(String instruction) { instructionMap.remove(getInstruction(instruction)); }

    public void removeInstruction(String instruction, int place) { instructionMap.remove(getInstruction(instruction, place)); }

    public void removeInstruction(int loc){ instructionMap.remove(loc); }

    public void addInstruction(Instruction instruction){
        instructionMap.add(instruction);
    }

    public void insertInstruction(Instruction instruction, int index){ instructionMap.add(index, instruction); }

    public void executeInstruction(int place, OpMode robot){
        if(place > -1 && place < instructionMap.size()) instructionMap.get(place).execute(robot);
    }

    public void executeInstructions(OpMode robot){
        synchronized (robot) {
            for (int i = 0; i < instructionMap.size(); i++){
                executeInstruction(i, robot);
                ((LinearOpMode)robot).idle();
            }
        }
    }
}
