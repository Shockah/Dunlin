package io.shockah.dunlin.terrariadb;

public final class Item {
	public int id;
	public String name;
	public int rare;
	public int damage;
	public int crit;
	public boolean accessory;
	public boolean melee;
	public boolean ranged;
	public boolean magic;
	public boolean summon;
	public boolean thrown;
	public int value;
	public String[] tooltip = new String[0];
	
	public String getDamageTypeName() {
		if (melee)
			return "Melee damage";
		if (ranged)
			return "Ranged damage";
		if (magic)
			return "Magic damage";
		if (summon)
			return "Summon damage";
		if (thrown)
			return "Thrown damage";
		return "Damage";
	}
	
	public String getFormattedValue() {
		int c = 0, s = 0, g = 0, p = 0;
		
		c = value;
		if (c >= 100) {
			s = c / 100;
			c %= 100;
		}
		if (s >= 100) {
			g = s / 100;
			s %= 100;
		}
		if (g >= 100) {
			p = g / 100;
			g %= 100;
		}
		
		StringBuilder sb = new StringBuilder();
		if (p > 0) {
			if (sb.length() != 0)
				sb.append(' ');
			sb.append(String.format("**%d**p", p));
		}
		if (p + g > 0) {
			if (sb.length() != 0)
				sb.append(' ');
			sb.append(String.format("**%d**g", g));
		}
		if (p + g + s > 0) {
			if (sb.length() != 0)
				sb.append(' ');
			sb.append(String.format("**%d**s", s));
		}
		if (p + g + s + c > 0) {
			if (sb.length() != 0)
				sb.append(' ');
			sb.append(String.format("**%d**c", c));
		}
		return sb.toString();
	}
}