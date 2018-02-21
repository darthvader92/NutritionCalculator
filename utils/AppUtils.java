package hr.iii.nutritioncalculator.utils;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smiletic on 20.2.2018..
 * Default implementation of utilities
 */

public class AppUtils {
    private static final String EMPTY = "";

    public boolean areTextViewsEmpty( final TextView... textViews ) {
        final List<TextView> textViewList = new ArrayList<>();

        for ( final TextView textView : textViews )
            if ( textView.getText().toString().matches( EMPTY ) )
                textViewList.add( textView );

        return textViewList.size() > 0;
    }

    public boolean checkNotNull( final Object... objects ) {
        final List<Object> nullObjectsList = new ArrayList<>();

        for ( int i = 0; i < objects.length-1; i++ )
            if ( null == objects[i] ) nullObjectsList.add( objects[i] );

        return nullObjectsList.isEmpty();
    }

    public boolean isNameEmpty( final String name ) {
        return name.trim().equals( EMPTY );
    }

    public boolean isAllDataEntered( final List<String> properties, final String nameFieldString, final Context context ) {
        if ( !isNameEmpty( nameFieldString ) ) {
            for ( final String property : properties ) {
                if ( property.trim().equals( EMPTY ) ) {
                    toast( "Some fields are empty!", context );
                    return false;
                }
            }
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String decimalFormat( final Double quantityValue ) {
        if ( checkNotNull( quantityValue ) ) {
            final DecimalFormat decimalFormat = new DecimalFormat( "#.00" );
            return decimalFormat.format( quantityValue );
        }

        return EMPTY;
    }

    public boolean toast( final String message, final Context context ) {
        Toast.makeText( context, message, Toast.LENGTH_SHORT ).show();
        return true;
    }
}
