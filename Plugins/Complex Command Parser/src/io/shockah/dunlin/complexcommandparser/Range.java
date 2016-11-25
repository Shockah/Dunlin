package io.shockah.dunlin.complexcommandparser;

public final class Range {
	public final Integer min;
	public final Integer max;
	
	public Range(Integer min, Integer max) {
		this.min = min;
		this.max = max;
	}
	
	public boolean contains(int v) {
		if (min != null && v < min)
			return false;
		if (max != null && v > max)
			return false;
		return true;
	}
}