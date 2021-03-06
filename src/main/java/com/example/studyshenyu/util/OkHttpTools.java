/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.studyshenyu.util;

import okhttp3.*;
import sun.net.www.content.image.jpeg;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * OkHttpTools
 */
public final class OkHttpTools {


    /**
     * The constant JSON.
     */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * The octet-stream
     */
    public static final MediaType FILE = MediaType.parse("application/octet-stream");


    private static final OkHttpTools OK_HTTP_TOOLS = new OkHttpTools();


    private final OkHttpClient client;

    private OkHttpTools() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        client = builder.build();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static OkHttpTools getInstance() {
        return OK_HTTP_TOOLS;
    }

    /**
     * post json string
     *
     * @param url  the url
     * @param json the json
     * @return the string
     * @throws IOException the io exception
     */
    public String post(final String url, final String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return client.newCall(request).execute().body().string();
    }

    /**
     * get string
     *
     * @param url the url
     * @return the string
     * @throws IOException the io exception
     */
    public String get(final String url) throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return client.newCall(request).execute().body().string();
    }

    /**
     * get with paramMap
     *
     * @param url      the url
     * @param paramMap the paramMap
     * @return the string
     */
    public String get(String url, Map<String, Object> paramMap) throws IOException {
        // ??????????????????
        if (!paramMap.isEmpty()) {
            StringBuilder buffer = new StringBuilder(url);
            buffer.append('?');
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                buffer.append(entry.getKey());
                buffer.append('=');
                buffer.append(entry.getValue());
                buffer.append('&');
            }
            buffer.deleteCharAt(buffer.length() - 1);
            url = buffer.toString();
        }
        Request request = new Request.Builder().get()
                .url(url)
                .build();
        return client.newCall(request).execute().body().string();
    }

    /**
     * post with form
     *
     * @param url      the url
     * @param paramMap paramMap
     * @return the string
     * @throws IOException IOException
     */
    public String post(String url, Map<String, Object> paramMap) throws IOException {
        FormBody.Builder formBody = new FormBody.Builder();
        if (!paramMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue().toString());
            }
        }
        RequestBody body = formBody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return client.newCall(request).execute().body().string();
    }

    /**
     * post file
     * @param url the url
     * @param paramMap paramMap
     * @param files files
     * @return the string
     * @throws IOException IOException
     */
    public String post(String url, Map<String, Object> paramMap, Map<String, File> files) throws IOException {
        MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //????????????
        if (!paramMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                body.addFormDataPart(entry.getKey(), entry.getValue().toString());
            }
        }
        //????????????
        if (!files.isEmpty()) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                RequestBody fileBody = RequestBody.create(files.get(entry.getKey()), FILE);
                body.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\";filename=\"" + files.get(entry.getKey()).getName() + "\""), fileBody);
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .post(body.build())
                .build();
        return client.newCall(request).execute().body().string();
    }

    //https://www.cnblogs.com/whoislcj/p/5529827.html
    /**
     * text/html???HTML??????
     * text/pain??????????????????
     * image/jpeg???jpg????????????
     * application/json???JSON????????????
     * application/octet-stream???????????????????????????????????????????????????
     * application/x-www-form-urlencoded???form??????encType??????????????????????????????????????????key/value???????????????????????????
     * multipart/form-data??????????????????????????????
     */
}
