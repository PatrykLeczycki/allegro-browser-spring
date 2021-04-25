package com.pleczycki.allegrobrowser.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;

@RequiredArgsConstructor
@Service
public class AllegroService {

    public String invokeGetMethod(String auth, String url) throws ClientHandlerException {

        Client client = Client.create();
        WebResource webResource = client.resource(url);
        ClientResponse response = webResource.header("Authorization", "Bearer " + auth)
                .accept("application/vnd.allegro.public.v1+json")
                .get(ClientResponse.class);

        if (response.getStatus() == 401) {
            return null;
        }
        return response.getEntity(String.class);
    }

    public HttpEntity<String> getAuthHeader(){

        String clientId = "cfb094bc2e46447796003d89ef3794ec";
        String clientSecret = "XM1d0HDRGXljsw9RwlaXLMAhkI1NsfJZVN7ZDxGkFtqIG7H0UKkSLcWpo4s9RCKG";

        String test = clientId + ":" + clientSecret;
        String encoded = Base64.getEncoder().encodeToString(test.getBytes());

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Basic " + encoded);
        return new HttpEntity<>(headers);
    }
}
