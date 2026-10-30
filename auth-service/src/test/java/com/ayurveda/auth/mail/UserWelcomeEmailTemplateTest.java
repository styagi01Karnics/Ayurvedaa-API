package com.ayurveda.auth.mail;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserWelcomeEmailTemplateTest {

    @Test
    void rendersCredentialsAndSetPasswordLinkWithoutPassword() {
        String html = UserWelcomeEmailTemplate.render(
                "Sri Ayur Hospital",
                "https://cdn.example.com/logo.png",
                "Asha Rao",
                "SAH001",
                "asha@sah.example.com",
                "Doctor",
                "http://localhost:3000/reset-password?token=abc123",
                30);

        assertThat(html).contains("<!DOCTYPE html>");
        assertThat(html).contains("Sri Ayur Hospital");
        assertThat(html).contains("Hello Asha Rao,");
        assertThat(html).contains("SAH001");
        assertThat(html).contains("asha@sah.example.com");
        assertThat(html).contains("href=\"http://localhost:3000/reset-password?token=abc123\"");
        assertThat(html).contains("30 minutes");
        assertThat(html).doesNotContainIgnoringCase("password:");
    }

    @Test
    void escapesUserSuppliedValues() {
        String html = UserWelcomeEmailTemplate.render(
                "<script>x</script>",
                null,
                "<b>Eve</b>",
                "T1",
                "eve@example.com",
                "Staff",
                "http://localhost:3000/reset-password?token=t&x=1",
                15);

        assertThat(html).doesNotContain("<script>x</script>");
        assertThat(html).contains("&lt;script&gt;x&lt;/script&gt;");
        assertThat(html).contains("&lt;b&gt;Eve&lt;/b&gt;");
        assertThat(html).contains("token=t&amp;x=1");
        assertThat(html).doesNotContain("<img");
    }

    @Test
    void subjectFallsBackWhenHospitalNameMissing() {
        assertThat(UserWelcomeEmailTemplate.subject("Sri Ayur Hospital"))
                .isEqualTo("Welcome to Sri Ayur Hospital - set up your account");
        assertThat(UserWelcomeEmailTemplate.subject(" ")).startsWith("Welcome to Ayurvedaa");
    }
}
