package com.kimsreviews.API.Controllers;
import com.kimsreviews.API.Services.MortalityService;
import com.kimsreviews.API.models.Mortality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mortalities")
@CrossOrigin(origins = {"http://localhost:4200", "https://agri-business-market-hub.onrender.com"})
public class MortalityController {

    @Autowired
    private MortalityService mortalityService;

    @GetMapping
    public List<Mortality> getAllMortalities() {
        return mortalityService.getAllMortalities();
    }

    @PostMapping
    public Mortality addMortality(@RequestBody Mortality mortality) {
        return mortalityService.saveMortality(mortality);
    }

    @PutMapping("/{id}")
    public Mortality updateMortality(@PathVariable Long id, @RequestBody Mortality mortality) {
        return mortalityService.updateMortality(id, mortality);
    }

    @DeleteMapping("/{id}")
    public void deleteMortality(@PathVariable Long id) {
        mortalityService.deleteMortality(id);
    }
}
