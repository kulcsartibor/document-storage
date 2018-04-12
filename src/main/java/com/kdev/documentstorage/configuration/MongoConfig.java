package com.kdev.documentstorage.configuration;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author trovo.st@gmail.com
 * 2018-04-12
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.kdev.documentstorage.dal")
public class MongoConfig extends AbstractMongoConfiguration {

	@Value("${spring.data.mongodb.host}")
	private String mongoHost;
	@Value("${spring.data.mongodb.port}")
	private Integer mongoPort;
	@Value("${spring.data.mongodb.database}")
	private String dbName;


	@Override
	@Bean(name = "mongoClient")
	public MongoClient mongoClient() {
		return new MongoClient(mongoHost, mongoPort);
	}

	@Override
	protected String getDatabaseName() {
		return dbName;
	}
}
