package com.ayurveda.auth.mail;

import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * Renders the HTML welcome email sent after a hospital user is created.
 * The email carries the login email, tenant code and a set-password link.
 * It never carries the password itself.
 */
public final class UserWelcomeEmailTemplate {

    private static final String BRAND_COLOR = "#2E7D5B";
    private static final String TEXT_COLOR = "#1F2937";
    private static final String MUTED_COLOR = "#6B7280";

    private UserWelcomeEmailTemplate() {
    }

    public static String subject(String hospitalName) {
        String name = StringUtils.hasText(hospitalName) ? hospitalName.trim() : "Ayurvedaa";
        return "Welcome to " + name + " - set up your account";
    }

    public static String render(
            String hospitalName,
            String logoUrl,
            String fullName,
            String tenantCode,
            String loginEmail,
            String roleLabel,
            String setPasswordUrl,
            int expiryMinutes) {

        String hospital = esc(StringUtils.hasText(hospitalName) ? hospitalName.trim() : "Ayurvedaa");
        String greetingName = esc(StringUtils.hasText(fullName) ? fullName.trim() : "there");
        String code = esc(tenantCode);
        String email = esc(loginEmail);
        String role = esc(StringUtils.hasText(roleLabel) ? roleLabel : "Staff");
        String link = esc(setPasswordUrl);

        String logoBlock = StringUtils.hasText(logoUrl)
                ? "<img src=\"" + esc(logoUrl) + "\" alt=\"" + hospital
                        + "\" height=\"48\" style=\"display:block;margin:0 auto 8px;\" />"
                : "";

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>%1$s</title>
                </head>
                <body style="margin:0;padding:0;background:#F3F4F6;font-family:Arial,Helvetica,sans-serif;color:%2$s;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#F3F4F6;padding:24px 12px;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="max-width:600px;width:100%%;background:#FFFFFF;border-radius:8px;overflow:hidden;">
                          <tr>
                            <td style="background:%3$s;padding:24px;text-align:center;color:#FFFFFF;">
                              %4$s
                              <div style="font-size:20px;font-weight:bold;">%1$s</div>
                              <div style="font-size:13px;opacity:0.9;">Ayurveda hospital management</div>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:28px 28px 8px;">
                              <p style="margin:0 0 16px;font-size:16px;">Hello %5$s,</p>
                              <p style="margin:0 0 16px;font-size:15px;line-height:1.6;">
                                An account has been created for you at <strong>%1$s</strong> with the role <strong>%6$s</strong>.
                                Use the details below to sign in.
                              </p>
                              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#F9FAFB;border:1px solid #E5E7EB;border-radius:6px;margin:0 0 20px;">
                                <tr>
                                  <td style="padding:14px 16px;font-size:14px;line-height:1.8;">
                                    <div><span style="color:%7$s;">Tenant code:</span> <strong>%8$s</strong></div>
                                    <div><span style="color:%7$s;">Login email:</span> <strong>%9$s</strong></div>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:0 0 20px;font-size:15px;line-height:1.6;">
                                Click the button below to set your password. This link is valid for <strong>%10$d minutes</strong>.
                              </p>
                              <table role="presentation" cellpadding="0" cellspacing="0" style="margin:0 auto 20px;">
                                <tr>
                                  <td align="center" style="border-radius:6px;background:%3$s;">
                                    <a href="%11$s" style="display:inline-block;padding:14px 28px;font-size:15px;font-weight:bold;color:#FFFFFF;text-decoration:none;border-radius:6px;">Set your password</a>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:0 0 8px;font-size:13px;line-height:1.6;color:%7$s;">
                                If the button does not work, copy and paste this link into your browser:
                              </p>
                              <p style="margin:0 0 24px;font-size:13px;word-break:break-all;"><a href="%11$s" style="color:%3$s;">%11$s</a></p>
                              <p style="margin:0 0 8px;font-size:13px;line-height:1.6;color:%7$s;">
                                For your security, never share your password. If you did not expect this email, please contact your hospital administrator.
                              </p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:16px 28px;background:#F9FAFB;border-top:1px solid #E5E7EB;font-size:12px;color:%7$s;text-align:center;">
                              This is an automated message from %1$s. Please do not reply to this email.
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(
                hospital,
                TEXT_COLOR,
                BRAND_COLOR,
                logoBlock,
                greetingName,
                role,
                MUTED_COLOR,
                code,
                email,
                expiryMinutes,
                link);
    }

    private static String esc(String value) {
        return value == null ? "" : HtmlUtils.htmlEscape(value);
    }
}
