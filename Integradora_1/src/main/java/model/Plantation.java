package model;

public class Plantation {
    private String name;
    private Season season;
    private int growthDays;

    public Plantation(String name, Season season) {
        this.name = name;
        this.season = season;
        this.growthDays = 0;
    }

    public String getName() {
        return name;
    }

    public Season getSeason() {
        return season;
    }

    public void setGrowthDays(int growthDays) {
        this.growthDays = growthDays;
    }

    public int getGrowthDays() {
        return growthDays;
    }

    public void incrementGrowthDays(int days) {
        this.growthDays += days;
    }

    public boolean hasExceededSeason() {
        return growthDays >= 28;
    }


    @Override
    public String toString() {
        return name + " (" + season + ")";
    }
}
