package com.github.jvogit.springreactnextjs.model.response;

public class RefreshTokenResponse {

    private String accessToken;

    public RefreshTokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public static RefreshTokenResponse.Builder builder() {
        return new RefreshTokenResponse.Builder();
    }

    public static class Builder {

        private String accessToken;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public RefreshTokenResponse build() {
            return new RefreshTokenResponse(accessToken);
        }
    }
}
