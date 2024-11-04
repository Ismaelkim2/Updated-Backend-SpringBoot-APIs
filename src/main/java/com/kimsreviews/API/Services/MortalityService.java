package com.kimsreviews.API.Services;
import com.kimsreviews.API.Repository.MortalityRepository;
import com.kimsreviews.API.models.Mortality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MortalityService {
    @Autowired
    private MortalityRepository mortalityRepository;

    public List<Mortality> getAllMortalities() {
        return mortalityRepository.findAll();
    }

    public Mortality saveMortality(Mortality mortality) {
        return mortalityRepository.save(mortality);
    }

    public Mortality updateMortality(Long id, Mortality mortality) {
        mortality.setId(id);
        return mortalityRepository.save(mortality);
    }

    public void deleteMortality(Long id) {
        mortalityRepository.deleteById(id);
    }
}
