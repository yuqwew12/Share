package com.example.share.activity.Adapter;


import com.example.share.activity.ImageInfo;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
public class ImageInfoListTypeAdapter implements JsonDeserializer<List<ImageInfo>> {
    @Override
    public List<ImageInfo> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = json.getAsJsonArray();
        List<ImageInfo> imageInfos = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setUrl(element.getAsString());
            imageInfos.add(imageInfo);
        }
        return imageInfos;
    }
}
