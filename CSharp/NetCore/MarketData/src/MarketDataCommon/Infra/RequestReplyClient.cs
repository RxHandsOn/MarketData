using System;

namespace MarketDataCommon.Infra
{
    public interface IRequestReplyClient
    {
        IObservable<string> Request(string parameter);
    }
}
