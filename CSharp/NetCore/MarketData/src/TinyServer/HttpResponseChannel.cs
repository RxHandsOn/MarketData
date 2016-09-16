﻿using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace BrowserLog.TinyServer
{
    public class HttpResponseChannel : IEventChannel
    {
        private readonly TcpClient _tcpClient;

        public HttpResponseChannel(TcpClient tcpClient)
        {
            _tcpClient = tcpClient;
        }

        public void Send(ServerSentEvent sse, CancellationToken token)
        {
            Send((object)sse, token);
        }

        public Task Send(object obj, CancellationToken token)
        {
            var content = Encoding.UTF8.GetBytes(obj.ToString());
            var stream = _tcpClient.GetStream();
            var writeTask = stream.WriteAsync(content, 0, content.Length, token);
            return writeTask.ContinueWith(t => stream.FlushAsync(token), token);
        }

        public void Dispose()
        {
            _tcpClient.GetStream().Dispose();
            _tcpClient.Dispose();
        }
    }
}
