package com.crosswordservice;

import com.crosswordservice.baseClasses.Configuration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CrosswordServiceApplicationTests {

    @Test
    void testStartApplication() {
        Configuration test = new Configuration();
        String testName = "testName";

        test.setName(testName);
        assertThat(test.getName()).isEqualTo(testName);
    }
}
