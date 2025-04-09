package com.shiro.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//@RequestBody FileData formData

@Controller
@RequestMapping("myController")
public class ContractController {
    private final String contractAddress = "0x1bf8b3493f137ee839af181e75b94868cb1d041c";
    private final String private_key = "413ff4c8f6351d5e6adcb31219d186573cebea090bd92ae6448cccfec5715194";
    private String account;
    @GetMapping("/setValue")
    public String setValue(@RequestParam("fileName") String fileName,
                           @RequestParam("author") String author,
                           @RequestParam("fileHash") String fileHash,
                           @RequestParam("time") String time, Model model) {
        if (Web3jUtils.isWeb3jAvailable()) {
            Web3j web3= Web3j.build(new HttpService("http://127.0.0.1:8545"));
            try{
                if (web3.ethAccounts().send().getAccounts().size() == 0) {
                    // 处理没有账户的情况
                    model.addAttribute("errortransactionMessage", "没有可用的账户");
                    return "save";
                }

                EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                        web3.ethAccounts().send().getAccounts().get(0), DefaultBlockParameterName.LATEST).sendAsync().get();
                BigInteger nonce = ethGetTransactionCount.getTransactionCount();

                Function function = new Function(
                        "saveEvidence",
                        Arrays.asList(new Utf8String(fileName), new Utf8String(author), new Utf8String(fileHash), new Utf8String(time)),
                        Collections.emptyList()
                );

                String encodedFunction = FunctionEncoder.encode(function);
                BigInteger gasPrice=BigInteger.valueOf(1000000000);
                try {
                    // 获取当前燃气价格
                    EthGasPrice gasprice = web3.ethGasPrice().send();
                    // 从 GasPrice 对象中获取燃气价格（以 Wei 为单位）
                    BigInteger gasPriceWei = gasprice.getGasPrice();
                    gasPrice=gasPriceWei;

                } catch (IOException e) {
                    // 处理异常情况
                    e.printStackTrace();
                }
                BigInteger gasLimit = BigInteger.valueOf(200000);  // 设置一个合适的燃气限制
                RawTransaction rawTransaction = RawTransaction.createTransaction(
                        nonce,
                        gasPrice,
                        gasLimit,
                        contractAddress,
                        encodedFunction
                );

                byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(private_key));
                String hexValue = Numeric.toHexString(signedMessage);

                EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
                String transactionHash = ethSendTransaction.getTransactionHash();
                model.addAttribute("successtransactionMessage", "Transaction sent, transaction hash:" + transactionHash);
                return "save";
            }catch(Exception e) {
                // 处理异常情况
                e.printStackTrace();
                model.addAttribute("errortransactionMessage", "Error when sending transaction:" + e.getMessage());
                return "save";
            }
        }else {
            model.addAttribute("errortransactionMessage", "Unable to connect to an ethernet node");
            return "save";
        }
    }
    @PostMapping("/getValue")
    public String getValue(@RequestParam("fileName") String fileName,Model model) {
        if (Web3jUtils.isWeb3jAvailable()) {
            Web3j web3= Web3j.build(new HttpService("http://127.0.0.1:8545"));
            try {
                Function function = new Function(
                        "getEvidence",
                        Arrays.asList(new Utf8String(fileName)),
                        Arrays.asList(new TypeReference<Utf8String>() {})
                );

                String encodedFunction = FunctionEncoder.encode(function);
                Transaction transaction = Transaction.createEthCallTransaction(
                        account,
                        contractAddress,
                        encodedFunction
                );

                EthCall ethCall = web3.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
                List<Type> result = FunctionReturnDecoder.decode(
                        ethCall.getValue(),
                        function.getOutputParameters()
                );

                if (result.size() > 0) {
                    model.addAttribute("successtransactionMessage", "Obtained value successfully:" + result.get(0).getValue());
                    return "get";
                } else {
                    model.addAttribute("errortransactionMessage", "No value found for this file");
                    return "get";
                }
            } catch (Exception e) {
                // 处理异常情况
                e.printStackTrace();
                model.addAttribute("errortransactionMessage", "Error in obtaining value:" + e.getMessage());
                return "get";
            }
        } else {
            model.addAttribute("errortransactionMessage", "Unable to connect to an ethernet node");
            return "get";
        }
    }
    @PostMapping("/deletValue")
    public String deleteEvidence(@RequestParam("fileName") String fileName,Model model) {
        if (Web3jUtils.isWeb3jAvailable()) {
            Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));
            try {
                if (web3.ethAccounts().send().getAccounts().size() == 0) {
                    // 处理没有账户的情况
                    model.addAttribute("errortransactionMessage", "No available accounts");
                    return "delete";
                }
                // 创建一个 Function 对象，表示要调用的函数及其参数
                Function function = new Function(
                        "deleteEvidence",
                        Arrays.asList(new Utf8String(fileName)),
                        Arrays.asList(new TypeReference<Uint>() {})
                );

                String encodedFunction = FunctionEncoder.encode(function);
                BigInteger gasPrice = BigInteger.valueOf(1000000000);
                BigInteger gasLimit = BigInteger.valueOf(50000);

                EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                        web3.ethAccounts().send().getAccounts().get(0), DefaultBlockParameterName.LATEST).sendAsync().get();
                BigInteger nonce = ethGetTransactionCount.getTransactionCount();

                RawTransaction rawTransaction = RawTransaction.createTransaction(
                        nonce,
                        gasPrice,
                        gasLimit,
                        contractAddress,
                        encodedFunction
                );

                byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(private_key));
                String hexValue = Numeric.toHexString(signedMessage);

                EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
                String transactionHash = ethSendTransaction.getTransactionHash();
                model.addAttribute("successtransactionMessage", "Transaction sent, transaction hash:" + transactionHash);
                return "delete";
            } catch (Exception e) {
                // 处理异常情况
                e.printStackTrace();
                model.addAttribute("errortransactionMessage", "Error when sending transaction:" + e.getMessage());
                return "delete";
            }
        } else {
            model.addAttribute("errortransactionMessage", "Unable to connect to an ethernet node");
            return "delete";
        }
    }
}