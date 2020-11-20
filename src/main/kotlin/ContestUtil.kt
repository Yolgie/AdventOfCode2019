
fun interface Worker {
    fun work(input: List<String>): String
}

fun runOnInputFile(inputFile: String, worker: Worker) {
    println(
        inputFile.asResource {
            worker.work(
                it
                    .split('\n')
                    .filterNot(String::isBlank)
            )
        }
    )
}

fun <RETURN_TYPE> String.asResource(work: (String) -> RETURN_TYPE): RETURN_TYPE {
    val content = {}::class.java.getResource(this).readText()
    return work(content)
}