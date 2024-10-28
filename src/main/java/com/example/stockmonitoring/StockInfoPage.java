package com.example.stockmonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StockInfoPage extends BaseActivity {

    Button goToWebpage;

    float placeHolderPrice;

    TextView name;
    TextView price;
    TextView ownedBy;

    String stockName;
    String stockOwnedBy;
    String stockPrice;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info_page);

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        ownedBy = findViewById(R.id.ownedBy);
        goToWebpage = findViewById(R.id.goToPage);

        stockName = getIntent().getStringExtra("name");
        stockOwnedBy = getIntent().getStringExtra("ownedBy");
        placeHolderPrice = getIntent().getFloatExtra("price", 0);

        stockPrice = Float.toString(placeHolderPrice);

        name.setText(stockName);
        ownedBy.setText(stockOwnedBy);
        price.setText(stockPrice);
    }

    public void goToWebpage(View view) {
        if (view == goToWebpage)
        {
            Intent goToWebpage = new Intent(Intent.ACTION_VIEW, Uri.parse("https://finance.yahoo.com/quote/" + stockName + "/"));
            startActivity(goToWebpage);
        }
    }
}