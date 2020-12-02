package com.ar.echoafcavlapplication.Models;

import com.ar.echoafcavlapplication.Enums.CardValidationResults;

public class ProcessCardResult {
    private Card card;
    private CardValidationResults validationResults;

    public ProcessCardResult(Card card, CardValidationResults validationResults) {
        this.card = card;
        this.validationResults = validationResults;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public CardValidationResults getValidationResults() {
        return validationResults;
    }

    public void setValidationResults(CardValidationResults validationResults) {
        this.validationResults = validationResults;
    }
}
