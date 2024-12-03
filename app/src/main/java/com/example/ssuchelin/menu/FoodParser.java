package com.example.ssuchelin.menu;

import java.util.ArrayList;
import java.util.List;
public class FoodParser {

    public static class FoodInfo {
        String category;
        String mainMenu;
        List<String> subMenu;
        String allergyInfo;
        String originInfo;
    }

    public FoodInfo parseFoodData(String data) {
        FoodInfo foodInfo = new FoodInfo();

        // Category 파싱 (대괄호 안의 텍스트)
        foodInfo.category = parseCategory(data);

        // Main Menu 파싱 (★로 시작하는 첫 번째 메뉴, 숫자가 포함된 부분까지)
        foodInfo.mainMenu = parseMainMenu(data);

        // Sub Menu 파싱 (Main Menu 이후 *알러지유발식품 전에 나오는 항목들)
        foodInfo.subMenu = parseSubMenu(data);

        // Allergy Info 파싱 (알러지 정보는 *알러지유발식품: 이후)
        foodInfo.allergyInfo = parseAllergyInfo(data);

        // Origin Info 파싱 (원산지 정보는 *원산지: 이후)
        foodInfo.originInfo = parseOriginInfo(data);

        return foodInfo;
    }

    private String parseCategory(String data) {
        int startIdx = data.indexOf("[");
        int endIdx = data.indexOf("]");
        if (startIdx != -1 && endIdx != -1) {
            return data.substring(startIdx + 1, endIdx).trim();
        }
        return "Category 없음";
    }

    private String parseMainMenu(String data) {
        // Main Menu는 ★로 시작하며, 숫자가 포함된 부분까지
        int startIdx = data.indexOf("★");
        if (startIdx != -1) {
            // ★ 이후 공백 또는 숫자가 포함된 부분을 메인 메뉴로 인식
            int endIdx = data.indexOf(" ", startIdx);  // 공백을 기준으로 끝 찾기
            if (endIdx == -1) endIdx = data.length(); // 공백이 없으면 끝까지

            // 숫자가 있는 위치 찾기
            String mainMenu = data.substring(startIdx, endIdx).trim();
            // 숫자가 포함되었을 경우, 숫자 끝까지 잘라내어 반환
            int numEndIdx = findNumberEndIdx(mainMenu);
            if (numEndIdx != -1) {
                return mainMenu.substring(0, numEndIdx).trim();
            }

            return mainMenu; // 기본적으로는 메뉴 끝까지
        }
        return "메인 메뉴 정보 없음";
    }

    // 숫자 끝을 찾는 함수 (메인 메뉴에서 숫자가 끝나는 위치를 찾음)
    private int findNumberEndIdx(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i)) && text.charAt(i) != '.') {
                return i; // 숫자가 끝나는 위치
            }
        }
        return -1; // 숫자가 없는 경우
    }

    private List<String> parseSubMenu(String data) {
        List<String> subMenu = new ArrayList<>();
        int mainMenuEndIdx = data.indexOf("★") + parseMainMenu(data).length();
        int allergyStartIdx = data.indexOf("*알러지유발식품:");

        // Main 메뉴 이후, Allergy 정보가 시작되기 전까지를 서브 메뉴로 처리
        String subMenuText = data.substring(mainMenuEndIdx, allergyStartIdx != -1 ? allergyStartIdx : data.length()).trim();

        if (!subMenuText.isEmpty()) {
            // 영어 텍스트를 무시하고, 나머지 메뉴 항목들만 추가
            String[] items = subMenuText.split(" ");
            for (String item : items) {
                if (!item.isEmpty() && !containsEnglish(item)) {
                    subMenu.add(item);
                }
            }
        }

        return subMenu;
    }

    // 영어 텍스트가 포함되어 있는지 확인하는 함수
    private boolean containsEnglish(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetter(text.charAt(i)) && Character.isAlphabetic(text.charAt(i))) {
                return true; // 영어가 포함되어 있으면 true
            }
        }
        return false;
    }

    private String parseAllergyInfo(String data) {
        int startIdx = data.indexOf("*알러지유발식품:");
        if (startIdx != -1) {
            return data.substring(startIdx).trim();
        }
        return "알러지 정보 없음";
    }

    private String parseOriginInfo(String data) {
        int startIdx = data.indexOf("*원산지:");
        if (startIdx != -1) {
            return data.substring(startIdx).trim();
        }
        return "원산지 정보 없음";
    }
}