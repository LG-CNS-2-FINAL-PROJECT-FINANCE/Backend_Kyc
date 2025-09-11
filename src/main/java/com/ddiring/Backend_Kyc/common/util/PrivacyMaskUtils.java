package com.ddiring.Backend_Kyc.common.util;

public class PrivacyMaskUtils {

    private PrivacyMaskUtils() {
    }

    /**
     * 이름을 마스킹합니다.
     * 예: "홍길동" -> "홍*동", "김철수" -> "김*수"
     * 2글자인 경우: "김철" -> "김*"
     * 1글자인 경우: "김" -> "*"
     * 
     * @param name 원본 이름
     * @return 마스킹된 이름
     */
    public static String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        String trimmedName = name.trim();
        int length = trimmedName.length();

        if (length == 1) {
            return "*";
        } else if (length == 2) {
            return trimmedName.charAt(0) + "*";
        } else {
            // 3글자 이상인 경우 첫 글자와 마지막 글자를 제외하고 가운데를 마스킹
            StringBuilder masked = new StringBuilder();
            masked.append(trimmedName.charAt(0));
            for (int i = 1; i < length - 1; i++) {
                masked.append("*");
            }
            masked.append(trimmedName.charAt(length - 1));
            return masked.toString();
        }
    }

    /**
     * 주민번호 앞자리를 마스킹합니다.
     * 예: "950101" -> "95****"
     * 
     * @param rrn1 주민번호 앞자리
     * @return 마스킹된 주민번호 앞자리
     */
    public static String maskRrn1(String rrn1) {
        if (rrn1 == null || rrn1.isEmpty()) {
            return rrn1;
        }

        String trimmedRrn1 = rrn1.trim();
        if (trimmedRrn1.length() <= 2) {
            return "*".repeat(trimmedRrn1.length());
        }

        // 앞 2자리만 보여주고 나머지는 마스킹
        return trimmedRrn1.substring(0, 2) + "*".repeat(trimmedRrn1.length() - 2);
    }

    /**
     * 주민번호 뒷자리를 마스킹합니다.
     * 예: "1234567" -> "*******"
     * 
     * @param rrn2 주민번호 뒷자리
     * @return 마스킹된 주민번호 뒷자리
     */
    public static String maskRrn2(String rrn2) {
        if (rrn2 == null || rrn2.isEmpty()) {
            return rrn2;
        }

        // 뒷자리는 완전히 마스킹
        return "*".repeat(rrn2.trim().length());
    }

    /**
     * 발급일자를 마스킹합니다.
     * 예: "20231201" -> "2023****"
     * 
     * @param date 발급일자
     * @return 마스킹된 발급일자
     */
    public static String maskDate(String date) {
        if (date == null || date.isEmpty()) {
            return date;
        }

        String trimmedDate = date.trim();
        if (trimmedDate.length() <= 4) {
            return "*".repeat(trimmedDate.length());
        }

        // 앞 4자리(년도)만 보여주고 나머지는 마스킹
        return trimmedDate.substring(0, 4) + "*".repeat(trimmedDate.length() - 4);
    }

    /**
     * API 키를 마스킹합니다.
     * 예: "abcd1234efgh5678" -> "abcd****"
     * 
     * @param apiKey API 키
     * @return 마스킹된 API 키
     */
    public static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return apiKey;
        }

        String trimmedKey = apiKey.trim();
        if (trimmedKey.length() <= 4) {
            return "*".repeat(trimmedKey.length());
        }

        // 앞 4자리만 보여주고 나머지는 마스킹
        return trimmedKey.substring(0, 4) + "*".repeat(trimmedKey.length() - 4);
    }
}
