package widget.com.com;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
//import android.util.Log;
import android.widget.RemoteViews;


public class MyWidgetProvider extends AppWidgetProvider {
	
	
	//private static final String LOG = "widget.com.com";
	final static String ACTION_SEND = "widget.com.com.send_cmd";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		//Log.w(LOG, "onUpdate method called");
	    
	    SharedPreferences sp = context.getSharedPreferences(
	        ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
	    for (int id : appWidgetIds) {
	      updateWidget(context, appWidgetManager, sp, id);
	    }
	    
	    
	    
//		// Get all ids
//		ComponentName thisWidget = new ComponentName(context,
//				MyWidgetProvider.class);
//		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
//
//		// Build the intent to call the service
//		Intent intent = new Intent(context.getApplicationContext(),
//				UpdateWidgetService.class);
//		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
//
//		// Update the widgets via the service
//		context.startService(intent);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		//Log.d(LOG, "onDeleted " + Arrays.toString(appWidgetIds));

		// Удаляем Preferences
		Editor editor = context.getSharedPreferences(
				ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit();
		for (int widgetID : appWidgetIds) {
			editor.remove(ConfigActivity.WIDGET_TEXT + widgetID);
			editor.remove(ConfigActivity.WIDGET_CMD + widgetID);
		}
		editor.commit();
	}
	
	static void updateWidget(Context context, AppWidgetManager appWidgetManager,
	      SharedPreferences sp, int widgetID) {
	    //Log.d(LOG, "updateWidget " + widgetID);

	    // Читаем параметры Preferences
	    String widgetText = sp.getString(ConfigActivity.WIDGET_TEXT + widgetID, null);
	    if (widgetText == null) return;
	    
		// Настраиваем внешний вид виджета
		RemoteViews widgetView = new RemoteViews(context.getPackageName(),
		    R.layout.widget_layout);
	    int widgetColor = sp.getInt(ConfigActivity.WIDGET_COLOR + widgetID, 0);
	    widgetView.setInt(R.id.update, "setBackgroundColor", widgetColor);
		widgetView.setTextViewText(R.id.update, widgetText);
		
	    Intent countIntent = new Intent(context, MyWidgetProvider.class);
	    countIntent.setAction(ACTION_SEND);
	    countIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
	    PendingIntent pIntent = PendingIntent.getBroadcast(context, widgetID, countIntent, 0);
	    widgetView.setOnClickPendingIntent(R.id.update, pIntent);

		
		// Обновляем виджет
		appWidgetManager.updateAppWidget(widgetID, widgetView);
	}
	
  public void onReceive(Context context, Intent intent) {
	    super.onReceive(context, intent);
	    // Проверяем, что это intent от нажатия на третью зону
	    if (intent.getAction().equalsIgnoreCase(ACTION_SEND)) {

	      // извлекаем ID экземпляра
	      int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	      Bundle extras = intent.getExtras();
	      if (extras != null) {
	        mAppWidgetId = extras.getInt(
	            AppWidgetManager.EXTRA_APPWIDGET_ID,
	            AppWidgetManager.INVALID_APPWIDGET_ID);

	      }
	      if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
	        // Читаем значение счетчика, увеличиваем на 1 и записываем
	        SharedPreferences sp = context.getSharedPreferences(
	            ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
		    String cmdText = sp.getString(ConfigActivity.WIDGET_CMD + mAppWidgetId, null);
		    if (cmdText == null) return;
		    String ip = sp.getString(ConfigActivity.WIDGET_IP + mAppWidgetId, null);
		    if (ip == null) return;
		    String port = sp.getString(ConfigActivity.WIDGET_PORT + mAppWidgetId, null);
		    if (port == null) return;
		    sendStr(cmdText, ip, Integer.parseInt(port));
		    //Log.d(LOG, "send " + cmdText);
		    //Log.d(LOG, "ip " + ip);
		    //Log.d(LOG, "port " + port);
		    // Обновляем виджет
	        updateWidget(context, AppWidgetManager.getInstance(context), sp,
	            mAppWidgetId);
	      }
	    }
  }
	
	public void sendStr(String str, String IPaddr, int port)
	{
		final String str2=str;
		final String ip=IPaddr;
		final int p=port;
		//System.out.println(str2);
		new Thread(new Runnable() {
          public void run() { 
          	String tx_msg = str2 + '\n';
      	    DatagramSocket ds = null;
      	    try {
      	        ds = new DatagramSocket();
      	        InetAddress serverAddr = InetAddress.getByName(ip);
      	        //InetAddress serverAddr = IPAddress;
      	        //System.out.println("MESSAGE RECEIVED  "+serverAddr);
      	        DatagramPacket dp;
      	        dp = new DatagramPacket(tx_msg.getBytes(), tx_msg.length(), serverAddr, p);
      	        ds.send(dp);
      	    } catch (SocketException e) {
      	        e.printStackTrace();
      	    }catch (UnknownHostException e) {
      	        e.printStackTrace();
      	    } catch (IOException e) {
      	        e.printStackTrace();
      	    } catch (Exception e) {
      	        e.printStackTrace();
      	    } finally {
      	        if (ds != null) {
      	            ds.close();
      	        }
      	    }

          }
      }).start(); 
	}
  
  
  
}

