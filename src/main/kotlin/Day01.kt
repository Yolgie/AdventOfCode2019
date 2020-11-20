package at.cnoize.adventOfCode2019.day01

import Worker
import runOnInputFile

const val INPUT_FILE ="Day01.input"

fun main() {
    runOnInputFile(INPUT_FILE, workerPuzzle1)
    runOnInputFile(INPUT_FILE, workerPuzzle2)
}

val workerPuzzle1 = Worker { input ->
    input
        .map(String::toInt)
        .map(::getFuel)
        .sum().toString()
}

val workerPuzzle2 = Worker { input ->
    input
        .map(String::toInt)
        .map(::getFuelRecursive)
        .sum().toString()
}

fun getFuelRecursive(mass: Int): Int {
    var totalFuel = 0
    var newFuel = getFuel(mass)

    while (newFuel > 0) {
        totalFuel += newFuel
        newFuel = getFuel(newFuel)
    }
    return totalFuel
}

fun getFuel(mass: Int): Int {
    return (mass / 3) - 2
}
