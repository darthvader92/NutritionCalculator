package hr.iii.nutritioncalculator;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.inject.Inject;


import java.util.ArrayList;
import java.util.List;

import hr.iii.nutritioncalculator.utils.AppUtils;
import hr.iii.nutritioncalculator.utils.UserContext;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView( R.layout.activity_add_food )
public class AddFoodActivity extends RoboCustomActivity implements TextWatcher {

    @InjectView( R.id.name_field )
    private EditText nameField;

    @InjectView( R.id.carb_field )
    private EditText carbField;

    @InjectView( R.id.protein_field )
    private EditText proteinField;

    @InjectView( R.id.fat_field )
    private EditText fatField;

    @InjectView( R.id.calorie_field )
    private EditText calorieField;

    @InjectView( R.id.add_new_food )
    private Button addNewFood;

    @InjectView( R.id.cancel )
    private Button cancel;

    private UserContext userContext;
    private AppUtils appUtils;

    @Inject
    public void setUserContext( UserContext userContext ) {
        this.userContext = userContext;
    }

    @Inject
    public void setAppUtils(AppUtils appUtils) {
        this.appUtils = appUtils;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        if ( init() ) Log.i( "INFO", "Initialization finished successfully!");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        final String validationMessage = "Name is required!";
        final String nameFieldString = nameField.getText().toString();

        if ( appUtils.isNameEmpty( nameFieldString ) ) appUtils.toast( validationMessage, this );

        if ( !doNameExists( nameFieldString ) ) {
            Log.i( "INFO", "Valid name is added!" );
            collectDataFromFields( nameFieldString );
        }
    }

    private boolean init() {
        nameField.addTextChangedListener( this );
        carbField.addTextChangedListener( this );
        proteinField.addTextChangedListener( this );
        fatField.addTextChangedListener( this );
        calorieField.addTextChangedListener( this );

        return initButtons() && appUtils.checkNotNull( nameField, carbField, proteinField, fatField,
                calorieField );
    }

    private boolean initButtons() {
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addNewFood.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( saveFood() ) Log.i( "INFO", "Food is saved!" );
                else appUtils.toast( "Enter all fields!", getApplicationContext() );

                final Intent intent = new Intent( getApplicationContext(), MainActivity.class );
                startActivity( intent );
                finish();
            }
        });

        return appUtils.checkNotNull( cancel, addNewFood );
    }

    private boolean doNameExists( final String nameFieldString ) {
        final String empty = "";
        final String message = "This food already exists! Choose another name!";

        for ( final Food food : userContext.getFoodList() ) {
            if ( nameFieldString.equalsIgnoreCase( food.getName() ) ) {
                nameField.setText( empty );
                appUtils.toast( message, this );
                return true;
            }
        }

        return false;
    }

    private boolean collectDataFromFields( final String nameFieldString ) {
        final String carbFieldString = nameField.getText().toString();
        final String proteinFieldString = nameField.getText().toString();
        final String fatFieldString = nameField.getText().toString();
        final String calorieFieldString = nameField.getText().toString();

        final List<String> properties = new ArrayList<>();
        properties.add( carbFieldString );
        properties.add( proteinFieldString );
        properties.add( fatFieldString );
        properties.add( calorieFieldString );

        return appUtils.isAllDataEntered( properties, nameFieldString, this );
    }

    private boolean saveFood() {
        final String name = nameField.getText().toString();
        final Double carbs = Double.valueOf( carbField.getText().toString() );
        final Double proteins = Double.valueOf( proteinField.getText().toString() );
        final Double fat = Double.valueOf( fatField.getText().toString() );
        final Double calories = Double.valueOf( calorieField.getText().toString() );

        if ( appUtils.checkNotNull( name, carbs, proteins, fat, calories ) ) {
            final Food newFood = Food.createFromMacros( name, carbs, proteins, fat, calories );
            userContext.addFood( newFood );

            return true;
        }

        return false;
    }
}
