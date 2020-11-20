package at.cnoize.adventOfCode2019.dayXX

import Worker
import runOnInputFile

const val INPUT_FILE ="DayXX.input"

fun main() {
    runOnInputFile(INPUT_FILE, workerPuzzle1)
    //runOnInputFile(INPUT_FILE, workerPuzzle2)
}

val workerPuzzle1 = Worker { input ->
    input.toString()
}

val workerPuzzle2 = Worker { input ->
    input.toString()
}

