package team.tnt.collectoralbum.data.boosts;

import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class AlbumCardBoostCollection {

    private final Map<OpType, IAction[]> byOps = new EnumMap<>(OpType.class);

    public AlbumCardBoostCollection(IAction[] cleanUpActions, IAction[] activeActions) {
        byOps.put(OpType.CLEANUP, cleanUpActions);
        byOps.put(OpType.ACTIVE, activeActions);
    }

    public void processOp(OpType type, IBoostContext ctx) {
        IAction[] actions = byOps.get(type);
        if (actions != null) {
            for (IAction action : actions) {
                action.apply(ctx);
            }
        }
    }

    public Component[] getDescription() {
        IAction[] actions = byOps.get(OpType.ACTIVE);
        if (actions == null || actions.length == 0) {
            return new Component[0];
        }
        return Arrays.stream(actions)
                .sorted(IDescriptionProvider::compareTo)
                .map(IDescriptionProvider::getDescription)
                .filter(components -> components.length > 0)
                .flatMap(Arrays::stream)
                .toArray(Component[]::new);
    }
}
