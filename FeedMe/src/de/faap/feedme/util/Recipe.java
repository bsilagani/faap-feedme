package de.faap.feedme.util;

import java.util.HashSet;

import de.faap.feedme.util.Ingredient.Unit;

public class Recipe {
    private String name;
    private int portions;
    // private double[] quantities;
    // private String[] units;
    // private String[] ingredients;
    private String preparation;

    private Effort effort;

    public enum Effort {
	small, large, instant
    }

    private HashSet<Ingredient> ingredients = new HashSet<Ingredient>();

    public Recipe(String name, int portions, double[] quantities, Unit[] units,
	    String[] ingredientNames, String preperation, Effort effort) {
	this.name = name;
	this.portions = portions;
	// this.quantities = quantities;
	// this.units = units;
	// this.ingredients = ingredients;
	assert quantities.length == units.length
		&& quantities.length == ingredientNames.length;
	for (int i = 0; i < quantities.length; i++) {
	    Ingredient ingredient = new Ingredient(quantities[i], units[i],
		    ingredientNames[i]);
	    this.ingredients.add(ingredient);
	}
	this.preparation = preperation;
    }

    public Recipe(String name) {
	this.name = name;
    }

    public void setEffort(Effort effort) {
	this.effort = effort;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setPortions(int portions) {
	this.portions = portions;
    }

    /**
     * Convenience method.
     * 
     * @see #addIngredient(Ingredient)
     * 
     * @param ingredient
     * @param unit
     */
    public boolean addIngredient(double quantity, Unit unit, String name) {
	Ingredient newIngredient = new Ingredient(quantity, unit, name);
	return addIngredient(newIngredient);
    }

    public void setPreperation(String preperation) {
	this.preparation = preperation;
    }

    public Effort getEffort() {
	return effort;
    }

    public String getName() {
	return name;
    }

    public int getPortions() {
	return portions;
    }

    // public double[] getQuantities() {
    // return quantities;
    // }
    //
    // public String[] getUnits() {
    // return units;
    // }
    //
    // public String[] getIngredients() {
    // return ingredients;
    // }

    public String getPreperation() {
	return preparation;
    }

    public void changePortions(int newPortions) {
	double quotient = (double) newPortions / (double) portions;
	portions = newPortions;
	for (Ingredient ingredient : ingredients) {
	    ingredient.adaptQuantity(quotient);
	}
    }

    public Ingredient[] getIngredients() {
	return ingredients.toArray(new Ingredient[ingredients.size()]);
    }

    /**
     * Adds a new ingredient to the recipe.
     * 
     * @param newIngredient
     * @return true if the ingredient did not yet exist, false otherwise.
     */
    public boolean addIngredient(Ingredient newIngredient) {
	if (ingredients.contains(newIngredient))
	    return false;

	ingredients.add(newIngredient);
	return true;
    }

}
