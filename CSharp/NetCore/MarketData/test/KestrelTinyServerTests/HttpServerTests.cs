using System;
using System.Net.Http;
using System.Threading.Tasks;
using FluentAssertions;
using FluentAssertions.Specialized;
using Xunit;

namespace KestrelTinyServerTests
{
    public class HttpServerTests
    {
        [Fact]
        public async Task Should_call_handler_on_request()
        {
            //Given
            var expectedResult = "It works";
            string result;
            using (var server = WebHostTestHelper.CreateTestHttpServer(expectedResult))
            {

                //When
                var httpClient = new HttpClient();
                result = await httpClient.GetStringAsync(server.Url).ConfigureAwait(false);
            }

            //Then
            result.Should().BeEquivalentTo(expectedResult);
        }

        [Fact]
        public void Should_stop_handling_requests_When_disposed()
        {
            //Given
            var expectedResult = "It works";
            var server = WebHostTestHelper.CreateTestHttpServer(expectedResult);
            var url = server.Url;
            server.Dispose();

            //When
            var httpClient = new HttpClient();
            Func<Task> invokeAsync = async () => await httpClient.GetStringAsync(url).ConfigureAwait(false);

            //Then
            invokeAsync.ShouldThrow<Exception>();
        }

        [Fact]
        public void Should_throw_exception_running_a_disposed_server()
        {
            //Given
            var expectedResult = "It works";
            var server = WebHostTestHelper.CreateTestHttpServer(expectedResult, false);
            server.Dispose();

            //When
            Action invoke = () => server.Run();

            //Then
            invoke.ShouldThrow<ObjectDisposedException>();
        }
    }
}
