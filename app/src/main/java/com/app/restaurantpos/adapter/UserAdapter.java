package com.app.restaurantpos.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.settings.users.EditUserActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {


    private List<HashMap<String, String>> userData;
    private Context context;


    public UserAdapter(Context context, List<HashMap<String, String>> userData) {
        this.context = context;
        this.userData = userData;

    }


    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.MyViewHolder holder, int position) {

        String userId = userData.get(position).get(Constant.ID);
        String userName = userData.get(position).get(Constant.USER_NAME);
        String userPhone = userData.get(position).get(Constant.USER_PHONE);
        String userType = userData.get(position).get(Constant.USER_TYPE);


        holder.txtUserName.setText(userName);
        holder.txtUserPhone.setText(userPhone);
        holder.txtUserType.setText(userType);


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle(context.getString(R.string.delete))
                        .withMessage(context.getString(R.string.want_to_delete))
                        .withEffect(Slidetop)
                        .withDialogColor("#f29161") //use color code for dialog
                        .withButton1Text(context.getString(R.string.yes))
                        .withButton2Text(context.getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if (userId.equals("1")) {
                                    Toasty.warning(context, R.string.you_cant_delete_Admin, Toast.LENGTH_SHORT).show();
                                    dialogBuilder.dismiss();
                                } else {

                                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                    databaseAccess.open();
                                    boolean deleteUser = databaseAccess.deleteUser(userId);

                                    if (deleteUser) {

                                        Toasty.success(context, R.string.user_deleted, Toast.LENGTH_SHORT).show();

                                        userData.remove(holder.getAdapterPosition());

                                        // Notify that item at position has been removed
                                        notifyItemRemoved(holder.getAdapterPosition());

                                    } else {
                                        Toasty.error(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                        dialogBuilder.dismiss();
                                    }
                                    dialogBuilder.dismiss();
                                }

                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogBuilder.dismiss();
                            }
                        })
                        .show();


            }
        });

    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtUserName, txtUserPhone, txtUserType;
        ImageView imgDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserName = itemView.findViewById(R.id.txt_user_name);

            txtUserPhone = itemView.findViewById(R.id.txt_user_phone);
            txtUserType = itemView.findViewById(R.id.txt_user_type);
            imgDelete = itemView.findViewById(R.id.img_delete);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditUserActivity.class);
            i.putExtra(Constant.ID, userData.get(getAdapterPosition()).get(Constant.ID));
            i.putExtra(Constant.USER_NAME, userData.get(getAdapterPosition()).get(Constant.USER_NAME));
            i.putExtra(Constant.USER_PHONE, userData.get(getAdapterPosition()).get(Constant.USER_PHONE));
            i.putExtra(Constant.USER_TYPE, userData.get(getAdapterPosition()).get(Constant.USER_TYPE));
            i.putExtra(Constant.USER_PASSWORD, userData.get(getAdapterPosition()).get(Constant.USER_PASSWORD));
            context.startActivity(i);
        }
    }


}
