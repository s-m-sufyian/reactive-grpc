/*
 *  Copyright (c) 2017, salesforce.com, inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.reactivegrpccommon;

import io.grpc.stub.CallStreamObserver;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class ReactiveStreamObserverPublisherTest {
    @Test
    public void onNextDelegates() {
        CallStreamObserver<Object> obs = mock(CallStreamObserver.class);
        Subscriber<Object> sub = mock(Subscriber.class);

        ReactiveStreamObserverPublisher<Object> pub = new ReactiveStreamObserverPublisher<Object>(obs);
        pub.subscribe(sub);

        Object obj = new Object();

        pub.onNext(obj);
        verify(sub).onNext(obj);
    }

    @Test
    public void onErrorDelegates() {
        CallStreamObserver<Object> obs = mock(CallStreamObserver.class);
        Subscriber<Object> sub = mock(Subscriber.class);

        ReactiveStreamObserverPublisher<Object> pub = new ReactiveStreamObserverPublisher<Object>(obs);
        pub.subscribe(sub);

        Throwable obj = new Exception();

        pub.onError(obj);
        verify(sub).onError(obj);
    }

    @Test
    public void onCompletedDelegates() {
        CallStreamObserver<Object> obs = mock(CallStreamObserver.class);
        Subscriber<Object> sub = mock(Subscriber.class);

        ReactiveStreamObserverPublisher<Object> pub = new ReactiveStreamObserverPublisher<Object>(obs);
        pub.subscribe(sub);

        pub.onCompleted();
        verify(sub).onComplete();
    }

    @Test
    public void requestDelegates() {
        CallStreamObserver<Object> obs = mock(CallStreamObserver.class);
        Subscriber<Object> sub = mock(Subscriber.class);

        final AtomicReference<Subscription> subscription = new AtomicReference<Subscription>();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                subscription.set((Subscription) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(sub).onSubscribe(any(Subscription.class));

        ReactiveStreamObserverPublisher<Object> pub = new ReactiveStreamObserverPublisher<Object>(obs);
        pub.subscribe(sub);

        assertThat(subscription.get()).isNotNull();
        subscription.get().request(10);
        verify(obs).request(10);
    }
}
