package com.example.demo.entities;

import java.io.Serializable;
import java.time.ZonedDateTime;

public interface Entity<K extends Comparable<K>> extends Serializable, Versionable {

	K getId();

	void setId(K pId);

	ZonedDateTime getModified();

	void setModified(ZonedDateTime pModified);

	ZonedDateTime getCreated();

	void setCreated(ZonedDateTime pCreated);
}
