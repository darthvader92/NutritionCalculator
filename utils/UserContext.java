package hr.iii.nutritioncalculator.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import hr.iii.nutritioncalculator.Food;

/**
 * Created by smiletic on 13.4.2017..
 * Context class for specific user's data.
 */

@Singleton
public class UserContext {

    private List<Food> foodList = new ArrayList<>();
    private List<Food> selectedFoodList = new ArrayList<>();
    private Food selectedFood;

    public UserContext() {}

    public boolean addFood( final Food food ) {
        final int preFoodListSize = getFoodListSize();
        foodList.add( food );
        return foodList.size() > preFoodListSize;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    private boolean removeFood( Food food ) {
        final int preFoodListSize = getFoodListSize();
        foodList.remove( food );
        return foodList.size() < preFoodListSize;
    }

    private Food getFood( int position ) {
        return foodList.get( position );
    }

    public boolean setSelectedFoodFromDialog( int dialogFoodPosition, boolean singleChoice ) {
        if ( singleChoice ) selectedFoodList.clear();
        selectedFood = getFood( dialogFoodPosition );
        selectedFoodList.add( selectedFood );
        return true;
    }

    public Food getSelectedFoodFromDialog() {
        int selectedFoodPosition = selectedFoodList.indexOf( selectedFood );
        return selectedFoodList.get( selectedFoodPosition );
    }

    public void removeSelectedFoods() {
        for ( Food food : selectedFoodList ) removeFood( food );
        selectedFoodList.clear();
    }

    public <T> AlertDialog.Builder singleChoiceAlertDialog( final ArrayAdapter<T> arrayAdapter,
                                                            final Context context,
                                                            final String title ) {
        final int checkedItem = -1;
        final AlertDialog.Builder builder = new AlertDialog.Builder( context );
        builder.setTitle( title );
        builder.setSingleChoiceItems( arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int item ) {
                setSelectedFoodFromDialog( item, true );
            }
        });

        return builder;
    }

    public <T> AlertDialog.Builder multiChoiceAlertDialog( final ArrayAdapter<T> arrayAdapter,
                                                           final Context context,
                                                           final String title ) {

        AlertDialog.Builder builder = new AlertDialog.Builder( context );
        builder.setTitle( title );
        builder.setAdapter( arrayAdapter, null );

        return builder;
    }

    private int getFoodListSize() {
        return getFoodList().size();
    }
}
