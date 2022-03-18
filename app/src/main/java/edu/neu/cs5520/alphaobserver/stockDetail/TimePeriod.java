package edu.neu.cs5520.alphaobserver.stockDetail;

public enum TimePeriod {

    ONE_DAY(1), ONE_WEEK(7), ONE_MONTH(30);

    private final int numberOfDays;

    TimePeriod(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }
    public int getNumberOfDays() {
        return numberOfDays;
    }
}
