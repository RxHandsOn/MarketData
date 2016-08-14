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
            var channel = factory.Create(IPAddress.Loopback, 5000, 70);

            string line;


            while (!string.IsNullOrWhiteSpace(line = Console.ReadLine()))
            {
                Console.WriteLine("Sending : " + line);
                var t = Task.Run(async () =>
                {
                    await channel.SendAsync(new ServerSentEvent("sometype", line), CancellationToken.None).ConfigureAwait(false);
                });
                t.Wait();
            }
        }
    }
}
