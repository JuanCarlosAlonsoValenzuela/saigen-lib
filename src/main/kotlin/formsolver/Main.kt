package formsolver

import org.apache.jena.sparql.core.QuerySolutionBase
import java.io.FileInputStream
import edu.washington.cs.knowitall.morpha.MorphaStemmer
import org.droidmate.saigen.Lib.Companion.getInputsForLabels
import org.droidmate.saigen.utils.NLP
import uk.ac.susx.informatics.Morpha
import java.util.*
import java.io.FileWriter
import java.nio.file.Files
import java.io.IOException
import java.util.Arrays

object Main {

	@JvmStatic
	fun main(args: Array<String>) {

		var prova = listOf("countryCode")




		val saigenResults = getInputsForLabels(prova)

		var fileRoute = ".\\src\\main\\resources\\generatedTestDataSaigen\\"

		for (result in saigenResults){
			println(result.label)

			var fileWriter: FileWriter? = null

			try{
				var fileName = result.label + ".csv"
				fileWriter = FileWriter(fileRoute + fileName)

				for(value in result.values) {
					fileWriter.append(value)
					fileWriter.append('\n')
				}

				println("$fileName Generated successfully")

			} catch(e: Exception){
				print("Error generating csv file")
				e.printStackTrace()
			} finally {
				try {
					fileWriter!!.flush()
					fileWriter.close()
				} catch(e: IOException) {
					println("Flushing/closing error!")
					println(e.printStackTrace())
				}
			}


		}




	}

	private val cache = mutableMapOf<String, List<String>>()

	fun testGetSynonyms(word: String): List<String> {

		if (!cache.containsKey(word)) {
			try {
				val words = WordNet.getSynonyms(word)
					.map { it.replace("_", " ") }
					.map {
						it.map { c ->
							if (c in 'A'..'Z')
								" $c"
							else
								"$c"
						}.joinToString("")
					}
//					.filterNot { it.contains(" ") }
					.distinct()
				words.forEach { cache[it] = words }
			} catch (e: Exception) {
				cache[word] = emptyList()
				NLP.log.error("Unable to get synonym for $word", e)
			}
		}

		return cache[word].orEmpty()
	}
}