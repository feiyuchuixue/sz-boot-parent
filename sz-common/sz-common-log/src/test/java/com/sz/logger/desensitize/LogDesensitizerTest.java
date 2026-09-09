package com.sz.logger.desensitize;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogDesensitizerTest {

    private final LogDesensitizer desensitizer = new LogDesensitizer();

    @Test
    void shouldKeepBusinessIdsInJsonAuditParams() {
        String text = """
                {"dto":{"menu":{"menuIds":[705327582563930148]},"ids":[1,2,3],"roleId":1,"deptIds":[2],"userIds":[3]}}
                """;

        String result = desensitizer.desensitize(text);

        assertThat(result).contains("\"menuIds\":[705327582563930148]");
        assertThat(result).contains("\"ids\":[1,2,3]");
        assertThat(result).contains("\"roleId\":1");
        assertThat(result).contains("\"deptIds\":[2]");
        assertThat(result).contains("\"userIds\":[3]");
    }

    @Test
    void shouldMaskSensitiveFieldsInJsonByFieldName() {
        String text = """
                {"dto":{"idCard":"110101199001011234","phone":"13812345678","email":"demo@example.com","password":"abc","token":"xyz"}}
                """;

        String result = desensitizer.desensitize(text);

        assertThat(result).contains("\"idCard\":\"110101********1234\"");
        assertThat(result).contains("\"phone\":\"138****5678\"");
        assertThat(result).contains("\"email\":\"d**o@example.com\"");
        assertThat(result).contains("\"password\":\"******\"");
        assertThat(result).contains("\"token\":\"******\"");
    }

    @Test
    void shouldMaskSnakeCaseSensitiveFieldsInJson() {
        String text = """
                {"dto":{"identity_card":"110101199001011234","access_token":"abc","refresh_token":"xyz","cert_no":"110101199001011234"}}
                """;

        String result = desensitizer.desensitize(text);

        assertThat(result).contains("\"identity_card\":\"110101********1234\"");
        assertThat(result).contains("\"access_token\":\"******\"");
        assertThat(result).contains("\"refresh_token\":\"******\"");
        assertThat(result).contains("\"cert_no\":\"110101********1234\"");
    }

    @Test
    void shouldNotMaskBareSnowflakeIdInText() {
        String result = desensitizer.desensitize("menu id is 705327582563930148");

        assertThat(result).isEqualTo("menu id is 705327582563930148");
    }

    @Test
    void shouldMaskExplicitSecretsAndEmailInText() {
        String result = desensitizer.desensitize("password=abc token=xyz email demo@example.com");

        assertThat(result).contains("password=******");
        assertThat(result).contains("token=******");
        assertThat(result).contains("d**o@example.com");
    }
}
