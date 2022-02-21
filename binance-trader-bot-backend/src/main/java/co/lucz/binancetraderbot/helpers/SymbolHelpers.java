package co.lucz.binancetraderbot.helpers;

public class SymbolHelpers {
    public static String getSymbolId(String baseAsset, String quoteAsset) {
        return String.format("%s_%s", baseAsset, quoteAsset);
    }

    public static String getBaseAsset(String symbolId) {
        return symbolId.split("_")[0];
    }

    public static String getQuoteAsset(String symbolId) {
        return symbolId.split("_")[1];
    }

    public static String getSymbol(String baseAsset, String quoteAsset) {
        return baseAsset + quoteAsset;
    }

    public static String getSymbol(String symbolId) {
        return getSymbol(getBaseAsset(symbolId), getQuoteAsset(symbolId));
    }
}
