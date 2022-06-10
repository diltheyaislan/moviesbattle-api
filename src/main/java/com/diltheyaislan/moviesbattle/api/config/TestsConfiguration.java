package com.diltheyaislan.moviesbattle.api.config;

import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Profile("test")
public class TestsConfiguration {

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		return new EntityManagerFactory() {
			
			@Override
			public <T> T unwrap(Class<T> cls) {
				return null;
			}
			
			@Override
			public boolean isOpen() {
				return false;
			}
			
			@Override
			public Map<String, Object> getProperties() {
				return null;
			}
			
			@Override
			public PersistenceUnitUtil getPersistenceUnitUtil() {
				return null;
			}
			
			@Override
			public Metamodel getMetamodel() {
				return null;
			}
			
			@Override
			public CriteriaBuilder getCriteriaBuilder() {
				return null;
			}
			
			@Override
			public Cache getCache() {
				return null;
			}
			
			@Override
			public EntityManager createEntityManager(SynchronizationType synchronizationType, @SuppressWarnings("rawtypes") Map map) {
				return null;
			}
			
			@Override
			public EntityManager createEntityManager(SynchronizationType synchronizationType) {
				return null;
			}
			
			@Override
			public EntityManager createEntityManager(@SuppressWarnings("rawtypes") Map map) {
				return null;
			}
			
			@Override
			public EntityManager createEntityManager() {
				return null;
			}
			
			@Override
			public void close() {
				
			}
			
			@Override
			public void addNamedQuery(String name, Query query) {
				
			}
			
			@Override
			public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
				
			}
		};
	}
	
	@Bean
	public PlatformTransactionManager platformTransactionManager() {
		return new PlatformTransactionManager() {
			
			@Override
			public void rollback(TransactionStatus status) throws TransactionException {
			}
			
			@Override
			public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
				return null;
			}
			
			@Override
			public void commit(TransactionStatus status) throws TransactionException {
			}
		};
	}
}
