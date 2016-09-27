package com.ayushgarg.tictactoe2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int activePlayer = 1;
    int status[]=new int[9];
    int winner[][]={{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    boolean game = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void fadeIn(View view){
        if(game) {
            ImageView counter = (ImageView) view;
            Log.i("info", "Tag is " + counter.getTag().toString());
            //System.out.println("Tag is "+counter.getTag().toString());
            int x = Integer.parseInt(counter.getTag().toString());
            //System.out.println("Active Player is "+activePlayer);
            //System.out.println("Tag is "+i);
            if (status[x] == 0) {
                status[x] = activePlayer;
                if (activePlayer == 1) {
                    Log.i("info", "Active Player is " + activePlayer);
                    counter.setImageResource(R.drawable.convertbl);
                    counter.setTranslationY(-1000f);
                    counter.animate().translationYBy(1000f).rotation(360).setDuration(200);
                    activePlayer = 2;
                } else if (activePlayer == 2) {
                    Log.i("info", "Active Player is " + activePlayer);
                    counter.setImageResource(R.drawable.convertgr);
                    counter.setTranslationY(-1000f);
                    counter.animate().translationYBy(1000f).rotation(360).setDuration(200);
                    activePlayer = 1;
                }
                int counter1 = 0, counter2 = 0;
                String winnerFin = "";
                boolean disp = false;
                LinearLayout gameFinish = (LinearLayout) findViewById(R.id.playAgainLayout);
                int count = 0;
                for(int i=0;i<9;i++){
                    if(status[i]!=0) count++;
                }
                /*if (count >= 9){
                    disp = true;
                    game = false;
                    winnerFin = 6;
                    Log.i("Count",""+count);
                }*/
                {
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (status[winner[i][j]] == 1)
                                counter1++;
                            else if (status[winner[i][j]] == 2)
                                counter2++;
                        }
                        //Log.i("Counter 1", "" + counter1);
                        //Log.i("Counter 2", "" + counter2);
                        if (counter1 == 3) {
                            disp = true;
                            winnerFin = "Blue";
                            game = false;
                            break;
                        }
                        //Toast.makeText(getApplicationContext(), "Player 1 won!", Toast.LENGTH_LONG).show();
                        else if (counter2 == 3) {
                            disp = true;
                            winnerFin = "Green";
                            game = false;
                            break;
                        }
                        if(count>=9){
                            disp = true;
                            game = false;
                            winnerFin = "";
                        }
                        //Toast.makeText(getApplicationContext(), "Player 2 won!", Toast.LENGTH_LONG).show();
                        counter1 = counter2 = 0;

                    }
                    if (disp && !game) {
                        TextView winnerMsg = (TextView) findViewById(R.id.winnerText);
                        if (winnerFin.equals("")) winnerMsg.setText("It's a DRAW!");
                        else winnerMsg.setText(winnerFin + " has won!");
                        gameFinish.setVisibility(View.VISIBLE);
                    }
                }
            } else
                Toast.makeText(getApplicationContext(), "Already taken by Player: " + status[x], Toast.LENGTH_SHORT).show();
        }
    }
    public void playAgain(View view){
        LinearLayout a =(LinearLayout)findViewById(R.id.playAgainLayout);
        a.setVisibility(View.INVISIBLE);
        activePlayer = 1;
        for(int i=0;i<9;i++) status[i] = 0;
        game = true;
        GridLayout grid = (GridLayout)findViewById(R.id.gridLayout);
        for(int i=0;i<grid.getChildCount();i++){
            ((ImageView)grid.getChildAt(i)).setImageResource(0);
        }
    }

}
