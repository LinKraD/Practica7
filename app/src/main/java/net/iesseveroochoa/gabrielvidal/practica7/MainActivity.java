package net.iesseveroochoa.gabrielvidal.practica7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            finish();
        } else {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    //.setLogo(R.drawable.ic_developer_board_red_a700_48dp)
                    .setIsSmartLockEnabled(false).build(),RC_SIGN_IN);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, InicioAppActivity.class));
            } else {
                String msg_error = "";
                if (response == null) {
                    msg_error = "Es necesario autenticarse";
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    msg_error = "No hay red disponible para autenticarse";
                } else {
                    msg_error = "Error desconocido al autenticarse";
                }
                Toast.makeText(this,msg_error,Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}
