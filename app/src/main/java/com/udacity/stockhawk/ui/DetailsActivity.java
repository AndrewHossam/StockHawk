package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initCursor();
        LineChart lineChart = (LineChart) findViewById(R.id.chart);
        int id = getIntent().getIntExtra("position", -1);
        cursor.moveToPosition(id);
        String history = cursor.getString(Contract.Quote.POSITION_HISTORY);

        String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
        ArrayList<Entry> entries = new ArrayList<>();
        try {
            String[] strings = history.split("\n");
            for (int i = 0; i < strings.length; i++) {
                entries.add(new Entry((i * 2), Float.parseFloat(strings[i].split(",")[1])));
            }
            LineDataSet lineDataSet = new LineDataSet(entries, symbol);
            LineData data = new LineData(lineDataSet);
            lineChart.setData(data);
        } catch (Exception e) {
            Toast.makeText(this, R.string.no_available_data, Toast.LENGTH_LONG).show();
        }

    }

    private void initCursor() {
        if (cursor != null) {
            cursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        /**This is done because the widget runs as a separate thread
         when compared to the current app and hence the app's data won't be accessible to it
         because I'm using a content provided **/
        cursor = this.getContentResolver().query(
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS,
                null, null, Contract.Quote.COLUMN_SYMBOL);
        Binder.restoreCallingIdentity(identityToken);
    }

}
