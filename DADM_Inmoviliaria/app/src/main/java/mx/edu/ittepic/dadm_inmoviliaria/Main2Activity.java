package mx.edu.ittepic.dadm_inmoviliaria;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    EditText identificadorInm, domicilio, precioVenta, precioRenta, fecha, idp;
    Button insertar, eliminar, actualizar, consultar;
    BaseDatos base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        identificadorInm=findViewById(R.id.identificadorinmueble);
        domicilio=findViewById(R.id.domicilioInmu);
        precioVenta=findViewById(R.id.precioventa);
        precioRenta=findViewById(R.id.preciorenta);
        fecha=findViewById(R.id.fechatransaccion);
        idp=findViewById(R.id.idp);

        insertar=findViewById(R.id.insertarin);
        consultar=findViewById(R.id.consultarin);
        eliminar=findViewById(R.id.borrarin);
        actualizar=findViewById(R.id.actualizarin);

        base = new BaseDatos(this, "inmobiliaria", null, 1);

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               codigoInsertar();
            }
        });




        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });


        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizar.getText().toString().startsWith("CONFIRMAR")) {
                    invocarConfirmacionActualizacion();
                } else {
                    pedirID(2);
                }

            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(3);
            }
        });



    }

    private void invocarConfirmacionActualizacion() {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);
        confir.setTitle("IMPORTNATE").setMessage("estás seguro que deseas aplicar cambios")
                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aplicarActualizar();
                        dialog.dismiss();
                    }
                }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }

    private void aplicarActualizar() {
        try {
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "UPDATE INMUEBLE SET DOMICILIO='" + domicilio.getText().toString() + "', PRECIOVENTA=" + precioVenta.getText().toString() + ", PRECIORENTA=" + precioRenta.getText().toString()+",FECHATRANSACCION='"+fecha.getText().toString()+"' WHERE ID="+identificadorInm.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "SE actualizo", Toast.LENGTH_LONG).show();

        } catch (SQLiteException e) {
            Toast.makeText(this, "No se pudo actualizar " + e, Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }

    private void habilitarBotonesYLimpiarCampos() {
        identificadorInm.setText("");
        domicilio.setText("");
        precioVenta.setText("");
        precioRenta.setText("");
        fecha.setText("");
        idp.setText("");

        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
        identificadorInm.setEnabled(true);
    }


    private void eliminarIdtodo(String idEliminar) {

        try {
            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "DELETE FROM INMUEBLE WHERE ID=" + idEliminar;
            tabla.execSQL(SQL);
            tabla.close();
            habilitarBotonesYLimpiarCampos();

            Toast.makeText(this, "SE elimino el dato", Toast.LENGTH_LONG).show();
        } catch (SQLiteException e) {
            Toast.makeText(this, "No se pudo eliminar " + e, Toast.LENGTH_LONG).show();
        }
    }

    /////////////////////////para pedir el numero
    private void pedirID(final int origen) {
        final EditText pidoID = new EditText(this);
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("Valor entero mayor de 0");
        String mensaje = "Escriba el id a buscar";

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);

        if (origen == 2) {
            mensaje = "Ecriba el id a modificar";
        }
        if (origen == 3) {
            mensaje = "Escriba que desea eliminar";
        }

        alerta.setTitle("atencion").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pidoID.getText().toString().isEmpty()) {
                            Toast.makeText(Main2Activity.this, "Debes escribir un numero", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pidoID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", null).show();
    }
    //////////////////////////////////////////eliminar


    private void buscarDato(String idaBuscar, int origen){
        try{

            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "SELECT *FROM INMUEBLE WHERE ID="+idaBuscar;

            Cursor resultado = tabla.rawQuery(SQL,null);
            if(resultado.moveToFirst()){//mover le primer resultado obtenido de la consulta
                //si hay resulta´do
                if(origen==3){
                    //se consulto para borrar
                    String dato = idaBuscar+"&"+ resultado.getString(1)+"&"+resultado.getString(2)+
                            "&"+resultado.getString(3);
                    invocarConfirmacionEliminacion(dato);
                    return;
                }

                identificadorInm.setText(resultado.getString(0));
                domicilio.setText(resultado.getString(1));
                precioVenta.setText(resultado.getString(2));
                precioRenta.setText(resultado.getString(3));
                fecha.setText(resultado.getString(4));
                idp.setText(resultado.getString(5));
                if(origen==2){
                    //modificar
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR");
                    identificadorInm.setEnabled(false);
                    Toast.makeText(this, "PRESIONA EN EL BOTON 'CONFIRMAR' cuando finalices", Toast.LENGTH_LONG).show();

                }
            }else {
                //no hay resultado!
                Toast.makeText(this,"No se ENCONTRO EL RESULTADO",Toast.LENGTH_LONG).show();
            }
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this,"No se pudo buscar "+ e,Toast.LENGTH_LONG).show();
        }
    }

    private void invocarConfirmacionEliminacion(String dato) {


        String datos[] = dato.split("&");
        final String id = datos[0];
        String nombre = datos[1];

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("atencion").setMessage("Deseas eliminar al usuario: "+nombre)
                .setPositiveButton("Si a todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        eliminarIdtodo(id);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar",null).show();
    }



    private void codigoInsertar(){
        try {
            //metodo que compete a la inserccion,
            SQLiteDatabase tabla = base.getWritableDatabase();
                /*String SQL= "INSERT INTO PERSONA VALUES("+identificacion.getText().toString()+",'"+nombre.getText().toString()
                +"',"+edad.getText().toString()+",'"+genero.getText().toString()+"')";*/

            String SQL = "INSERT INTO INMUEBLE VALUES(1,'%2', 3, 4,'%5', '%6')";
            SQL = SQL.replace("1", identificadorInm.getText().toString());
            SQL = SQL.replace("%2", domicilio.getText().toString());
            SQL = SQL.replace("3", precioVenta.getText().toString());
            SQL = SQL.replace("4", precioRenta.getText().toString());
            SQL = SQL.replace("%5", fecha.getText().toString());
            SQL = SQL.replace("%6", idp.getText().toString());
            tabla.execSQL(SQL);

            Toast.makeText(this,"Si se pudo"+ tabla,Toast.LENGTH_LONG).show();
            tabla.close();
            habilitarBotonesYLimpiarCampos();
        }catch (SQLiteException e){

            Toast.makeText(this,"No se pudo \n"+ e,Toast.LENGTH_LONG).show();

        }
    }



}
