package tn.esprit.pidev.services;

import tn.esprit.pidev.entities.*;
import tn.esprit.pidev.repository.InvestmentRepository;

import java.util.List;

public interface IInvestmentService {

    public Investment saveInvestment(Investment investment);
    public Investment findInvestmentById(long id);
    public List<Investment> findAllInvestments();
    public void deleteInvestment(long id);
    public Investment updateInvestment(long id, Investment investment);
    public String sendMail(EmailDetails details, Investment investment, Investor investor);
    public Investment calculateAndUpdateROI(long investmentId, double netProfit);
    public Double getROIForInvestment(long investmentId);
    public List<Investment> getInvestmentWithROIForInvestor(long investorId); // Corrected return type
    public List<Investment> getInvestmentsByInvestorId(Long investorId);
    public List<Investment> findInvestmentsByInvestorId(Long investorId);
    public List<Investment> findInvestmentsByCriteria(
            InvestmentType investmentType,
            Double minROI,
            Double maxROI,
            InvestmentStatus status,
            Double minAmount,
            Double maxAmount,
            DividendPaymentFrequency dividendPaymentFrequency,
            Double minDividendRate,
            Double maxDividendRate
    );


}