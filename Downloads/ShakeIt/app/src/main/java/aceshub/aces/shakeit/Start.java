package aceshub.aces.shakeit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by honey on 18/9/16.
 */
public class Start extends Activity {

    TextView next;
    EditText ed;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        next = (TextView) findViewById(R.id.textView2);
        ed = (EditText) findViewById(R.id.editText);

        sp = getApplicationContext().getSharedPreferences("shakeit", MODE_PRIVATE);

        if(!sp.getString("name", "").contentEquals("")) {
            Intent i = new Intent(Start.this, Test.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!ed.getText().toString().contentEquals(""))
                    next.setVisibility(View.VISIBLE);
                else
                    next.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!ed.getText().toString().contentEquals("")) {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("name", ed.getText().toString());
                    edit.commit();

                    Intent i = new Intent(Start.this, Start.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } else
                    Toast.makeText(Start.this, "Please enter name", Toast.LENGTH_SHORT).show();

            }
        });



    }
}
