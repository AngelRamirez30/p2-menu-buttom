package com.example.appmenubutton.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AlumnosDbHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "sistema.db"
        private const val DATABASE_VERSION = 1
        private const val TEXT_TYPE = " TEXT"
        private const val INTEGER_TYPE = " INTEGER"
        private const val COMA = ","
        private const val SQL_CREATE_ALUMNO =
                          "CREATE TABLE " +
                          DatabaseDefinition.Alumnos.tabla +
                          "(${DatabaseDefinition.Alumnos.id}$INTEGER_TYPE PRIMARY KEY $COMA" +
                          "${DatabaseDefinition.Alumnos.matricula}$TEXT_TYPE$COMA" +
                          "${DatabaseDefinition.Alumnos.nombre}$TEXT_TYPE$COMA" +
                          "${DatabaseDefinition.Alumnos.domicilio}$TEXT_TYPE$COMA" +
                          "${DatabaseDefinition.Alumnos.especialidad}$TEXT_TYPE$COMA" +
                          "${DatabaseDefinition.Alumnos.foto}$TEXT_TYPE)"
        private const val SQL_DETELE_ALUMNO = "DROP TABLE IF EXISTS ${DatabaseDefinition.Alumnos.tabla}"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ALUMNO)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(SQL_DETELE_ALUMNO)
        db?.execSQL(SQL_CREATE_ALUMNO)
    }
}