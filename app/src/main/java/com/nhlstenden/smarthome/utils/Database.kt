package com.nhlstenden.smarthome.utils

import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import java.io.File

/**
 * Database util for storing in and loading data from an SQLite database easily
 *
 * @author Robert
 * @since 1.0
 */
class Database(private val database: SQLiteDatabase) {
    /** The database file */
    private val file = File(database.path)

    /**
     * Executes an SQL query
     *
     * @return true if the query succeeded, false otherwise
     */
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

    /**
     * Executes an SQL query, and handles the returned data in the callback runnable
     *
     * @return true if the query succeeded, false otherwise
     */
    fun exec(query: String, callback: (Cursor) -> Unit): Boolean {
        database.rawQuery(query, null).use {
            if (it.count == 0 || !it.moveToFirst()) {
                return false
            }
            callback(it)
            return true
        }
    }
}