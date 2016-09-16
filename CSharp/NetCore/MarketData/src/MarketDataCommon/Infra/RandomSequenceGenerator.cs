using System;
using System.Reactive.Linq;

namespace MarketDataCommon.Infra
{
    public class RandomSequenceGenerator
    {
        private readonly double _min;
        private readonly double _max;
        private readonly Random _random;
        private double _bias = 0.3;

        public RandomSequenceGenerator(double min, double max)
        {
            _min = min;
            _max = max;
            _random = new Random();
        }

        public IObservable<double> Create(TimeSpan timeSpan)
        {
            double init = (_min + _max) / 2;
            return Observable.Interval(timeSpan)
                             .Scan(init, (previous, i) => ComputeNextNumber(previous));
        }

        public IObservable<int> CreateIntegerSequence(TimeSpan timeSpan)
        {
            double range = _max - _min;
            return Observable.Interval(timeSpan)
                             .Select(i => (int)(_random.NextDouble() * range + _min));

        }

        private double ComputeNextNumber(double previous)
        {
            double range = (_max - _min) / 20;
            double scaled = (_random.NextDouble() - 0.5 + _bias) * range;
            double shifted = previous + scaled;

            if (shifted < _min || shifted > _max)
            {
                shifted = previous - scaled;
                _bias = -_bias;
            }

            shifted = (double)((long)Math.Round(shifted * 10000)) / 10000;

            return shifted;
        }
    }
}
