package com.yr.pet.ai;

/**
 * @author 李振生
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        JSONArray chat = null;
        while (true){
            System.out.print("与deepSeek对话，请输入内容（输入 'exit' 退出）：");
            String msg = scanner.nextLine(); // 读取一行输入
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS) // 连接超时时间
                    .readTimeout(300, TimeUnit.SECONDS)    // 读取超时时间
                    .writeTimeout(300, TimeUnit.SECONDS)   // 写入超时时间
                    .build();
            // 检查是否退出
            if ("exit".equalsIgnoreCase(msg)) {
                System.out.println("程序已退出。");
                break; // 退出循环
            }
            chat = sendMessage(client, msg, chat);
        }
    }

    private static JSONArray sendMessage(OkHttpClient client,String question,JSONArray chat) {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject param = new JSONObject();
        param.put("model","deepseek-chat");
        param.put("frequency_penalty",0);
        param.put("presence_penalty",0);
        JSONArray messages = chat;

        if (messages == null){
            messages = new JSONArray();
            JSONObject o1 = new JSONObject();
            o1.put("role","user");
            o1.put("content",question);
            messages.add(o1);
        }else {
            JSONObject o1 = new JSONObject();
            o1.put("role","user");
            o1.put("content",question);
            messages.add(o1);
        }
        param.put("messages",messages);


//        RequestBody body = RequestBody.create(mediaType, String.format("{\n  \"messages\": [\n    {\n      \"content\": \"%s\"," +
//                "\n      \"role\": \"user\"\n    },\n    {\n      \"content\": \"%s\",\n      \"role\": \"user\"\n    }\n  ],\n  \"model\": \"deepseek-chat\",\n  \"frequency_penalty\": 0,\n  \"max_tokens\": 2048," +
//                "\n  \"presence_penalty\": 0,\n  \"response_format\": {\n    \"type\": \"text\"\n  },\n  \"stop\": null,\n  \"stream\": false," +
//                "\n  \"stream_options\": null,\n  \"temperature\": 1,\n  \"top_p\": 1,\n  \"tools\": null,\n  \"tool_choice\": \"none\",\n  \"logprobs\": false,\n  \"top_logprobs\": null\n}",question,question1));

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(param));
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer sk-9e0355f16c854d74b04e194acf903ca6")//你申请的api_keys
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    // 将响应体转换为字符串
                    String responseString = responseBody.string();
                    JSONObject jsonObject = JSONObject.parseObject(responseString);
                    JSONArray choices = JSONArray.parseArray(jsonObject.getString("choices"));
                    String content = JSONObject.parseObject(JSONObject.parseObject(choices.get(0).toString()).get("message").toString()).get("content").toString();
                    if (StringUtils.isNotBlank(content)){
                        JSONObject o1 = new JSONObject();
                        o1.put("role","assistant");
                        o1.put("content",content);
                        messages.add(o1);
                    }
                    System.out.println(content);
                    return messages;
                }
            } else {
                System.out.println("Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

