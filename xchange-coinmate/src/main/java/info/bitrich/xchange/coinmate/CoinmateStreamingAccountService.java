package info.bitrich.xchange.coinmate;

import com.fasterxml.jackson.core.type.TypeReference;
import info.bitrich.xchange.coinmate.dto.CoinmateWebsocketBalance;
import info.bitrich.xchangestream.core.StreamingAccountService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import info.bitrich.xchangestream.service.pusher.PusherStreamingService;
import io.reactivex.Observable;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;

import java.util.*;

public class CoinmateStreamingAccountService implements StreamingAccountService {

    private final PusherStreamingService service;
    private final String userId;
    private final Set<Wallet.WalletFeature> walletFeatures = new HashSet<>(Arrays.asList(Wallet.WalletFeature.TRADING, Wallet.WalletFeature.FUNDING));

    public CoinmateStreamingAccountService(PusherStreamingService service, String userId) {
        this.service = service;
        this.userId = userId;
    }

    public Observable<Balance> getBalanceChanges(Currency currency, Object... args) {

        return getCoinmateBalances().map(balanceMap -> balanceMap.get(currency.toString()))
                .map((balance) -> {
                    return new Balance(
                            currency,
                            balance.getBalance(),
                            balance.getBalance().subtract(balance.getReserved()),
                            balance.getReserved());
        });
    }

    public Observable<Wallet> getWalletChanges(Object... args) {

        return getCoinmateBalances().map((balanceMap) -> {
            List<Balance> balances = new ArrayList<>();
            balanceMap.forEach((s, coinmateWebsocketBalance) -> {
                balances.add(
                        new Balance(
                                new Currency(s),
                                coinmateWebsocketBalance.getBalance(),
                                coinmateWebsocketBalance.getBalance().subtract(coinmateWebsocketBalance.getReserved()),
                                coinmateWebsocketBalance.getReserved())
                );
            });
            return balances;
        }).map((balances) -> {
            return Wallet.Builder.from(balances).features(walletFeatures).id("spot").build();
        });
    }

    private Observable<Map<String,CoinmateWebsocketBalance>> getCoinmateBalances(){
        String channelName = "private-user_balances-" + userId;

        return service.subscribeChannel(channelName,"user_balances")
                .map((message)->{
                    Map<String, CoinmateWebsocketBalance> balanceMap =
                            StreamingObjectMapperHelper.getObjectMapper().readValue(message, new TypeReference<Map<String, CoinmateWebsocketBalance>>() {});

                    return balanceMap;
        });
    }
}
