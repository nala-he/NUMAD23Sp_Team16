package edu.northeastern.numad23sp_team16.Project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.User;

//public class UserAdapter extends FiresbaseRecyclerAdapter<User, UserAdapter.UserHolder> {
//    private OnItemClickListener listener;
//
//    public UserAdapter() {
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
//        holder.name.setText(model.getUsername());
//    }
//
//    @Override
//    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_username_view, parent, false);
//        return new UserHolder(v);
//    }
//
//    class UserHolder extends RecyclerView.ViewHolder {
//        public TextView name;
//
//        public UserHolder(@NonNull View itemView) {
//            super(itemView);
//            name = itemView.findViewById(R.id.username_text);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getBindingAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION && listener != null) {
//                    }
//                }
//            });
//        }
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(DocumentSnapshot documentSnapshot, int position);
//    }
//
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }
//
//
//}
