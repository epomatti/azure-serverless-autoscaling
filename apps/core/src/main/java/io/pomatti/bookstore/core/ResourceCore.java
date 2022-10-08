package io.pomatti.bookstore.core;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class ResourceCore {

  protected <T> ResponseEntity<T> ok(T entity) {
    return ResponseEntity.ok(entity);
  }

  protected <T> ResponseEntity<T> created(T entity) {
    return new ResponseEntity<T>(entity, HttpStatus.CREATED);
  }

  protected <T> ResponseEntity<T> notFound() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  protected <T> ResponseEntity<T> optionalGet(Optional<T> optional) {
    if (optional.isPresent()) {
      return ok(optional.get());
    } else {
      return notFound();
    }
  }

}
