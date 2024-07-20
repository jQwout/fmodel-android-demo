package fraktal.io.android.demo.shared.db

import android.content.Context
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import fraktal.io.android.demo.Database
import fraktal.io.android.demo.Workers

object DbLocator {

    private var _database: Database? = null
    val database: Database get() = _database!!

    fun createDb(context: Context) {
        if (_database != null) {
            return
        }
        val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, context, "Sample.db")
        val adapter = Workers.Adapter(
            genderAdapter = EnumColumnAdapter(),
            positionAdapter = EnumColumnAdapter()
        )
        _database = Database(
            driver, adapter
        )
    }
}