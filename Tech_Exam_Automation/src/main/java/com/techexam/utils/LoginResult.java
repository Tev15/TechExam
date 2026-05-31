package com.techexam.utils;

import com.techexam.page.HomePage;
import com.techexam.page.LoginPage;

public class LoginResult {

	private final HomePage homePage;
    private final LoginPage loginPage;

    private LoginResult(HomePage homePage, LoginPage loginPage) {
        this.homePage = homePage;
        this.loginPage = loginPage;
    }

    public static LoginResult success(HomePage homePage) {
        return new LoginResult(homePage, null);
    }

    public static LoginResult failure(LoginPage loginPage) {
        return new LoginResult(null, loginPage);
    }

    public boolean isSuccess() {
        return homePage != null;
    }

    public HomePage getHomePage() {
        return homePage;
    }

    public LoginPage getLoginPage() {
        return loginPage;
    }
}
