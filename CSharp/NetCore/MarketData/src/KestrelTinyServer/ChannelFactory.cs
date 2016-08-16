using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;

namespace KestrelTinyServer
{
    public class ChannelFactory
    {
        private IPAddress _host;
        private int _port;

        public virtual IEventChannel Create(string host, int port, int bufferSize)
        {
            var ipAddress = IPAddress.Parse(host);
            return Create(ipAddress, port, bufferSize);
        }
        public virtual IEventChannel Create(IPAddress host, int port, int bufferSize)
        {
            _host = host;
            _port = port;
            var channel = new MulticastChannel(bufferSize);

            var httpServer = new HttpServer(_host, _port, async contextChannel =>
            {
                await HandleSseAsync(contextChannel);
                channel.AddChannel(contextChannel, contextChannel.Token);
            });
            channel.AttachServer(httpServer);
            httpServer.Run();

            return channel;
        }

        private async Task HandleSseAsync(HttpContextChannel httpContextChannel)
        {
            var httpResponse = httpContextChannel.HttpContext.Response;

            httpResponse.StatusCode = 200;
            httpResponse.Headers.Add("Connection", "keep-alive");
            httpResponse.Headers.Add("Content-Type", "text/event-stream");
            //httpResponse.Headers.Add("Transfer-Encoding", "chunked"); // seems already set to "chunked" in chrome, if uncommented prompt ERR_INVALID_CHUNKED_ENCODING
            httpResponse.Headers.Add("Cache-Control", "no-cache");
            httpResponse.Headers.Add("Access-Control-Allow-Origin", "*");

            await httpResponse.Body.FlushAsync();
            await httpContextChannel.SendAsync(new ServerSentEvent("INFO", "Connected successfully on LOG stream from " + _host + ":" + _port), CancellationToken.None).ConfigureAwait(false);

        }
    }
}
