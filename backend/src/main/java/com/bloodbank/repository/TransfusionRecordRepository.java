package com.bloodbank.repository;

import com.bloodbank.entity.TransfusionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransfusionRecordRepository extends JpaRepository<TransfusionRecord, Long> {
    List<TransfusionRecord> findByBloodUnitId(Long bloodUnitId);
}
