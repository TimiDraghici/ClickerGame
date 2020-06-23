package com.example.clickergame;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Game extends AppCompatActivity implements View.OnClickListener{

    private TextView points;
    private TextView pointsanim;
    private TextView cost_p1_text;
    private TextView cost_p2_text;
    private TextView cost_p3_text;
    private TextView text_cursor_cost;
    private TextView text_chicken_up;
    private TextView text_carton_up;
    private TextView text_coop_up;
    private TextView perclick;
    private TextView income;
    private TextView not_enough_eggs;

    private EditText saveusername;
    private EditText savepassword;
    private EditText loadusername;
    private EditText loadpassword;

    private ImageView img_chicken;
    private ImageView img_carton;
    private ImageView img_coop;

    private String username;
    private String password;
    private String from_server;
    private String aux;

    private int point = 0;
    private int eps = 0;
    private int click = 1;
    private int lvl_cursor= 1;
    private int cost_cursor= 5;
    private int lvl_p1 = 0;
    private int cost_p1 = 50;
    private int lvl_p2 = 0;
    private int cost_p2 = 200;
    private int lvl_p3 = 0;
    private int cost_p3 = 500;
    private boolean pause=false;
    float menu_button_Y;

    AnimationSet animation;
    Button menu_button;
    Button save_button;
    Button load_button;
    Button savegame;
    Button loadgame;
    FragmentManager fragmentManager;
    ConstraintLayout mainLayout;
    ConstraintLayout menulayout;
    ConstraintLayout save_layout;
    ConstraintLayout load_layout;
    ScrollView scrollView;
    Socket socket;
    PrintWriter send;
    BufferedReader get;

    private EggCounter eggCounter = new EggCounter();
    Random rand = new Random();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        username = "";
        password = "";

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    socket = new Socket("192.168.2.100", 2557);
                    System.out.println("Connected to Server");
                    System.out.println(socket.toString());
                    send = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    get = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    class run implements Runnable{

                        @Override
                        public void run() {
                            while (true){
                                try{
                                    if((from_server = get.readLine()) != null){
                                        Log.e("lista",from_server);
                                        if(from_server.compareTo("Err USER NOT IN DATABASE") == 0){
                                            username = "";
                                            password = "";
                                            System.out.println("Couldn't find user");
                                        }
                                        else if((from_server.substring(0,from_server.indexOf('.'))).compareTo(password) == 0) {
                                            aux = "";
                                            int nr=0;
                                            for (int i = from_server.indexOf('.') + 1; i < from_server.length(); i++) {
                                                if(from_server.charAt(i) != ' '){
                                                    char c = from_server.charAt(i);
                                                    aux += c;
                                                }
                                                else if(from_server.charAt(i) == ' '){
                                                    if(nr==0){
                                                        point = Integer.parseInt(aux);
                                                    }
                                                    else if(nr==1){
                                                        eps = Integer.parseInt(aux);
                                                    }
                                                    else if(nr==2){
                                                        click = Integer.parseInt(aux);
                                                    }
                                                    else if(nr==3){
                                                        lvl_cursor = Integer.parseInt(aux);
                                                    }
                                                    else if(nr==4){
                                                        cost_cursor = Integer.parseInt(aux);
                                                    }
                                                    else if(nr==5){
                                                        lvl_p1 = Integer.parseInt(aux);
                                                    }
                                                    else if(nr==6){
                                                        cost_p1 = Integer.parseInt(aux);
                                                    }
                                                    else if(nr==7){
                                                        lvl_p2 = Integer.parseInt(aux);
                                                    }
                                                    else if(nr==8){
                                                        cost_p2 = Integer.parseInt(aux);
                                                    }
                                                    else if(nr==9){
                                                        lvl_p3 = Integer.parseInt(aux);
                                                    }
                                                    else if(nr==10){
                                                        cost_p3 = Integer.parseInt(aux);
                                                    }
                                                    nr++;
                                                    aux = "";
                                                }
                                            }
                                        }
                                        else{
                                            username = "";
                                            password = "";
                                            System.out.println("Wrong password");
                                        }
                                    }
                                }
                                catch (Exception e){
                                    Log.e("Error:",e.toString());
                                }
                            }
                        }
                    }

                    Thread thread = new Thread(new run());
                    thread.start();

                }catch (Exception ex){
                    Log.e("connection", ex.toString());
                }
                return null;
            }
        }.execute();

        not_enough_eggs = findViewById(R.id.no_eggs);
        save_button = findViewById(R.id.save_button);
        load_button = findViewById(R.id.load_button);
        save_layout = findViewById(R.id.save_layout);
        load_layout = findViewById(R.id.load_layout);
        savegame = findViewById(R.id.savegame);
        loadgame = findViewById(R.id.loadgame);
        saveusername = findViewById(R.id.saveusername);
        savepassword = findViewById(R.id.savepassword);
        loadusername = findViewById(R.id.loadusername);
        loadpassword = findViewById(R.id.loadpassword);
        points = findViewById(R.id.points);
        pointsanim = findViewById(R.id.pointsanim);
        pointsanim.setVisibility(View.INVISIBLE);
        menu_button = findViewById(R.id.menu);
        menulayout = findViewById(R.id.menulayout);
        scrollView = findViewById(R.id.scrollView);
        text_cursor_cost = findViewById(R.id.text_cursor_cost);
        cost_p1_text = findViewById(R.id.text_passive1_cost);
        text_chicken_up = findViewById(R.id.text_chicken_up);
        cost_p2_text = findViewById(R.id.text_passive2_cost);
        text_carton_up = findViewById(R.id.text_carton_up);
        cost_p3_text = findViewById(R.id.text_passive3_cost);
        text_coop_up = findViewById(R.id.text_coop_up);
        img_chicken = findViewById(R.id.imgChicken);
        img_carton = findViewById(R.id.imgCarton);
        img_coop = findViewById(R.id.imgCoop);
        income = findViewById(R.id.income);
        perclick = findViewById(R.id.perclick);
        mainLayout = findViewById(R.id.main_layout);
        scrollView.setVisibility(View.INVISIBLE);
        not_enough_eggs.setVisibility(View.INVISIBLE);
        fragmentManager = getSupportFragmentManager();
        final FragmentMenu fragmentMenu = new FragmentMenu();

        menu_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                menu_button_Y = mainLayout.getHeight()-menu_button.getHeight();
                if(!pause) {
                    menu_button.setY(scrollView.getY()-(menu_button.getHeight()));
                    scrollView.setVisibility(View.VISIBLE);
                    fragmentManager.beginTransaction().replace(menulayout.getId(), fragmentMenu, fragmentMenu.getTag()).commit();

                    img_chicken.setVisibility(View.INVISIBLE);
                    img_carton.setVisibility(View.INVISIBLE);
                    img_coop.setVisibility(View.INVISIBLE);

                    pause = true;
                }
                else{

                    menu_button.setY(menu_button_Y);
                    scrollView.setVisibility(View.INVISIBLE);
                    fragmentManager.beginTransaction().remove(fragmentMenu).commit();

                    if(lvl_p1 > 0)
                        img_chicken.setVisibility(View.VISIBLE);
                    if(lvl_p2 > 0)
                        img_carton.setVisibility(View.VISIBLE);
                    if(lvl_p3 > 0)
                        img_coop.setVisibility(View.VISIBLE);

                    pause = false;
                }

            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username == "")
                    save_layout.setVisibility(View.VISIBLE);
                else{
                    try{
                        class run implements Runnable{
                            @Override
                            public void run() {
                                send.println("Save Game*"+ username + "," + password + "." + point + " " + eps + " " + click + " " + lvl_cursor + " " + cost_cursor + " " + lvl_p1 + " " + cost_p1 + " " + lvl_p2 + " " + cost_p2 + " " + lvl_p3 + " " + cost_p3);
                                System.out.println("Saved");
                            }
                        }
                        Thread thread = new Thread(new run());
                        thread.start();
                    }
                    catch(Exception e){
                        Log.e("error",e.toString());
                        //Log.e("socket",socket.toString());
                    }
                }
            }
        });
        load_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_layout.setVisibility(View.VISIBLE);
            }
        });
        savegame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = saveusername.getText().toString();
                password = savepassword.getText().toString();

                System.out.println("username: " + username);

                if (!username.matches("") && !password.matches("")){
                    try {
                        class run implements Runnable {
                            @Override
                            public void run() {
                                send.println("Save Game*" + username + "," + password + "." + point + " " + eps + " " + click + " " + lvl_cursor + " " + cost_cursor + " " + lvl_p1 + " " + cost_p1 + " " + lvl_p2 + " " + cost_p2 + " " + lvl_p3 + " " + cost_p3);
                                System.out.println("Saved");
                            }
                        }
                        Thread thread = new Thread(new run());
                        thread.start();
                    } catch (Exception e) {
                        Log.e("error", e.toString());
                        //Log.e("socket",socket.toString());
                    }
                    save_layout.setVisibility(View.INVISIBLE);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loadgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = loadusername.getText().toString();
                password = loadpassword.getText().toString();

                load_layout.setVisibility(View.INVISIBLE);
                try{
                    class run implements Runnable{
                        @Override
                        public void run() {
                            send.println("Load Game*"+ username + ",");
                            System.out.println("Sent Load request");
                        }
                    }
                    Thread thread = new Thread(new run());
                    thread.start();
                }
                catch(Exception e){
                    Log.e("error",e.toString());
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.imgclick)
        {
            Animation a = new AnimationUtils().loadAnimation(this,R.anim.egg);
            eggClick();
            v.startAnimation(a);
        }
        else if (v.getId()==R.id.up_cursor)
        {
            Animation a = new AnimationUtils().loadAnimation(this,R.anim.upgrade);
            int wallet = point-cost_cursor;
            if(wallet>=0) {
                point=wallet;
                points.setText("Eggs: "+Integer.toString(point));
                lvl_cursor++;
                click++;
                cost_cursor += (rand.nextInt(10) + (lvl_cursor*2));
                text_cursor_cost.setText("(Cost " + cost_cursor + ")");
            }
            else
            {
                System.out.println("NOT ENOUGH EGGS");
                NotEnoughEggs();
            }
            v.startAnimation(a);
        }
        else if(v.getId()==R.id.up_chicken)
        {
            Animation a = new AnimationUtils().loadAnimation(this,R.anim.upgrade);
            int wallet = point-cost_p1;
            if(wallet>=0) {
                point=wallet;
                points.setText("Eggs: "+Integer.toString(point));
                lvl_p1++;
                eps += lvl_p1;
                cost_p1 *= (rand.nextFloat() + 1.1);
                cost_p1_text.setText("(Cost " + cost_p1 + ")");
                text_chicken_up.setText("Chicken +" + Integer.toString(lvl_p1+1));
            }
            else
            {
                System.out.println("NOT ENOUGH EGGS");
                NotEnoughEggs();
            }
            v.startAnimation(a);
        }
        else if(v.getId()==R.id.up_carton)
        {
            Animation a = new AnimationUtils().loadAnimation(this,R.anim.upgrade);
            int wallet = point-cost_p2;
            if(wallet>=0) {
                point=wallet;
                points.setText("Eggs: "+Integer.toString(point));
                lvl_p2++;
                eps += (lvl_p2*2);
                cost_p2 *= (rand.nextFloat() + 1.1);
                cost_p2_text.setText("(Cost " + cost_p2 + ")");
                text_carton_up.setText("Carton +" + Integer.toString((lvl_p2*2)+2));
            }
            else
            {
                System.out.println("NOT ENOUGH EGGS");
                NotEnoughEggs();
            }
            v.startAnimation(a);
        }
        else if(v.getId()==R.id.up_coop)
        {
            Animation a = new AnimationUtils().loadAnimation(this,R.anim.upgrade);
            int wallet = point-cost_p3;
            if(wallet>=0) {
                point=wallet;
                points.setText("Eggs: "+Integer.toString(point));
                lvl_p3++;
                eps += (lvl_p3*4);
                cost_p3 *= (rand.nextFloat() + 1.1);
                cost_p3_text.setText("(Cost " + cost_p3 + ")");
                text_coop_up.setText("Carton +" + Integer.toString((lvl_p3*4)+4));
            }
            else
            {
                System.out.println("NOT ENOUGH EGGS");
                NotEnoughEggs();
            }
            v.startAnimation(a);
        }
    }

    private void NotEnoughEggs(){
        defaultButtonAnimation();
        not_enough_eggs.setVisibility(View.VISIBLE);
        not_enough_eggs.setAnimation(animation);
        if(animation.hasEnded())
            not_enough_eggs.clearAnimation();
        not_enough_eggs.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void eggClick() {
        point+=click;
        points.setText("Eggs: "+Integer.toString(point));
        pointsanim.setX(rand.nextInt(300) + 500);
        pointsanim.setY(rand.nextInt(150) + 500);
        defaultButtonAnimation();
        System.out.println("Egg Clicked");
        pointsanim.setText("+"+click);
        pointsanim.setVisibility(View.VISIBLE);
        pointsanim.setAnimation(animation);
        if(animation.hasEnded())
            pointsanim.clearAnimation();
        pointsanim.setVisibility(View.INVISIBLE);
    }

    private void defaultButtonAnimation(){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(300);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(300);
        fadeOut.setDuration(300);

        animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
    }

    @SuppressLint("SetTextI18n")
    private void update() {
        point += eps;
        points.setText("Eggs: "+Integer.toString(point));
        perclick.setText("Eggs/click: "+ Integer.toString(click));
        income.setText("Eggs/sec: "+ Integer.toString(eps));

        if(lvl_p1 > 0 && !pause)
            img_chicken.setVisibility(View.VISIBLE);
        if(lvl_p2 > 0 && !pause)
            img_carton.setVisibility(View.VISIBLE);
        if(lvl_p3 > 0 && !pause)
            img_coop.setVisibility(View.VISIBLE);
    }

    public class EggCounter{
        private Timer timer;

        EggCounter(){
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            update();
                        }
                    });
                }
            },1000,1000);
        }
    }

}
