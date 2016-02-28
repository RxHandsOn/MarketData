package com.handson.market;

import com.handson.infra.RxNettyEventServer;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class RateProvider extends RxNettyEventServer<Double> {

    public static final int PORT = 8099;

    enum Maturity {
        M_SPOT(1), M_3M(1.01), M_6M(1.02);

        private final double value;

        Maturity(double value) {

            this.value = value;
        }

    }

    public RateProvider(int port) {
        super(port);
    }

    @Override
    protected Observable<Double> getEvents(Map<String, List<String>> queryParameters) {
        List<String> maturityValues = queryParameters.get("MATURITY");
        Maturity rateMaturity;
        if (maturityValues == null || maturityValues.isEmpty()) {
            rateMaturity = Maturity.M_SPOT;
        } else {
            rateMaturity = Maturity.valueOf(maturityValues.get(0));
        }
        return Observable.just(rateMaturity.value);
    }

    public static void main(String[] args) {
        new RateProvider(PORT).createServer().startAndWait();
    }
}
