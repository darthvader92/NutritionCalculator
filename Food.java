package hr.iii.nutritioncalculator;

/**
 * Created by smiletic on 11.4.2017..
 * Domain class for food items.
 */

public class Food {
    private String name;
    private double carbs;
    private double proteins;
    private double fat;
    private double calories;

    private Food(String name, double carbs, double proteins, double fat, double calories ) {
        this.name = name;
        this.carbs = carbs;
        this.proteins = proteins;
        this.fat = fat;
        this.calories = calories;
    }

    static Food createFromMacros( final String name, final Double carbs, final Double proteins,
                                         final Double fat, final Double calories ) {
        return new Food( name, carbs, proteins, fat, calories );
    }

    public String getName() {
        return name;
    }

    double getCarbs() {
        return carbs;
    }

    double getProteins() {
        return proteins;
    }

    double getFat() {
        return fat;
    }

    double getCalories() {
        return calories;
    }

    @Override
    public String toString() {
        return getName();
    }
}
