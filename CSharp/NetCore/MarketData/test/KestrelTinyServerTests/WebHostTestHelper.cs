using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;
using KestrelTinyServer;
using Microsoft.AspNetCore.Http;

namespace KestrelTinyServerTests
{
    internal static class WebHostTestHelper
    {
        //Create and close TcpListener and just return the available free Tcp port
        private static int GetAnonymousPort()
        {
            TcpListener listener = new TcpListener(IPAddress.IPv6Any, 0);
            listener.Server.DualMode = true;
            listener.Start();

            int port = ((IPEndPoint)listener.LocalEndpoint).Port;
            listener.Stop();

            return port;
        }

        public static HttpServer CreateTestHttpServer(string content, bool shouldRun)
        {
            var server = new HttpServer(IPAddress.Loopback, GetAnonymousPort(), async context =>
            {
                await context.HttpContext.Response.WriteAsync(content).ConfigureAwait(false);
            });

            if(shouldRun)
                server.Run();

            return server;
        }

        public static HttpServer CreateTestHttpServer(string content)
        {
            return CreateTestHttpServer(content, true);
        }
    }
}