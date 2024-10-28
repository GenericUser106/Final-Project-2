package com.example.stockmonitoring;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileScreen extends BaseActivity implements AdapterView.OnItemClickListener {
    ArrayList<Stock> stockArrayList;

    Button addStock;
    //Button deleteStock;
    Button addPfp;
    Button savePfp;

    Bitmap savedImage;

    EditText enterName;

    FirebaseFirestore firestore;

    Intent goToInfo;
    Intent openNotif;

    ImageView profilePic;

    ListView stockList;

    PendingIntent convert;

    Stock selectedStock;
    StockAdapter stockAdapter;
    String username;
    String performanceId;

    TextView welcomeMessage2;


    final long TWO_MEGABYTES = 2048 * 2048;

    @SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
//  -------------------------------------------------------------------------
        //init stock list
        enterName = findViewById(R.id.enterName);
        addStock = findViewById(R.id.addStock);
        //deleteStock = findViewById(R.id.deleteStock);
        stockList = findViewById(R.id.stockList);

        //stockAdapter = new StockAdapter(ProfileScreen.this, 0, 0, stockArrayList);
        //stockArrayList = new ArrayList<>();

        //getUsersList();
//  -------------------------------------------------------------------------
            //welcome message
            saveUsername = getSharedPreferences("username", 0);
            username = saveUsername.getString("username", "");

            welcomeMessage2 = findViewById(R.id.welcomeMessage2);

            setWelcomeMessage2(username);
//  -------------------------------------------------------------------------
        //notification set up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("stockUp", "Stocks Update", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
//  -------------------------------------------------------------------------
        //notification creation
        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(ProfileScreen.this, "stockUp");
        builder.setContentTitle("Stocks Update");
        builder.setContentText("You might be interested in this stock");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ProfileScreen.this);
        managerCompat.notify(1, builder.build());

        notificationService.startService(new Intent(this, NotificationService.class));*/

        //set action for notification
        /*openNotif = new Intent(Intent.ACTION_VIEW, Uri.parse("https://finance.yahoo.com/quote/NVDA/"));
        openNotif.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        convert = PendingIntent.getActivity(this, 0, openNotif, PendingIntent.FLAG_IMMUTABLE);*/
//  -------------------------------------------------------------------------
        //set up profile picture
        profilePic = findViewById(R.id.profilePic);
        addPfp = findViewById(R.id.addPfp);
        savePfp = findViewById(R.id.savePfp);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
//  -------------------------------------------------------------------------
        //import profile data
        if (!username.equals(""))
        {
            setUpProfile();
        }
        else
        {
            Toast.makeText(this, "please sign in", Toast.LENGTH_SHORT).show();
            createLoginDialog();

        }
//  -------------------------------------------------------------------------
        }

        public void setWelcomeMessage2(String name)
        {
            if (name == null || name.equals(""))
            {
                welcomeMessage2.setText("Welcome guest");
            }

            else
            {
                welcomeMessage2.setText("Welcome " + name);
            }
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            StockAdapter stockAdapter = new StockAdapter(getApplicationContext(), 0, 0, stockArrayList);
            selectedStock = stockAdapter.getItem(position);

            goToInfo = new Intent(this, StockInfoPage.class);
            goToInfo.putExtra("name", selectedStock.getStockName());
            goToInfo.putExtra("ownedBy", selectedStock.getOwnedBy());
            goToInfo.putExtra("price", selectedStock.getPrice());
            startActivity(goToInfo);
        }

    public void addPicture(View view) {
        if (view == addPfp)
        {
            Intent getPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            setPfpActivityResultLauncher.launch(getPicture);
        }

        else if (view == savePfp)
        {
            if (!username.equals(""))
            {
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
                addPictureToFirebase();
            }
            else
            {
                Toast.makeText(this, "please sign in", Toast.LENGTH_SHORT).show();
                createLoginDialog();
            }
        }
    }

    public void setUpProfile() {
        {
            String uid = auth.getUid();
            firestore.collection("users").document(uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                stockArrayList = user.getStocks();
                                stockAdapter = new StockAdapter(ProfileScreen.this, 0, 0, stockArrayList);
                                stockList.setAdapter(stockAdapter);
                                stockList.setOnItemClickListener(ProfileScreen.this);
                                addImage();
                            }
                            else {
                                Toast.makeText(ProfileScreen.this, "please sign in", Toast.LENGTH_SHORT).show();
                                createLoginDialog();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileScreen.this, "unable to get image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void modifyList(View view)
    {
        //String input = enterName.getText().toString();
        //int position = Integer.parseInt(input);
        stockAdapter = new StockAdapter(getApplicationContext(), 0, 0, stockArrayList);
        if (view == addStock)
        {
            if (enterName.getText().toString().isEmpty() && username.equals(""))
            {
                Toast.makeText(this, "please type in a company's name and sign in", Toast.LENGTH_LONG).show();
                createLoginDialog();
            }
            else if ((enterName.getText().toString().isEmpty()))
            {
                Toast.makeText(this, "please type in a company's name", Toast.LENGTH_SHORT).show();
            }
            else if (username.equals(""))
            {
                Toast.makeText(this, "please sign in", Toast.LENGTH_SHORT).show();
                createLoginDialog();
            }
            else
            {
                JsonWork jsonWork = new JsonWork();
                jsonWork.execute();
            }
        }

        /*else if (view == deleteStock)
        {
            if ((enterName.getText().toString().isEmpty()))
            {
                Toast.makeText(this, "please type in a number", Toast.LENGTH_SHORT).show();
            }

            else if (position < stockArrayList.size())
            {
                Toast.makeText(this, "error: number is bigger than list's size", Toast.LENGTH_SHORT).show();
            }

            else
            {
                try {
                    Stock stockToRemove = stockArrayList.get(position);
                    stockArrayList.remove(position);

                    stockAdapter.notifyDataSetChanged();

                    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
                    collectionReference.document(stockToRemove.getOwnedBy()).delete()
                            .addOnSuccessListener(aVoid -> Log.d("deletedSuccess", "successfully deleted"))
                            .addOnFailureListener(e -> Log.d("deletedFail", "failed to delete", e));
                }
                catch (NumberFormatException e)
                {
                    Toast.makeText(this, "please type in a number", Toast.LENGTH_SHORT).show();
                }
            }
        }*/
    }

    @SuppressLint("StaticFieldLeak")
    private class JsonWork extends AsyncTask<String, String, ArrayList<Stock>> {
        @Override
        protected ArrayList<Stock> doInBackground(String... strings) {
            //get performanceId
            OkHttpClient client = new OkHttpClient();
            String stockName = enterName.getText().toString();
            String urlId = "https://ms-finance.p.rapidapi.com/market/v2/auto-complete?q=" + stockName;

            Request idRequest = new Request.Builder()
                    .url(urlId)
                    .get()
                    .addHeader("X-RapidAPI-Key", "40dfce209fmsh0195154946f59e0p1a9a41jsn4d1918beda85")
                    .addHeader("X-RapidAPI-Host", "ms-finance.p.rapidapi.com")
                    .build();

            Call call = client.newCall(idRequest);
            try {
                Response idResponse = call.execute();
                String idData = idResponse.body().string();
                JSONObject idObject = new JSONObject(idData);
                JSONArray resultsArray = idObject.getJSONArray("results");
                JSONObject firstResult = resultsArray.getJSONObject(0);
                performanceId = firstResult.getString("performanceId");
                String ticker = firstResult.getString("ticker");
                String ownedBy = firstResult.getString("name");

                Stock stock = new Stock(ticker, ownedBy);
                //get stock data
                String urlData = "https://ms-finance.p.rapidapi.com/stock/v2/get-realtime-data?performanceId=" + performanceId;
                Request stockRequest = new Request.Builder()
                        .url(urlData)
                        .get()
                        .addHeader("X-RapidAPI-Key", "40dfce209fmsh0195154946f59e0p1a9a41jsn4d1918beda85")
                        .addHeader("X-RapidAPI-Host", "ms-finance.p.rapidapi.com")
                        .build();

                Call call1 = client.newCall(stockRequest);

                try {
                    Response stockResponse = call1.execute();
                    String stockData = stockResponse.body().string();
                    JSONObject stockObject = new JSONObject(stockData);

                    String pricePlaceHolder = stockObject.getString("lastPrice");
                    float price = 0;
                    try {
                        price = Float.parseFloat(pricePlaceHolder);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProfileScreen.this, "unable to get price", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    stock.setPrice(price);
                    stockArrayList.add(stock);
                    updateUserStock(stock);
                    openNotif = new Intent(Intent.ACTION_VIEW, Uri.parse("https://finance.yahoo.com/quote/" + stock.getStockName() + "/"));
                    openNotif.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    convert = PendingIntent.getActivity(ProfileScreen.this, 0, openNotif, PendingIntent.FLAG_IMMUTABLE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(ProfileScreen.this, "stockUp");
                    builder.setContentTitle("Expanded info on " + stock.getStockName());
                    builder.setContentText("Here's info about " + stock.getStockName());
                    builder.setSmallIcon(R.drawable.capitalshares_logo);
                    builder.setContentIntent(convert);

                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ProfileScreen.this);
                    managerCompat.notify(1, builder.build());
                    return stockArrayList;
                }
                catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ProfileScreen.this, "unable to get information", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            Toast.makeText(ProfileScreen.this, "incorrect name, please enter another name", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute (ArrayList < Stock > stockArrayList)
        {
            super.onPostExecute(stockArrayList);
            if (stockArrayList != null) {
                stockAdapter = new StockAdapter(getApplicationContext(), 0, 0, stockArrayList);
                stockList.setAdapter(stockAdapter);
                stockList.setOnItemClickListener(ProfileScreen.this);
            }
        }
    }

    private void updateUserStock(Stock stock)
    {
        String id = auth.getUid();
        firestore.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);
                        if (user != null)
                        {
                            user.addStocks(stock);
                            FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .set(user);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileScreen.this, "Failed to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private ActivityResultLauncher<Intent> setPfpActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we will handle the result of our intent
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null)
                        {
                            Uri selectedImage = data.getData();
                            try {
                                savedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                                profilePic.setImageBitmap(savedImage);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else {
                        //cancelled
                        Toast.makeText(ProfileScreen.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void addPictureToFirebase() {
        Bitmap bitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();
        auth = FirebaseAuth.getInstance();

        UploadTask uploadTask = storageReference.child("images/users").child(auth.getUid()).putBytes(data);
        Toast.makeText(this, "processing", Toast.LENGTH_LONG).show();
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ProfileScreen.this, "Could not upload image", Toast.LENGTH_LONG).show();
            }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    Toast.makeText(ProfileScreen.this, "SUCCESS - image uploaded.", Toast.LENGTH_LONG).show();
                }
            });
        }

        private void addImage()
        {
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference userPfp = storageReference.child("images/users/" + auth.getUid());
            userPfp.getBytes(TWO_MEGABYTES)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            profilePic.setImageBitmap(compressedBitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileScreen.this, "image could not be downloaded", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
}