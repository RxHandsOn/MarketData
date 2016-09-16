using Newtonsoft.Json;

namespace MarketDataCommon.Dto
{
    public class Vwap
    {
        public string Code { get; set; }
        public double VwapValue { get; set; }
        public double Volume { get; set; }

        public Vwap(string code, double vwapValue, double volume)
        {
            Code = code;
            VwapValue = vwapValue;
            Volume = volume;
        }

        public Vwap AddTrade(Trade trade)
        {
            double volume = Volume + trade.Quantity;
            double vwapValue = (Volume*VwapValue + trade.Nominal)/Volume;
            return new Vwap(trade.Code, vwapValue, volume);
        }

        public static Vwap FromJson(string input)
        {
            return JsonConvert.DeserializeObject<Vwap>(input);
        }

        public string ToJson()
        {
            return JsonConvert.SerializeObject(this);
        }

        public override string ToString() => ToJson();

    }
}
