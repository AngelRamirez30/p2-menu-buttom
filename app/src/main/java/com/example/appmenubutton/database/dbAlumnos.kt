package com.example.appmenubutton.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.util.ArrayList

class dbAlumnos(private val context: Context) {
    private val dbHelper: AlumnosDbHelper = AlumnosDbHelper(context)

    private lateinit var db: SQLiteDatabase

    private val leerRegistro = arrayOf(
        DatabaseDefinition.Alumnos.id,
        DatabaseDefinition.Alumnos.matricula,
        DatabaseDefinition.Alumnos.nombre,
        DatabaseDefinition.Alumnos.domicilio,
        DatabaseDefinition.Alumnos.especialidad
    )

    fun openDataBase() {
        db = dbHelper.writableDatabase
    }

    fun insertarAlumno(alumno: Alumno): Long {
        val value = ContentValues().apply {
            put(DatabaseDefinition.Alumnos.matricula, alumno.matricula)
            put(DatabaseDefinition.Alumnos.nombre, alumno.nombre)
            put(DatabaseDefinition.Alumnos.domicilio, alumno.domicilio)
            put(DatabaseDefinition.Alumnos.especialidad, alumno.especialidad)
            put(DatabaseDefinition.Alumnos.foto, alumno.foto)
        }
        return db.insert(
            DatabaseDefinition.Alumnos.tabla, null, value
        )
    }

    fun actualizarAlumno(alumno: Alumno, id: Int): Int {
        val values = ContentValues().apply {
            put(DatabaseDefinition.Alumnos.matricula, alumno.matricula)
            put(DatabaseDefinition.Alumnos.nombre, alumno.nombre)
            put(DatabaseDefinition.Alumnos.domicilio, alumno.domicilio)
            put(DatabaseDefinition.Alumnos.especialidad, alumno.especialidad)
            put(DatabaseDefinition.Alumnos.foto, alumno.foto)
        }
        return db.update(
            DatabaseDefinition.Alumnos.tabla,
            values,
            "${DatabaseDefinition.Alumnos.id} = ?",
            arrayOf(id.toString())
        )
    }

    fun borrarAlumno(id: Int): Int{
        return db.delete(DatabaseDefinition.Alumnos.tabla,"${DatabaseDefinition.Alumnos.id} = ?", arrayOf(id.toString()))
    }

    fun mostrarAlumnos(cursor: Cursor): Alumno {
        return try {
            Alumno().apply {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseDefinition.Alumnos.id))
                matricula = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDefinition.Alumnos.matricula))
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDefinition.Alumnos.nombre))
                domicilio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDefinition.Alumnos.domicilio))
                especialidad = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDefinition.Alumnos.especialidad))

                // Verifica si la columna de foto existe y maneja posibles valores nulos
                val fotoIndex = cursor.getColumnIndex(DatabaseDefinition.Alumnos.foto)
                if (fotoIndex != -1 && !cursor.isNull(fotoIndex)) {
                    foto = cursor.getString(fotoIndex)
                } else {
                    foto = ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Alumno() // Retorna un objeto Alumno vacío en caso de error
        }
    }

    fun getAlumno(id: Long): Alumno{
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseDefinition.Alumnos.tabla,
            leerRegistro,
            "${DatabaseDefinition.Alumnos.id} = ?",
            arrayOf(id.toString()), null, null, null
            )
        cursor.moveToFirst()
        val alumno = mostrarAlumnos(cursor)
        cursor.close()
        return alumno
    }
    fun getAlumnoByMatricula(matricula: String): Alumno{
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseDefinition.Alumnos.tabla,
            leerRegistro,
            "${DatabaseDefinition.Alumnos.matricula} = ?",
            arrayOf(matricula), null, null, null
        )
        if (cursor.moveToFirst()){
            val alumno = mostrarAlumnos(cursor)
            cursor.close()
            return alumno
        }else return Alumno()
    }

    fun leerTodos(): ArrayList<Alumno> {
        val cursor = db.query(DatabaseDefinition.Alumnos.tabla, leerRegistro, null, null, null, null, null)
        val listaAlumnos = ArrayList<Alumno>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            val alumno = mostrarAlumnos(cursor)
            listaAlumnos.add(alumno)
            cursor.moveToNext()
        }
        cursor.close()
        return listaAlumnos
    }

    fun close (){
        dbHelper.close()
    }
}