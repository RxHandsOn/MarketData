using System;
using System.Threading;
using System.Threading.Tasks;
using KestrelTinyServer;
using NSubstitute;
using Xunit;

namespace KestrelTinyServerTests
{
    public class MulticastChannelTests
    {
        [Fact]
        public async Task Should_send_event_to_added_channel()
        {
            // given
            var multicastChannel = new MulticastChannel();
            var channel = Substitute.For<IEventChannel>();
            multicastChannel.AddChannel(channel, CancellationToken.None);

            // when
            await multicastChannel.SendAsync(new ServerSentEvent("DEBUG", "whatever"), CancellationToken.None).ConfigureAwait(false);

            // then
            await channel.Received().SendAsync(Arg.Any<ServerSentEvent>(), CancellationToken.None);
        }

        [Fact]
        public async Task Should_stop_sending_events_on_a_close_channel()
        {
            // given
            var multicastChannel = new MulticastChannel();
            var channel = Substitute.For<IEventChannel>();
            channel.When(c => c.SendAsync(Arg.Any<ServerSentEvent>(), CancellationToken.None)).Do(x => { throw new Exception(); });
            multicastChannel.AddChannel(channel, CancellationToken.None);

            // when
            await multicastChannel.SendAsync(new ServerSentEvent("DEBUG", "whatever"), CancellationToken.None).ConfigureAwait(false); // exception raised
            await multicastChannel.SendAsync(new ServerSentEvent("DEBUG", "whatever"), CancellationToken.None).ConfigureAwait(false); // channel should be removed

            // then
            await channel.Received(1).SendAsync(Arg.Any<ServerSentEvent>(), CancellationToken.None);
        }

        [Fact]
        public async Task Should_replay_last_event_when_adding_a_channel()
        {
            // given
            var multicastChannel = new MulticastChannel();
            var channel = Substitute.For<IEventChannel>();
            await multicastChannel.SendAsync(new ServerSentEvent("DEBUG", "whatever"), CancellationToken.None).ConfigureAwait(false);
            
            // when
            multicastChannel.AddChannel(channel, CancellationToken.None);

            // then
            await channel.Received().SendAsync(Arg.Any<ServerSentEvent>(), CancellationToken.None);
        }

        [Fact]
        public async Task Should_replay_only_last_events_when_adding_a_channel()
        {
            // given
            var multicastChannel = new MulticastChannel(2);
            var channel = Substitute.For<IEventChannel>();
            await multicastChannel.SendAsync(new ServerSentEvent("DEBUG", "first"), CancellationToken.None).ConfigureAwait(false);
            await multicastChannel.SendAsync(new ServerSentEvent("DEBUG", "whatever2"), CancellationToken.None).ConfigureAwait(false);
            await multicastChannel.SendAsync(new ServerSentEvent("DEBUG", "whatever3"), CancellationToken.None).ConfigureAwait(false);
            
            // when
            multicastChannel.AddChannel(channel, CancellationToken.None);

            // then
            await channel.Received(2).SendAsync(Arg.Any<ServerSentEvent>(), CancellationToken.None).ConfigureAwait(false);
            await channel.DidNotReceive().SendAsync(Arg.Is<ServerSentEvent>(e => e.ToString().Contains("first")), CancellationToken.None).ConfigureAwait(false);
        }
    }
}
