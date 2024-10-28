package com.example.stockmonitoring;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ArticleAdapter extends ArrayAdapter<Article> {
    ArrayList<Article> articles;

    Context context;

    public ArticleAdapter(Context context, int resource, int textViewResourceId, ArrayList<Article> articles)
    {
        super(context, resource, textViewResourceId, articles);

        this.context = context;
        this.articles = articles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.list_view_article_row_layout,parent,false);

        TextView tvName = view.findViewById(R.id.tvArticleName);

        Article article = articles.get(position);
        tvName.setText(article.getArticleName());

        return view;
    }
}
