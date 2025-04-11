package com.example.imperativetransactions;

import org.json.JSONArray;
import org.json.JSONObject;

public class HttpResultDO {
    private String requestId = null;
    private JSONObject responseObject = null;
    private JSONArray responseArray = null;
    private WebServiceHelper.ServiceCallStatus result = WebServiceHelper.ServiceCallStatus.Pending;
    private String responseContent = "";
    private String errorMessage = "";
    private String requestType = "";
    private String cookie = "";
    private String DBTokenId = "";

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public JSONObject getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(JSONObject responseObject) {
        this.responseObject = responseObject;
    }

    public JSONArray getResponseArray() {
        return responseArray;
    }

    public void setResponseArray(JSONArray responseArray) {
        this.responseArray = responseArray;
    }

    public WebServiceHelper.ServiceCallStatus getResult() {
        return result;
    }

    public void setResult(WebServiceHelper.ServiceCallStatus result) {
        this.result = result;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getDBTokenId() {
        return DBTokenId;
    }

    public void setDBTokenId(String DBTokenId) {
        this.DBTokenId = DBTokenId;
    }

    @Override
    public String toString() {
        return "HttpResultDO - requestId:" + requestId + " $$ ServiceCallStatus:" + result + " $$ errorMessage:" + errorMessage + " $$ responseContent:" + responseContent + " $$ responseObject:" + responseObject + " $$ responseArray:" + responseArray + " $$ requestType:" + requestType + " $$ cookie:" + cookie;
    }
}
