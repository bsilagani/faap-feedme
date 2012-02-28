package de.faap.feedme.provider;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.*;
import de.faap.feedme.*;
import de.faap.feedme.util.*;

public class RecipeProvider implements IRecipeProvider {
    private static final int NO_TIME = 0;
    private static final int LITTLE_TIME = 1;
    private static final int MUCH_TIME = 2;

    private Context mContext;

    public RecipeProvider(Context context) {
        mContext = context;
    }

    @Override
    public Recipe getRecipe(String name) {
        int portions;
        double[] quantities;
        int[] units;
        String[] ingredients;
        String preparation;
        int _id;

        SQLiteDatabase db =
                new RecipeDatabaseHelper(mContext).getReadableDatabase();

        // query to get _id, preparation and portions
        Cursor simpleRecipeData =
                db.rawQuery("SELECT _id, preparation, portions " + "FROM "
                        + RecipeDatabaseHelper.Tables.Recipes.toString() + " "
                        + "WHERE name = \"" + name + "\"", null);

        simpleRecipeData.moveToNext();
        _id = simpleRecipeData.getInt(0);
        preparation = simpleRecipeData.getString(1);
        portions = simpleRecipeData.getInt(2);
        simpleRecipeData.close();

        // query to get quantities, units and ingredients
        Cursor complexRecipeData =
                db.rawQuery("SELECT TEMP.amount, "
                                    + RecipeDatabaseHelper.Tables.Ingredients
                                            .toString()
                                    + ".name, "
                                    + RecipeDatabaseHelper.Tables.Ingredients
                                            .toString()
                                    + ".unit "
                                    + "FROM (SELECT amount, ingredient "
                                    + "FROM "
                                    + RecipeDatabaseHelper.Tables.One_takes
                                            .toString()
                                    + " WHERE recipe = \""
                                    + _id
                                    + "\") AS TEMP "
                                    + "INNER JOIN "
                                    + RecipeDatabaseHelper.Tables.Ingredients
                                            .toString()
                                    + " on TEMP.ingredient = "
                                    + RecipeDatabaseHelper.Tables.Ingredients
                                            .toString() + "._id ", null);

        int length = complexRecipeData.getCount();
        quantities = new double[length];
        units = new int[length];
        ingredients = new String[length];

        for (int i = 0; complexRecipeData.moveToNext(); i++) {
            quantities[i] = complexRecipeData.getDouble(0);
            ingredients[i] = complexRecipeData.getString(1);
            units[i] = complexRecipeData.getInt(2);
        }

        Ingredient.Unit[] internalUnits = new Ingredient.Unit[units.length];
        for (int i = 0; i < units.length; i++) {
            assert Ingredient.Unit.values().length >= units[i] : "The unit number "
                    + units[i] + " is no member of the Units enum.";
            internalUnits[i] = Ingredient.Unit.values()[units[i]];
        }

        Log.d("faap.feedme.RecPrv", "reached1337");
        complexRecipeData.close();
        db.close();

        return new Recipe(name, portions, quantities, internalUnits,
                ingredients, preparation);
    }

    @Override
    public String[] getNewWeek(int[] prefs) {
        // TODO query

        int[] time = convertPrefs(prefs);
        String[] week = new String[prefs.length];
        for (int i = 0; i < time.length; i++) {
            if (time[i] == LITTLE_TIME) {
                // TODO choose either fertiggericht oder schnelles gericht
            } else if (time[i] == MUCH_TIME) {
                // TODO choose großes gericht
                if (i != 6) {
                    if (time[i + 1] != NO_TIME) {
                        // Cook for two days
                        i++;
                    }
                }
            } else {
                week[i] = "";
            }
        }
        return week;
    }

    /**
     * Converts an array with radio-button ids into an with time indicators
     */
    private int[] convertPrefs(int[] prefs) {
        int[] convertedPrefs = new int[prefs.length];
        for (int i = 0; i < prefs.length; i++) {
            if (prefs[i] == R.id.radio00 || prefs[i] == R.id.radio10
                    || prefs[i] == R.id.radio20 || prefs[i] == R.id.radio30
                    || prefs[i] == R.id.radio40 || prefs[i] == R.id.radio50
                    || prefs[i] == R.id.radio60) {
                convertedPrefs[i] = NO_TIME;
            } else if (prefs[i] == R.id.radio01 || prefs[i] == R.id.radio11
                    || prefs[i] == R.id.radio21 || prefs[i] == R.id.radio31
                    || prefs[i] == R.id.radio41 || prefs[i] == R.id.radio51
                    || prefs[i] == R.id.radio61) {
                convertedPrefs[i] = LITTLE_TIME;
            } else {
                convertedPrefs[i] = MUCH_TIME;
            }
        }
        return convertedPrefs;
    }
}
