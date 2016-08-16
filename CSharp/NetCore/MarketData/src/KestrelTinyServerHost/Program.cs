using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
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
                var t = Task.Run(async () =>
                {
                    var sse = new ServerSentEvent("sometype", line);
                    await channel.SendAsync(sse, CancellationToken.None).ConfigureAwait(false);
                });
                t.Wait();
            }
        }
    }
}
