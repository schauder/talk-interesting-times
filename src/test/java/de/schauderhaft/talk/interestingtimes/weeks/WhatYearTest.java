/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schauderhaft.talk.interestingtimes.weeks;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeProperty;
import oracle.jdbc.pool.OracleDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;

import static de.schauderhaft.talk.interestingtimes.weeks.WhatWeekIsItTest.*;
import static org.assertj.core.api.Assertions.*;

public class WhatYearTest {

	OracleContainer oracle = new OracleContainer("gvenzl/oracle-xe").withReuse(true);
	private NamedParameterJdbcTemplate template;


	@BeforeProperty
	void setUp() {
		try {
			oracle.start();
			final OracleDataSource ds;
			ds = new OracleDataSource();
			ds.setURL(oracle.getJdbcUrl());
			ds.setPassword(oracle.getPassword());
			ds.setUser(oracle.getUsername());

			template = new NamedParameterJdbcTemplate(ds);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Property
	void thereIsOneIsoStandard(@ForAll LocalDate date) {

		Integer weekFromOracle = template.queryForObject("select to_char(:date, 'IW') from dual", Collections.singletonMap("date", date), Integer.class);
		Integer weekFromJava = week(date);

		assertThat(weekFromJava).isEqualTo(weekFromOracle);
	}
}
