using System;
using System.Linq;
using System.Text;

namespace KestrelTinyServer
{
    public class ServerSentEvent
    {
        private static readonly string[] LogLevels = { "DEBUG", "INFO", "WARN", "ERROR" };
        private readonly string _type;
        private readonly string _data;

        public ServerSentEvent(string type, string data)
        {
            _type = type;
            _data = data;
        }

        public override string ToString()
        {
            var lines = _data.Split(new[] { "\r\n" }, StringSplitOptions.None);
            var builder = new StringBuilder();

            if (LogLevels.Any(logLevels => logLevels.Contains(_type)))
            {
                builder.Append("event: " + _type + "\r\n");
            }

            foreach (var line in lines)
            {
                builder.Append("data: " + line + "\r\n");
            }

            builder.Append("\r\n");
            return builder.ToString();
        }
    }
}