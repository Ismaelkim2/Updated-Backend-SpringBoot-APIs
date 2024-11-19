package com.kimsreviews.API.Repository;

import com.kimsreviews.API.models.EggsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EggsRecordRepository extends JpaRepository<EggsRecord, Long> {
}
