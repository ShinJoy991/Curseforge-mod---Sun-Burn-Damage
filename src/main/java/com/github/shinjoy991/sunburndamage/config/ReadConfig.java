package com.github.shinjoy991.sunburndamage.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.*;

import static com.github.shinjoy991.sunburndamage.SunBurnDamage.LOGGER;

public class ReadConfig {
    public static JsonObject jsonObject;
    public static ArrayList<String> keysList = new ArrayList<>();
    private static int errordelay = 300;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String readJsonValue(Path configFile) {
        try {
            String jsonString = new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8);
            jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String memberName = entry.getKey();
                if (!memberName.equals("__comment")) {
                    keysList.add(memberName);
                }
            }
        } catch (IOException e) {
            LOGGER.error("[SunBurnDamage] Error when reading Json" + e);
        }
        return null;
    }

    public static Object getConfig(String key, String subKey, int getInt) {
        try {
            JsonElement element = jsonObject.getAsJsonObject(key).get(subKey);
            if (getInt != 1) {
                return element.getAsString();
            }
            return element.getAsInt();
        } catch (Exception e) {
            if (errordelay > 300) {
                errordelay = 0;
                LOGGER.error(String.valueOf(e));
                LOGGER.error("[SunBurnDamage] error " + subKey + " in " + key);
            } else
                errordelay++;
            if (getInt != 1) {
                return "null";
            }
            return -1;
        }
    }

    public static Object getConfig(String key) {
        try {
            JsonElement element = jsonObject.get(key);
            if (element == null) {
                throw new JsonParseException("Element not found: " + key);
            }
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                List<String> list = new ArrayList<>();
                for (JsonElement elem : array) {
                    list.add(elem.getAsString());
                }
                return list;
            } else {
                return element.getAsString();
            }
        } catch (Exception e) {
            if (errordelay > 300) {
                errordelay = 0;
                LOGGER.error("Error retrieving config: ", e);
                LOGGER.error("[SunBurnDamage] Error retrieving config key " + key);
            } else {
                errordelay++;
            }
            return "[SunBurnDamage] Error retrieving config key";
        }
    }

    public static int reloadConfig() {
        try {
            String jsonString = new String(Files.readAllBytes(CreateJson.configFile),
                    StandardCharsets.UTF_8);
            jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
            return 0;
        } catch (IOException e) {
            LOGGER.error("[SunBurnDamage] Config reload error " + e);
            return 1;
        }
    }

    public static void updateConfig(String key, List<String> newList, Integer type) {
        try {
            JsonArray jsonArray = jsonObject.getAsJsonArray(key);
            if (type == 0) {
                for (String value : newList) {
                    if (!jsonArray.contains(new JsonPrimitive(value))) {
                        jsonArray.add(value);
                    }
                }
            }
            else if (type == 1) {
                for (JsonElement value : jsonArray) {
                    for (String list : newList) {
                        if (value.getAsString().equals(list))
                            jsonArray.remove(value);
                        }
                }
            }

            Path configFile = CreateJson.configFile;
            Files.write(configFile, GSON.toJson(jsonObject).getBytes(StandardCharsets.UTF_8));
            LOGGER.info("[SunBurnDamage] Config updated successfully.");
        } catch (IOException e) {
            LOGGER.error("[SunBurnDamage] Error updating config", e);
        }
    }

    public static void updateConfig(String key, String removeString, Integer type) {
        try {
            JsonArray jsonArray = jsonObject.getAsJsonArray(key);
            if (type == 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonElement value = jsonArray.get(i);
                    if (value.getAsString().equals(removeString)) {
                        jsonArray.remove(i);
                        i--;
                    }
                }
            }
//            else if (type == 1) {
//                for (JsonElement value : jsonArray) {
//                        if (value.getAsString().equals(removeString))
//                            jsonArray.remove(value);
//                }
//            }

            Path configFile = CreateJson.configFile;
            Files.write(configFile, GSON.toJson(jsonObject).getBytes(StandardCharsets.UTF_8));
            LOGGER.info("[SunBurnDamage] Config updated successfully.");
        } catch (Exception e) {
            LOGGER.error("[SunBurnDamage] Error updating config", e);
        }
    }
}