using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MarketDataCommon.Infra
{
    public interface ISubscription : IDisposable
    {
        void Unsuscribe();

        bool IsUnsuscribe { get; }
    }
}
