package mx.tecnm.tepic.ladm_u3_p1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL("CREATE TABLE APARTADO (ID INT NOT NULL PRIMARY KEY, NOMBRECLIENTE VARCHAR (200), PRODUCTO VARCHAR (200), PRECIO FLOAT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}
//----------------------------------------------------------------------------------------------
