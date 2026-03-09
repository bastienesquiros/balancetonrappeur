package org.balancetonrappeur.dto.view;

import org.balancetonrappeur.entity.Rapper;
import org.balancetonrappeur.entity.Submission;
import org.balancetonrappeur.entity.WithdrawalRequest;

import java.util.List;

public record AdminDashboardDto(
    long nbRappers,
    long nbAccusations,
    long nbSubmissions,
    long nbWithdrawals,
    long nbNoSpotify,
    List<Submission> pendingSubmissions,
    List<WithdrawalRequest> pendingWithdrawals,
    List<Rapper> rappersWithoutSpotify
) {}

