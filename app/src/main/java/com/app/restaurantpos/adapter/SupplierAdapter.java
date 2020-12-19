package com.app.restaurantpos.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.app.restaurantpos.suppliers.EditSuppliersActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.MyViewHolder> {


    private List<HashMap<String, String>> supplierData;
    private Context context;


    public SupplierAdapter(Context context, List<HashMap<String, String>> supplierData) {
        this.context = context;
        this.supplierData = supplierData;

    }


    @NonNull
    @Override
    public SupplierAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final SupplierAdapter.MyViewHolder holder, int position) {

        final String suppliersId = supplierData.get(position).get(Constant.SUPPLIERS_ID);
        String name = supplierData.get(position).get(Constant.SUPPLIERS_NAME);
        String contactPerson = supplierData.get(position).get(Constant.SUPPLIERS_CONTACT_PERSON);
        String cell = supplierData.get(position).get(Constant.SUPPLIERS_CELL);
        String email = supplierData.get(position).get(Constant.SUPPLIERS_EMAIL);
        String address = supplierData.get(position).get(Constant.SUPPLIERS_ADDRESS);

        holder.txtSuppliersName.setText(name);
        holder.txtSupplierContactPerson.setText(contactPerson);
        holder.txtSupplierCell.setText(cell);
        holder.txtSupplierEmail.setText(email);
        holder.txtSupplierAddress.setText(address);


        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String phone = "tel:" + cell;
                callIntent.setData(Uri.parse(phone));
                context.startActivity(callIntent);
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle(context.getString(R.string.delete))
                        .withMessage(context.getString(R.string.want_to_delete_supplier))
                        .withEffect(Slidetop)
                        .withDialogColor("#f29161") //use color code for dialog
                        .withButton1Text(context.getString(R.string.yes))
                        .withButton2Text(context.getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean deleteSupplier = databaseAccess.deleteSupplier(suppliersId);

                                if (deleteSupplier) {
                                    Toasty.error(context, R.string.supplier_deleted, Toast.LENGTH_SHORT).show();

                                    supplierData.remove(holder.getAdapterPosition());

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
        return supplierData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtSuppliersName, txtSupplierContactPerson, txtSupplierCell, txtSupplierEmail, txtSupplierAddress;
        ImageView imgDelete, imgCall;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtSuppliersName = itemView.findViewById(R.id.txt_supplier_name);
            txtSupplierContactPerson = itemView.findViewById(R.id.txt_contact_person);
            txtSupplierCell = itemView.findViewById(R.id.txt_supplier_cell);
            txtSupplierEmail = itemView.findViewById(R.id.txt_supplier_email);
            txtSupplierAddress = itemView.findViewById(R.id.txt_supplier_address);

            imgDelete = itemView.findViewById(R.id.img_delete);
            imgCall = itemView.findViewById(R.id.img_call);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(context, EditSuppliersActivity.class);
            i.putExtra(Constant.SUPPLIERS_ID, supplierData.get(getAdapterPosition()).get(Constant.SUPPLIERS_ID));
            i.putExtra(Constant.SUPPLIERS_NAME, supplierData.get(getAdapterPosition()).get(Constant.SUPPLIERS_NAME));
            i.putExtra(Constant.SUPPLIERS_CONTACT_PERSON, supplierData.get(getAdapterPosition()).get(Constant.SUPPLIERS_CONTACT_PERSON));
            i.putExtra(Constant.SUPPLIERS_CELL, supplierData.get(getAdapterPosition()).get(Constant.SUPPLIERS_CELL));
            i.putExtra(Constant.SUPPLIERS_EMAIL, supplierData.get(getAdapterPosition()).get(Constant.SUPPLIERS_EMAIL));
            i.putExtra(Constant.SUPPLIERS_ADDRESS, supplierData.get(getAdapterPosition()).get(Constant.SUPPLIERS_ADDRESS));
            context.startActivity(i);
        }
    }


}
