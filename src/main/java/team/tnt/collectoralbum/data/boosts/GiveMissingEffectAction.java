package team.tnt.collectoralbum.data.boosts;

import com.google.gson.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;
import team.tnt.collectoralbum.util.JsonHelper;

public class GiveMissingEffectAction implements IAction {

    private final GiveEffectsAction.IEffectFactory[] factories;

    public GiveMissingEffectAction(GiveEffectsAction.IEffectFactory[] factories) {
        this.factories = factories;
    }

    @Override
    public void apply(IBoostContext context) {
        PlayerEntity player = context.get(SimpleBoostContext.PLAYER, PlayerEntity.class);
        for (GiveEffectsAction.IEffectFactory factory : factories) {
            EffectInstance instance = factory.makeEffect();
            EffectInstance old = player.getEffect(instance.getEffect());
            if (old == null || old.getAmplifier() != instance.getAmplifier()) {
                player.removeEffect(instance.getEffect());
                player.addEffect(instance);
            }
        }
    }

    public static final class Serializer implements IActionSerializer<GiveMissingEffectAction> {

        @Override
        public GiveMissingEffectAction fromJson(JsonObject data, OpType opType) throws JsonParseException {
            JsonArray array = JSONUtils.getAsJsonArray(data, "effects");
            GiveEffectsAction.IEffectFactory[] factories = new GiveEffectsAction.IEffectFactory[array.size()];
            int i = 0;
            for (JsonElement element : array) {
                JsonObject effectJson = JsonHelper.asObject(element);
                ResourceLocation effectId = new ResourceLocation(JSONUtils.getAsString(effectJson, "effect"));
                Effect effect = ForgeRegistries.POTIONS.getValue(effectId);
                if (effect == null) {
                    throw new JsonSyntaxException("Unknown effect: " + effectId);
                }
                int duration = Math.max(JSONUtils.getAsInt(effectJson, "duration", 20), 0);
                int amplifier = MathHelper.clamp(JSONUtils.getAsInt(effectJson, "amplifier", 0), 0, 255);
                boolean ambient = JSONUtils.getAsBoolean(effectJson, "ambient", false);
                boolean visible = JSONUtils.getAsBoolean(effectJson, "visible", true);
                boolean showIcon = JSONUtils.getAsBoolean(effectJson, "showIcon", true);
                factories[i++] = () -> new EffectInstance(effect, duration, amplifier, ambient, visible, showIcon);
            }
            return new GiveMissingEffectAction(factories);
        }
    }
}
