package com.handson.infra;


import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestTest {

    @Test
    public void should_return_parameter_value() {
        // given
        Map<String, List<String>> rawParams = Collections.singletonMap("myParam", Collections.singletonList("123"));
        HttpRequest httpRequest = new HttpRequest(rawParams);
        // when
        String myParamValue = httpRequest.getParameter("myParam");
        // then
        assertThat(myParamValue).isEqualTo("123");
    }

    @Test
    public void should_return_null_when_no_parameter__with_given_name() {
        // given
        Map<String, List<String>> rawParams = Collections.singletonMap("myParam", Collections.singletonList("123"));
        HttpRequest httpRequest = new HttpRequest(rawParams);
        // when
        String myParamValue = httpRequest.getParameter("myParam2");
        // then
        assertThat(myParamValue).isNull();
    }

}