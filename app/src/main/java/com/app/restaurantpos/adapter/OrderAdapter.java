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
import com.app.restaurantpos.orders.OrderDetailsActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {


    Context context;
    private List<HashMap<String, String>> orderData;


    public OrderAdapter(Context context, List<HashMap<String, String>> orderData) {
        this.context = context;
        this.orderData = orderData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        String customerName = orderData.get(position).get(Constant.CUSTOMER_NAME);
        String invoiceId = orderData.get(position).get(Constant.INVOICE_ID);
        String orderDate = orderData.get(position).get(Constant.ORDER_DATE);
        String orderTime = orderData.get(position).get(Constant.ORDER_TIME);
        String orderPaymentMethod = orderData.get(position).get(Constant.ORDER_PAYMENT_METHOD);
        String orderType = orderData.get(position).get(Constant.ORDER_TYPE);
        String tableNo = orderData.get(position).get(Constant.TABLE_NO);


        holder.txtCustomerName.setText(customerName);
        holder.txtOrderId.setText(context.getString(R.string.order_id) + invoiceId);
        holder.txtOrderStatus.setText(context.getString(R.string.payment_method) + orderPaymentMethod);
        holder.txtOrderType.setText(context.getString(R.string.order_type) + orderType);
        holder.txtDate.setText(orderTime + " " + orderDate);

        if (tableNo == null || tableNo.equals("N/A")) {
            holder.txtTableNo.setVisibility(View.GONE);
        } else {
            holder.txtTableNo.setText(context.getString(R.string.table_number) + " :" + tableNo);
            holder.imgOrderImage.setImageResource(R.drawable.table_booking);
        }
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle(context.getString(R.string.delete))
                        .withMessage(context.getString(R.string.want_to_delete_order))
                        .withEffect(Slidetop)
                        .withDialogColor("#f29161") //use color code for dialog
                        .withButton1Text(context.getString(R.string.yes))
                        .withButton2Text(context.getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean deleteOrder = databaseAccess.deleteOrder(invoiceId);

                                if (deleteOrder) {
                                    Toasty.error(context, R.string.order_deleted, Toast.LENGTH_SHORT).show();

                                    orderData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                } else {
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
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
        return orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCustomerName, txtTableNo, txtOrderId, txtOrderType, txtOrderStatus, txtDate;
        ImageView imgDelete, imgOrderImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtCustomerName = itemView.findViewById(R.id.txt_customer_name);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtOrderType = itemView.findViewById(R.id.txt_order_type);
            txtOrderStatus = itemView.findViewById(R.id.txt_order_status);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtTableNo = itemView.findViewById(R.id.txt_table_no);
            imgDelete = itemView.findViewById(R.id.img_delete);
            imgOrderImage = itemView.findViewById(R.id.img_order_image);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, OrderDetailsActivity.class);
            i.putExtra("order_id", orderData.get(getAdapterPosition()).get("invoice_id"));
            i.putExtra(Constant.CUSTOMER_NAME, orderData.get(getAdapterPosition()).get(Constant.CUSTOMER_NAME));
            i.putExtra("order_date", orderData.get(getAdapterPosition()).get("order_date"));
            i.putExtra("order_time", orderData.get(getAdapterPosition()).get("order_time"));
            i.putExtra("tax", orderData.get(getAdapterPosition()).get("tax"));
            i.putExtra("discount", orderData.get(getAdapterPosition()).get("discount"));
            i.putExtra("table_no", orderData.get(getAdapterPosition()).get("table_no"));
            context.startActivity(i);
        }
    }


}