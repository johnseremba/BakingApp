package com.example.bakingapp.util;

public class IngredientUtils {
    public static String matchMeasurements(String measure) {
        switch (measure.toLowerCase()) {
            case "cup":
            case "c":
                return "Cup";
            case "tbl":
            case "t":
            case "tb":
            case "tbsp":
            case "tblsp":
                return "Tablespoon";
            case "tsp":
                return "Teaspoon";
            case "k":
                return "Kilogram";
            case "g":
                return "Gram";
            case "oz":
                return "Ounce";
            case "ml":
                return "Milliliter";
            case "unit":
                return "Unit";
            case "l":
                return "Liter";
            case "pt":
                return "Pint";
            case "lb":
                return "Pound";
            default:
                return measure;
        }
    }
}
