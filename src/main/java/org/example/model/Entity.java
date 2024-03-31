package org.example.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class Entity {
  @Id
  private ObjectId id;
  @Field(name = "name")
  private String name;

  public Entity(ObjectId id) {
    this.id = id;
  }

  public Entity(String name) {
    this.name = name;
  }

  public Entity(ObjectId id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }
}
