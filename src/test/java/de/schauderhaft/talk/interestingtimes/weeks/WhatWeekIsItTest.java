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

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

public class WhatWeekIsItTest {


	@Property
	void weekOfYear(@ForAll LocalDate date) {
		assertThat(week(date))
				.describedAs("week of %s is %s".formatted(date, week(date)))
				.isBetween(1, 53);
	}

	@Property
	void weekOfYearIsIncreasing(@ForAll("datesIn2022") LocalDate date) {

		Assume.that(date.isBefore(LocalDate.of(2022, 12, 31)));

		final LocalDate nextDay = date.plusDays(1);

		assertThat(date).isBefore(nextDay);
		assertThat(week(date))
				.describedAs(date.toString())
				.isLessThanOrEqualTo(week(nextDay));
	}

	@Property
	void weekOfYearIsConstantInWeek(@ForAll("monday") LocalDate date) {

		final LocalDate nextDay = date.plusDays(1);

		assertThat(week(date))
				.describedAs(date.toString())
				.isEqualTo(week(nextDay));
	}

	@Test
	void weekOfYear() {
		for (int i = 1900; i < 2100; i++) {
			final LocalDate startOfYear = LocalDate.of(i, 1,1);
			final LocalDate endOfYear = LocalDate.of(i, 12,31);

			System.out.printf("%s %s - %s %s%n", startOfYear, week(startOfYear), endOfYear, week(endOfYear) );

			assertThat(week(endOfYear))
					.describedAs(endOfYear.toString())
					.isIn(52,53);
		}

	}

	static int week(LocalDate date) {
		return date.get(WeekFields.ISO.weekOfYear());
	}

	@Provide
	Arbitrary<LocalDate> datesIn2022() {
		return Arbitraries.randomValue(this::randomDateIn2022);
	}

	private LocalDate randomDateIn2022(Random random) {
		final int nextInt = random.nextInt(365);
		return LocalDate.of(2022, 1, 1).plusDays(nextInt);
	}

	@Provide
	Arbitrary<LocalDate> monday() {
		return Arbitraries.randomValue(this::randomMonday);
	}

	private LocalDate randomMonday(Random random) {
		return LocalDate.of(2022, 2, 21).plusDays(7L*random.nextInt(-500,500));
	}

}
