package widget.com.com;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

public class ConfigActivity extends Activity {

  int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
  Intent resultValue;
  
  public final static String LOG_TAG = "myLogs";

  public final static String WIDGET_PREF = "widget_pref";
  public final static String WIDGET_TEXT = "widget_text_";
  public final static String WIDGET_CMD = "widget_cmd_";
  public final static String WIDGET_IP = "widget_ip_";
  public final static String WIDGET_PORT = "widget_port_";
  public final static String WIDGET_COLOR = "widget_color_";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Log.d(LOG_TAG, "onCreate config");

    // ????????? ID ???????????????? ???????
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
          AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    // ? ????????? ??? ????????????
    if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish();
    }
    
    // ????????? intent ??????
    resultValue = new Intent();
    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
    
    // ????????????? ?????
    setResult(RESULT_CANCELED, resultValue);
    
    setContentView(R.layout.config);
  }
  
  
  public void onClick(View v) {
	  int selRBColor = ((RadioGroup) findViewById(R.id.rgColor))
	        .getCheckedRadioButtonId();
	    int color = Color.RED;
	    switch (selRBColor) {
	    case R.id.radioRed:
	      color = Color.parseColor("#66ff0000");
	      break;
	    case R.id.radioGreen:
	      color = Color.parseColor("#6600ff00");
	      break;
	    case R.id.radioBlue:
	      color = Color.parseColor("#660000ff");
	      break;
	   }
    EditText nameText = (EditText) findViewById(R.id.Name_Text);
    EditText cmdText = (EditText) findViewById(R.id.Cmd);
    EditText ip = (EditText) findViewById(R.id.address);
    EditText port = (EditText) findViewById(R.id.port);
    // ?????????? ???????? ? ?????? ? Preferences
    SharedPreferences sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
    Editor editor = sp.edit();
    editor.putString(WIDGET_TEXT + widgetID, nameText.getText()
        .toString());
    editor.putString(WIDGET_CMD + widgetID, cmdText.getText()
            .toString());
    editor.putString(WIDGET_IP + widgetID, ip.getText()
            .toString());
    editor.putString(WIDGET_PORT + widgetID, port.getText()
                .toString());
    editor.putInt(WIDGET_COLOR + widgetID, color);
    editor.commit();
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    MyWidgetProvider.updateWidget(this, appWidgetManager, sp, widgetID);

    // ????????????? ????? 
    setResult(RESULT_OK, resultValue);
    
    //Log.d(LOG_TAG, "finish config " + widgetID);
    finish();
  }
}
