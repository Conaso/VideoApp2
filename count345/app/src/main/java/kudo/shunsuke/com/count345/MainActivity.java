package kudo.shunsuke.com.count345;

import android.graphics.Color;
import android.support.v4.text.TextDirectionHeuristicCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView);
        count = 0;
    }

    public void add(View v){
        count = count + 1;
        textView.setText(String.valueOf(count));
        if (count % 5 == 0){
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        }else{
            textView.setTextColor(Color.parseColor("#ff669900"));
        }
    }

    public void add2(View v){
        count = count - 1;
        textView.setText(String.valueOf(count));
        if (count % 5 == 0){
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        }else{
            textView.setTextColor(Color.parseColor("#ff669900"));
        }
    }

    public void reset(View v){
        count = count - count;
        textView.setText(String.valueOf(count));
        if (count % 5 == 0){
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        }else{
            textView.setTextColor(Color.parseColor("#ff669900"));
        }
    }

//    public void textView(View v){
//        for (int i = 0 ; i < 5 ; i++){
//            if (i % 2 == 0){
//                count.setTextColor(Color.parseColor("#FFFFFF"));
//            }
//        }
//    }
}
