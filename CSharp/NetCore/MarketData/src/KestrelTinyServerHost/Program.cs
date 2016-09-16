using System;
using System.Net;
using System.Threading;
using KestrelTinyServer;

namespace KestrelTinyServerHost
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var factory = new ChannelFactory();
            var channel = factory.Create(IPAddress.Loopback, 5000, 5);

            string line;


            while (!string.IsNullOrWhiteSpace(line = Console.ReadLine()))
            {
                Console.WriteLine("Sending : " + line);
                var sse = new ServerSentEvent("sometype", line);
                channel.SendAsync(sse, CancellationToken.None).ConfigureAwait(false).GetAwaiter();
            }

            channel.Dispose();
        }
    }
}
