using System;
using System.Collections.Generic;
using System.Linq;

namespace MarketDataCommon.Infra
{
    public class HttpRequest
    {
        private readonly Dictionary<string, List<string>> _parameters;

        public HttpRequest(Dictionary<string, List<string>> parameters)
        {
            _parameters = parameters;
        }

        public String GetParameter(string name)
        {
            if (!_parameters.ContainsKey(name))
                return null;

            return _parameters[name].First();
        }
    }
}
