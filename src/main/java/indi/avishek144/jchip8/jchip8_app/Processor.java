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

package indi.avishek144.jchip8.jchip8_app;

import java.util.Random;

public class Processor
{
    private int[] stack    = new int[Integer.MAX_VALUE],
                  register = new int[16];

    private int instruction_pointer = 0x200,
                stack_pointer       = 0,
                delay_timer         = 0,
                sound_timer         = 0,
                index;
    
    private Display screen;
    private Keypad keypad;
    private Memory memory;


    public void start(String[] args)
    {
        while (true) {
            try {
                var instruction_msb = memory.read(instruction_pointer++);
                var instruction_lsb = memory.read(instruction_pointer++);

                var instruction = instruction_msb * 0x100 + instruction_lsb;
                var x = instruction_msb % 0x10;
                var y = instruction_lsb / 0x10;
                var address = instruction % 0x1000;

                var opcode = instruction_msb / 0x10;

                switch (opcode) {
                    case 0x0 -> {
                        switch (address) {
                            case 0x0E0 -> screen.clear();

                            case 0x0EE -> instruction_pointer = pop();

                            default -> throw new InvalidMemoryAddressException(address);
                        }
                    }

                    case 0x1 -> {
                    	instruction_pointer = address;
                    }

                    case 0x2 -> {
                        push(instruction_pointer);
                        instruction_pointer = address;
                    }

                    case 0x3 -> {
                        if (register[x] == instruction_lsb)
                            instruction_pointer += 2;
                        else
                            continue;
                    }

                    case 0x4 -> {
                        if (register[x] != instruction_lsb)
                            instruction_pointer += 2;
                        else
                            continue;
                    }

                    case 0x5 -> {
                        if ((instruction_lsb % 0x10) == 0) {
                            if (register[x] == register[y])
                                instruction_pointer += 2;
                            else
                                continue;
                        }
                        else {
                            throw new InvalidInstructionException(instruction, instruction_pointer-2);
                        }
                    }

                    case 0x6 -> {
                        register[x] = instruction_lsb;
                    }

                    case 0x7 -> {
                        register[x] += instruction_lsb;
                    }

                    case 0x8 -> {
                        var n = instruction_lsb % 0x10;
                        switch (n) {
                            case 0x0 -> register[y] = register[x];

                            case 0x1 -> register[x] |= register[y];

                            case 0x2 -> register[x] &= register[y];

                            case 0x3 -> register[x] ^= register[y];

                            case 0x4 -> {
                                register[x] += register[y];
                                if (register[x] > 0xFF) {
                                    register[0xF] = 1;
                                    register[x] %= 0x100;
                                }
                                else
                                    register[0xF] = 0;
                            }

                            case 0x5 -> {
                                register[x] -= register[y];
                                if (register[x] > register[y])
                                    register[0xF] = 1;
                                else {
                                    register[0xF] = 0;
                                    register[x] %= 0x100;
                                }
                            }

                            case 0x6 -> {
                                register[0xF] = register[x] % 2;
                                register[x] /= 2;
                            }

                            case 0x7 -> {
                                register[x] = register[y] - register[x];
                                if (register[y] > register[x])
                                    register[0xF] = 1;
                                else {
                                    register[0xF] = 0;
                                    register[x] %= 0x100;
                                }
                            }

                            case 0xE -> {
                                if (register[x] > 0xEF)
                                    register[0xF] = 0x1;
                                else
                                    register[0xF] = 0x0;
                                register[x] = (register[x] * 2) % 0x100;
                            }

                            default -> throw new InvalidInstructionException(instruction, instruction_pointer-2);
                        }
                    }

                    case 0x9 -> {
                        if ((instruction_lsb % 0x10) == 0) {
                            if (register[x] != register[y])
                                instruction_pointer += 2;
                            else
                                continue;
                        }
                        else
                            throw new InvalidInstructionException(instruction, instruction_pointer-2);
                    }

                    case 0xA -> index = address;

                    case 0xB -> {
                    	instruction_pointer = register[0x0] + address;
                    }

                    case 0xC -> register[x] = (new Random().nextInt()) & instruction_lsb;

                    case 0xD -> screen.draw(register[x], register[y], index, instruction_lsb % 0x10);

                    case 0xE -> {
                        switch (instruction_lsb) {
                            case 0x9E -> {
                                if (keypad.pressed(x))
                                    instruction_pointer += 2;
                                else
                                    continue;
                            }

                            case 0xA1 -> {
                                if (!keypad.pressed(x))
                                    instruction_pointer += 2;
                                else
                                    continue;
                            }

                            default -> throw new InvalidInstructionException(instruction, instruction_pointer-2);
                        }
                    }

                    case 0xF -> {
                        switch (instruction_lsb) {
                            case 0x07 -> {

                            }

                            case 0x0A -> {

                            }

                            case 0x15 -> delay_timer = register[x];

                            case 0x18 -> sound_timer = register[x];

                            case 0x1E -> index = (index + register[x]) % 0x1000;

                            case 0x29 -> index = x;

                            case 0x33 -> {

                            }

                            case 0x55 -> {
                                var start_address = index;
                                for (var i = 0; i < x; ++i, ++start_address)
                                    memory.write(start_address, register[i]);
                            }

                            case 0x65 -> {
                                var start_address = index;
                                for (var i = 0; i < x; ++i, ++start_address)
                                    register[i] = memory.read(start_address);
                            }

                            default -> throw new InvalidInstructionException(instruction, instruction_pointer-2);
                        }
                    }

                    default -> {

                    }
                }
            }
            catch (
                    StackOverflowException |
                    StackUnderflowException |
                    InvalidInstructionException |
                    InvalidMemoryAddressException invalid_data_exception
            ) {
                
            }
        }
    }

    private int pop() throws StackUnderflowException
    {
        if (stack_pointer == 0)
            throw new StackUnderflowException();
        else
            return stack[--stack_pointer];
    }

    private void push(int address) throws StackOverflowException
    {
        try {
        	stack[stack_pointer++] = address;
        }
        catch (ArrayIndexOutOfBoundsException index_out_of_bound_exception) {
        	throw new StackOverflowException();
        }
    }

}
