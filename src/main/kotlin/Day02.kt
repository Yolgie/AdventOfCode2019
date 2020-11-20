package at.cnoize.adventOfCode2019.day02

import Worker
import runOnInputFile
import java.lang.IllegalStateException

const val INPUT_FILE ="Day02.input"

fun main() {
    runOnInputFile(INPUT_FILE, workerPuzzle1) // 3895705
    runOnInputFile(INPUT_FILE, workerPuzzle2) // 6417
}

val workerPuzzle1 = Worker { input ->
    val intcode = input.first().split(",").map(String::toInt).toMutableList()

    // initialize
    intcode[1] = 12
    intcode[2] = 2

    // run program
    runIntcodeProgram(intcode)

    return@Worker intcode[0].toString()
}

fun runIntcodeProgram(intcode: MutableList<Int>): Int {
    var instructionPointer = 0
    while (intcode[instructionPointer] != OPCODES.STOP.opcode) {
        when (intcode[instructionPointer]) {
            OPCODES.ADD.opcode -> intcode[intcode[instructionPointer + 3]] = intcode[intcode[instructionPointer + 2]] + intcode[intcode[instructionPointer + 1]]
            OPCODES.MUL.opcode -> intcode[intcode[instructionPointer + 3]] = intcode[intcode[instructionPointer + 2]] * intcode[intcode[instructionPointer + 1]]
            else -> throw IllegalStateException("OPCODE ${intcode[instructionPointer]} not valid")
        }
        instructionPointer += step
    }
    return intcode[0]
}

val workerPuzzle2 = Worker { input ->
    val originalIntcode = input.first().split(",").map(String::toInt)

    for (noun in 1..99) {
        for (verb in 1..99) {
            val intcode = originalIntcode.toMutableList()
            intcode[1] = noun
            intcode[2] = verb

            if (runIntcodeProgram(intcode) == 19690720) {
                return@Worker (noun*100+verb).toString()
            }
        }
    }

    throw IllegalStateException("no noun/verb combination found")
}

const val step = 4

enum class OPCODES(val opcode: Int) {
    ADD(1), //add
    MUL(2), // multiply
    STOP(99) //finished


}

class instruction(val opcode: OPCODES, parameterCount: Int) {
    val totalSize = parameterCount+1
}
