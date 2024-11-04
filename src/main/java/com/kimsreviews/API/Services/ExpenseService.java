package com.kimsreviews.API.Services;
import com.kimsreviews.API.Repository.ExpenseRepo;
import com.kimsreviews.API.models.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepo expenseRepo;

    public List<Expense> getAllExpenses() {
        return expenseRepo.findAll();
    }

    public Expense saveExpense(Expense expense) {
        return expenseRepo.save(expense);
    }

    public Expense updateExpense(Long id, Expense expenseDetails) {
        Expense expense = expenseRepo.findById(id).orElseThrow();
        expense.setDate(expenseDetails.getDate());
        expense.setExpenseType(expenseDetails.getExpenseType());
        expense.setAmount(expenseDetails.getAmount());
        expense.setNotes(expenseDetails.getNotes());
        return expenseRepo.save(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepo.deleteById(id);
    }
}
