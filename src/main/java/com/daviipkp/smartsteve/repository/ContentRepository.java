package com.daviipkp.smartsteve.repository;

import com.daviipkp.smartsteve.Instance.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByTagContainingIgnoreCaseOrDataContainingIgnoreCase(String tagQuery, String dataQuery);
}
