package com.sdutacm.imooc_wuziqi;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private WuziqiPanel wuziqiPanel;
    RelativeLayout relative;
    private boolean change = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wuziqiPanel = (WuziqiPanel) findViewById(R.id.id_wuziqi);
        relative = (RelativeLayout) findViewById(R.id.relative);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_setting)
        {
            wuziqiPanel.start();
            return true;
        }
        if(id == R.id.tack_back){
            wuziqiPanel.takeBack();
            return true;
        }
        if(id == R.id.hint){
            wuziqiPanel.hint();
            return true;
        }
        if(id == R.id.ai){
            wuziqiPanel.ai();
            return true;
        }
        if(id==R.id.change){
            if(change){
                relative.setBackgroundColor(0x00000000);
                Drawable d = Drawable.createFromPath(String.valueOf(R.drawable.bg2));
                relative.setBackground(d);
            }else {
                Drawable d = Drawable.createFromPath(String.valueOf(R.drawable.bg));
                relative.setBackground(d);
            }
            change = !change;

        }
        return super.onOptionsItemSelected(item);
    }
}
