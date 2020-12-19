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
import com.app.restaurantpos.settings.payment_method.EditPaymentMethodActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.MyViewHolder> {


    private List<HashMap<String, String>> paymentMethodData;
    private Context context;


    public PaymentMethodAdapter(Context context, List<HashMap<String, String>> paymentMethodData) {
        this.context = context;
        this.paymentMethodData = paymentMethodData;

    }


    @NonNull
    @Override
    public PaymentMethodAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_method_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PaymentMethodAdapter.MyViewHolder holder, int position) {

        final String payment_method_id = paymentMethodData.get(position).get(Constant.PAYMENT_METHOD_ID);
        String paymentMethodName = paymentMethodData.get(position).get(Constant.PAYMENT_METHOD_NAME);


        holder.txtPaymentMethodName.setText(paymentMethodName);


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

                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean deleteCustomer = databaseAccess.deletePaymentMethod(payment_method_id);

                                if (deleteCustomer) {
                                    Toasty.success(context, R.string.payment_method_deleted, Toast.LENGTH_SHORT).show();

                                    paymentMethodData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                } else {
                                    Toasty.error(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }

                                dialogBuilder.dismiss();
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
        return paymentMethodData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtPaymentMethodName;
        ImageView imgDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPaymentMethodName = itemView.findViewById(R.id.txt_payment_method_name);

            imgDelete = itemView.findViewById(R.id.img_delete);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditPaymentMethodActivity.class);
            i.putExtra("payment_method_id", paymentMethodData.get(getAdapterPosition()).get("payment_method_id"));
            i.putExtra("payment_method_name", paymentMethodData.get(getAdapterPosition()).get("payment_method_name"));

            context.startActivity(i);
        }
    }


}
