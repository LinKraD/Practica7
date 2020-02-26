package net.iesseveroochoa.gabrielvidal.practica7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.iesseveroochoa.gabrielvidal.practica7.model.Empresa;
import net.iesseveroochoa.gabrielvidal.practica7.model.FirebaseContract;

public class EmpresaActivity extends AppCompatActivity {

    private Empresa empresa;

    private TextView nombre;
    private TextView direccion;
    private TextView telefono;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        nombre=findViewById(R.id.tvNombre);
        direccion=findViewById(R.id.tvDireccion);
        telefono=findViewById(R.id.tvTelefono);

        obtenDatosEmpresa();
    }

    void obtenDatosEmpresa(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef=db.collection(FirebaseContract.EmpresaEntry.NODE_NAME).document(FirebaseContract.EmpresaEntry.ID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                empresa = documentSnapshot.toObject(Empresa.class);
                asignaValoresEmpresa();
            }
        });
    }

    private void asignaValoresEmpresa() {
        nombre.setText(empresa.getNombre());
        direccion.setText(empresa.getDireccion());
        telefono.setText(empresa.getTelefono());
    }
}
