package database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import model.FavouritesResults
import model.User

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        private const val TABLE_FAVORITES = "favorites"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_POKEMON_ID = "pokemon_id"
        private const val COLUMN_POKEMON_NAME = "pokemon_name"

        fun doesDatabaseExist(context: Context): Boolean {
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            return dbFile.exists()
        }

        fun initializeDatabase(context: Context) {
            if (!doesDatabaseExist(context)) {
                val dbHelper = DatabaseHelper(context)
                val db = dbHelper.writableDatabase
                db.close()
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USERNAME TEXT, "
                + "$COLUMN_PASSWORD TEXT)")

        val createFavoritesTable = ("CREATE TABLE $TABLE_FAVORITES ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_ID INTEGER, "
                + "$COLUMN_POKEMON_ID INTEGER, "
                + "$COLUMN_POKEMON_NAME TEXT, "
                + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID))")

        db.execSQL(createUsersTable)
        db.execSQL(createFavoritesTable)

        addSampleUser(db, "sampleuser", "password123")
    }

    private fun addSampleUser(db: SQLiteDatabase, username: String, password: String) {
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, password)
        db.insert(TABLE_USERS, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
            val createFavoritesTable = ("CREATE TABLE $TABLE_FAVORITES ("
                    + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COLUMN_USER_ID INTEGER, "
                    + "$COLUMN_POKEMON_ID INTEGER, "
                    + "$COLUMN_POKEMON_NAME TEXT, "
                    + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID))")
            db.execSQL(createFavoritesTable)
        }
    }

    fun addUser(username: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, password)

        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result
    }

    fun checkUser(username: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_ID), "$COLUMN_USERNAME = ?", arrayOf(username), null, null, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    fun checkUserCredentials(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    fun getUser(username: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS, arrayOf(COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD),
            "$COLUMN_USERNAME = ?", arrayOf(username), null, null, null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val user = User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
            cursor.close()
            user
        } else {
            cursor?.close()
            null
        }
    }

    fun getUserById(userId: Int): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS, arrayOf(COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD),
            "$COLUMN_ID = ?", arrayOf(userId.toString()), null, null, null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val user = User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
            cursor.close()
            user
        } else {
            cursor?.close()
            null
        }
    }

    fun addFavorite(userId: Int, pokemon: FavouritesResults): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_ID, userId)
        values.put(COLUMN_POKEMON_ID, pokemon.pokemonId)
        values.put(COLUMN_POKEMON_NAME, pokemon.pokemonName)

        val result = db.insert(TABLE_FAVORITES, null, values)
        db.close()
        return result
    }

    fun removeFavorite(userId: Int, pokemonId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_FAVORITES, "$COLUMN_USER_ID = ? AND $COLUMN_POKEMON_ID = ?", arrayOf(userId.toString(), pokemonId.toString()))
        db.close()
        return result
    }

    fun getFavorites(userId: Int): List<FavouritesResults> {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_FAVORITES, arrayOf(COLUMN_POKEMON_ID, COLUMN_POKEMON_NAME), "$COLUMN_USER_ID = ?", arrayOf(userId.toString()), null, null, null)
        val favorites = mutableListOf<FavouritesResults>()
        if (cursor.moveToFirst()) {
            do {
                val pokemonId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_ID))
                val pokemonName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POKEMON_NAME))
                favorites.add(FavouritesResults(pokemonId, pokemonName))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return favorites
    }

    fun deleteUser(userId: Int): Boolean {
        val db = this.writableDatabase
        var success = true

        val deleteFavorites = db.delete(TABLE_FAVORITES, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()))
        if (deleteFavorites < 0) {
            success = false
        }

        val deleteUser = db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        if (deleteUser < 0) {
            success = false
        }

        db.close()
        return success
    }
}