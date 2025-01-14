package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddtripActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener  {

    String[] courses = {"High","Medium","Less"};;
    public Button yes,no;
    private EditText tripname;
    private String currentUserID;
    private DatePicker datepicker;
    private DatabaseReference RootRef;
    String string_priority = "Less" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtrip);

        Spinner spino = findViewById(R.id.priority_spinner);
        spino.setOnItemSelectedListener(this);
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                courses);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        spino.setAdapter(ad);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        RootRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID);


        yes = (Button) findViewById(R.id.create_trip_submit_butyyon);
        no = (Button) findViewById(R.id.cancel_trip_submit_butyyon);
        tripname = (EditText)findViewById(R.id.edit_text_trip_name);
        datepicker = (DatePicker) findViewById(R.id.edit_text_trip_date);




        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                YESONCLICK();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NOONCLICK();
            }
        });

    }

    private void NOONCLICK() {
        Intent loginIntent = new Intent ( AddtripActivity.this,MainActivity.class );
        loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( loginIntent );
    }

    private void YESONCLICK() {
        String trip_key = RootRef.child("Goals").child("Active").push().getKey();
        CreteATripNew(trip_key);
    }

    private void CreteATripNew(String string_trip) {



        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat format1_only = new SimpleDateFormat("yyyy-M-dd");
        String today_only = format1_only.format(today);
        String todaay = format.format(today);



        int year = datepicker.getYear();
        int month = datepicker.getMonth();
        int day = datepicker.getDayOfMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);



        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatt = new SimpleDateFormat("dd/M/yyyy");
        String strDate = formatt.format(calendar.getTime())+" 23:59:59";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatt_only = new SimpleDateFormat("yyyy-M-dd");
        String strDate_only = formatt_only.format(calendar.getTime());

        String string = tripname.getText().toString();




        if (TextUtils.isEmpty (string))
        {
            Toast.makeText(AddtripActivity.this, "Enter any Trip name ..", Toast.LENGTH_SHORT).show();
        }
        else
        {



                            HashMap<String,Object> onlineStat = new HashMap<> (  );
                            onlineStat.put ( "GoalName", string);
                            onlineStat.put ( "GoalType", string_priority);
                            onlineStat.put ( "EndTime", strDate);
                            onlineStat.put ( "TodayTime", todaay);
                            onlineStat.put ( "Consistency","100");
                            onlineStat.put ( "Win","");

                       RootRef.child("Goals").child("Active").child(string_trip)
                                    .updateChildren ( onlineStat );

            Intent loginIntent = new Intent ( AddtripActivity.this,MainActivity.class );
            loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( loginIntent );


        }


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        string_priority = courses[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static List<Date> getDates(String dateString1, String dateString2)
    {
        ArrayList<Date> dates = new ArrayList<Date>();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(dateString1);
            date2 = df1 .parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }
}