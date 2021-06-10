package mx.tecnm.tepic.ladm_u3_p1

import android.content.ContentValues
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.SQLDataException

class MainActivity : AppCompatActivity() {
    var baseSQlite = BaseDatos(this,"prueba1", null, 1)
    var listaID = ArrayList<String>()
    var baseRemota = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttoninsertar.setOnClickListener {
            insertar()
        }

        buttonsincronizar.setOnClickListener {
            consulta()
        }

        cargarContactos()

    }

    fun insertar() {
        try {
            var transaccion = baseSQlite.writableDatabase
            var data = ContentValues()

            data.put("ID", id.text.toString().toInt())
            data.put("NOMBRECLIENTE", nombre.text.toString())
            data.put("PRODUCTO", producto.text.toString())
            data.put("PRECIO", precio.text.toString().toFloat())

            var respuesta = transaccion.insert("APARTADO", null, data)
            if (respuesta == -1L){
                mensaje ("ERROR! no se pudo insertar el dato")
            } else{
                mensaje ("EXITO! se inserto correctamente")
                limpiarcampos()
                cargarContactos()
            }
            transaccion.close()
        } catch (err: SQLiteException){
            mensaje(err.message.toString())
        }
    }

    private fun cargarContactos() {
        try {
            var transaccion = baseSQlite.readableDatabase
            var personas = ArrayList<String>()
            //Consulta
            var cursor = transaccion.query("APARTADO", arrayOf("*"), null, null, null,null,null)

            if(cursor.moveToFirst()){
                listaID.clear()
                do{
                    var data = "[" + cursor.getInt(0)+"] - "+cursor.getString(1)
                    personas.add(data)
                    listaID.add(cursor.getInt(0).toString())
                } while (cursor.moveToNext())
            }else {
                personas.add("NO HAY DATOS CAPTURADOS")
            }

            listaproductos.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, personas)

            listaproductos.setOnItemClickListener { adapterView, view, posicionItemSeleccionado, id ->
                var idABorrar = listaID.get(posicionItemSeleccionado)
                AlertDialog.Builder(this)
                        .setMessage("¿Estas seguro que deseas borrar ID:"+idABorrar+"?")
                        .setTitle("ATENCION")
                        .setPositiveButton("SI"){d,i->
                            eliminar(idABorrar)
                        }
                        .setNegativeButton("NO"){d,i->
                            d.dismiss()
                        }
                        .show()
            }

            transaccion.close()
        } catch (err: SQLiteException) {
            mensaje(err.message!!)
        }
    }

    fun eliminar(idABorrar:String){
        try {
            var transaccion = baseSQlite.writableDatabase
            var resultado = transaccion.delete("APARTADO", "ID=?", arrayOf(idABorrar))
            if(resultado==0){
                mensaje("NO SE ENCONTRO EL ID" +idABorrar+ "\nNO SE PUDO ELIMINAR")
            } else {
                mensaje("EXITO ID ${idABorrar} SE ELIMINO CORRECTAMENTE")
            }
            transaccion.close()
            cargarContactos()
        } catch(err:SQLiteException){
            mensaje(err.message!!)
        }
    }

    fun consulta() {
        try {
            var transaccion = baseSQlite.readableDatabase
            var idABuscar = id.text.toString()

            var cursor = transaccion.query("APARTADO", arrayOf("NOMBRECLIENTE","PRODUCTO", "PRECIO"),"ID=?",
                    arrayOf(idABuscar), null, null, null)

            if (cursor.moveToFirst()){
                consulta.setText("NOMBRECLIENTE: ${cursor.getString(0)}," +
                        "\nPRODUCTO ${cursor.getString(1)}\nPRECIO: ${cursor.getString(2)}")

                var datosInsertar = hashMapOf(
                        "Nombre" to cursor.getString(0),
                        "Producto" to cursor.getString(1),
                        "Precio" to cursor.getString(2)
                )
                baseRemota.collection("apartadoFire").add(datosInsertar)
                mensaje("Se inserto el documento ${idABuscar}en la colección de firestore")

            } else {
                mensaje("ERROR! no se encontro resultado tras la consulta")
            }
            transaccion.close()
            limpiarcampos()
        } catch (err: SQLiteException){
            mensaje(err.message.toString())
        }
    }

    fun mensaje(m:String){
        AlertDialog.Builder(this)
                .setTitle("ATENCION")
                .setMessage(m)
                .setPositiveButton("OK"){d,i->}
                .show()
    }

    fun limpiarcampos(){
        id.setText("")
        nombre.setText("")
        producto.setText("")
        precio.setText("")
    }

}