package io.shockah.dunlin;

import javax.persistence.Id;

public abstract class BaseEntity {
	@Id
	private long id;
}