package com.phantomisland.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IslandService extends Service {
    private WindowManager wm;
    private View island;
    private boolean expanded = false;
    private WindowManager.LayoutParams params;
    private int cyan = Color.rgb(85,244,255), purple = Color.rgb(177,91,255);

    @Override public void onCreate() {
        super.onCreate();
        startForeground(81, buildNotification());
        showIsland();
    }

    private Notification buildNotification() {
        String channel = "phantom_island_running";
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(channel, "Phantom Island", NotificationManager.IMPORTANCE_LOW);
            c.setDescription("Keeps the floating island running while enabled");
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        }
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(this, channel) : new Notification.Builder(this);
        return b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("Phantom Island active").setContentText("Tap the floating island to open its controls").setOngoing(true).build();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius * getResources().getDisplayMetrics().density); d.setStroke((int)(1.2f*getResources().getDisplayMetrics().density), cyan); return d;
    }
    private TextView text(String s, int size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setGravity(Gravity.CENTER); t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); t.setPadding(12,8,12,8); return t;
    }
    private void showIsland() {
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(10,7,10,10); box.setBackground(shape(Color.rgb(6,8,16), 25));
        LinearLayout top = new LinearLayout(this); top.setGravity(Gravity.CENTER); top.setOrientation(LinearLayout.HORIZONTAL);
        TextView orb = text("◈", 19, purple); TextView title = text("PHANTOM", 13, Color.WHITE); TextView dot = text("●", 10, cyan);
        top.addView(orb); top.addView(title); top.addView(dot); box.addView(top);
        LinearLayout details = new LinearLayout(this); details.setOrientation(LinearLayout.VERTICAL); details.setVisibility(View.GONE);
        TextView status = text("CYBER CORE  /  ONLINE", 11, cyan); details.addView(status);
        TextView music = text("♫   MUSIC PANEL\nDemo controls • not connected to player", 12, Color.WHITE); music.setBackground(shape(Color.rgb(21,25,43), 16));
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(-1, -2); mp.setMargins(0,8,0,6); details.addView(music, mp);
        LinearLayout actions = new LinearLayout(this); actions.setGravity(Gravity.CENTER);
        TextView pulse = text("◉  PULSE", 11, cyan); TextView close = text("✕  CLOSE", 11, Color.WHITE);
        actions.addView(pulse, new LinearLayout.LayoutParams(0,-2,1)); actions.addView(close, new LinearLayout.LayoutParams(0,-2,1)); details.addView(actions);
        box.addView(details);
        box.setOnClickListener(v -> { expanded = !expanded; details.setVisibility(expanded ? View.VISIBLE : View.GONE); params.width = dp(expanded ? 285 : 190); params.height = -2; try { wm.updateViewLayout(box, params); } catch(Exception ignored) {} });
        pulse.setOnClickListener(v -> { pulse.setText("✦  PULSE ON"); pulse.setTextColor(purple); });
        close.setOnClickListener(v -> stopSelf());
        params = new WindowManager.LayoutParams(dp(190), -2, Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL; params.y = dp(24);
        // Allow dragging without making the island hard to tap.
        final int[] start = new int[2]; final float[] touch = new float[2];
        box.setOnTouchListener((v,e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) { start[0]=params.x; start[1]=params.y; touch[0]=e.getRawX(); touch[1]=e.getRawY(); return false; }
            if (e.getAction() == MotionEvent.ACTION_MOVE && Math.abs(e.getRawY()-touch[1]) > dp(8)) { params.y = Math.max(0, start[1] + (int)(e.getRawY()-touch[1])); try { wm.updateViewLayout(box,params); } catch(Exception ignored) {} return true; }
            return false;
        });
        island = box; wm.addView(island, params);
    }
    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override public void onDestroy() { if (wm != null && island != null) try { wm.removeView(island); } catch(Exception ignored) {} super.onDestroy(); }
    @Override public IBinder onBind(Intent intent) { return null; }
}
