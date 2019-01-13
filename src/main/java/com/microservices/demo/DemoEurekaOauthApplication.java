package com.microservices.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableOAuth2Client
@RestController
public class DemoEurekaOauthApplication {
	
	@Autowired
	private OAuth2RestTemplate restTemplate;
	
	@Bean
	public OAuth2RestTemplate restTemplate()
	{
		return new OAuth2RestTemplate(resource(),new DefaultOAuth2ClientContext());
	}
	@Bean
	protected OAuth2ProtectedResourceDetails resource()
	{
		ResourceOwnerPasswordResourceDetails details = new ResourceOwnerPasswordResourceDetails();
		//details.setAccessTokenUri("localhost:9090/oauth/token?grant_type=password&username=admin&password=password2");
		details.setAccessTokenUri("http://localhost:9090/oauth/token");
		details.setClientId("webapp");
		details.setClientSecret("{noop}websecret");
		details.setGrantType("password");
		return details;
	}
	@RequestMapping("/execute")
	public String execute(Principal principal) throws URISyntaxException
	{
		User user =(User)((Authentication)principal).getPrincipal();
		URI uri = new URI("http://localhost:9090/resource/endpoint");
		RequestEntity<String> request = new RequestEntity<>(HttpMethod.POST, uri);
		AccessTokenRequest accessTokenRequest = restTemplate().getOAuth2ClientContext().getAccessTokenRequest();
		accessTokenRequest.set("username",user.getUsername());
		System.out.println("password is "+user.getPassword());
		accessTokenRequest.set("password", user.getPassword());
		//accessTokenRequest.setStateKey("703aba8f-c053-42d1-be1e-cc754538c5c0");
		System.out.println("->"+accessTokenRequest.get("password"));
		System.out.println("->"+accessTokenRequest.get("clientSecret"));
		System.out.println("->"+accessTokenRequest.getExistingToken());
		for(String key : accessTokenRequest.keySet())
		{System.out.println("->"+key+" "+accessTokenRequest.get(key));}
		System.out.println("->"+accessTokenRequest.toString());
		return restTemplate.exchange(request, String.class).getBody();
	}
	public static void main(String[] args) {
		SpringApplication.run(DemoEurekaOauthApplication.class, args);
	}

}

