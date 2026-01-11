package com.example.financetracker.controller;

import com.example.financetracker.model.Bill;
import com.example.financetracker.model.User;
import com.example.financetracker.repo.UserRepo;
import com.example.financetracker.service.BillService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:5173")
public class BillController {

    private final BillService billService;
    private final UserRepo userRepo;

    public BillController(BillService billService, UserRepo userRepo) {
        this.billService = billService;
        this.userRepo = userRepo;
    }

    @PostMapping("/add")
    public Bill addBill(@RequestBody Bill bill) {
        return billService.addBill(bill);
    }

    // ✅ Updated endpoint to get bills for the currently logged-in user
    @GetMapping("/user")
    public List<Bill> getBills(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("Bill Contoller");
        String username = userDetails.getUsername();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Bill> arr=billService.getBillsByUser(user.getId());
        System.out.println(arr);
        return billService.getBillsByUser(user.getId());
    }
}
