using System;

namespace MarketDataCommon.Infra
{
    public interface IEventStreamClient
    {
        IObservable<string> ReadServerSideEvents();
    }
}
