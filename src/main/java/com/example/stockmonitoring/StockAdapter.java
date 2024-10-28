package com.example.stockmonitoring;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class StockAdapter extends ArrayAdapter<Stock>
{
    ArrayList<Stock> stocks;

    Context context;

    public StockAdapter(Context context, int resource, int textViewResourceId, ArrayList<Stock> stocks)
    {
        super(context, resource, textViewResourceId, stocks);

        this.context = context;
        this.stocks = stocks;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_view_stock_row_layout, parent, false);
        }

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvOwnedBy = view.findViewById(R.id.tvOwnedBy);
        TextView tvPrice = view.findViewById(R.id.tvPrice);

        Stock stock = stocks.get(position);
        tvPrice.setText(String.valueOf(stock.getPrice()));
        tvName.setText(stock.getStockName());
        tvOwnedBy.setText(stock.getOwnedBy());

        return view;
    }
}
