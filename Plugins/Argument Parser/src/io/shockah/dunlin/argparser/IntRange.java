package io.shockah.dunlin.argparser;

public abstract class IntRange {
	public final int min;
	public final int max;
	
	public IntRange(int a, int b) {
		min = Math.min(a, b);
		max = Math.max(a, b);
	}
	
	public abstract boolean contains(int value);
	
	public static class Inclusive extends IntRange {
		public Inclusive(int a, int b) {
			super(a, b);
		}
		
		public static Inclusive left(int i) {
			return new Inclusive(i, Integer.MAX_VALUE);
		}
		
		public static Inclusive right(int i) {
			return new Inclusive(Integer.MIN_VALUE, i);
		}

		@Override
		public boolean contains(int value) {
			return value >= min && value <= max;
		}
	}
	
	public static class Exclusive extends IntRange {
		public Exclusive(int a, int b) {
			super(a, b);
		}
		
		public static Exclusive left(int i) {
			return new Exclusive(i, Integer.MAX_VALUE);
		}
		
		public static Exclusive right(int i) {
			return new Exclusive(Integer.MIN_VALUE, i);
		}

		@Override
		public boolean contains(int value) {
			return value > min && value < max;
		}
	}
}