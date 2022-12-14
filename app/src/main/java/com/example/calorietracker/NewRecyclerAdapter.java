package com.example.calorietracker;

import static android.provider.Settings.System.getString;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewRecyclerAdapter extends RecyclerView.Adapter<NewRecyclerAdapter.ViewHolder>
{
    private String email;
    private String user_name;
    private String age;
    private String gender;
    private Context context;
    private String cardview_name;
    private int cardview_count;
    private LoadTheDatabase loadTheDatabase;
    private List<List<String>> item_values;
    private ExcelClass excelClass;
    private String[] favorites_list;
    private Dialog dialog;
    private String BASE_URL = "";

    public String chosen_date="";
    public String chosen_time="";
    private int quantity_val = 1;

    private void create_water()
    {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.water_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        Button apply_btn = (Button) dialog.findViewById(R.id.apply_btn);
        Button cancel_btn = (Button) dialog.findViewById(R.id.cancel_btn);

        NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.numberPicker1);

        String numval = "0";

        numberPicker.setMaxValue(1);
        numberPicker.setMinValue(0);
//        numberPicker2.setValue(0);
//
//        String[] vals = {"ml",""};
//
//        numberPicker2.setFormatter(new NumberPicker.Formatter() {
//            @Override
//            public String format(int value) {
//                // TODO Auto-generated method stub
//                return vals[value];
//            }
//        });

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(100);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int diff = value * 10;
                return "" + diff;
            }
        };
        numberPicker.setFormatter(formatter);

        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (chosen_date.equals("")) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();
                    chosen_date = formatter.format(date);
                    //Toast.makeText(getApplicationContext(),formatter.format(date),Toast.LENGTH_SHORT).show();
                }

                if (chosen_time.equals("")) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    Date date = new Date();
                    chosen_time = formatter.format(date);
                }

                String numval = numberPicker.getValue()*10+"";
                List<String> item_val = loadTheDatabase.get_nutrition("122");
                item_val.set(2,numval);
                InsertCSV insertCSV = new InsertCSV(context);
                insertCSV.insert_into_csv("Water",item_val,chosen_date,chosen_time);
                dialog.dismiss();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.create();
        dialog.show();
    }

    public NewRecyclerAdapter(Context context, String cardview_name, int cardview_count, LoadTheDatabase loadTheDatabase, List<List<String>> item_values,ExcelClass excelClass,String[] favorites_list,String chosen_date,String chosen_time)
    {
        this.context = context;
        this.cardview_name = cardview_name;
        this.cardview_count = cardview_count;
        this.loadTheDatabase = loadTheDatabase;
        this.item_values = item_values;
        this.excelClass = excelClass;
        this.chosen_date = chosen_date;
        this.chosen_time = chosen_time;
        this.favorites_list = favorites_list;
        this.BASE_URL = this.context.getString(R.string.BASE_URL);

        SharedPreferences pref = context.getSharedPreferences("Login",0);

        this.email = pref.getString("email","");

        this.user_name = pref.getString("name","");

        this.age = pref.getString("age","");

        this.gender = pref.getString("gender","");
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView item_img;
        private TextView item_text;
        private TextView item_id;
        private TextView item_calories;
        private ImageView heart_btn;
        private TextView save_btn;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            this.item_img = itemView.findViewById(R.id.cardview_image);
            this.item_text = itemView.findViewById(R.id.cardview_text);
            this.item_id = itemView.findViewById(R.id.item_id);
            this.item_calories = itemView.findViewById(R.id.cardview_calories);
            this.heart_btn = itemView.findViewById(R.id.heartbutton);
            this.save_btn = itemView.findViewById(R.id.save_btn);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(item_text.getText().toString().equals("Custom Value"))
                    {
                        create_water();
                        return;
                    }

                    Intent newintent = new Intent(context,DisplayTable.class);
                    newintent.putExtra("item_id",item_id.getText().toString());
                    newintent.putExtra("item_name",item_text.getText().toString());
                    newintent.putExtra("item_type",cardview_name);
                    newintent.putExtra("chosen_date",chosen_date);
                    newintent.putExtra("chosen_time",chosen_time);
                    context.startActivity(newintent);
                }
            });
        }
    }

    @NonNull
    @Override
    public NewRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.newactivity_card,parent,false);
        return new ViewHolder(view);
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    @Override
    public void onBindViewHolder(@NonNull NewRecyclerAdapter.ViewHolder holder, int position) {
        //TODO Call the LoadDatabase function and get the values regarding the cardview such as
        holder.item_img.setClipToOutline(true); //to set the image with curved edges
        List<String> this_item = this.item_values.get(position);

        int resid;

        try {
            String img_path = this_item.get(3);

            resid = this.context.getResources().getIdentifier(img_path, "drawable", this.context.getPackageName());

            if (resid != 0) {
                holder.item_img.setImageResource(resid);
            }
        } catch (Exception e) {
            Toast.makeText(this.context, "Unable to Fetch Data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        holder.item_text.setText(this_item.get(1));
        holder.item_id.setText(this_item.get(0));
        holder.item_calories.setText("Calories: " + this_item.get(2) + " gms");

        if (holder.item_text.getText().toString().equals("Custom Value")) {
            holder.save_btn.setVisibility(View.GONE);
        }
//        if(this_item.get(3).equals("1"))
//        {
//            holder.heart_btn.setBackgroundResource(R.drawable.heart_liked);
//            holder.heart_btn.setContentDescription("1");
//        }
//        else
//        {
//            holder.heart_btn.setBackgroundResource(R.drawable.heart_it);
//            holder.heart_btn.setContentDescription("0");
//        }

        this.favorites_list = excelClass.get_favorites(cardview_name);

        if (Arrays.asList(this.favorites_list).contains(holder.item_id.getText().toString())) {
            holder.heart_btn.setBackgroundResource(R.drawable.heart_liked);
            holder.heart_btn.setContentDescription("1");
        } else {
            holder.heart_btn.setBackgroundResource(R.drawable.heart_it);
            holder.heart_btn.setContentDescription("0");
        }

        //Toast.makeText(this.context,this.cardview_count+"",Toast.LENGTH_SHORT).show();

        holder.heart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (view.getContentDescription().equals("1")) {
                    //Toast.makeText(context,"Disliked",Toast.LENGTH_SHORT).show();
                    //TODO write logic to change the values in .csvFile and change the value in database and notify the recyclerView.
                    excelClass.remove_from_favorites(holder.item_id.getText().toString(), cardview_name);
                    //loadTheDatabase.remove_liked(holder.item_id.getText().toString());
                    view.setBackgroundResource(R.drawable.heart_it);
                    view.setContentDescription("0");
                    favorites_list = excelClass.get_favorites(cardview_name);
                    String[] unfavorites = loadTheDatabase.get_unFavorites(cardview_name, favorites_list);
                    item_values = loadTheDatabase.get_smaller_card_values(favorites_list, unfavorites);
                    if (favorites_list.length != cardview_count) {
                        notifyItemMoved(holder.getAdapterPosition(), favorites_list.length);
                    } else {
                        notifyItemMoved(holder.getAdapterPosition(), cardview_count - 1);
                    }

                } else //logic to add liked buottn
                {
                    //Toast.makeText(context,"liked",Toast.LENGTH_SHORT).show();
                    excelClass.add_to_favorites(holder.item_id.getText().toString(), cardview_name);
                    //loadTheDatabase.add_liked(holder.item_id.getText().toString());
                    view.setBackgroundResource(R.drawable.heart_liked);
                    view.setContentDescription("1");
                    favorites_list = excelClass.get_favorites(cardview_name);
                    String[] unfavorites = loadTheDatabase.get_unFavorites(cardview_name, favorites_list);
                    item_values = loadTheDatabase.get_smaller_card_values(favorites_list, unfavorites);
                    notifyItemMoved(holder.getAdapterPosition(), 0);
                }
            }
        });

        holder.save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cardview_name.equals("Fruits")) {
                    create_quantity_dialog(holder);
                }
                else{
                    create_save_dialog(holder);
                }
            }
        });
    }

    private void  create_save_dialog(ViewHolder holder)
    {
        InsertCSV insertCSV = new InsertCSV(context);
        LoadTheDatabase loadTheDatabase = new LoadTheDatabase(context);
        List<String> item_values = loadTheDatabase.get_nutrition(holder.item_id.getText().toString());
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.save_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        Button apply_btn = (Button) dialog.findViewById(R.id.apply_btn);
        Button cancel_btn = (Button) dialog.findViewById(R.id.cancel_btn);

        TextView date = dialog.findViewById(R.id.dialog_date);
        TextView time = dialog.findViewById(R.id.dialog_time);
        TextView calories = dialog.findViewById(R.id.dialog_calories);
        TextView protein = dialog.findViewById(R.id.dialog_protein);
        TextView fat = dialog.findViewById(R.id.dialog_fat);
        TextView carbs = dialog.findViewById(R.id.dialog_carbs);
        TextView name = dialog.findViewById(R.id.dialog_name);
        TextView type = dialog.findViewById(R.id.dialog_type);

        if(chosen_date.equals(""))
        {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = new Date();
            chosen_date = formatter.format(date1);
            //Toast.makeText(getApplicationContext(),formatter.format(date),Toast.LENGTH_SHORT).show();
        }

        if(chosen_time.equals(""))
        {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date date1 = new Date();
            chosen_time = formatter.format(date1);
        }

        String str;

        float x=1.0f;

        if(cardview_name.equals("Fruits"))
        {
            for(int i=0;i<item_values.size();i++)
            {
                try
                {
                    x = Float.parseFloat(item_values.get(i));
                    item_values.set(i,(x*quantity_val)+"");
                }
                catch(Exception e)
                {
                    Toast.makeText(context,item_values.get(i),Toast.LENGTH_SHORT).show();
                }
            }
        }

        str = "<b>Name: </b>"+holder.item_text.getText().toString();
        name.setText(Html.fromHtml(str));

        str = "<b>Type: </b>"+cardview_name;
        type.setText(Html.fromHtml(str));

        str = "<b>Date: </b>"+chosen_date;
        date.setText(Html.fromHtml(str));

        str = "<b>Time: </b>"+chosen_time;
        time.setText(Html.fromHtml(str));

        str = "<b>Calories: </b>"+item_values.get(3)+"g";
        calories.setText(Html.fromHtml(str));

        str = "<b>Protein: </b>"+item_values.get(13)+"%";
        protein.setText(Html.fromHtml(str));

        str = "<b>Carbohydrates: </b>"+item_values.get(9)+"%";
        carbs.setText(Html.fromHtml(str));

        str = "<b>Fat: </b>"+item_values.get(4)+"%";
        fat.setText(Html.fromHtml(str));

        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                //Toast.makeText(getApplicationContext(),chosen_date+" "+chosen_time,Toast.LENGTH_LONG).show();

                insertCSV.insert_into_csv(cardview_name,item_values,chosen_date,chosen_time);

                if(isConnected())
                {
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

                    RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

                    //Toast.makeText(context,"Connected",Toast.LENGTH_SHORT).show();

                    HashMap<String,String> map = insertCSV.read_from_buffer();

                    //Toast.makeText(context,map.size()+"",Toast.LENGTH_SHORT).show();

                    map.put("size",map.size()+"");

                    map.put("email",email);
                    map.put("name",user_name);
                    map.put("age",age);
                    map.put("gender",gender);

                    Call<Void> call = retrofitInterface.executesend(map);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response)
                        {
                            if(response.code() == 200) {
                                insertCSV.delete_buffer();
                            }
                            //Toast.makeText(context,"Success ra",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            //Toast.makeText(context,"Failrue",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else
                {
                    //Toast.makeText(context,"Not Connected",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
//                chosen_time = getIntent().getStringExtra("chosen_time");
//                chosen_date = getIntent().getStringExtra("chosen_date");
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.create();
        dialog.show();
    }

    private void create_quantity_dialog(ViewHolder holder)
    {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.age_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        TextView side_text = dialog.findViewById(R.id.side_text);

        TextView choose_txt = dialog.findViewById(R.id.choose_text);

        choose_txt.setText("Choose Quantity:");

        side_text.setText("nos");

        Button apply_btn = (Button) dialog.findViewById(R.id.apply_btn);
        Button cancel_btn = (Button) dialog.findViewById(R.id.cancel_btn);

        NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.age_picker);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int diff = value;
                return "" + diff;
            }
        };
        numberPicker.setFormatter(formatter);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                quantity_val = newVal;
            }
        });

        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();create_save_dialog(holder);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.create();
        dialog.show();
    }

    @Override
    public int getItemCount()
    {
        return this.cardview_count;
    }
}
