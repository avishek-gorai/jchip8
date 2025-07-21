/* Copyright (C) 2025  Avishek Gorai <avishekgorai@myyahoo.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package avishek.gorai.jchip8.jchip8_app;

import java.util.Random;

public class Processor {
    private int[] stack, registers;
    private int instructionPointer, stackPointer, delayTimer, soundTimer, index;
    private Display screen;
    private Keypad keypad;
    private Memory memory;

    Processor() {
        setStack(new int[0xF]);
        setRegisters(new int[0xF]);
        setInstructionPointer(0x200);
        setDelayTimer(0);
        setSoundTimer(0);
        setScreen(new Display());
        setKeypad(new Keypad());
        setMemory(new Memory());
    }
    
    private Processor incrementInstructionPointerBy(int n) {
        setInstructionPointer(getInstructionPointer() + n);
        return this;
    }
    
    private Processor incrementInstructionPointer() {
        incrementInstructionPointerBy(1);
        return this;
    }

    /**
     * @return the stack
     */
    private int[] getStack() {
        return stack;
    }

    /**
     * @param stack the stack to set
     */
    private Processor setStack(int[] stack) {
        this.stack = stack;
        return this;
    }

    /**
     * @return the register
     */
    private int[] getRegisters() {
        return registers;
    }

    /**
     * @param register the register to set
     */
    private Processor setRegisters(int[] register) {
        this.registers = register;
        return this;
    }

    /**
     * @return the instruction_pointer
     */
    private int getInstructionPointer() {
        return instructionPointer;
    }

    /**
     * @param instruction_pointer the instruction_pointer to set
     */
    private Processor setInstructionPointer(int instruction_pointer) {
        this.instructionPointer = instruction_pointer;
        return this;
    }

    /**
     * @return the stack_pointer
     */
    private int getStackPointer() {
        return stackPointer;
    }

    /**
     * @param stack_pointer the stack_pointer to set
     */
    private Processor setStackPointer(int stack_pointer) {
        this.stackPointer = stack_pointer;
        return this;
    }

    /**
     * @return the delay_timer
     */
    private int getDelayTimer() {
        return delayTimer;
    }

    /**
     * @param delay_timer the delay_timer to set
     */
    private Processor setDelayTimer(int delay_timer) {
        this.delayTimer = delay_timer;
        return this;
    }

    /**
     * @param sound_timer the sound_timer to set
     */
    private Processor setSoundTimer(int sound_timer) {
        this.soundTimer = sound_timer;
        return this;
    }

    /**
     * @return the index
     */
    private int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    private Processor setIndex(int index) {
        this.index = index % 0x1000;
        return this;
    }

    /**
     * @return the screen
     */
    private Display getScreen() {
        return screen;
    }

    /**
     * @param screen the screen to set
     */
    private Processor setScreen(Display screen) {
        this.screen = screen;
        return this;
    }

    /**
     * @return the keypad
     */
    private Keypad getKeypad() {
        return keypad;
    }

    /**
     * @param keypad the keypad to set
     */
    private Processor setKeypad(Keypad keypad) {
        this.keypad = keypad;
        return this;
    }

    /**
     * @return the memory
     */
    private Memory getMemory() {
        return memory;
    }

    /**
     * @param memory the memory to set
     */
    private Processor setMemory(Memory memory) {
        this.memory = memory;
        return this;
    }
    
    private int getRegister(int n) {
        return getRegisters()[n];
    }
    
    private Processor setRegister(int n, int value) {
        getRegisters()[n] = value;
        return this;
    }
    
    private Processor addToRegister(int r, int n) {
        setRegister(r, getRegister(r) + n);
        return this;
    }

    public Processor start() {
        while (true) {
            var instruction_msb = getMemory().read(getInstructionPointer());
            incrementInstructionPointer();
            var instruction_lsb = getMemory().read(getInstructionPointer());
            incrementInstructionPointer();
            var instruction = instruction_msb * 0x100 + instruction_lsb;
            var x = instruction_msb % 0x10;
            var y = instruction_lsb / 0x10;
            var address = instruction % 0x1000;
            var opcode = instruction_msb / 0x10;

            switch (opcode) {
                case 0x0 -> {
                    switch (address) {
                        case 0x0E0 -> getScreen().clear();
                        case 0x0EE -> setInstructionPointer(pop());
                    }
                }
    
                case 0x1 -> setInstructionPointer(address);
    
                case 0x2 -> {
                    push(getInstructionPointer());
                    setInstructionPointer(address);
                }
    
                case 0x3 -> {
                    if (getRegister(x) == instruction_lsb) {
                        incrementInstructionPointerBy(2);
                    }
                }
    
                case 0x4 -> {
                    if (getRegister(x) != instruction_lsb) {
                            incrementInstructionPointerBy(2);
                    }
                }
    
                case 0x5 -> {
                    if ((instruction_lsb % 0x10) == 0) {
                        if (getRegister(x) == getRegister(y)) {
                            incrementInstructionPointerBy(2);
                        }
                    }
                }
    
                case 0x6 -> setRegister(x, instruction_lsb);
    
                case 0x7 -> addToRegister(x, instruction_lsb);
    
                case 0x8 -> {
                    var n = instruction_lsb % 0x10;
                    switch (n) {
                        case 0x0 -> setRegister(y, getRegister(x));
                        case 0x1 -> bitwiseOrRegister(x, getRegister(y));
                        case 0x2 -> bitwiseAndRegister(x, getRegister(y));
                        case 0x3 -> bitwiseXorRegister(x, getRegister(y));
                        case 0x4 -> addToRegisterWithCarry(x, getRegister(y));
                        case 0x5 -> substractFromRegisterWithBorrow(x, getRegister(y));
                        case 0x6 -> shiftRightRegister(x);
                        case 0x7 -> substractFromRegisterWithBorrow(y, getRegister(x));
                        case 0xE -> shiftLeftregister(x);
                    }
                }
    
                case 0x9 -> {
                    if ((instruction_lsb % 0x10) == 0) {
                        if (getRegister(x) != getRegister(y)) {
                            incrementInstructionPointerBy(2);
                        }
                    }
                }
    
                case 0xA -> setIndex(address);
    
                case 0xB -> setInstructionPointer(getRegister(0) + address);
    
                case 0xC -> setRegister(x, (new Random().nextInt()) & instruction_lsb);
    
                case 0xD -> getScreen().draw(getRegister(x), getRegister(y), getIndex(), instruction_lsb % 0x10);
    
                case 0xE -> {
                    switch (instruction_lsb) {
                        case 0x9E -> {
                            if (getKeypad().pressed(x)) {
                                incrementInstructionPointerBy(2);
                            }
                        }
        
                        case 0xA1 -> {
                            if (!getKeypad().pressed(x)) {
                                incrementInstructionPointerBy(2);
                            }
                        }
                    }
                }
    
                case 0xF -> {
                    switch (instruction_lsb) {
                        case 0x07 -> setRegister(x, getDelayTimer());
        
                        case 0x0A -> setRegister(x, getKeypad().getKey());
        
                        case 0x15 -> setDelayTimer(getRegister(x));
        
                        case 0x18 -> setSoundTimer(getRegister(x));
        
                        case 0x1E -> setIndex(getRegister(x) + getIndex());
        
                        case 0x29 -> setIndex(x);
        
                        case 0x33 -> {
                            var n = getRegister(x);
                            var tmp_index = getIndex() + 2;
                            for (var i = 1; i <= 3; ++i) {
                                getMemory().write(tmp_index, n % 10);
                                tmp_index = tmp_index - 1;
                                n = n / 10;
                            }
                        }
        
                        case 0x55 -> {
                            var tmp_index = getIndex();
                            for (int n : getRegisters()) {
                                getMemory().write(tmp_index, n);
                                tmp_index = tmp_index + 1;
                            }
                        }
        
                        case 0x65 -> {
                            var tmp_index = getIndex();
                            for (var i = 0; i < x; ++i, ++tmp_index)
                                setRegister(i, getMemory().read(tmp_index));
                        }
                    }
                }
            }
        }
    }

    private Processor shiftLeftregister(int r) {
        setRegister(r, getRegister(r) << 1);
        return this;
    }

    private Processor shiftRightRegister(int r) {
        setRegister(0xF, getRegister(r) % 2);
        setRegister(r, getRegister(r) >> 1);
        return this;
    }

    private Processor substractFromRegisterWithBorrow(int r, int n) {
        substractFromregister(r, n);
        if (r >= n) {
            setRegister(0xF, 0);
        }
        else {
            setRegister(0xF, 1);
        }
        return this;
    }

    private Processor substractFromregister(int r, int n) {
        setRegister(r, getRegister(r) - n);
        return this;
    }

    private Processor addToRegisterWithCarry(int r, int n) {
        addToRegister(r, n);
        if (getRegister(r) > 0xFF) {
            setRegister(0xF, 1);
        }
        else {
            setRegister(0xF, 0);
        }
        return this;
    }

    private Processor bitwiseXorRegister(int r, int n) {
        setRegister(r, getRegister(r) ^ n);
        return this;
    }

    private Processor bitwiseAndRegister(int x, int n) {
        setRegister(x, getRegister(x) & n);
        return this;
    }

    private Processor bitwiseOrRegister(int x, int n) {
        setRegister(x, getRegister(x) | n);
        return this;
    }

    private int pop() {
        setStackPointer(getStackPointer() - 1);
        return getStack()[getStackPointer()];
    }

    private Processor push(int address) {
        getStack()[getStackPointer()] = address;
        setStackPointer(getStackPointer() + 1);
        return this;
    }

}
