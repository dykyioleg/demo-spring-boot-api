package com.example.demo.config;

import com.example.demo.mappers.MainIssueMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

	@Bean
	public MainIssueMapper mainIssueMapper() {
		return Mappers.getMapper(MainIssueMapper.class);
	}
}
