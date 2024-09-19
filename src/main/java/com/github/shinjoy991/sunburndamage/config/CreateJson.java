package com.github.shinjoy991.sunburndamage.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.shinjoy991.sunburndamage.SunBurnDamage.LOGGER;

public class CreateJson {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    public static Path configPath = FMLPaths.CONFIGDIR.get().resolve("sunburndamage");
    public static Path configFile = configPath.resolve("sunburndamage_config.json");

    public static void CreateJsonConfigFile() {
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath);
            } catch (IOException e) {
                LOGGER.error("[SunBurnDamage] Failed to create directory: {}", configPath, e);
                return;
            }
        }

        if (Files.exists(configFile)) {
            LOGGER.info("[SunBurnDamage] Config file already exists, checking for missing sections: {}", configFile);
            updateConfigFile(configFile);
            return;
        }

        Map<String, Object> jsonData = new LinkedHashMap<>();
        List<String> comments = new ArrayList<>();
        comments.add("This is config section for SunBurnDamage mod");
        comments.add("Pretty easy, change it to match your desire, go to mod's page for more information");
        comments.add("Use integer numbers and true/false to prevent errors");
        comments.add("All minecraft mob type can be toggle for burning below");
        jsonData.put("__comment", comments);

        jsonData.put("mechanic", createData1());
        jsonData.put("affectmobname", createData2());
        jsonData.put("non-affectmobname", createData3());

        try (FileWriter writer = new FileWriter(configFile.toFile())) {
            GSON.toJson(jsonData, writer);
        } catch (IOException exception) {
            LOGGER.error("[SunBurnDamage] Failed to write config file: {}", configFile, exception);
        }
    }

    private static void updateConfigFile(Path configFile) {
        try {
            String jsonString = new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8);
            JsonObject jsonObject = GSON.fromJson(jsonString, JsonObject.class);

            addMissingSection(jsonObject, "mechanic", createData1());
            addMissingSection(jsonObject, "affectmobname", createData2());
            addMissingSection(jsonObject, "non-affectmobname", createData3());

            try (FileWriter writer = new FileWriter(configFile.toFile())) {
                GSON.toJson(jsonObject, writer);
            }
        } catch (IOException e) {
            LOGGER.error("[SunBurnDamage] Error updating Json config file: {}", configFile, e);
        }
    }

    private static void addMissingSection(JsonObject jsonObject, String key, JsonObject data) {
        if (!jsonObject.has(key)) {
            jsonObject.add(key, data);
        }
    }

    private static void addMissingSection(JsonObject jsonObject, String key, JsonArray data) {
        if (!jsonObject.has(key)) {
            jsonObject.add(key, data);
        }
    }
    private static JsonObject createData1() {
        JsonObject data1 = new JsonObject();
        data1.addProperty("enable", "true");
        data1.addProperty("damageadded", "3");
        data1.addProperty("UNDEAD", "true");
        data1.addProperty("ARTHROPOD", "false");
        data1.addProperty("ILLAGER", "false");
        data1.addProperty("WATER", "false");
        data1.addProperty("UNDEFINED", "false");
        return data1;
    }
    private static JsonArray createData2() {
        JsonArray affectMobNameArray = new JsonArray();
        affectMobNameArray.add("minecraft:zombie");
        affectMobNameArray.add("minecraft:drowned");
        return affectMobNameArray;
    }

    private static JsonArray createData3() {
        JsonArray noneAffectMobNameArray = new JsonArray();
        noneAffectMobNameArray.add("minecraft:zombified_piglin");
        noneAffectMobNameArray.add("minecraft:husk");
        return noneAffectMobNameArray;
    }

}