package hr.iii.nutritioncalculator;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

import hr.iii.nutritioncalculator.utils.AppUtils;
import hr.iii.nutritioncalculator.utils.UserContext;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView( R.layout.activity_main )
public class MainActivity extends RoboCustomActivity {

    @InjectView( R.id.choose_item_btn )
    private Button chooseFood;

    @InjectView( R.id.add_item_btn )
    private Button addFood;

    @InjectView( R.id.delete_item_btn )
    private Button deleteFood;

    @InjectView( R.id.item_name )
    private TextView foodName;

    @InjectView ( R.id.food_label )
    private TextView foodLabel;

    @InjectView( R.id.total_carb_value )
    private TextView totalCarbValue;

    @InjectView( R.id.total_protein_value )
    private TextView totalProteinValue;

    @InjectView( R.id.total_fat_value )
    private TextView totalFatValue;

    @InjectView( R.id.total_calories_value )
    private TextView totalCaloriesValue;

    @InjectView( R.id.amount )
    private EditText amount;

    @InjectView( R.id.calculate )
    private Button calculate;

    @InjectView( R.id.reset )
    private Button reset;

    private static final String DELIMETER = " ";
    private static final int DEFAULT_AMOUNT = 100;

    private static final String FOOD_LIST_EMPTY_MESSAGE = "Add some food first! There is no food on the list!";

    private UserContext userContext;
    private AppUtils appUtils;
    private Food selectedFood;
    private String defaultFoodLabelString;

    @Inject
    public void setUserContext( UserContext userContext ) {
        this.userContext = userContext;
    }

    @Inject
    public void setAppUtils( AppUtils appUtils ) {
        this.appUtils = appUtils;
    }

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        if ( init() ) Log.i( "INFO", "App successfully initialized!" );
    }

    private boolean init() {
        return initLabels() && initButtons();
    }

    private boolean initLabels() {
        defaultFoodLabelString = getStringResource( R.string.food ) + "("
                + DEFAULT_AMOUNT + getStringResource( R.string.g ) + "):" + DELIMETER;
        foodLabel.setText( defaultFoodLabelString );

        return appUtils.checkNotNull( defaultFoodLabelString, foodLabel );
    }

    private boolean initButtons() {
        chooseFood.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if ( userContext.getFoodList().isEmpty() )
                    appUtils.toast( FOOD_LIST_EMPTY_MESSAGE, getApplicationContext() );
                else {
                    if ( showingListOfItems() ) Log.i( "INFO", "List of items is showing correctly!" );
                }
            }
        });

        addFood.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent( getApplicationContext(), AddFoodActivity.class );
                startActivity( intent );
            }
        });

        deleteFood.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( userContext.getFoodList().isEmpty() )
                        appUtils.toast( FOOD_LIST_EMPTY_MESSAGE, getApplicationContext() );
                else {
                    if ( deleteFoodFromList() ) Log.i( "INFO", "Food successfully deleted from list!" );
                }
            }
        });

        calculate.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if ( calculateMacros() ) Log.i( "INFO", "Macros successfully calculated and shown!" );
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( resetFoodDetails() ) Log.i( "INFO", "Food details successfully reseted!" );
            }
        });

        return appUtils.checkNotNull( chooseFood, addFood, deleteFood, calculate, reset );
    }

    private boolean showingListOfItems() {
        final ArrayAdapter<Food> arrayAdapter = new ArrayAdapter<>( this,
                android.R.layout.simple_list_item_single_choice, userContext.getFoodList() );
        final AlertDialog.Builder builder = userContext.singleChoiceAlertDialog( arrayAdapter, this,
                    defaultFoodLabelString );

        builder.setPositiveButton( getStringResource( R.string.ok ),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int item ) {
                            try {
                                selectedFood = userContext.getSelectedFoodFromDialog();
                                if ( showingFoodDetails( selectedFood ) )
                                    Log.i( "INFO", "Food details have been shown successfully!" );
                            } catch ( IndexOutOfBoundsException e ) {
                                dialog.dismiss();
                            }
                            dialog.dismiss();
                        }
                    });

        builder.setNegativeButton( getStringResource( R.string.cancel ),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return alertDialog.isShowing();
    }

    private boolean showingFoodDetails( final Food selectedFood ) {
        if ( !appUtils.checkNotNull( selectedFood ) ) return false;

        foodName.setText( selectedFood.getName() );
        totalCarbValue.setText( String.valueOf( selectedFood.getCarbs() ) + DELIMETER
                    + getStringResource(  R.string.g ) );
        totalProteinValue.setText( String.valueOf( selectedFood.getProteins() ) + DELIMETER
                    + getStringResource( R.string.g ) );
        totalFatValue.setText( String.valueOf( selectedFood.getFat() ) + DELIMETER
                    + getStringResource( R.string.g ) );
        totalCaloriesValue.setText( String.valueOf( selectedFood.getCalories() ) + DELIMETER
                    + getStringResource( R.string.cal ) );

        return !appUtils.areTextViewsEmpty( foodName, totalCarbValue, totalProteinValue, totalFatValue,
                totalCaloriesValue );
    }

    private boolean deleteFoodFromList() {
        final int height = -1;
        final ArrayAdapter<Food> arrayAdapter = new ArrayAdapter<>( this,
                android.R.layout.simple_list_item_multiple_choice, userContext.getFoodList() );

        final AlertDialog.Builder builder = userContext.multiChoiceAlertDialog( arrayAdapter, this,
                    getStringResource( R.string.delete_foods ) );
        builder.setPositiveButton( getStringResource( R.string.ok ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                userContext.removeSelectedFoods();
                resetFoodDetails();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton( getStringResource( R.string.cancel ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();

        final ListView listView = alertDialog.getListView();
        listView.setAdapter( new ArrayAdapter<>( getApplicationContext(),
                    android.R.layout.simple_list_item_multiple_choice, userContext.getFoodList() ) );
        listView.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                userContext.setSelectedFoodFromDialog( position, false );
            }
        });

        listView.setDivider( null );
        listView.setDividerHeight( height );

        alertDialog.show();

        return alertDialog.isShowing();
    }

    private boolean resetFoodDetails() {
        foodName.setText( DELIMETER );
        totalCarbValue.setText( DELIMETER );
        totalProteinValue.setText( DELIMETER );
        totalFatValue.setText( DELIMETER );
        totalCaloriesValue.setText( DELIMETER );
        amount.setText( DELIMETER );
        foodLabel.setText( defaultFoodLabelString );

        return appUtils.areTextViewsEmpty( foodName, totalCarbValue, totalProteinValue, totalFatValue,
                totalCaloriesValue, amount, foodLabel );
    }

    private String getStringResource( final int resId ) {
        return getResources().getString( resId );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean calculateMacros() {
        return doCalculation( appUtils.decimalFormat( preparedAmountValue() * DEFAULT_AMOUNT ) );
    }

    private Double preparedAmountValue() {
        final double defaultValue = 1.0;
        final String stringAmount = amount.getText().toString();
        final Double amountValue = Double.valueOf( stringAmount );

        return stringAmount.isEmpty() ? defaultValue : amountValue;
    }

    private boolean doCalculation( final String trimedQuantityValue ) {
        final String name = selectedFood.getName();
        final Double calculatedCarbValue = selectedFood.getCarbs() * preparedAmountValue();
        final Double calculatedProteinValue = selectedFood.getProteins() * preparedAmountValue();
        final Double calculatedFatValue = selectedFood.getFat() * preparedAmountValue();
        final Double calculatedCalorieValue = selectedFood.getCalories() * preparedAmountValue();

        return showFoodMacros( Food.createFromMacros( name, calculatedCarbValue, calculatedProteinValue,
                calculatedFatValue, calculatedCalorieValue ), trimedQuantityValue );
    }

    private boolean showFoodMacros( final Food foodDetails, final String trimedQuantityValue ) {
        if ( !appUtils.checkNotNull( foodDetails, trimedQuantityValue ) ) return false;

        final String calculationError = "Nothing selected or no value for amount to calculate!";
        final String alteredFoodLabelText = getStringResource( R.string.food ) + "("
                + trimedQuantityValue + getStringResource( R.string.g ) + "):"
                + DELIMETER;

        if ( showingFoodDetails( foodDetails ) ) {
            Log.i( "INFO", "Food details have been shown successfully!" );
            foodLabel.setText( alteredFoodLabelText );
            return true;
        } else {
            appUtils.toast( calculationError, getApplicationContext() );
            return false;
        }
    }
}
