using System;
using System.Threading;
using System.Threading.Tasks;

namespace KestrelTinyServer
{
    public interface IEventChannel : IDisposable
    {
        Task SendAsync(ServerSentEvent message, CancellationToken token);
    }
}
