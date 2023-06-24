package com.threemovie.threemovieapi.global.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import redis.embedded.RedisServer

@TestConfiguration
@EnableRedisRepositories
class TestRedisConfig {
	val host = "127.0.0.1"
	val port = 3332
	val server = RedisServer(port)
	
	@PostConstruct
	fun startRedisServer() {
		server.start()
	}
	
	@PreDestroy
	fun stopRedisServer() {
		server.stop()
	}
	
	@Primary
	@Bean
	fun redisConnectionFactory() = LettuceConnectionFactory(host, port)
	
	@Primary
	@Bean
	fun redisTemplate(): RedisTemplate<String, String> {
		val redisTemplate = RedisTemplate<String, String>()
		redisTemplate.setConnectionFactory(redisConnectionFactory())
		return redisTemplate
	}
}
