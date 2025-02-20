package com.dolphin.adminbackend.utility;

public class ColorUtility {

    public static String getDemographicColor(String gender, String ageRange) {
        switch (ageRange) {
            case "(18-25)":
                return gender.equals("MALE") ? "#BBDEFB" : "#F8BBD0"; // Soft baby blue / Soft blush pink
            case "(26-30)":
                return gender.equals("MALE") ? "#90CAF9" : "#F48FB1"; // Muted cornflower blue / Gentle rose pink
            case "(31-40)":
                return gender.equals("MALE") ? "#64B5F6" : "#F06292"; // Gentle periwinkle blue / Warm pastel pink
            case "(41-50)":
                return gender.equals("MALE") ? "#42A5F5" : "#EC407A"; // Pastel cobalt blue / Rich pastel pink
            case "(51-60)":
                return gender.equals("MALE") ? "#1E88E5" : "#D81B60"; // Slightly deeper pastel blue / Slightly deeper dusty pink
            case "(Over 60)":
                return gender.equals("MALE") ? "#1565C0" : "#AD1457"; // Soft navy blue / Deep rose pink
            default:
                return "#989898"; // Default gray for unknown values
        }
    }
    
    
}
