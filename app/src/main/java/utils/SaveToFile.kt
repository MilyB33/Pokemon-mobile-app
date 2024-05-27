package utils

import android.content.Context
import model.FavouritesResults
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

fun saveToFile(context: Context, data: String, fileName: String) {
    var fos: FileOutputStream? = null
    try {
        fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        fos.write(data.toByteArray())
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

private fun readFile(context: Context, fileName: String, type: String): List<FavouritesResults> {
    val pokemons = mutableListOf<FavouritesResults>()
    var fis: FileInputStream? = null
    try {
        fis = context.openFileInput(fileName)
        val reader = BufferedReader(InputStreamReader(fis))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val parts = line?.split(",")
            if (parts != null && parts.size == 2) {
                val pokemonId = parts[0].toIntOrNull()
                val pokemonName = parts[1].removeSuffix(";")
                if (pokemonId != null) {
                    pokemons.add(FavouritesResults(pokemonId, pokemonName))
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            fis?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return pokemons
}

fun saveToTxtFile(context: Context, userId: Int, pokemons: List<FavouritesResults>) {
    val lineSeparator = System.lineSeparator()
    val data = pokemons.joinToString(lineSeparator) { "${it.pokemonId},${it.pokemonName};" }
    saveToFile(context, data, "favorites_$userId.txt")
}

fun saveToCsvFile(context: Context, userId: Int, pokemons: List<FavouritesResults>) {
    val lineSeparator = System.lineSeparator()
    val data = pokemons.joinToString(lineSeparator) { "${it.pokemonId},${it.pokemonName};" }
    saveToFile(context, data, "favorites_$userId.csv")
}

fun readFromTxtFile(context: Context, userId: Int): List<FavouritesResults> {
    return readFile(context, "favorites_$userId.txt", "txt")
}

fun readFromCsvFile(context: Context, userId: Int): List<FavouritesResults> {
    return readFile(context, "favorites_$userId.csv", "csv")
}