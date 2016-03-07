package com.handson.market;

import com.handson.infra.HttpRequest;
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
    protected Observable<Double> getEvents(HttpRequest request) {
        String maturity = request.getParameter("MATURITY");
        Maturity rateMaturity;
        if (maturity == null) {
            rateMaturity = Maturity.M_SPOT;
        } else {
            rateMaturity = Maturity.valueOf(maturity);
        }
        return Observable.just(rateMaturity.value);
    }

    public static void main(String[] args) {
        new RateProvider(PORT).createServer().startAndWait();
    }
}
