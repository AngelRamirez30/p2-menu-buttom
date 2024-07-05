package com.example.appmenubutton

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.appmenubutton.database.dbAlumnos
import com.example.appmenubutton.database.Alumno


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DbFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var db: dbAlumnos

    private lateinit var btnGuardar: Button
    private lateinit var btnBuscar: Button
    private lateinit var btnBorrar: Button

    private lateinit var txtMatricula: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtDomicilio: EditText
    private lateinit var txtEspecialidad: EditText
    private lateinit var imgProfilePic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_db, container, false)

        // Inicializar los botones y otros elementos aquí
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnBuscar = view.findViewById(R.id.btnBuscar)
        btnBorrar = view.findViewById(R.id.btnBorrar)

        txtMatricula = view.findViewById(R.id.txtMatricula)
        txtNombre = view.findViewById(R.id.txtNombre)
        txtDomicilio = view.findViewById(R.id.txtDomicilio)
        txtEspecialidad = view.findViewById(R.id.txtEspecialidad)

        imgProfilePic = view.findViewById(R.id.imgProfilePic)

        btnGuardar.setOnClickListener {
            val nombre = txtNombre.text?.toString() ?: ""
            val matricula = txtMatricula.text?.toString() ?: ""
            val domicilio = txtDomicilio.text?.toString() ?: ""
            val especialidad = txtEspecialidad.text?.toString() ?: ""

            if (nombre.isEmpty() || matricula.isEmpty() || domicilio.isEmpty() || especialidad.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Faltó información",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                db = dbAlumnos(requireContext())
                db.openDataBase()

                val alumno = Alumno().apply {
                    this.nombre = nombre
                    this.matricula = matricula
                    this.domicilio = domicilio
                    this.especialidad = especialidad
                    this.foto = "Pendiente"
                }

                // Verificar si la matrícula ya existe
                val existingAlumno = db.getAlumnoByMatricula(matricula)
                if (existingAlumno.id != 0) {
                    // Verificar si los datos son diferentes
                    if (existingAlumno.nombre != nombre ||
                        existingAlumno.domicilio != domicilio ||
                        existingAlumno.especialidad != especialidad ||
                        existingAlumno.foto != alumno.foto) {

                        // Si los datos son diferentes, actualizar los datos del alumno
                        val rowsAffected: Int = db.actualizarAlumno(alumno, existingAlumno.id)
                        if (rowsAffected > 0) {
                            Toast.makeText(
                                requireContext(),
                                "Alumno editado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al editar el alumno",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No hay cambios en los datos del alumno",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Si la matrícula no existe, insertar un nuevo alumno
                    val id: Long = db.insertarAlumno(alumno)
                    Toast.makeText(
                        requireContext(),
                        "Se agregó el alumno con el id: $id",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                db.close()
            }
        }

        btnBuscar.setOnClickListener {
            if (txtMatricula.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Faltó ingresar matrícula",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                db = dbAlumnos(requireContext())
                db.openDataBase()

                val alumno: Alumno = db.getAlumnoByMatricula(txtMatricula.text.toString())
                if (alumno.id != 0) {
                    txtNombre.setText(alumno.nombre)
                    txtDomicilio.setText(alumno.domicilio)
                    txtEspecialidad.setText(alumno.especialidad)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No se encontró el alumno",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                db.close()
            }
        }
        // En tu onCreate o en el método donde configuras tus botones y EditTexts
        txtMatricula.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnBorrar.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnBorrar.isEnabled = false

        btnBorrar.setOnClickListener {
            val matricula = txtMatricula.text?.toString() ?: ""
            if (matricula.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Faltó ingresar matrícula",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirmación de eliminación")
                builder.setMessage("¿Estás seguro de que deseas eliminar este alumno?")
                builder.setPositiveButton("Sí") { dialog, _ ->
                    try {
                        db = dbAlumnos(requireContext())
                        db.openDataBase()

                        // Obtener el alumno por matrícula
                        val alumno: Alumno = db.getAlumnoByMatricula(matricula)

                        if (alumno.id != 0) {
                            // Llamar al método borrarAlumno
                            val result = db.borrarAlumno(alumno.id)
                            if (result > 0) {
                                Toast.makeText(
                                    requireContext(),
                                    "Alumno eliminado exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Limpiar campos de texto después de eliminar
                                txtNombre.setText("")
                                txtDomicilio.setText("")
                                txtEspecialidad.setText("")
                                txtMatricula.setText("")
                                // Deshabilitar el botón de borrar nuevamente
                                btnBorrar.isEnabled = false
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Error al eliminar el alumno",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No se encontró el alumno",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        db.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            "Ocurrió un error al eliminar el alumno",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    dialog.dismiss()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }
        }


        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DbFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
