package com.nhlstenden.smarthome.utils

import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import java.io.File

/**
 * Database util for storing in and loading data from an SQLite database easily.
 *
 * @author Robert
 * @since 1.0
 */
class Database(private val database: SQLiteDatabase) {
    /** Gets the database file */
    private val file = File(database.path)

    /** Execute an SQL query */
    fun exec(query: String): Boolean {
        database.apply {
            return database.isOpen && try {
                execSQL(query)
                true
            } catch (e: SQLException) {
                close()
                file.delete()
                false
            }
        }
    }

    /** Run a query, and handles the returned data in the callback runnable */
    fun exec(query: String, callback: ((Cursor) -> Unit)) {
        database.rawQuery(query, null).use {
            it.moveToFirst()
            callback.invoke(it)
        }
    }
}