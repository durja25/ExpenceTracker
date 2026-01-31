package org.development.ExpenceTracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.development.ExpenceTracker.dto.ExpenseDTO;
import org.development.ExpenceTracker.entity.ProfileEntity;
import org.development.ExpenceTracker.repository.ProfileRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ProfileRepo profileRepo;
    private final ExpenseService expenseService;
    private final EmailService emailService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder() {
        List<ProfileEntity> profiles = profileRepo.findAll();
        for (ProfileEntity profile : profiles) {
            String body = "Hi " + profile.getName() + "<br><br>" +
                    "Today is " + LocalDate.now() + "<br>" +
                    "A friendly reminder to add your income and " +
                    "expenses for today.<br><br>" +

                    "<a href='" + frontendUrl + "' " +
                    "style='display: inline-block; padding: 10px " +
                    "20px; " +
                    "background-color: #4CAF50; color: white; " +
                    "text-decoration: none; border-radius: 5px; " +
                    "font-weight: bold;'>" +
                    "Add Transactions" +
                    "</a><br><br>" +

                    "Best regards,<br>" +
                    "Expense Tracker Team";
            emailService.sendEmail(profile.getEmail(),"fill in your expenses",body);
        }
    }

//    @Scheduled(cron = "0 0 23 * * *",zone = "IST")
//    public void sendDailyExpenseSummary(){
//        log.info("senging daily summary");
//        List<ProfileEntity> profiles = profileRepo.findAll();
//        for (ProfileEntity profile : profiles) {
//            List<ExpenseDTO> expensesForUserOnDate = expenseService.getExpensesForUserOnDate(
//                profile.getId(), LocalDate.now());
//
//            if (!expensesForUserOnDate.isEmpty()) {
//                StringBuilder table = new StringBuilder();
//
//            }
//
//
//        }
//
//
//    }
}