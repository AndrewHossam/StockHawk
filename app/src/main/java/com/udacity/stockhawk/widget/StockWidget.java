package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link StockWidgetConfigureActivity StockWidgetConfigureActivity}
 */
public class StockWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = StockWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        Set<String> stockPref = PrefUtils.getStocks(context);
        List<String> strings = new ArrayList<>();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget);

        for (String symbol : stockPref) {
            strings.add(symbol);
        }
        if (!widgetText.equals("EXAMPLE")) {
            if (strings.indexOf(widgetText) != -1) {
                int id = strings.indexOf(widgetText);
                Cursor cursor = context.getContentResolver().query(
                        Contract.Quote.URI,
                        Contract.Quote.QUOTE_COLUMNS,
                        null, null, null);
                cursor.moveToPosition(id);
                String price = cursor.getString(Contract.Quote.POSITION_PRICE);
                String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
                String change = cursor.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
                double changeInt = Double.parseDouble(change);
                if (changeInt < 0.0) {

                    views.setInt(R.id.stock_change_widget, "setBackgroundColor", Color.RED);
                } else if (changeInt > 0.0) {
                    views.setInt(R.id.stock_change_widget, "setBackgroundColor", Color.GREEN);
                }

                views.setTextViewText(R.id.stock_change_widget, change + " %");
                views.setTextViewText(R.id.stock_price_widget, price + " $");
                views.setTextViewText(R.id.stock_symble_widget, symbol);

            }
        } else {
            views.setTextViewText(R.id.stock_symble_widget, widgetText);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            StockWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

