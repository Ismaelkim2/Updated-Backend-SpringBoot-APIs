package com.kimsreviews.API.Services;

import com.kimsreviews.API.Repository.SalesRepo;
import com.kimsreviews.API.models.Sales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalesService {

    private final SalesRepo salesRepo;

   @Autowired
    public SalesService(SalesRepo salesRepo){
       this.salesRepo=salesRepo;
   }

   public List <Sales> findAllSales(){
       return salesRepo.findAll();
   }

   public Sales saveSales (Sales sales){
       return salesRepo.save(sales);
   }

   public Optional <Sales> findSalesById(Long id){
       return salesRepo.findById(id);
   }

   public void deleteSales(Long id){
       salesRepo.deleteById(id);
   }


}
