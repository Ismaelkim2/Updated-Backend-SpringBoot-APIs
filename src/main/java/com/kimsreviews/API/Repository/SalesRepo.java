package com.kimsreviews.API.Repository;

import com.kimsreviews.API.models.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepo extends JpaRepository <Sales,Long> {
}
