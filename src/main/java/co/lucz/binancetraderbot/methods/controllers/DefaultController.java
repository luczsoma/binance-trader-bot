package co.lucz.binancetraderbot.methods.controllers;

import co.lucz.binancetraderbot.binance.entities.Balance;
import co.lucz.binancetraderbot.binance.entities.OpenOrderResponse;
import co.lucz.binancetraderbot.methods.entities.requests.CreateTradingConfigurationRequest;
import co.lucz.binancetraderbot.methods.entities.requests.EditTradingConfigurationRequest;
import co.lucz.binancetraderbot.methods.entities.requests.SetGlobalTradingLockRequest;
import co.lucz.binancetraderbot.services.ConfigurationRepositoryService;
import co.lucz.binancetraderbot.services.TraderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DefaultController {
    @Autowired
    private ConfigurationRepositoryService configurationRepositoryService;

    @Autowired
    private TraderService traderService;

    @RequestMapping(
            name = "CreateTradingConfiguration",
            path = "create-trading-configuration",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void createTradingConfiguration(@RequestBody CreateTradingConfigurationRequest request) {
        this.configurationRepositoryService.createTradingConfiguration(request);
    }

    @RequestMapping(
            name = "EditTradingConfiguration",
            path = "edit-trading-configuration",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void editTradingConfiguration(@RequestBody EditTradingConfigurationRequest request) {
        this.configurationRepositoryService.editTradingConfiguration(request);
    }

    @RequestMapping(
            name = "SetGlobalTradingLock",
            path = "set-global-trading-lock",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void setGlobalTradingLock(@RequestBody SetGlobalTradingLockRequest request) {
        this.traderService.setGlobalTradingLock(request);
    }

    @RequestMapping(
            name = "GetCurrentOpenOrders",
            path = "get-current-open-orders",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<OpenOrderResponse> getCurrentOpenOrders() {
        return this.traderService.getCurrentOpenOrders();
    }

    @RequestMapping(
            name = "GetBalances",
            path = "get-balances",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Balance> getBalances() {
        return this.traderService.getBalances();
    }
}
