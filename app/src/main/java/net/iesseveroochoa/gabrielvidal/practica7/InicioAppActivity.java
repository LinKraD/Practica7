package net.iesseveroochoa.gabrielvidal.practica7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.iesseveroochoa.gabrielvidal.practica7.adapters.ChatAdapter;
import net.iesseveroochoa.gabrielvidal.practica7.model.Conferencia;
import net.iesseveroochoa.gabrielvidal.practica7.model.FirebaseContract;
import net.iesseveroochoa.gabrielvidal.practica7.model.Mensaje;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class InicioAppActivity extends AppCompatActivity {

    private static final String TAG_CONFERENCIA = "TAG_CONFERENCIA";
    private static final String TAG_CONFERENCIA_INICIADA = "TAG_INICIADA";

    private String usuario;

    ArrayList<Conferencia> listaConferencias;

    private Spinner spConferencias;

    private Conferencia conferenciaActual;

    private TextView tvDatosUsr;
    private TextView tvConferenciaIniciada;

    private EditText etMensaje;
    private Button btEnviar;

    RecyclerView rvChat;
    ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);

        spConferencias = findViewById(R.id.spn_Conferencias);

        tvDatosUsr = findViewById(R.id.tv_datosUsr);
        tvConferenciaIniciada = findViewById(R.id.tv_ConferenciaIni);
        etMensaje=findViewById(R.id.et_Mensaje);
        btEnviar=findViewById(R.id.bt_Enviar);
        rvChat=findViewById(R.id.rv_Chat);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser usrFB = auth.getCurrentUser();
        usuario=usrFB.getDisplayName();
        tvDatosUsr.setText(usrFB.getDisplayName() + " - " + usrFB.getEmail());

        rvChat.setLayoutManager(new LinearLayoutManager(this));

        leerConferencias();
        iniciarConferenciasIniciadas();

        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensaje();
            }
        });

        auth.signOut();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_muestraConf:
                muestaConferencia();
                return true;
            case R.id.action_empresa:
                muestraEmpresa();
                return true;
            case R.id.action_salir:
                salir();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void muestaConferencia() {
        Conferencia conferencia = (Conferencia) spConferencias.getSelectedItem();

        AlertDialog.Builder builder = new AlertDialog.Builder(InicioAppActivity.this);
        builder.setTitle("Información de la conferencia");
        String mensaje = conferencia.getNombre() + "\n" + getResources().getString(R.string.dialogConferenciaFecha)
                + new SimpleDateFormat("dd/MM/yyyy").format(conferencia.getFecha()) + "\n" +
                getResources().getString(R.string.dialogConferenciaHora) + conferencia.getHorario() + "\n" +
                getResources().getString(R.string.dialogConferenciaSala) + conferencia.getSala();
        builder.setMessage(mensaje);

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    private void salir() {
        startActivity(new Intent(InicioAppActivity.this, MainActivity.class));
        finish();
    }

    private void muestraEmpresa() {
        startActivity(new Intent(InicioAppActivity.this, EmpresaActivity.class));
    }

    private void leerConferencias() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        listaConferencias = new ArrayList<Conferencia>();
        db.collection(FirebaseContract.ConferenciaEntry.NODE_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG_CONFERENCIA, document.getId() + " => " + document.getData());
                        listaConferencias.add(document.toObject(Conferencia.class));
                    }
                    cargaSpinner();
                } else {
                    Log.d(TAG_CONFERENCIA, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void cargaSpinner() {
        spConferencias.setAdapter(new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listaConferencias));
        conferenciaActual = (Conferencia) spConferencias.getSelectedItem();
        spConferencias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Cuando cambien el valor del Spinner guardamos la conferencia
                conferenciaActual = (Conferencia) spConferencias.getSelectedItem();
                defineAdaptador();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void iniciarConferenciasIniciadas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection(FirebaseContract.ConferenciaIniciadaEntry.COLLECTION_NAME).document(FirebaseContract.ConferenciaIniciadaEntry.ID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG_CONFERENCIA_INICIADA, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String conferenciaIniciada = snapshot.getString(FirebaseContract.ConferenciaIniciadaEntry.CONFERENCIA);
                    tvConferenciaIniciada.setText("C.iniciada: " + conferenciaIniciada);
                    Log.d(TAG_CONFERENCIA_INICIADA, "Conferencia iniciada: " + snapshot.getData());
                } else {
                    Log.d(TAG_CONFERENCIA_INICIADA, "Currentdata: null");
                }
            }
        });
    }

    private void enviarMensaje() {
        String body = etMensaje.getText().toString();
        if (!body.isEmpty()) {
            Mensaje mensaje = new Mensaje(usuario, body);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(FirebaseContract.ConferenciaEntry.NODE_NAME)
                .document(conferenciaActual.getId())
                .collection(FirebaseContract.ChatEntry.COLLECTION_NAME)
                .add(mensaje);etMensaje.setText("");ocultarTeclado();}
    }

    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etMensaje.getWindowToken(), 0);
        }
    }

    private void defineAdaptador() {
            Query query = FirebaseFirestore.getInstance()
                    .collection(FirebaseContract.ConferenciaEntry.NODE_NAME)
                    .document(conferenciaActual.getId())
                    .collection(FirebaseContract.ChatEntry.COLLECTION_NAME)
                    .orderBy(FirebaseContract.ChatEntry.FECHA_CREACION, Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<Mensaje> options = new FirestoreRecyclerOptions.Builder<Mensaje>()
                    .setQuery(query, Mensaje.class).setLifecycleOwner(this).build();
            if (adapter != null) {adapter.stopListening();}
            adapter = new ChatAdapter(options);
            rvChat.setAdapter(adapter);

            adapter.startListening();
            adapter.getSnapshots().addChangeEventListener(new ChangeEventListener() {
                @Override
                public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                    rvChat.smoothScrollToPosition(0);
                }
                @Override
                public void onDataChanged() {

                }
                @Override
                public void onError(@NonNull FirebaseFirestoreException e) {

                }
            });
        }
        @Override
        protected void onStop() {
            super.onStop();
            adapter.stopListening();
        }
    }


