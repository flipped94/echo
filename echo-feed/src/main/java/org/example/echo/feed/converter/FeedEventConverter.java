package org.example.echo.feed.converter;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.reflect.TypeToken;
import org.example.echo.feed.entity.FeedPullEvent;
import org.example.echo.feed.entity.FeedPushEvent;
import org.example.echo.sdk.feed.FeedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Mapper
public interface FeedEventConverter {

    FeedEventConverter INSTANCE = Mappers.getMapper(FeedEventConverter.class);

    List<FeedEvent> pushEventsConvert(List<FeedPushEvent> events);

    @Mapping(source = "content", target = "ext", qualifiedByName = "contentConvert")
    FeedEvent pushEventsConvert(FeedPushEvent event);

    List<FeedEvent> pullEventsConvert(List<FeedPullEvent> events);

    @Mapping(source = "content", target = "ext", qualifiedByName = "contentConvert")
    FeedEvent pullEventsConvert(FeedPullEvent event);

    @Named("contentConvert")
    default Map<String, String> contentConvert(String content) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(content, type);
    }
}
