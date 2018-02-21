package hr.iii.nutritioncalculator;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;

/**
 * Created by smiletic on 7.4.2017..
 * Setting up roboguice behaviour
 */

public class RoboCustomActivity extends RoboActivity {

    static {
        RoboGuice.setUseAnnotationDatabases( false );
    }
}
