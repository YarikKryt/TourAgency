package db;

import java.math.BigDecimal;
import java.util.Objects;

public class Tour {
    private int tourID;
    private String type;
    private BigDecimal price;
    private String fromCity;
    private String toCity;
    private String transport;
    private boolean meals;
    private int mealsPerDay;
    private int durationInDays;

    public Tour(int tourID, String type, BigDecimal price, String fromCity, String toCity, String transport, boolean meals, int mealsPerDay, int durationInDays) {
        this.tourID = tourID;
        this.type = type;
        this.price = price;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.transport = transport;
        this.meals = meals;
        this.mealsPerDay = mealsPerDay;
        this.durationInDays = durationInDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tour tour = (Tour) o;
        return tourID == tour.tourID &&
                Objects.equals(type, tour.type) &&
                Objects.equals(price, tour.price) &&
                Objects.equals(fromCity, tour.fromCity) &&
                Objects.equals(toCity, tour.toCity) &&
                Objects.equals(transport, tour.transport) &&
                meals == tour.meals &&
                mealsPerDay == tour.mealsPerDay &&
                durationInDays == tour.durationInDays;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tourID, type, price, fromCity, toCity, transport, meals, mealsPerDay, durationInDays);
    }

    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public boolean getMeals() {
        return meals;
    }

    public void setMeals(boolean meals) {
        this.meals = meals;
    }

    public int getMealsPerDay() {
        return mealsPerDay;
    }

    public void setMealsPerDay(int mealsPerDay) {
        this.mealsPerDay = mealsPerDay;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }
}
