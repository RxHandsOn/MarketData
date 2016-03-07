package com.handson.rx;

import com.handson.dto.Trade;
import com.handson.dto.Vwap;
import com.handson.infra.EventStreamClient;
import com.handson.infra.HttpRequest;
import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.TestSubject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VwapServerTest {

    private EventStreamClient tradeEventStreamClient;
    private TestScheduler scheduler;
    private VwapServer vwapServer;
    private TestSubject<String> tradeSourceSubject;


    @Before
    public void setUpServer() {
        tradeEventStreamClient = mock(EventStreamClient.class);
        scheduler = Schedulers.test();
        vwapServer = new VwapServer(42, tradeEventStreamClient, scheduler);
        tradeSourceSubject = TestSubject.create(scheduler);
        when(tradeEventStreamClient.readServerSideEvents()).thenReturn(tradeSourceSubject);
    }

    /**
     * Test 5
     */
    @Test
    public void should_generate_one_google_vwap_event_when_a_google_trade_is_done() {
        // given
        TestSubscriber<Vwap> testSubscriber = new TestSubscriber<>();
        HttpRequest request = createRequest("STOCK", "GOOGLE");
        vwapServer.getEvents(request).subscribe(testSubscriber);
        // when
        tradeSourceSubject.onNext(new Trade("GOOGLE", 10, 7058.673).toJson());
        tradeSourceSubject.onNext(new Trade("APPLE", 10, 981.8).toJson());
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // then
        List<Vwap> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(1);
        Vwap vwap = events.get(0);
        assertThat(vwap.code).isEqualTo("GOOGLE");
        assertThat(vwap.vwap).isEqualTo(705.8673);
        assertThat(vwap.volume).isEqualTo(10);
    }

    /**
     * Test 6
     */
    @Test
    public void should_add_all_google_trades_to_generate_vwap_events() {
        // given
        TestSubscriber<Vwap> testSubscriber = new TestSubscriber<>();
        HttpRequest request = createRequest("STOCK", "GOOGLE");
        vwapServer.getEvents(request).subscribe(testSubscriber);
        // when
        tradeSourceSubject.onNext(new Trade("GOOGLE", 10, 7058).toJson());
        tradeSourceSubject.onNext(new Trade("GOOGLE", 10, 7062).toJson());
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // then
        List<Vwap> events = testSubscriber.getOnNextEvents();
        assertThat(events).isNotEmpty();
        Vwap vwap = events.get(events.size()-1);
        assertThat(vwap.code).isEqualTo("GOOGLE");
        assertThat(vwap.vwap).isEqualTo(706);
        assertThat(vwap.volume).isEqualTo(20);
    }

    /**
     * Test 7
     */
    @Test
    public void should_generate_at_most_one_event_per_sec() {
        // given
        TestSubscriber<Vwap> testSubscriber = new TestSubscriber<>();
        HttpRequest request = createRequest("STOCK", "GOOGLE");
        vwapServer.getEvents(request).subscribe(testSubscriber);
        // when
        for (int i = 1; i < 11; i++) {
            tradeSourceSubject.onNext(new Trade("GOOGLE", 10, 7000).toJson(), i * 50);
        }
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        // then
        List<Vwap> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(1);
        Vwap vwap = events.get(0);
        assertThat(vwap.code).isEqualTo("GOOGLE");
        assertThat(vwap.vwap).isEqualTo(700);
        assertThat(vwap.volume).isEqualTo(100);
    }

    public HttpRequest createRequest(String name, String value) {
        return new HttpRequest(Collections.singletonMap(name, Arrays.asList(value)));
    }
}