package com.kimsreviews.API.Repository;

import com.kimsreviews.API.models.EggsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EggsRecordRepository extends JpaRepository<EggsRecord, Long> {

    @Query("SELECT e FROM EggsRecord e WHERE e.date < :date")
    List<EggsRecord> findByDateBefore(LocalDate date);

//    @Query("SELECT e FROM EggsRecord e WHERE e.date >= :startDate AND e.date <= :endDate")
//    List<EggsRecord> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM EggsRecord e WHERE e.archived = false ORDER BY e.date ASC")
    List<EggsRecord> findAllUnarchived();

    @Query("SELECT e FROM EggsRecord e WHERE e.date BETWEEN :startDate AND :endDate")
    List<EggsRecord> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
