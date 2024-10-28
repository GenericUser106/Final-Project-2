package com.example.stockmonitoring;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    Animation animationScale;

    ArrayList<Article> articleList;

    GraphView stockGraph;

    Intent openNotif;

    ImageView appLogo;

    ListView articlesList;

    Article selectedArticle;
    ArticleAdapter articleAdapter;


    SharedPreferences saveUsername;

    String username;

    TextView welcomeMessage;
    TextView timeDisplay;
//  -------------------------------------------------------------------------
    @SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appLogo = findViewById(R.id.appLogo);
        animationScale = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale);
        appLogo.setAnimation(animationScale);
//      -------------------------------------------------------------------------
        //set action for notification
        openNotif = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ynet.co.il/economy/article/sj4ngjke3"));
        openNotif.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//      -------------------------------------------------------------------------
        //welcome message init
        welcomeMessage = findViewById(R.id.welcomeMessage);

        saveUsername = getSharedPreferences("username", 0);
        username = saveUsername.getString("username", "");

        setWelcomeMessage(username);
        welcomeMessage.setAnimation(animationScale);
//      -------------------------------------------------------------------------
        //broadcast receiver: clock
        timeDisplay = findViewById(R.id.timeDisplay);
        TimeBroadcastReceiver timeBroadcastReceiver = new TimeBroadcastReceiver(timeDisplay);
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(timeBroadcastReceiver, filter);
//      -------------------------------------------------------------------------
        //help articles list init
        articlesList = findViewById(R.id.articlesList);

        articleList = new ArrayList<>();

        Article article1 = new Article("how to invest in stock, for beginners", "https://www.bankrate.com/investing/how-to-invest-in-stocks/");
        Article article2 = new Article("how to invest in share market", "https://www.forbes.com/advisor/in/investing/how-to-invest-in-share-market/");
        Article article3 = new Article("10 tips for successful long-term investing", "https://www.investopedia.com/articles/00/082100.asp");
        Article article4 = new Article("5 of Warren Buffett's best tips for investing in the stock market", "https://www.cnbc.com/2018/12/17/warren-buffett-tips-on-how-to-invest-in-the-stock-market.html");
        Article article5 = new Article("How to invest in today's market", "https://fortune.com/recommends/investing/investing-amid-high-inflation-and-bank-failures/");

        articleList.add(article1);
        articleList.add(article2);
        articleList.add(article3);
        articleList.add(article4);
        articleList.add(article5);

        articleAdapter = new ArticleAdapter(this, 0, 0, articleList);

        articlesList.setAdapter(articleAdapter);

        articlesList.setOnItemClickListener(this);
//      -------------------------------------------------------------------------
        //graph init
        stockGraph = findViewById(R.id.stockGraph);
        createGraph(stockGraph);
    }

    //      -------------------------------------------------------------------------
    public void setWelcomeMessage(String name) {
        if (name.equals("")) {
            welcomeMessage.setText("Welcome guest");
        } else {
            welcomeMessage.setText("Welcome " + name);
        }
    }

    //      -------------------------------------------------------------------------
    public void createGraph(GraphView graphView) {
        LineGraphSeries<DataPoint> series;
        series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(1, 2),
                new DataPoint(2, 1),
                new DataPoint(3, 4),
                new DataPoint(4, 8),
                new DataPoint(5, 3),
                new DataPoint(6, 7),
                new DataPoint(7, 5),
                new DataPoint(8, 6)
        });
        graphView.addSeries(series);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
    }

    //      -------------------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectedArticle = articleAdapter.getItem(i);

        Intent goToWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedArticle.getWebAddress()));
        startActivity(goToWeb);
    }
//      -------------------------------------------------------------------------
    //  init BroadcastReceiver
    public static class TimeBroadcastReceiver extends BroadcastReceiver
    {
        TextView timeDisplay;

        public TimeBroadcastReceiver(TextView timeDisplay)
        {
            this.timeDisplay = timeDisplay;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(calendar.getTime());
            timeDisplay.setText(time);
        }
    }
}