using System;
using System.Collections.Generic;
using System.Linq;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace MarketDataCommon.Infra
{
    public class SubscriptionLimiter
    {
        public static IObservable<T> LimitSubscriptions<T>(int maxNumber, IObservable<T> source)
        {
            return Observable.Throw<T>(new NotImplementedException());
        }

    }
}
