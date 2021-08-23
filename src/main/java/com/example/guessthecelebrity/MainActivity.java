package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> img = new ArrayList<String>();
    ArrayList<String> names = new ArrayList<String>();
    int choosen = 0;
    String[] answers = new String[4];
    int correctAnswer = 0;
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void chosen(View view){
        if (view.getTag().toString().equals(Integer.toString(correctAnswer))) {
            Toast.makeText(getApplicationContext(), "Correct!",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Wrong! It was "+names.get(choosen),Toast.LENGTH_SHORT).show();
        }
        newQues();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {


            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(urls[0]);
                //convert to actual url and get the first object in our pseudo array
                urlConnection = (HttpURLConnection) url.openConnection();
                //set up url connection
                InputStream in = urlConnection.getInputStream();
                //create Input Stream
                InputStreamReader reader = new InputStreamReader(in);
                //Stream reader
                int data = reader.read();
                while (data!=-1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    public void newQues(){
        try{
            Random rand = new Random();
            choosen = rand.nextInt(img.size());
            ImageDownloader imageDownloader = new ImageDownloader();
            Bitmap celebImage = imageDownloader.execute(img.get(choosen)).get();
            imageView.setImageBitmap(celebImage);
            correctAnswer = rand.nextInt(4);
            int incorrectAnswer;
            for(int i=0; i<4; i++){
                if(i == correctAnswer){
                    answers[i]=names.get(choosen);
                }else{
                    incorrectAnswer = rand.nextInt(img.size());

                    while (incorrectAnswer == correctAnswer) {
                        incorrectAnswer = rand.nextInt(img.size());
                    }
                    answers[i] = names.get(incorrectAnswer);
                }
            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        String result = null;

        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

         try{
             result= task.execute("https://www.forbesindia.com/lists/2019-celebrity-100/1819/all").get();
             String[] split = result.split("<div class=\"news-story-outer clearfix row\">");

             Pattern p = Pattern.compile("img src\"(.*?)\"");
             Matcher m = p.matcher(split[0]);

             while(m.find()){
                 img.add(m.group(1));
             }

             p = Pattern.compile("alt=\"(.*?)\"");
             m = p.matcher(split[0]);

             while(m.find()){
                 names.add(m.group(1));
             }

             newQues();

         } catch (Exception e){
             e.printStackTrace();
         }
    }
}