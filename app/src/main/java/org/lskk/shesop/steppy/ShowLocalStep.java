package org.lskk.shesop.steppy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import org.lskk.teamshesop.steppy.R;

public class ShowLocalStep extends Activity {

 TableLayout table_layout;
 EditText firstname_et, lastname_et;
 Button addmem_btn;

 SQLController sqlcon;

 ProgressDialog PD;

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_show_local);

  sqlcon = new SQLController(this);

  table_layout = (TableLayout) findViewById(R.id.tableLayout1);

  BuildTable();


 }

 private void BuildTable() {

  sqlcon.open();
  Cursor c = sqlcon.readEntry();

  int rows = c.getCount();
  int cols = c.getColumnCount();

  c.moveToFirst();

  // outer for loop
  for (int i = 0; i < rows; i++) {

   TableRow row = new TableRow(this);
   row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
     LayoutParams.WRAP_CONTENT));

   // inner for loop
   for (int j = 0; j < cols; j++) {

    TextView tv = new TextView(this);
    tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
      LayoutParams.WRAP_CONTENT));
 //   tv.setBackgroundResource(R.drawable.cell_shape);
    tv.setGravity(Gravity.CENTER);
    tv.setTextSize(18);
    tv.setPadding(0, 5, 0, 5);

    tv.setText(c.getString(j));

    row.addView(tv);

   }

   c.moveToNext();

   table_layout.addView(row);

  }
/*  int tLastStep = 0;
  String tgl = "";
  Cursor tCursor = sqlcon.getLast();
  if (tCursor != null) {
		tLastStep = tCursor.getInt(0);
		tgl = tCursor.getString(4);
		tCursor.close();
	}
  
  Toast.makeText(ShowLocalStep.this,
			"Last step : "+Integer.toString(tLastStep)+" - "+tgl, Toast.LENGTH_LONG)
			.show();
			*/
  sqlcon.close();
 
 }
}