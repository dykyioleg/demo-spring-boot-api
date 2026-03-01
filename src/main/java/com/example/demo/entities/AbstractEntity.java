package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.ZonedDateTime;

@MappedSuperclass
@Setter
@Getter
@ToString
public abstract class AbstractEntity<K extends Comparable<K>> implements Serializable, Entity<K> {

	private static final long serialVersionUID = 1L;

	public static final String ID_SEQ_NAME = "entity_seq";

	@Version
	@Column(name = "VERSION")
	private Long version;

	@Column(name = "MODIFIED")
	@SuppressWarnings("squid:S3437")
	private ZonedDateTime modified;

	@Column(name = "CREATED")
	@SuppressWarnings("squid:S3437")
	private ZonedDateTime created;

	@PreUpdate
	void preUpdate() {
		modified = ZonedDateTime.now();
	}

	@PrePersist
	protected void prePersist() {
		final ZonedDateTime now = ZonedDateTime.now();
		if (created == null) {
			created = now;
		}
		if (modified == null) {
			modified = now;
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		return getId() != null && getId().equals(((AbstractEntity<?>) o).getId());
	}

	@Override
	public int hashCode() {
		if (getId() == null) {
			return System.identityHashCode(this);
		}

		return getId().hashCode();
	}
}
