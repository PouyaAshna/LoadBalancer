package org.example.loadbalancer.service.impl;

import org.example.loadbalancer.service.InstructionService;
import org.example.loadbalancer.util.PrinterUtil;
import org.example.loadbalancer.util.annotation.Component;

@Component
public class DefaultInstructionService implements InstructionService {

    private final String instruction = """
                Instruction Rules:
                1- Provider Lists
                2- Change loadbalancer
                3- Add more providers
                4- Route
                5- Manual exclude provider
                6- Manual include provider
                7- Turn-off provider
                8- Turn-on provider
                9- Show instruction
                10- Exit
            """;

    @Override
    public void showInstruction() {
        PrinterUtil.print(instruction);
    }
}
