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
        return count;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getAverage() {
        return average;
    }

    public int getStandardDeviation() {
        return standardDeviation;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "AverageTable{" +
                "count=" + count +
                ", min=" + min +
                ", max=" + max +
                ", average=" + average +
                ", standardDeviation=" + standardDeviation +
                ", confidence=" + confidence +
                '}';
    }
}
