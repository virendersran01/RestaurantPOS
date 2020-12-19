package com.app.restaurantpos.report;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.utils.BaseActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GraphReportActivity extends BaseActivity {


    int mYear = 2020;
    BarChart barChart;
    TextView txtTotalSales, txtSelectYear, txtTotalTax, txtTotalDiscount, txtNetSales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_report);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.monthly_sales_graph);


        barChart = findViewById(R.id.barchart);
        txtTotalSales = findViewById(R.id.txt_total_sales);
        txtSelectYear = findViewById(R.id.txt_select_year);
        txtTotalTax = findViewById(R.id.txt_total_tax);
        txtTotalDiscount = findViewById(R.id.txt_discount);
        txtNetSales = findViewById(R.id.txt_net_sales);

        barChart.setDrawBarShadow(false);

        barChart.setDrawValueAboveBar(true);

        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);


        String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
        txtSelectYear.setText(getString(R.string.year) + currentYear);


        getGraphData();
    }


    public void getGraphData() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(GraphReportActivity.this);

        String[] monthNumber = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            databaseAccess.open();
            barEntries.add(new BarEntry(i, databaseAccess.getMonthlySalesAmount(monthNumber[i], "" + mYear)));
        }


        String[] monthList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthList));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(12);

        BarDataSet barDataSet = new BarDataSet(barEntries, getString(R.string.monthly_sales_report));
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);

        barChart.setScaleEnabled(false);  //for fixed bar chart,no zoom


        databaseAccess.open();
        String currency = databaseAccess.getCurrency();
        databaseAccess.open();
        double subTotal = databaseAccess.getTotalOrderPrice(Constant.YEARLY);
        txtTotalSales.setText(getString(R.string.total_sales) + currency + subTotal);

        databaseAccess.open();
        double getTax = databaseAccess.getTotalTax(Constant.YEARLY);
        txtTotalTax.setText(getString(R.string.total_tax) + "(+) : " + currency + getTax);


        databaseAccess.open();
        double getDiscount = databaseAccess.getTotalDiscount(Constant.YEARLY);
        txtTotalDiscount.setText(getString(R.string.total_discount) + "(-) : " + currency + getDiscount);

        double netSales = subTotal + getTax - getDiscount;
        txtNetSales.setText(getString(R.string.net_sales) + ": " + currency + netSales);


    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
