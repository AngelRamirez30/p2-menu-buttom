package com.example.appmenubutton.database

import android.provider.BaseColumns

class DatabaseDefinition {

    object Alumnos:BaseColumns{
        const val tabla = "alumnos"
        const val id = "id"
        const val matricula = "matricula"
        const val nombre = "nombre"
        const val domicilio = "domicilio"
        const val especialidad = "especialidad"
        const val foto = "foto"
    }
}