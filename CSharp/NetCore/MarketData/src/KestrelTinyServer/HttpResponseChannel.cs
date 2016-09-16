using System.Threading;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;

namespace KestrelTinyServer
{
    public class HttpResponseChannel : IEventChannel
    {
        private readonly HttpContextChannel _contextChannel;

        public HttpResponseChannel(HttpContextChannel contextChannel)
        {
            _contextChannel = contextChannel;
        }

        public async Task SendAsync(ServerSentEvent sse, CancellationToken token)
        {
            await SendAsync((object)sse, token).ConfigureAwait(false);
        }

        public async Task SendAsync(object obj, CancellationToken token)
        {
            await _contextChannel.HttpContext.Response.WriteAsync(obj.ToString(), token).ConfigureAwait(false);
        }

        public void Close()
        {
            _contextChannel.HttpContext.Abort();
        }

        public void Dispose()
        {
            Close();
        }

    }
}
