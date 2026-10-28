package com.phantomisland.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int cyan = Color.rgb(85, 244, 255);
    private int bg = Color.rgb(7, 9, 18);
    private int panel = Color.rgb(19, 23, 39);

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(0);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(36), dp(24), dp(24));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(bg);

        TextView logo = new TextView(this);
        logo.setText("PHANTOM  /  ISLAND");
        logo.setTextColor(cyan); logo.setTextSize(23); logo.setGravity(Gravity.CENTER);
        logo.setLetterSpacing(.08f);
        root.addView(logo, new LinearLayout.LayoutParams(-1, dp(56)));

        TextView sub = new TextView(this);
        sub.setText("CYBER INTERFACE  •  BUILD 0.1");
        sub.setTextColor(Color.LTGRAY); sub.setTextSize(11); sub.setGravity(Gravity.CENTER);
        root.addView(sub, new LinearLayout.LayoutParams(-1, dp(30)));

        TextView preview = new TextView(this);
        preview.setText("◈   PHANTOM CORE   ◈\n\nFloating interface ready\nTap the island to expand it");
        preview.setTextColor(Color.WHITE); preview.setTextSize(16); preview.setGravity(Gravity.CENTER);
        preview.setBackgroundColor(panel);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(-1, dp(170)); pp.setMargins(0, dp(36), 0, dp(24));
        root.addView(preview, pp);

        Button start = new Button(this); start.setText("ENABLE PHANTOM ISLAND");
        root.addView(start, new LinearLayout.LayoutParams(-1, dp(58)));
        start.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(i);
            } else startIsland();
        });

        TextView privacy = new TextView(this);
        privacy.setText("PRIVACY FIRST\nThis prototype does not read accounts, contacts, messages, or bank data.\n\nPermission used: display the floating island over other apps.");
        privacy.setTextColor(Color.rgb(175, 185, 205)); privacy.setTextSize(13); privacy.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams pv = new LinearLayout.LayoutParams(-1, -2); pv.setMargins(0, dp(26), 0, 0);
        root.addView(privacy, pv);
        setContentView(root);
    }

    @Override protected void onResume() { super.onResume(); }
    private void startIsland() {
        Intent i = new Intent(this, IslandService.class);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
        finish();
    }
    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
}
