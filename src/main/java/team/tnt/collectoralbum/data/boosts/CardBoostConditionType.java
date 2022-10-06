package team.tnt.collectoralbum.data.boosts;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import team.tnt.collectoralbum.common.init.CardBoostConditionRegistry;
import team.tnt.collectoralbum.util.JsonHelper;

public final class CardBoostConditionType<C extends ICardBoostCondition> {

    private final ResourceLocation id;
    private final ICardBoostConditionSerializer<C> serializer;

    public CardBoostConditionType(ResourceLocation id, ICardBoostConditionSerializer<C> serializer) {
        this.id = id;
        this.serializer = serializer;
    }

    public ResourceLocation getId() {
        return id;
    }

    public static <C extends ICardBoostCondition> C fromJson(JsonElement element) throws JsonParseException {
        JsonObject object = JsonHelper.asObject(element);
        ResourceLocation location = new ResourceLocation(JSONUtils.getAsString(object, "type"));
        CardBoostConditionType<C> type = CardBoostConditionRegistry.lookup(location);
        if (type == null) {
            throw new JsonSyntaxException("Unknown condition type: " + location);
        }
        ICardBoostConditionSerializer<C> serializer = type.serializer;
        return serializer.fromJson(object);
    }
}
