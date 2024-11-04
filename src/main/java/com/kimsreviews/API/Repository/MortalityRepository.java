package com.kimsreviews.API.Repository;


import com.kimsreviews.API.models.Mortality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MortalityRepository extends JpaRepository<Mortality, Long> {
}
