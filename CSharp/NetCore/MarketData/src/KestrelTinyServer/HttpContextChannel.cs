using System.Threading;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;

namespace KestrelTinyServer
{
    public class HttpContextChannel:IEventChannel
    {
        public HttpContextChannel(HttpContext context, CancellationToken token)
        {
            HttpContext = context;
            ResponseChannel = new HttpResponseChannel(this);
            Token = token;
        }

        public HttpContext HttpContext { get; }

        public HttpResponseChannel ResponseChannel { get; }

        public CancellationToken Token { get; }

        public async Task SendAsync(ServerSentEvent sse, CancellationToken token)
        {
            await HttpContext.Response.WriteAsync($"{sse}", token);
            await HttpContext.Response.Body.FlushAsync(token).ConfigureAwait(false);
        }

        public void Dispose()
        {
            ResponseChannel.Dispose();
        }
    }
}