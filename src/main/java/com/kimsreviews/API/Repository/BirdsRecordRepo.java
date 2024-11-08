package com.kimsreviews.API.Repository;

import com.kimsreviews.API.models.BirdRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BirdsRecordRepo extends JpaRepository<BirdRecord, Long> {

}