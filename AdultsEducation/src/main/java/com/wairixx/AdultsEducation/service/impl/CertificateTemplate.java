package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.model.entity.Certificate;

import java.time.format.DateTimeFormatter;

final class CertificateTemplate {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMMM yyyy", new java.util.Locale("uk"));

    private CertificateTemplate() {}

    static String render(Certificate c) {
        return """
                <!DOCTYPE html>
                <html lang="uk">
                <head>
                <meta charset="UTF-8"/>
                <title>Сертифікат %s</title>
                <style>
                  @page { size: A4 landscape; margin: 0; }
                  * { box-sizing: border-box; }
                  body {
                    margin: 0;
                    font-family: "DejaVu Sans", sans-serif;
                    color: #2d2a26;
                    background: #fbf7ee;
                  }
                  .page {
                    width: 100%%;
                    height: 100vh;
                    padding: 40px;
                    position: relative;
                    background:
                      radial-gradient(circle at top left, rgba(212,169,78,0.08), transparent 60%%),
                      radial-gradient(circle at bottom right, rgba(212,169,78,0.08), transparent 60%%),
                      linear-gradient(135deg, #fdfbf5 0%%, #f5ecd6 100%%);
                  }
                  .frame-outer {
                    border: 3px solid #b8860b;
                    height: 100%%;
                    padding: 10px;
                    position: relative;
                  }
                  .frame-inner {
                    border: 1px solid #d4a94e;
                    height: 100%%;
                    padding: 40px 70px;
                    text-align: center;
                    position: relative;
                  }
                  /* Кутові орнаменти */
                  .corner {
                    position: absolute;
                    width: 60px; height: 60px;
                    color: #b8860b;
                  }
                  .corner.tl { top: -2px; left: -2px; }
                  .corner.tr { top: -2px; right: -2px; transform: scaleX(-1); }
                  .corner.bl { bottom: -2px; left: -2px; transform: scaleY(-1); }
                  .corner.br { bottom: -2px; right: -2px; transform: scale(-1,-1); }

                  .brand {
                    font-family: "Playfair Display", "DejaVu Sans", serif;
                    font-size: 16px;
                    letter-spacing: 6px;
                    color: #8b6914;
                    text-transform: uppercase;
                    margin-bottom: 8px;
                  }
                  .brand-sub {
                    font-size: 11px;
                    letter-spacing: 3px;
                    color: #a08752;
                    text-transform: uppercase;
                    margin-bottom: 30px;
                  }
                  h1 {
                    font-family: "Playfair Display", "DejaVu Sans", serif;
                    font-size: 60px;
                    font-weight: 700;
                    margin: 0;
                    color: #2d2a26;
                    letter-spacing: 4px;
                    text-transform: uppercase;
                  }
                  .divider {
                    width: 140px;
                    height: 2px;
                    background: #b8860b;
                    margin: 18px auto 18px;
                    position: relative;
                  }
                  .divider::before, .divider::after {
                    content: "";
                    width: 8px; height: 8px;
                    background: #b8860b;
                    border-radius: 50%%;
                    position: absolute;
                    top: -3px;
                  }
                  .divider::before { left: -14px; }
                  .divider::after { right: -14px; }
                  .subtitle {
                    font-family: "Playfair Display", serif;
                    font-size: 18px;
                    color: #6b4e0b;
                    letter-spacing: 4px;
                    font-style: italic;
                    margin-bottom: 30px;
                  }
                  .awarded {
                    font-size: 15px;
                    color: #555;
                    font-style: italic;
                    margin-bottom: 10px;
                  }
                  .name {
                    font-family: "Playfair Display", serif;
                    font-size: 46px;
                    font-weight: 700;
                    color: #2d2a26;
                    padding: 6px 40px 12px;
                    display: inline-block;
                    border-bottom: 2px solid #b8860b;
                    margin: 4px 0 24px;
                    letter-spacing: 1px;
                  }
                  .course-label {
                    font-size: 15px;
                    color: #555;
                    margin-top: 6px;
                    font-style: italic;
                  }
                  .course {
                    font-family: "Playfair Display", serif;
                    font-style: italic;
                    font-weight: 700;
                    font-size: 26px;
                    color: #8b6914;
                    margin: 8px 0 25px;
                  }
                  .meta {
                    width: 80%%;
                    margin: 10px auto 0;
                    font-size: 13px;
                    color: #444;
                    display: table;
                    table-layout: fixed;
                  }
                  .meta > div { display: table-cell; padding: 0 12px; text-align: center; }
                  .meta b {
                    display: block;
                    color: #8b6914;
                    font-size: 11px;
                    letter-spacing: 2px;
                    text-transform: uppercase;
                    margin-bottom: 4px;
                  }
                  .meta span { font-size: 14px; color: #2d2a26; }

                  /* Печатка */
                  .seal {
                    position: absolute;
                    right: 90px;
                    bottom: 80px;
                    width: 130px; height: 130px;
                  }
                  .signature {
                    position: absolute;
                    left: 90px;
                    bottom: 80px;
                    text-align: center;
                    min-width: 220px;
                  }
                  .sig-line {
                    border-top: 1px solid #2d2a26;
                    padding-top: 6px;
                    font-size: 12px;
                    color: #444;
                    letter-spacing: 1px;
                  }
                  .sig-name {
                    font-family: "Playfair Display", serif;
                    font-style: italic;
                    font-size: 18px;
                    color: #2d2a26;
                    margin-bottom: 2px;
                  }
                  .cert-no {
                    position: absolute;
                    bottom: 30px; left: 0; right: 0;
                    font-family: "DejaVu Sans", monospace;
                    font-size: 10px;
                    letter-spacing: 2px;
                    color: #a08752;
                    text-align: center;
                  }
                </style>
                </head>
                <body>
                  <div class="page">
                    <div class="frame-outer">
                      <div class="frame-inner">
                        <!-- Кутові орнаменти -->
                        %s %s %s %s

                        <div class="brand">Adult Learning Academy</div>
                        <div class="brand-sub">online education platform</div>

                        <h1>Сертифікат</h1>
                        <div class="divider"></div>
                        <div class="subtitle">Про успішне проходження курсу</div>

                        <div class="awarded">Цим документом засвідчується, що</div>
                        <div class="name">%s</div>
                        <div class="course-label">успішно завершив(-ла) навчальний курс</div>
                        <div class="course">«%s»</div>

                        <div class="meta">
                          <div><b>Дата видачі</b><span>%s</span></div>
                          <div><b>Тривалість</b><span>%d академічних годин</span></div>
                          <div><b>Викладач</b><span>%s</span></div>
                        </div>

                        <!-- Підпис -->
                        <div class="signature">
                          <div class="sig-name">%s</div>
                          <div class="sig-line">Підпис викладача</div>
                        </div>

                        <!-- Печатка -->
                        %s

                        <div class="cert-no">Сертифікат № %s</div>
                      </div>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                c.getCertificateNumber(),
                cornerSvg("tl"), cornerSvg("tr"), cornerSvg("bl"), cornerSvg("br"),
                escape(c.getStudentName()),
                escape(c.getCourseTitle()),
                c.getIssueDate().format(DF),
                c.getDurationHours(),
                escape(c.getTeacherName()),
                escape(c.getTeacherName()),
                sealSvg(c.getCertificateNumber()),
                c.getCertificateNumber()
        );
    }

    /** Кутовий орнамент-завиток. */
    private static String cornerSvg(String pos) {
        return """
               <svg class="corner %s" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg">
                 <path d="M2 2 L2 25 M2 2 L25 2" stroke="currentColor" stroke-width="2" fill="none"/>
                 <path d="M8 8 Q 30 8, 30 30 M8 8 Q 8 30, 30 30"
                       stroke="currentColor" stroke-width="1" fill="none" opacity="0.6"/>
                 <circle cx="8" cy="8" r="3" fill="currentColor"/>
               </svg>
               """.formatted(pos);
    }

    /** SVG-печатка з номером. */
    private static String sealSvg(String number) {
        String shortNo = number.length() > 10 ? number.substring(5, 15) : number;
        return """
               <svg class="seal" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
                 <defs>
                   <path id="circlePath" d="M 100, 100 m -72, 0 a 72,72 0 1,1 144,0 a 72,72 0 1,1 -144,0"/>
                 </defs>
                 <circle cx="100" cy="100" r="90" fill="none" stroke="#b8860b" stroke-width="2"/>
                 <circle cx="100" cy="100" r="80" fill="none" stroke="#b8860b" stroke-width="1"/>
                 <circle cx="100" cy="100" r="55" fill="#b8860b" opacity="0.08"/>
                 <text font-family="Playfair Display, serif" font-size="14" fill="#8b6914"
                       font-weight="700" letter-spacing="2">
                   <textPath href="#circlePath" startOffset="0">ADULT LEARNING ACADEMY •••</textPath>
                 </text>
                 <!-- Центральна зірка -->
                 <polygon points="100,62 108,88 135,88 113,104 122,130 100,114 78,130 87,104 65,88 92,88"
                          fill="#b8860b" opacity="0.85"/>
                 <text x="100" y="148" text-anchor="middle" font-family="DejaVu Sans, sans-serif"
                       font-size="9" fill="#8b6914" letter-spacing="1">%s</text>
               </svg>
               """.formatted(escape(shortNo));
    }

    private static String escape(String s) {
        return s == null ? "" : s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}