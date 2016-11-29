package com.demo.config;

import javax.jms.Session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;

@Configuration
@EnableJms
public class Config {
		
	@Value("${awsAccessKeyId}")
	private String awsAccessKeyId;
	
	@Value("${awsSecretKey}")
	private String awsSecretKey;
	
	@Value("${queue.endpoint}")
	private String endpoint;
	
	@Value("${queue.name}")
	private String queueName;
	
	@Bean
	public BasicAWSCredentials awsCredentials(){
		return new BasicAWSCredentials(awsAccessKeyId,
				awsSecretKey);
	}

	@Bean
	public SQSConnectionFactory connectionFactory(){
	SQSConnectionFactory connectionFactory = SQSConnectionFactory
			.builder()
			.withRegion(Region.getRegion(Regions.US_EAST_1))
			.withAWSCredentialsProvider(new AWSCredentialsProvider() {
				
				@Override
				public void refresh() {
				}
				
				@Override
				public AWSCredentials getCredentials() {
					return awsCredentials();
				}
			}).build();
	return connectionFactory;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setDestinationResolver(new DynamicDestinationResolver());
		factory.setConcurrency("3-10");
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		return factory;
	}

	@Bean
	public JmsTemplate defaultJmsTemplate() {
		return new JmsTemplate(connectionFactory());
	}
    
	@Bean
	public AmazonSQSClient createSQSClient() {
		AmazonSQSClient amazonSQSClient = new AmazonSQSClient(awsCredentials());
		amazonSQSClient.setEndpoint(endpoint);
		return amazonSQSClient;
	}
   

}
