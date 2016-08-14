using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace KestrelTinyServer
{
    public class MulticastChannel : IEventChannel
    {
        private readonly IList<IEventChannel> _channels = new List<IEventChannel>();
        private readonly object _syncRoot = new object();
        private readonly int _replayBufferSize;
        private readonly IList<ServerSentEvent> _replayBuffer;
        private HttpServer _httpServer;

        public MulticastChannel(int replayBufferSize = 1)
        {
            _replayBufferSize = replayBufferSize;
            _replayBuffer = new List<ServerSentEvent>(replayBufferSize);
        }

        public void AddChannel(IEventChannel channel, CancellationToken token)
        {
            _channels.Add(channel);
            foreach (var message in _replayBuffer)
            {
                var t = Task.Run(async () => await channel.SendAsync(message, token).ConfigureAwait(false));
                t.Wait(token);
            }
        }

        public async Task SendAsync(ServerSentEvent message, CancellationToken token)
        {
            var closeChannels = new List<IEventChannel>();
            foreach (var channel in _channels)
            {
                try
                {
                    await channel.SendAsync(message, token).ConfigureAwait(false);
                }
                catch (Exception)
                {
                    closeChannels.Add(channel);
                }
            }
            foreach (var channel in closeChannels)
            {
                _channels.Remove(channel);
            }

            while (_replayBuffer.Count >= _replayBufferSize)
            {
                _replayBuffer.RemoveAt(0);
            }
            _replayBuffer.Add(message);
        }

        public void AttachServer(HttpServer httpServer)
        {
            _httpServer = httpServer;
        }

        public void Dispose()
        {
            _httpServer?.Dispose();
            foreach (var channel in _channels)
            {
                channel.Dispose();
            }
        }
    }
}
