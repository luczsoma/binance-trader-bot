package co.lucz.binancetraderbot.helpers;

public class SymbolHelpers {
    private static final String SYMBOL_ID_SEPARATOR = "_";

    public static String getSymbolId(String baseAsset, String quoteAsset) {
        return String.format("%s%s%s", baseAsset, SymbolHelpers.SYMBOL_ID_SEPARATOR, quoteAsset).toUpperCase();
    }

    public static String getBaseAsset(String symbolId) {
        return symbolId.split(SymbolHelpers.SYMBOL_ID_SEPARATOR)[0].toUpperCase();
    }

    public static String getQuoteAsset(String symbolId) {
        return symbolId.split(SymbolHelpers.SYMBOL_ID_SEPARATOR)[1].toUpperCase();
    }

    public static String getSymbol(String baseAsset, String quoteAsset) {
        return baseAsset.toUpperCase() + quoteAsset.toUpperCase();
    }

    public static String getSymbol(String symbolId) {
        return getSymbol(getBaseAsset(symbolId), getQuoteAsset(symbolId));
    }
}
