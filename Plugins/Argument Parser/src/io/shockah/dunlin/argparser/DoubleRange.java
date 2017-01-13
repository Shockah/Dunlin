package io.shockah.dunlin.argparser;

public abstract class DoubleRange {
	public final double min;
	public final double max;
	
	public DoubleRange(double a, double b) {
		min = Math.min(a, b);
		max = Math.max(a, b);
	}
	
	public abstract boolean contains(double value);
	
	public static class Inclusive extends DoubleRange {
		public Inclusive(double a, double b) {
			super(a, b);
		}
		
		public static Inclusive left(double d) {
			return new Inclusive(d, Double.POSITIVE_INFINITY);
		}
		
		public static Inclusive right(double d) {
			return new Inclusive(Double.NEGATIVE_INFINITY, d);
		}

		@Override
		public boolean contains(double value) {
			return value >= min && value <= max;
		}
	}
	
	public static class Exclusive extends DoubleRange {
		public Exclusive(double a, double b) {
			super(a, b);
		}
		
		public static Exclusive left(double d) {
			return new Exclusive(d, Double.POSITIVE_INFINITY);
		}
		
		public static Exclusive right(double d) {
			return new Exclusive(Double.NEGATIVE_INFINITY, d);
		}

		@Override
		public boolean contains(double value) {
			return value > min && value < max;
		}
	}
}