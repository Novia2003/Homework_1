package ru.tbank.integration.http;

import ru.tbank.dto.cbr.curs.ValCursDTO;
import ru.tbank.dto.cbr.valute.ValuteFullDTO;

public interface CBRClient {
    ValCursDTO getCurrencyRates();
    ValuteFullDTO getCurrencies();
}
