package net.iesseveroochoa.gabrielvidal.practica7.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import net.iesseveroochoa.gabrielvidal.practica7.R;
import net.iesseveroochoa.gabrielvidal.practica7.model.Mensaje;

public class ChatAdapter extends FirestoreRecyclerAdapter<Mensaje, ChatAdapter.ChatHolder>{

    RecyclerView rvChat;
    ChatAdapter adapter;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Mensaje> options) {
        super(options);
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview= LayoutInflater.from(parent.getContext()).inflate(R.layout.mensaje_item,parent,false);
        return new ChatHolder(itemview);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatAdapter.ChatHolder holder, int position, @NonNull Mensaje mensaje) {
        holder.tvMensaje.setText(mensaje.getUsuario()+"=>"+mensaje.getBody());
    }

    public class ChatHolder extends RecyclerView.ViewHolder{
        TextView tvMensaje;

        public ChatHolder(View view){
            super(view);
            tvMensaje=view.findViewById(R.id.tv_Mensaje);
        }
    }
}
