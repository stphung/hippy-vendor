package org.stphung.pricing;

public class AverageTable {
    private final int count;
    private final int min;
    private final int max;
    private final int average;
    private final int standardDeviation;
    private final double confidence;

    public AverageTable(int count, int min, int max, int average, int standardDeviation, double confidence) {
        this.count = count;
        this.min = min;
        this.max = max;
        this.average = average;
        this.standardDeviation = standardDeviation;
        this.confidence = confidence;
    }

    public int getCount() {
        return this.count;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public int getAverage() {
        return this.average;
    }

    public int getStandardDeviation() {
        return this.standardDeviation;
    }

    public double getConfidence() {
        return this.confidence;
    }

    @Override
    public String toString() {
        return "AverageTable{" +
                "count=" + this.count +
                ", min=" + this.min +
                ", max=" + this.max +
                ", average=" + this.average +
                ", standardDeviation=" + this.standardDeviation +
                ", confidence=" + this.confidence +
                '}';
    }
}
