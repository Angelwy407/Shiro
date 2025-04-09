package com.shiro.controller;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

public class Web3jUtils {
    public static boolean isWeb3jAvailable() {
        try {
            Web3j web3j = Web3j.build(new HttpService("http://localhost:8545"));
            web3j.web3ClientVersion().send();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

