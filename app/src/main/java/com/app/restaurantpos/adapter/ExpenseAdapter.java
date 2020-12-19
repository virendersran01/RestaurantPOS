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
import com.app.restaurantpos.expense.EditExpenseActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {


    private List<HashMap<String, String>> expenseData;
    private Context context;


    public ExpenseAdapter(Context context, List<HashMap<String, String>> expenseData) {
        this.context = context;
        this.expenseData = expenseData;

    }


    @NonNull
    @Override
    public ExpenseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ExpenseAdapter.MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        final String expenseId = expenseData.get(position).get(Constant.EXPENSE_ID);
        String expenseName = expenseData.get(position).get(Constant.EXPENSE_NAME);
        String expenseNote = expenseData.get(position).get(Constant.EXPENSE_NOTE);
        String expenseAmount = expenseData.get(position).get(Constant.EXPENSE_AMOUNT);
        String date = expenseData.get(position).get(Constant.EXPENSE_DATE);
        String time = expenseData.get(position).get(Constant.EXPENSE_TIME);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        holder.txtExpenseName.setText(expenseName);
        holder.txtExpenseAmount.setText(currency + expenseAmount);
        holder.txtExpenseDateTime.setText(date + " " + time);
        holder.txtExpenseNote.setText(context.getString(R.string.note) + expenseNote);


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle(context.getString(R.string.delete))
                        .withMessage(context.getString(R.string.want_to_delete_expense))
                        .withEffect(Slidetop)
                        .withDialogColor("#f29161") //use color code for dialog
                        .withButton1Text(context.getString(R.string.yes))
                        .withButton2Text(context.getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                databaseAccess.open();
                                boolean deleteProduct = databaseAccess.deleteExpense(expenseId);

                                if (deleteProduct) {
                                    Toasty.error(context, R.string.expense_deleted, Toast.LENGTH_SHORT).show();

                                    expenseData.remove(holder.getAdapterPosition());

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
        return expenseData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtExpenseName, txtExpenseAmount, txtExpenseNote, txtExpenseDateTime;
        ImageView imgDelete, productImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtExpenseName = itemView.findViewById(R.id.txt_expense_name);
            txtExpenseAmount = itemView.findViewById(R.id.txt_expense_amount);
            txtExpenseNote = itemView.findViewById(R.id.txt_expense_note);
            txtExpenseDateTime = itemView.findViewById(R.id.txt_date_time);

            imgDelete = itemView.findViewById(R.id.img_delete);
            productImage = itemView.findViewById(R.id.product_image);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditExpenseActivity.class);
            i.putExtra("expense_id", expenseData.get(getAdapterPosition()).get("expense_id"));
            i.putExtra("expense_name", expenseData.get(getAdapterPosition()).get("expense_name"));
            i.putExtra("expense_note", expenseData.get(getAdapterPosition()).get("expense_note"));
            i.putExtra("expense_amount", expenseData.get(getAdapterPosition()).get("expense_amount"));
            i.putExtra("expense_date", expenseData.get(getAdapterPosition()).get("expense_date"));
            i.putExtra("expense_time", expenseData.get(getAdapterPosition()).get("expense_time"));
            context.startActivity(i);
        }
    }


}
