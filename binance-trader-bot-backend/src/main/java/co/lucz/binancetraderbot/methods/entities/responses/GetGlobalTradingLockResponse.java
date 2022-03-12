package co.lucz.binancetraderbot.methods.entities.responses;

public class GetGlobalTradingLockResponse {
    private final boolean tradingIsLocked;

    public GetGlobalTradingLockResponse(boolean tradingIsLocked) {
        this.tradingIsLocked = tradingIsLocked;
    }

    public boolean getTradingIsLocked() {
        return tradingIsLocked;
    }
}
