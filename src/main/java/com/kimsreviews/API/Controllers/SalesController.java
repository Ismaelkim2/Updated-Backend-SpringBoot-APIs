package com.kimsreviews.API.Controllers;
import com.kimsreviews.API.Services.SalesService;
import com.kimsreviews.API.models.Sales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = {"http://localhost:4200", "https://agri-business-market-hub.onrender.com"})
public class SalesController {

    private final SalesService salesService;

    @Autowired
    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    public List<Sales> getAllSales() {
        return salesService.findAllSales();
    }

    @PostMapping
    public Sales saveSale(@RequestBody Sales sales) {
        return salesService.saveSales(sales);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sales> updateSale(@PathVariable Long id, @RequestBody Sales sales) {
        return salesService.findSalesById(id).map(existingSale -> {
            sales.setId(id);
            salesService.saveSales(sales);
            return ResponseEntity.ok(sales);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        if (salesService.findSalesById(id).isPresent()) {
            salesService.deleteSales(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
