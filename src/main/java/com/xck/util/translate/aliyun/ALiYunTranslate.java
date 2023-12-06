package com.xck.util.translate.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.alimt20181012.AsyncClient;
import com.aliyun.sdk.service.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.sdk.service.alimt20181012.models.TranslateGeneralResponse;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//import javax.net.ssl.KeyManager;
//import javax.net.ssl.X509TrustManager;

/**
 * Copyright 2023 IDSS
 * <p>
 * All right reserved.
 *
 * @author 夏城柯 E-mail: xiack@idss-cn.com
 * @version Create on: 2023/10/25 14:50
 * 类说明
 */
public class ALiYunTranslate {
    //    /**
//     * 使用AK&SK初始化账号Client
//     * @param accessKeyId
//     * @param accessKeySecret
//     * @return Client
//     * @throws Exception
//     */
//    public static com.aliyun.alimt20181012.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
//        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
//                // 必填，您的 AccessKey ID
//                .setAccessKeyId(accessKeyId)
//                // 必填，您的 AccessKey Secret
//                .setAccessKeySecret(accessKeySecret);
//        // Endpoint 请参考 https://api.aliyun.com/product/alimt
//        config.endpoint = "mt.aliyuncs.com";
//        return new com.aliyun.alimt20181012.Client(config);
//    }
//
//    public static void main(String[] args_) throws Exception {
//        java.util.List<String> args = java.util.Arrays.asList(args_);
//        // 请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 和 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
//        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例使用环境变量获取 AccessKey 的方式进行调用，仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
//        com.aliyun.alimt20181012.Client client = ALiYunTranslate.createClient(
//                "LTAI5tK7S8K4NiTvpvuohVXR",
//                "fIaYJRDZ7gTUnlZpRxProtjjew9ThN");
//        com.aliyun.alimt20181012.models.TranslateGeneralRequest translateGeneralRequest = new com.aliyun.alimt20181012.models.TranslateGeneralRequest();
//        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
//        try {
//            // 复制代码运行请自行打印 API 的返回值
//            client.translateGeneralWithOptions(translateGeneralRequest, runtime);
//        } catch (TeaException error) {
//            // 如有需要，请打印 error
//            com.aliyun.teautil.Common.assertAsString(error.message);
//        } catch (Exception _error) {
//            TeaException error = new TeaException(_error.getMessage(), _error);
//            // 如有需要，请打印 error
//            com.aliyun.teautil.Common.assertAsString(error.message);
//        }
//    }
    public static String getTransResult(String word) throws ExecutionException, InterruptedException {

        // HttpClient Configuration
        /*HttpClient httpClient = new ApacheAsyncHttpClientBuilder()
                .connectionTimeout(Duration.ofSeconds(10)) // Set the connection timeout time, the default is 10 seconds
                .responseTimeout(Duration.ofSeconds(10)) // Set the response timeout time, the default is 20 seconds
                .maxConnections(128) // Set the connection pool size
                .maxIdleTimeOut(Duration.ofSeconds(50)) // Set the connection pool timeout, the default is 30 seconds
                // Configure the proxy
                .proxy(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("<your-proxy-hostname>", 9001))
                        .setCredentials("<your-proxy-username>", "<your-proxy-password>"))
                // If it is an https connection, you need to configure the certificate, or ignore the certificate(.ignoreSSL(true))
                .x509TrustManagers(new X509TrustManager[]{})
                .keyManagers(new KeyManager[]{})
                .ignoreSSL(false)
                .build();*/

        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
                .accessKeyId("xxx")
                .accessKeySecret("xxx")
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou") // Region ID
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                // Endpoint 请参考 https://api.aliyun.com/product/alimt
                                .setEndpointOverride("mt.cn-hangzhou.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        // Parameter settings for API request
        TranslateGeneralRequest translateGeneralRequest = TranslateGeneralRequest.builder()
                .sourceLanguage("en")
                .targetLanguage("zh")
                .formatType("text")
                .sourceText(word)
                .scene("general")
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<TranslateGeneralResponse> response = client.translateGeneral(translateGeneralRequest);
        // Synchronously get the return value of the API request
        TranslateGeneralResponse resp = response.get();

        String x = new Gson().toJson(resp);
        // Asynchronous processing of return values
        /*response.thenAccept(resp -> {
            System.out.println(new Gson().toJson(resp));
        }).exceptionally(throwable -> { // Handling exceptions
            System.out.println(throwable.getMessage());
            return null;
        });*/

        // Finally, close the client
        client.close();
        JSONObject jsonObject = JSONObject.parseObject(x);
        JSONObject body = jsonObject.getJSONObject("body");
        if (body != null) {
            JSONObject data = body.getJSONObject("data");
            if (data != null) {

                return data.getString("translated");
            }
        }

        return word;

    }
}







